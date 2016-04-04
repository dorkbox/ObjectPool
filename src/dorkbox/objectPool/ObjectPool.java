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

import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * @author dorkbox, llc
 */
public abstract
class ObjectPool<T> implements Pool<T> {
    /**
     * Gets the version number.
     */
    public static
    String getVersion() {
        return "2.4";
    }


    /**
     * Creates a blocking pool of a specific size, where the entire pool is initially filled, and when the pool is empty, a
     * {@link ObjectPool#take()} will wait for a corresponding {@link ObjectPool#put(Object)}.
     *
     * @param poolableObject controls the lifecycle of the pooled objects.
     * @param size the size of the pool to create
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ArrayBlockingQueue implementation of a specific size
     */
    public static <T> ObjectPool<T> Blocking(final PoolableObject<T> poolableObject, final int size) {
        return new BlockingPool<T>(poolableObject, size);
    }

    /**
     * Creates a blocking pool of a specific size, where the entire pool is initially filled, and when the pool is empty, a
     * {@link ObjectPool#take()} will wait for a corresponding {@link ObjectPool#put(Object)}.
     *
     * @param poolableObject controls the lifecycle of the pooled objects.
     * @param queue the blocking queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ArrayBlockingQueue implementation of a specific size
     */
    public static <T> ObjectPool<T> Blocking(final PoolableObject<T> poolableObject, final BlockingQueue<T> queue, final int size) {
        return new BlockingPool<T>(poolableObject, queue, size);
    }


    /**
     * Creates a non-blocking pool which will grow as much as needed. If the pool is empty, new objects will be created. The items in the
     * pool will never expire (see {@link #NonBlockingSoftReference(PoolableObject)} for pooled objects that will expire as needed).
     *
     * @param poolableObject controls the lifecycle of the pooled objects.
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    public static <T> ObjectPool<T> NonBlocking(final PoolableObject<T> poolableObject) {
        return new NonBlockingPool<T>(poolableObject);
    }

    /**
     * Creates a non-blocking pool which will grow as much as needed. If the pool is empty, new objects will be created. The items in the
     * pool will never expire (see {@link #NonBlockingSoftReference(PoolableObject)} for pooled objects that will expire as needed).
     *
     * @param poolableObject controls the lifecycle of the pooled objects.
     * @param queue the  queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    public static <T> ObjectPool<T> NonBlocking(final PoolableObject<T> poolableObject, final Queue<T> queue) {
        return new NonBlockingPool<T>(poolableObject, queue);
    }


    /**
     * Creates a non-blocking pool which will grow as much as needed. If the pool is empty, new objects will be created. The items in the
     * pool will expire in response to memory demand. (See {@link #NonBlocking(PoolableObject)} for pooled objects that will never expire)
     *
     * @param poolableObject controls the lifecycle of the pooled objects.
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    public static <T> ObjectPool<T> NonBlockingSoftReference(final PoolableObject<T> poolableObject) {
        return new NonBlockingSoftPool<T>(poolableObject);
    }

    /**
     * Creates a non-blocking pool which will grow as much as needed. If the pool is empty, new objects will be created. The items in the
     * pool will expire in response to memory demand. (See {@link #NonBlocking(PoolableObject)} for pooled objects that will never expire)
     *
     * @param poolableObject controls the lifecycle of the pooled objects.
     * @param queue the  queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    public static <T> ObjectPool<T> NonBlockingSoftReference(final PoolableObject<T> poolableObject, final Queue<SoftReference<T>> queue) {
        return new NonBlockingSoftPool<T>(poolableObject, queue);
    }
}

