/*
 * Copyright 2016 dorkbox, llc
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
package dorkbox.objectpool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A non-blocking pool which will grow as much as needed. If the pool is empty, new objects will be created. The items in the
 * pool will never expire or be automatically garbage collected. (see {@link #NonBlockingSoftReference(PoolableObject)} for pooled objects
 * that will expire/GC as needed).
 *
 * @author dorkbox, llc
 */
class NonBlockingPool<T> extends ObjectPool<T> {
    private final Queue<T> queue;
    private final PoolableObject<T> poolableObject;

    NonBlockingPool(final PoolableObject<T> poolableObject) {
        this(poolableObject, new ConcurrentLinkedQueue<T>());
    }

    NonBlockingPool(final PoolableObject<T> poolableObject, final Queue<T> queue) {
        this.poolableObject = poolableObject;
        this.queue = queue;
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     */
    public
    T take() {
        T take = this.queue.poll();
        if (take == null) {
            take = poolableObject.create();
        }

        poolableObject.onTake(take);
        return take;
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     * <p/>
     * This method catches {@link InterruptedException} and discards it silently.
     */
    public
    T takeInterruptibly() throws InterruptedException {
        return take();
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

