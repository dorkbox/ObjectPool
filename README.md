ObjectPool
==========

This provides an ObjectPool, for providing for a safe, and fixed sized pool of objects. This is only recommended in systems were garbage collection is to be kept to a minimum, and the created objects are large.


- This is for cross-platform use, specifically - linux 32/64, mac 32/64, and windows 32/64. Java 6+


Usage:
```
    ObjectPool<T> pool = ObjectPool.NonBlocking(new PoolableObject<T>() {
            /**
             * Called when an object is returned to the pool, useful for resetting an objects state, for example.
             */
            public
            void onReturn(T object) {
                object.foo = 0;
                object.bar = null;
            }
    
            /**
             * Called when an object is taken from the pool, useful for setting an objects state, for example.
             */
            public
            void onTake(T object) {
            }
    
            /**
             * Called when a new instance is created
             */
            @Override
            public
            T create() {
                return new Object();
            }
        });
        
        

    /**
     * Takes an object from the pool. If the pool is a {@link BlockingPool}, this will wait until an item is available in
     * the pool.
     * <p/>
     * This method catches {@link InterruptedException} and discards it silently.
     */
    T take();

    /**
     * Takes an object from the pool. If the pool is a {@link BlockingPool}, this will wait until an item is available in the pool.
     */
    T takeInterruptibly() throws InterruptedException;

    /**
     * Return object to the pool. If the pool is a {@link BlockingPool}, this will wake the threads that have blocked during take/takeInterruptibly()
     */
    void put(T object);

    /**
     * @return a new object instance created by the pool.
     */
    T newInstance();
```

&nbsp; 
&nbsp; 

  
Maven Info
---------
```
<dependencies>
    ...
    <dependency>
      <groupId>com.dorkbox</groupId>
      <artifactId>ObjectPool</artifactId>
      <version>2.11</version>
    </dependency>
</dependencies>
```

Or if you don't want to use Maven, you can access the files directly here:  
https://oss.sonatype.org/content/repositories/releases/com/dorkbox/ObjectPool/


License
---------
This project is © 2014 dorkbox llc, and is distributed under the terms of the Apache v2.0 License. See file "LICENSE" for further references.

