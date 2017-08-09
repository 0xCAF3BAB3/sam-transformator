package com.jwa.pushlistener.code.architecture.communication.port;

public interface AsynchronousSender extends Sender {
    /**
     * If no callback is set, a default-callback gets set (which only logs the call).
     * @param callback must not be null
     */
    void setCallback(final AsynchronousSenderCallback callback);
}
