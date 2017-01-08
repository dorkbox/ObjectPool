/*
 * Copyright 2014 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.objectPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A blocking pool of a specific size, where the entire pool is initially filled, and when the pool is empty, a
 * {@link ObjectPool#take()} will wait for a corresponding {@link ObjectPool#put(Object)}.
 *
 * @author dorkbox, llc
 */
class BlockingPool<T> extends ObjectPool<T> {
    private final BlockingQueue<T> queue;
    private final PoolableObject<T> poolableObject;

    BlockingPool(PoolableObject<T> poolableObject, int size) {
        this(poolableObject, new ArrayBlockingQueue<T>(size), size);
    }

    BlockingPool(final PoolableObject<T> poolableObject, final BlockingQueue<T> queue, final int size) {
        this.poolableObject = poolableObject;
        this.queue = queue;

        for (int x = 0; x < size; x++) {
            T e = poolableObject.create();
            poolableObject.onReturn(e);
            this.queue.add(e);
        }
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     * <p/>
     * This method catches {@link InterruptedException} and discards it silently.
     */
    public
    T take() {
        try {
            return takeInterruptibly();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     */
    public
    T takeInterruptibly() throws InterruptedException {
        final T take = this.queue.take();
        poolableObject.onTake(take);
        return take;
    }

    /**
     * Return object to the pool, waking the threads that have blocked during take()
     */
    public
    void put(T object) {
        poolableObject.onReturn(object);
        this.queue.offer(object);
    }

    /**
     * @return a new object instance created by the pool.
     */
    public
    T newInstance() {
        return poolableObject.create();
    }
}

