package io.netty.channel;

import java.util.function.IntSupplier;

public interface SelectStrategy {

    int SELECT = -1;

    int CONTINUE = -2;

    int BUSY_WAIT = -3;

    int calculateStrategy(IntSupplier supplier, boolean hasTasks);
}
