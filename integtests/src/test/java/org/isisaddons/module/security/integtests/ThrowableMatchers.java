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
