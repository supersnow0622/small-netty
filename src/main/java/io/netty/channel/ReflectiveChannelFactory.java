package io.netty.channel;

import java.lang.reflect.Constructor;

public class ReflectiveChannelFactory<T extends Channel> {

    private Constructor<T> constructor;

    public ReflectiveChannelFactory(Class<T> clazz) {
        try {
            this.constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public T newChannel() {
        try {
            return constructor.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create Channel from class " + constructor.getDeclaringClass());
        }
    }
}
