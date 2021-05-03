ObjectPool
==========

###### [![Dorkbox](https://badge.dorkbox.com/dorkbox.svg "Dorkbox")](https://git.dorkbox.com/dorkbox/ObjectPool) [![Github](https://badge.dorkbox.com/github.svg "Github")](https://github.com/dorkbox/ObjectPool) [![Gitlab](https://badge.dorkbox.com/gitlab.svg "Gitlab")](https://gitlab.com/dorkbox/ObjectPool)

This provides an ObjectPool, for providing for a safe, and fixed sized pool of objects. This is only recommended in systems were garbage collection is to be kept to a minimum, and the created objects are large.


- This is for cross-platform use, specifically - linux 32/64, mac 32/64, and windows 32/64. Java 11+


Usage:
```
    val <T> pool = ObjectPool.nonBlocking(PoolObject<T>() {
        /**
         * Called when an object is returned to the pool, useful for resetting an objects state, for example.
         */
         fun onReturn(`object`: Foo) {
            object.foo = 0;
            object.bar = null;
         }
    
         /**
          * Takes an object from the pool, if there is no object available, will create a new object.
          */
          fun onTake(`object`: Foo) {
          }
    
          /**
           * @return a new object instance created by the pool.
           */
          override fun newInstance(): Foo {
             return Foo();
          }
        });


    val foo = pool.take()
    pool.put(foo)
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
      <version>3.4</version>
    </dependency>
</dependencies>
```

Gradle Info
---------
```
dependencies {
    ...
    implementation "com.dorkbox:ObjectPool:3.4"
}
````

License
---------
This project is © 2020 dorkbox llc, and is distributed under the terms of the Apache v2.0 License. See file "LICENSE" for further
 references.

