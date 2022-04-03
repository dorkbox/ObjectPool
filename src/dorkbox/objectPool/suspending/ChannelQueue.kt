package dorkbox.objectPool.suspending

import kotlinx.coroutines.channels.Channel

/**
 * Wraps a Kotlin channel into a LIMITED queue implementations
 */
class ChannelQueue<E>(size: Int): SuspendingQueue<E> {
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

    override suspend fun take(): E {
        return channel.receive()
    }
}
