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

import dorkbox.objectPool.BoundedPoolObject
import java.util.*
import java.util.concurrent.atomic.*

/**
 * A non-blocking pool which will create as many objects as much as needed but will only store maxSize in the pool.
 * If the pool is empty, new objects will be created.
 *
 * The items added to pool will never expire or be automatically garbage collected.
 * The items not added back to the pool will be garbage collected
 *
 * See [ObjectPool.NonBlockingSoftReference] for pooled objects that will expire/GC as needed
 *
 * @author dorkbox, llc; Abinav Janakiraman
 */
internal class BoundedNonBlockingPool<T>(
        private val poolObject: BoundedPoolObject<T>,
        private val maxSize: Int,
        private val queue: Queue<T>) : NonBlockingPool<T>(poolObject, queue) {

    private val currentSize: AtomicLong = AtomicLong(0)

    /**
     * Return object to the pool, waking the threads that have blocked during take()
     * If current pool size is larger than max size, don't add object back into the pool
     */
    override fun put(`object`: T) {
        if (currentSize.get() <= maxSize) {
            poolObject.onReturn(`object`)
            queue.offer(`object`)
        } else {
            currentSize.decrementAndGet()

            poolObject.onRemove(`object`)
        }
    }

    /**
     * @return a new object instance created by the pool.
     */
    override fun newInstance(): T {
        currentSize.incrementAndGet()

        return poolObject.newInstance()
    }
}
