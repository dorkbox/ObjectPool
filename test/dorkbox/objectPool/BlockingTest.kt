/*
 * Copyright 2020 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    fun nonBlockingBoundedTest() {
        var removed = 0

        val pobj = object : BoundedPoolObject<String>() {
            override fun onRemoval(`object`: String) {
                removed++
            }

            override fun newInstance(): String {
                return ""
            }
        }

        val pool = ObjectPool.nonBlockingBounded(pobj, 2)

        val take = pool.take()
        val take1 = pool.take()
        val take2 = pool.take()
        val take3 = pool.take()
        val take4 = pool.take() // this does not block

        pool.put(take)
        pool.put(take1)
        pool.put(take2)
        pool.put(take3)
        pool.put(take4)

        Assert.assertEquals(3, removed)
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
//            val take4 = pool.take() // this suspends
//            Assert.fail("shouldn't get here")

            pool.put(take3)

            Assert.assertTrue(pool.take() === take3)
        }
    }
}
