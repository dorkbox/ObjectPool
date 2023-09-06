/*
 * Copyright 2023 dorkbox, llc
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
package dorkbox.objectPool.suspending

import dorkbox.objectPool.Pool
import dorkbox.objectPool.SuspendingPool
import kotlinx.coroutines.runBlocking

/**
 * A suspending pool of a specific size, where the entire pool is initially filled, and when the pool is empty,
 * a [Pool.take] will wait for a corresponding [Pool.put].
 *
 * @author dorkbox, llc
 */
internal class SuspendingPoolCollection<T: Any>(
        private val queue: SuspendingQueue<T>,
        collection: Collection<T>) : SuspendingPool<T> {

    private val dummyValue: T = collection.elementAt(0)

    init {
        runBlocking {
            for (x in collection) {
                queue.offer(x)
            }
        }
    }

    /**
     * Takes an object from the pool, Suspends until an item is available in the pool.
     *
     * This method catches [InterruptedException] and discards it silently.
     */
    override suspend fun take(): T {
        return try {
            takeInterruptibly()
        } catch (e: InterruptedException) {
            dummyValue
        }
    }

    /**
     * Takes an object from the pool, Suspends until an item is available in the pool.
     *
     * @throws InterruptedException
     */
    override suspend fun takeInterruptibly(): T {
        return queue.take()
    }

    /**
     * Return object to the pool, waking the threads that have suspended during take()
     */
    override suspend fun put(`object`: T) {
        queue.put(`object`)
    }

    /**
     * Return object to the pool, blocking if necessary, and waking the threads that have suspended during take()
     */
    override fun putBlocking(`object`: T) {
        queue.putBlocking(`object`)
    }

    /**
     * @return a new object instance created by the pool.
     */
    override suspend fun newInstance(): T {
        return dummyValue
    }

    /**
     * Closes the underlying queue implementation
     */
    override fun close() {
        queue.close()
    }
}
