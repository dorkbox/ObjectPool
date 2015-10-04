/*
 * from: https://code.google.com/p/furious-objectpool/
 * copyright Eddie Raapoto 2012
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

import java.util.concurrent.ArrayBlockingQueue;


class SafeObjectPool<T> implements ObjectPool<T> {

    private final ArrayBlockingQueue<T> queue;
    private final PoolableObject<T> poolableObject;

    SafeObjectPool(PoolableObject<T> poolableObject, int size) {
        this.poolableObject = poolableObject;

        this.queue = new ArrayBlockingQueue<T>(size);

        for (int x = 0; x < size; x++) {
            T e = poolableObject.create();
            poolableObject.onReturn(e);
            this.queue.add(e);
        }
    }

    @Override
    public
    T take() throws InterruptedException {
        final T take = this.queue.take();
        poolableObject.onTake(take);
        return take;
    }

    @SuppressWarnings({"Duplicates", "SpellCheckingInspection"})
    @Override
    public
    T takeUninterruptibly() {
        try {
            T take = take();
            return take;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public
    void release(T object) {
        poolableObject.onReturn(object);
        this.queue.offer(object);
    }

    @Override
    public
    T newInstance() {
        return poolableObject.create();
    }

    @Override
    public
    int size() {
        return queue.size();
    }
}
