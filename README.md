ObjectPool
==========

This provides an ObjectPool, for providing for a safe, and quick pool of objects of a specific size. This is only recommended in systems 
were garbage collection is to be kept to a minimum, and the created objects are large.


- This is for cross-platform use, specifically - linux 32/64, mac 32/64, and windows 32/64. Java 6+


Usage:
```
   /**
    * Takes an object from the pool, Blocks until an item is available in the pool.
    */
    public ObjectPoolHolder<T> take();

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     * <p/>
     * This method catches {@link InterruptedException} and discards it silently.
     */
    T takeUninterruptibly() {

    /**
     * Return object to the pool, waking the threads that have blocked during take()
     */
    void release(T object);

    /**
     * @return a new object instance created by the pool.
     */
    T newInstance();

    /**
     * @return the number of currently pooled objects
     */
    int size();
```
