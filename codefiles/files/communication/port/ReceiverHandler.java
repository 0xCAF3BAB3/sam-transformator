package com.jwa.pushlistener.code.architecture.communication.port;

import com.google.common.base.Optional;

import com.jwa.pushlistener.code.architecture.communication.Message;

public interface ReceiverHandler {
    /**
     *
     * @param msg must not be null
     * @return
     */
    Optional<Message> handle(final Message msg);
}
