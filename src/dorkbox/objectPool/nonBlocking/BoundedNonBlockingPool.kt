package dorkbox.objectPool.nonBlocking

import dorkbox.objectPool.BoundedPoolObject
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * A non-blocking pool which will create as many objects as much as needed but will only store maxSize in the pool.
 * If the pool is empty, new objects will be created.
 * The items added to pool will never expire or be automatically garbage collected.
 * The items not added back to the pool will be garbage collected
 * See [ObjectPool.NonBlockingSoftReference] for pooled objectsthat will expire/GC as needed
 *
 * @author dorkbox, llc
 */

internal class BoundedNonBlockingPool<T>(
        private val poolObject: BoundedPoolObject<T>,
        private val maxSize: Long,
        private val queue: Queue<T> = ConcurrentLinkedQueue()) : NonBlockingPool<T>(poolObject, queue) {

    private val currentSize: AtomicLong = AtomicLong(0)

    /**
     * Return object to the pool, waking the threads that have blocked during take()
     * If current pool size is larger then max size, don't add object back into the pool
     */
    override fun put(`object`: T) {
        if (currentSize.get() <= maxSize) {
            poolObject.onReturn(`object`)
            queue.offer(`object`)
        } else {
            currentSize.decrementAndGet()

            poolObject.onRemoval(`object`)
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