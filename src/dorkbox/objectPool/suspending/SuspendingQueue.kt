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

import java.util.Collection
import java.util.concurrent.*

/**
 * A limited version of a [BlockingQueue] that suspends instead of blocking
 */
interface SuspendingQueue<E> {
    /**
     * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions, returning
     * `true` upon success and throwing an `IllegalStateException` if no space is currently available.
     *
     * When using a capacity-restricted queue, it is generally preferable to use [offer][.offer].
     *
     * @param element the element to add
     * @return `true` (as specified by [Collection.add])
     * @throws IllegalStateException if the element cannot be added at this time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this queue
     */
    fun add(element: E): Boolean

    /**
     * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions, returning
     * `true` upon success and `false` if no space is currently available.
     *
     * When using a capacity-restricted queue, this method is generally preferable to [.add], which can fail to insert an
     * element only by throwing an exception.
     *
     * @param element the element to add
     * @return `true` if the element was added to this queue, else `false`
     * @throws ClassCastException if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this queue
     */
    fun offer(element: E): Boolean

    /**
     * Inserts the specified element into this queue, waiting if necessary  for space to become available.
     *
     * @param element the element to add
     * @throws InterruptedException if interrupted while waiting
     * @throws ClassCastException if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified element prevents it from being added to this queue
     */
    @Throws(InterruptedException::class)
    suspend fun put(element: E)

    /**
     * Retrieves and removes the head of this queue, waiting if necessary until an element becomes available.
     *
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    @Throws(InterruptedException::class)
    suspend fun take(): E

    /**
     * Retrieves and removes the head of this queue.  This method differs from {@link #poll() poll()} only in that it throws an exception if
     * this queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    fun remove(): E

    /**
     * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
     *
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    fun poll(): E?

    /**
     * Closes the underlying implementation
     */
    fun close()
}
