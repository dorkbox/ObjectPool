package dorkbox.objectPool

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class BlockingTest {
    @Test
    fun blockingTest() {
        val pobj = object : PoolObject<String>() {
            override fun newInstance(): String {
               return ""
            }
        }

        val pool = ObjectPool.blocking(pobj, 4)

        val take = pool.take()
        val take1 = pool.take()
        val take2 = pool.take()
        val take3 = pool.take()
        val take4 = pool.take() // this blocks
        Assert.fail("shouldn't get here")
    }

    @Test
    fun nonblockingTest() {
        val pobj = object : PoolObject<String>() {
            override fun newInstance(): String {
               return ""
            }
        }

        val pool = ObjectPool.nonBlocking(pobj)

        val take = pool.take()
        val take1 = pool.take()
        val take2 = pool.take()
        val take3 = pool.take()
        val take4 = pool.take() // this does not block
    }

    @Test
    fun suspendTest() {
        val pobj = object : SuspendingPoolObject<String>() {
            override suspend fun newInstance(): String {
               return ""
            }
        }

        val pool = ObjectPool.suspending(pobj, 4)

        runBlocking {
            val take = pool.take()
            val take1 = pool.take()
            val take2 = pool.take()
            val take3 = pool.take()
            val take4 = pool.take() // this suspends
            Assert.fail("shouldn't get here")
        }
    }
}
