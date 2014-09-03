/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.security.integtests;

import java.util.List;
import com.google.common.base.Throwables;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ThrowableMatchers {

    ThrowableMatchers(){}

    public static <T extends Throwable> Matcher<T> causalChainContains(final Class<?> cls) {
        return new TypeSafeMatcher<T>(){
            @Override
            protected boolean matchesSafely(T item) {
                final List<Throwable> causalChain = Throwables.getCausalChain(item);
                for (Throwable t : causalChain) {
                    if(cls.isAssignableFrom(t.getClass())) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("causal chain contains " + cls.getName());
            }
        };
    }

}
