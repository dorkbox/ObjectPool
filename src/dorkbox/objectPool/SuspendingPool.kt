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
package dorkbox.objectPool

/**
 * @author dorkbox, llc
 */
interface SuspendingPool<T: Any> {
    /**
     * Takes an object from the pool. If the pool is a [SuspendingPool], this will wait until an item is available in
     * the pool.
     *
     * This method catches [InterruptedException] and discards it silently.
     */
    suspend fun take(): T

    /**
     * Takes an object from the pool. If the pool is a [SuspendingPool], this will wait until an item is available in the pool.
     *
     * @throws InterruptedException
     */
    suspend fun takeInterruptibly(): T

    /**
     * Return object to the pool. If the pool is a [SuspendingPool], this will wake the threads that have blocked during take/takeInterruptibly()
     */
    suspend fun put(`object`: T)

    /**
     * @return a new object instance created by the pool.
     */
    suspend fun newInstance(): T

    /**
     * Closes the underlying implementation
     */
    fun close()
}
