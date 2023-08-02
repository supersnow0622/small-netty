package io.netty.channel;

import java.util.function.IntSupplier;

public class DefaultSelectStrategy implements SelectStrategy {

    public static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

    private DefaultSelectStrategy() {

    }

    @Override
    public int calculateStrategy(IntSupplier supplier, boolean hasTasks) {
        return hasTasks ? supplier.getAsInt() : SelectStrategy.SELECT;
    }
}
