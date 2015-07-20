/*
 * Copyright 2014 dorkbox, llc
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
package dorkbox.util.objectPool;

@SuppressWarnings("ALL")
public
interface ObjectPool<T> {
    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     */
    T take() throws InterruptedException;

    /**
     * Takes an object from the pool, Blocks until an item is available in the pool.
     * <p/>
     * This method catches an
     * {@link InterruptedException} and discards it silently.
     */
    T takeUninterruptibly();

    /**
     * Return object to the pool, waking those that have blocked during take()
     */
    void release(T object);

    /**
     * @return a new object instance created by the pool.
     */
    T newInstance();
}
