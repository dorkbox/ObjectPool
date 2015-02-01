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
package dorkbox.objectPool;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;

public class GetUnsafe implements PrivilegedExceptionAction<sun.misc.Unsafe> {
    GetUnsafe() {
    }

    @Override
    public sun.misc.Unsafe run() throws Exception {
        Class<sun.misc.Unsafe> unsafeClass = sun.misc.Unsafe.class;
        Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);

        Object unsafeObject = theUnsafe.get(null);
        if (unsafeClass.isInstance(unsafeObject)) {
            return unsafeClass.cast(unsafeObject);
        }

        throw new NoSuchFieldError("the Unsafe");
    }
}
