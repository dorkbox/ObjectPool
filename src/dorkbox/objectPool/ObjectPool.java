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

/**
 * @author dorkbox, llc
 */
public
class ObjectPool<T> {
    private final ArrayBlockingQueue<T> queue;
    private final PoolableObject<T> poolableObject;

    /**
     * Gets the version number.
     */
    public static
    String getVersion() {
        return "2.0";
    }

    public
    ObjectPool(PoolableObject<T> poolableObject, int size) {
        this.poolableObject = poolableObject;

        this.queue = new ArrayBlockingQueue<T>(size);

        for (int x = 0; x < size; x++) {
            T e = poolableObject.create();
            poolableObject.onReturn(e);
            this.queue.add(e);
        }
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     */
    public
    T take() throws InterruptedException {
        final T take = this.queue.take();
        poolableObject.onTake(take);
        return take;
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     * <p/>
     * This method catches {@link InterruptedException} and discards it silently.
     */
    @SuppressWarnings({"Duplicates", "SpellCheckingInspection"})
    public
    T takeUninterruptibly() {
        try {
            return take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Return object to the pool, waking the threads that have blocked during take()
     */
    public
    void release(T object) {
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


    /**
     * @return the number of currently pooled objects
     */
    public
    int size() {
        return queue.size();
    }
}


