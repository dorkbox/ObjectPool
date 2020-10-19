package dorkbox.objectPool

abstract class BoundedPoolObject<T>: PoolObject<T>() {
    /**
     * Called when an object is removed from the pool. Useful for logging how many objects are being removed
     */
    open fun onRemoval(`object`: T) {}
}