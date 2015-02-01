ObjectPool
==========

This provides an ObjectPool factory, for providing two different types of object pools. Safe and unsafe.

The main distinction between this pool and others, is speed and compatibility. The faster implementation,
UNSAFE, is used unless unavailable (ie: android), in which case a LinkedBlockingDeque is used.

- This is for cross-platform use, specifically - linux 32/64, mac 32/64, and windows 32/64. Java 6+


Usage:
```
   /**
    * Takes an object from the pool
    */
    public ObjectPoolHolder<T> take();

   /**
    * Return object to the pool
    */
    public void release(ObjectPoolHolder<T> object);
```
