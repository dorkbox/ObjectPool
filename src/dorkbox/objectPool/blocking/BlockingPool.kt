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
package dorkbox.objectPool.blocking

import dorkbox.objectPool.Pool
import dorkbox.objectPool.PoolObject
import java.util.concurrent.BlockingQueue

/**
 * A blocking pool of a specific size, where the entire pool is initially filled, and when the pool is empty,
 * a [Pool.take] will wait for a corresponding [Pool.put].
 *
 * @author dorkbox, llc
 */
internal class BlockingPool<T> constructor(
        private val poolObject: PoolObject<T>,
        private val queue: BlockingQueue<T>,
        size: Int) : Pool<T> {

    init {
        for (x in 0 until size) {
            val e = newInstance()
            poolObject.onReturn(e)
            queue.offer(e)
        }
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     *
     * This method catches [InterruptedException] and discards it silently.
     */
    override fun take(): T {
        return try {
            takeInterruptibly()
        } catch (e: InterruptedException) {
            val newInstance = newInstance()
            poolObject.onTake(newInstance)
            newInstance
        }
    }

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     *
     * @throws InterruptedException
     */
    override fun takeInterruptibly(): T {
        val take = queue.take()
        poolObject.onTake(take)

        return take
    }

    /**
     * Return object to the pool, waking the threads that have blocked during take()
     */
    override fun put(`object`: T) {
        poolObject.onReturn(`object`)
        queue.put(`object`)
    }

    /**
     * @return a new object instance created by the pool.
     */
    override fun newInstance(): T {
        return poolObject.newInstance()
    }

    /**
     * Clears the underlying queue implementation
     */
    override fun close() {
        queue.clear()
    }
}
