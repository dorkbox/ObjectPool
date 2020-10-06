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
package dorkbox.objectPool.nonBlocking

import dorkbox.objectPool.ObjectPool
import dorkbox.objectPool.Pool
import dorkbox.objectPool.PoolObject
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A non-blocking pool which will grow as much as needed. If the pool is empty, new objects will be created.
 *
 * The items in the pool will never expire or be automatically garbage collected.
 *
 * (see [ObjectPool.nonBlockingSoftReference] for pooled objects that will expire/GC as needed).
 *
 * @author dorkbox, llc
 */
internal class NonBlockingPool<T>(
        private val poolObject: PoolObject<T>,
        private val queue: Queue<T> = ConcurrentLinkedQueue()) : Pool<T> {

    /**
     * Takes an object from the pool, if there is no object available, will create a new object.
     */
    override fun take(): T {
        return takeInterruptibly()
    }

    /**
     * Takes an object from the pool, if there is no object available, will create a new object.
     */
    override fun takeInterruptibly(): T {
        var take = queue.poll()
        if (take == null) {
            take = poolObject.newInstance()
        }
        poolObject.onTake(take)

        return take
    }

    /**
     * Return object to the pool
     */
    override fun put(`object`: T) {
        poolObject.onReturn(`object`)
        queue.offer(`object`)
    }

    /**
     * @return a new object instance created by the pool.
     */
    override fun newInstance(): T {
        return poolObject.newInstance()
    }
}
