package io.netty.util.concurrent;

import java.util.EventListener;
import java.util.concurrent.Future;

public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    void operationComplete(F f) throws Exception;
}
