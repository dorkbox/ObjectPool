/*
 * Copyright 2020 dorkbox, llc
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
package dorkbox.objectPool

import com.conversantmedia.util.concurrent.DisruptorBlockingQueue
import dorkbox.objectPool.blocking.BlockingPool
import dorkbox.objectPool.nonBlocking.BoundedNonBlockingPool
import dorkbox.objectPool.nonBlocking.NonBlockingPool
import dorkbox.objectPool.nonBlocking.NonBlockingSoftPool
import dorkbox.objectPool.suspending.SuspendingPool
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author dorkbox, llc
 */
object ObjectPool {
    /**
     * Gets the version number.
     */
    const val version = "3.0"

    /**
     * Creates a suspending pool of a specific size, where the entire pool is initially filled, and when the pool is empty, a
     * [Pool.take] will wait for a corresponding [Pool.put].
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param size the size of the pool to create
     * @param <T> the type of object used in the pool
     *
     * @return a suspending pool using the kotlin Channel implementation of a specific size
     */
    fun <T> suspending(poolObject: SuspendingPoolObject<T>, size: Int): dorkbox.objectPool.SuspendingPool<T> {
        return SuspendingPool(poolObject, size)
    }

    /**
     * Creates a high-performance blocking pool of a specific size, where the entire pool is initially filled, and when the pool is empty, a
     * [Pool.take] will wait for a corresponding [Pool.put].
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param size the size of the pool to create
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the DisruptorBlockingQueue implementation of a specific size
     */
    fun <T> blocking(poolObject: PoolObject<T>, size: Int): Pool<T> {
        return blocking(poolObject, DisruptorBlockingQueue(size), size)
    }

    /**
     * Creates a blocking pool of a specific size, where the entire pool is initially filled, and when the pool is empty, a
     * [Pool.take] will wait for a corresponding [Pool.put].
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param queue the blocking queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the specified [BlockingQueue] implementation of a specific size
     */
    fun <T> blocking(poolObject: PoolObject<T>, queue: BlockingQueue<T>, size: Int): Pool<T> {
        return BlockingPool(poolObject, queue, size)
    }

    /**
     * Creates a non-blocking pool which will grow as much as needed.
     *
     * If the pool is empty, new objects will be created. The items in the pool will never expire or be automatically garbage collected.
     *
     * (see [ObjectPool.nonBlockingSoftReference] for pooled objects that will expire/GC as needed).
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default [ConcurrentLinkedQueue] implementation
     */
    fun <T> nonBlocking(poolObject: PoolObject<T>): Pool<T> {
        return NonBlockingPool(poolObject)
    }

    /**
     * Creates a non-blocking pool which will grow as much as needed.
     *
     * If the pool is empty, new objects will be created. The items in the pool will never expire or be automatically garbage collected.
     *
     * (see [ObjectPool.nonBlockingSoftReference] for pooled objects that will expire/GC as needed).
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param queue the  queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    fun <T> nonBlocking(poolObject: PoolObject<T>, queue: Queue<T>): Pool<T> {
        return NonBlockingPool(poolObject, queue)
    }

    /**
     * Creates a non-blocking pool which will grow as much as needed.
     *
     * If the pool is empty, new objects will be created. The items in the pool will expire and be automatically Garbage Collected in
     * response to memory demand.
     *
     * (See [ObjectPool.nonBlocking] for pooled objects that will never expire).
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    fun <T> nonBlockingSoftReference(poolObject: PoolObject<T>): Pool<T> {
        return NonBlockingSoftPool(poolObject)
    }

    /**
     * Creates a non-blocking pool which will grow as much as needed.
     *
     * If the pool is empty, new objects will be created. The items in the pool will expire and be automatically Garbage Collected in
     * response to memory demand.
     *
     * (See [ObjectPool.nonBlocking] for pooled objects that will never expire).
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param queue the  queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the specified Queue implementation
     */
    fun <T> nonBlockingSoftReference(poolObject: PoolObject<T>, queue: Queue<SoftReference<T>>): Pool<T> {
        return NonBlockingSoftPool(poolObject, queue)
    }

    /**
     * A non-blocking pool which will create as many objects as much as needed but will only store maxSize in the pool.
     * If the pool is empty, new objects will be created.
     * The items added to pool will never expire or be automatically garbage collected.
     * The items not added back to the pool will be garbage collected
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param maxSize controls the maxSize the pool can be
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    fun <T> nonBlockingBounded(poolObject: BoundedPoolObject<T>, maxSize: Long): Pool<T> {
        return BoundedNonBlockingPool(poolObject, maxSize)
    }

    /**
     * A non-blocking pool which will create as many objects as much as needed but will only store maxSize in the pool.
     * If the pool is empty, new objects will be created.
     * The items added to pool will never expire or be automatically garbage collected.
     * The items not added back to the pool will be garbage collected
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param maxSize controls the maxSize the pool can be
     * @param queue the  queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    fun <T> nonBlockingBounded(poolObject: BoundedPoolObject<T>, maxSize: Long, queue: Queue<T>): Pool<T> {
        return BoundedNonBlockingPool(poolObject, maxSize, queue)
    }
}
