package com.jwa.pushlistener.code.architecture.communication.port;

import com.google.common.base.Optional;

import com.jwa.pushlistener.code.architecture.communication.Message;

public interface Sender extends Port {
    /**
     *
     * @throws PortException if connection can't be established or already connected
     */
    void connect() throws PortException;

    boolean isConnected();

    /**
     *
     * @param msg must not be null
     * @return Optional<Message> if communication is synchronous, Optional.empty() if communication is asynchronous
     * @throws PortException if a connection problem occured or not connected
     */
    Optional<Message> execute(final Message msg) throws PortException;

    /**
     * Closes the connection.
     * If already disconnected: no operation.
     */
    void disconnect();
}
