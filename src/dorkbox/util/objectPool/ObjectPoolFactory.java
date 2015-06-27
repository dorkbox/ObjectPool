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


public
class ObjectPoolFactory {

    private
    ObjectPoolFactory() {
    }

    /**
     * Creates a pool of the specified size
     */
    public static
    <T> ObjectPool<T> create(PoolableObject<T> poolableObject, int size) {
        try {
            // here we use FAST (via UNSAFE)
            UnsafeObjectPool<T> fastObjectPool = new UnsafeObjectPool<T>(poolableObject, size);
            return fastObjectPool;
        } catch (Throwable ignored) {
            // fallback (LinkedBlockingDeque) in case UNSAFE isn't available. (ie: android)
            SafeObjectPool<T> slowObjectPool = new SafeObjectPool<T>(poolableObject, size);
            return slowObjectPool;
        }
    }
}
