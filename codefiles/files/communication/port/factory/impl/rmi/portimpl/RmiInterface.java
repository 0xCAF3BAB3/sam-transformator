package com.jwa.pushlistener.code.architecture.communication.port.factory.impl.rmi.portimpl;

import com.google.common.base.Optional;

import com.jwa.pushlistener.code.architecture.communication.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiInterface extends Remote {
    Optional<Message> execute(final Message msg) throws RemoteException;
}
