package nl._42.beanie.compatibility;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class Methods {

    public static MethodHandle getMethodHandle(final Method method) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();

        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup());
        return lookup.unreflectSpecial(method, declaringClass);
    }

}
