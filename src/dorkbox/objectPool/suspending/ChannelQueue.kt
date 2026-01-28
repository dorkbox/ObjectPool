/*
 * Copyright 2026 dorkbox, llc
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

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking

/**
 * Wraps a Kotlin channel into a LIMITED queue implementations
 */
internal class ChannelQueue<E: Any>(size: Int): SuspendingQueue<E> {
    private val channel = Channel<E>(size)

    override fun offer(element: E): Boolean {
        val result = channel.trySend(element)
        return result.isSuccess
    }

    override fun remove(): E {
        val tryReceive = channel.tryReceive()
        return tryReceive.getOrNull() ?: throw NoSuchElementException("Channel is empty")
    }

    override fun poll(): E? {
        val tryReceive = channel.tryReceive()
        return tryReceive.getOrNull()
    }

    override fun add(element: E): Boolean {
        val result = channel.trySend(element)
        if (result.isSuccess) {
            return true
        } else {
            throw IllegalStateException("Channel is full.")
        }
    }

    override suspend fun put(element: E) {
        channel.send(element)
    }

    override fun putBlocking(element: E) {
        channel.trySendBlocking(element)
    }

    override suspend fun take(): E {
        return channel.receive()
    }

    override fun close() {
        channel.close()
    }
}
