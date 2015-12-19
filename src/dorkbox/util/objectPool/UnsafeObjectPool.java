/*
 * from: http://ashkrit.blogspot.com/2013/05/lock-less-java-object-pool.html
 *       https://github.com/ashkrit/blog/tree/master/FastObjectPool
 * copyright ashkrit 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by dorkbox, llc
 */
package dorkbox.util.objectPool;

import org.jctools.queues.MpmcArrayQueue;
import org.jctools.util.Pow2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class UnsafeObjectPool<T> implements ObjectPool<T> {
    public static final int FULL_RETRY_LIMIT = 200;

    private final MpmcArrayQueue<T> objects;

    private final Lock lock = new ReentrantLock();
    private final Condition empty = lock.newCondition();
    private final PoolableObject<T> poolableObject;

    UnsafeObjectPool(final PoolableObject<T> poolableObject, final int size) throws Throwable {
        this.poolableObject = poolableObject;
        int newSize = Pow2.roundToPowerOfTwo(size);
        objects = new MpmcArrayQueue<T>(newSize);

        for (int x = 0; x < newSize; x++) {
            T e = poolableObject.create();
            poolableObject.onReturn(e);
            objects.offer(e);
        }
    }

    @Override
    public
    T take() throws InterruptedException {
        T poll = objects.poll();
        if (poll == null) {
            lock.lock();
            try {
                while ((poll = objects.poll()) == null) {
                    empty.await();
                }
            } finally {
                lock.unlock();
            }
        }

        poolableObject.onTake(poll);
        return poll;
    }

    @SuppressWarnings({"Duplicates", "SpellCheckingInspection"})
    @Override
    public
    T takeUninterruptibly() {
        try {
            final T take = take();
            return take;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public
    void release(T object) {
        poolableObject.onReturn(object);
        boolean waiting = objects.peek() == null;

        // This could potentially happen due to optimistic calculations by the implementation queue.
        // From my observations, 1 retry was all that was needed.
        if (!objects.offer(object)) {
            int limit = FULL_RETRY_LIMIT;
            while (!objects.offer(object) && limit-- > 0) {
                try {
                    Thread.sleep(2L);
                } catch (InterruptedException ignored) {
                }
            }

            if (limit <= 0) {
                throw new RuntimeException("Unable to insert item into object pool. Pool is full, and retry limit exceeded.");
            }
        }

        if (waiting) {
            lock.lock();
            try {
                if (objects.peek() == null) {
                    // we only need to signal one, since the take/release calls must be symmetric
                    empty.signal();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public
    T newInstance() {
        return poolableObject.create();
    }


    /**
     * This is an optimistic calculation, and if being used by multiple threads, can be inaccurate.
     */
    @Override
    public
    int size() {
        return objects.size();
    }
}
