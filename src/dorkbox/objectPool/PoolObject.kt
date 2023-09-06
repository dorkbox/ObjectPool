/*
 * Copyright 2023 dorkbox, llc
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

abstract class PoolObject<T: Any> {
    /**
     * Called when an object is returned to the pool, useful for resetting an objects state, for example.
     */
    open fun onReturn(`object`: T) {}

    /**
     * Called when an object is taken from the pool, useful for setting an objects state, for example.
     */
    open fun onTake(`object`: T) {}

    /**
     * Called when a new instance is created
     */
    abstract fun newInstance(): T
}
