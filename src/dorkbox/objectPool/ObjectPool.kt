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
import dorkbox.objectPool.blocking.BlockingPoolCollection
import dorkbox.objectPool.nonBlocking.BoundedNonBlockingPool
import dorkbox.objectPool.nonBlocking.NonBlockingPool
import dorkbox.objectPool.nonBlocking.NonBlockingSoftPool
import dorkbox.objectPool.suspending.ChannelQueue
import dorkbox.objectPool.suspending.SuspendingPool
import dorkbox.objectPool.suspending.SuspendingPoolCollection
import dorkbox.objectPool.suspending.SuspendingQueue
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.*

/**
 * @author dorkbox, llc
 */
object ObjectPool {
    /**
     * Gets the version number.
     */
    const val version = "4.3"

    init {
        // Add this project to the updates system, which verifies this class + UUID + version information
        dorkbox.updates.Updates.add(ObjectPool::class.java, "1dc60a2801d941cba9c7964255d8b061", version)
    }

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
    fun <T: Any> suspending(poolObject: SuspendingPoolObject<T>, size: Int): dorkbox.objectPool.SuspendingPool<T> {
        return suspending(poolObject, size, ChannelQueue(size))
    }


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
    fun <T> suspending(poolObject: SuspendingPoolObject<T>, size: Int, queue: SuspendingQueue<T>): dorkbox.objectPool.SuspendingPool<T> {
        return SuspendingPool(poolObject, size, queue)
    fun <T: Any> suspending(poolObject: SuspendingPoolObject<T>, size: Int, queue: SuspendingQueue<T>): dorkbox.objectPool.SuspendingPool<T> {
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
    fun <T: Any> blocking(poolObject: PoolObject<T>, size: Int): Pool<T> {
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
    fun <T: Any> blocking(poolObject: PoolObject<T>, queue: BlockingQueue<T>, size: Int): Pool<T> {
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
    fun <T: Any> nonBlocking(poolObject: PoolObject<T>): Pool<T> {
        return nonBlocking(poolObject, ConcurrentLinkedQueue())
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
    fun <T: Any> nonBlocking(poolObject: PoolObject<T>, queue: Queue<T>): Pool<T> {
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
    fun <T: Any> nonBlockingSoftReference(poolObject: PoolObject<T>): Pool<T> {
        return nonBlockingSoftReference(poolObject, ConcurrentLinkedQueue())
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
    fun <T: Any> nonBlockingSoftReference(poolObject: PoolObject<T>, queue: Queue<SoftReference<T>>): Pool<T> {
        return NonBlockingSoftPool(poolObject, queue)
    }

    /**
     * A non-blocking pool which will create as many objects as much as needed but will only store maxSize in the pool.
     * If the pool is empty, new objects will be created.
     *
     * The items added to pool will never expire or be automatically garbage collected.
     * The items not added back to the pool will be garbage collected
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param maxSize controls the maxSize the pool can be
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    fun <T: Any> nonBlockingBounded(poolObject: BoundedPoolObject<T>, maxSize: Int): Pool<T> {
        return nonBlockingBounded(poolObject, maxSize, DisruptorBlockingQueue(maxSize))
    }

    /**
     * A non-blocking pool which will create as many objects as much as needed but will only store maxSize in the pool.
     * If the pool is empty, new objects will be created.
     *
     * The items added to pool will never expire or be automatically garbage collected.
     * The items not added back to the pool will be garbage collected
     *
     * @param poolObject controls the lifecycle of the pooled objects.
     * @param maxSize controls the maxSize the pool can be
     * @param queue the queue implementation to use
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the default ConcurrentLinkedQueue implementation
     */
    fun <T: Any> nonBlockingBounded(poolObject: BoundedPoolObject<T>, maxSize: Int, queue: Queue<T>): Pool<T> {
        return BoundedNonBlockingPool(poolObject, maxSize, queue)
    }









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
    fun <T: Any> suspending(collection: Collection<T>): dorkbox.objectPool.SuspendingPool<T> {
        return suspending(ChannelQueue(collection.size), collection)
    }

    /**
     * Creates a suspending pool of an existing collection, where the entire pool is initially filled, and when the pool is empty, a
     * [Pool.take] will wait for a corresponding [Pool.put].
     *
     * @param collection the existing collection to convert to a pool
     * @param <T> the type of object used in the pool
     *
     * @return a suspending pool using the kotlin Channel implementation of a specific size
     */
    fun <T: Any> suspending(queue: SuspendingQueue<T>, collection: Collection<T>): dorkbox.objectPool.SuspendingPool<T> {
        return SuspendingPoolCollection(queue, collection)
    }

    /**
     * Creates a high-performance blocking pool of an existing collection, where the entire pool is initially filled, and when the pool is empty, a
     * [Pool.take] will wait for a corresponding [Pool.put].
     *
     * @param collection the existing collection to convert to a pool
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the DisruptorBlockingQueue implementation of a specific size
     */
    fun <T: Any> blocking(collection: Collection<T>): Pool<T> {
        return blocking(DisruptorBlockingQueue(collection.size), collection)
    }

    /**
     * Creates a blocking pool from an existing collection, where the entire pool is initially filled, and when the pool is empty, a
     * [Pool.take] will wait for a corresponding [Pool.put].
     *
     * @param queue the blocking queue implementation to use
     * @param collection the existing collection to convert to a pool
     * @param <T> the type of object used in the pool
     *
     * @return a blocking pool using the specified [BlockingQueue] implementation of a specific size
     */
    fun <T: Any> blocking(queue: BlockingQueue<T>, collection: Collection<T>): Pool<T> {
        return BlockingPoolCollection(queue, collection)
    }
}
