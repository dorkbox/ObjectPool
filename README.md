ObjectPool
==========

This provides an ObjectPool factory, for providing two different types of object pools. Safe and unsafe.

The main distinction between this pool and others, is speed and compatibility. The factory offers two
implementations:
- https://github.com/ashkrit/blog/tree/master/FastObjectPool
- https://code.google.com/p/furious-objectpool


The faster implementation uses UNSAFE, which is unavailable on android and non-oracle JVMs, in which case the
fallback pool is used, which is based on a LinkedBlockingDeque.  


If the list gets hot and contended, you can get scaling bugs. It gets complicated too fast and is not 
worth it for small to even moderate sized objects.  

Use it only for large objects.  

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
