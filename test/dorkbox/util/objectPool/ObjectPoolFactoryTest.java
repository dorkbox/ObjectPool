package dorkbox.util.objectPool;

import org.junit.Test;

/**
 *
 */
public
class ObjectPoolFactoryTest {

    public static
    void main(String[] args) throws Exception {
        new ObjectPoolFactoryTest().testCreate();
    }

    @Test
    public
    void testCreate() throws Exception {

        final ObjectPool<Integer> pool = ObjectPoolFactory.create(new PoolableObject<Integer>() {
            int id = 1;

            @Override
            public
            Integer create() {
                return this.id++;
            }
        }, 4);

        Integer one = pool.take();
        Integer two = pool.take();
        Integer three = pool.take();
        Integer four = pool.take();

        pool.release(one);
        pool.release(two);
        pool.release(three);
        pool.release(four);

        one = pool.take();
        two = pool.take();
        pool.release(one);
        pool.release(two);

        three = pool.take();
        four = pool.take();
        one = pool.take();
        two = pool.take();
    }
}
