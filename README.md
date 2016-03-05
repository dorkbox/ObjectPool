ObjectPool
==========

This provides an ObjectPool, for providing for a safe, and fixed sized pool of objects. This is only recommended in systems were garbage collection is to be kept to a minimum, and the created objects are large.


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


<h4>We now release to maven!</h4> 

```
<dependency>
  <groupId>com.dorkbox</groupId>
  <artifactId>ObjectPool</artifactId>
  <version>2.2</version>
</dependency>
```

Or if you don't want to use Maven, you can access the files directly here:  
https://oss.sonatype.org/content/repositories/releases/com/dorkbox/ObjectPool/


<h2>License</h2>

This project is distributed under the terms of the Apache v2.0 License. See file "LICENSE" for further references.

