package com.jwa.pushlistener.code.architecture.communication.port.factory.impl.udp.portimpl;

import com.google.common.base.Optional;

import com.jwa.pushlistener.code.architecture.communication.Message;
import com.jwa.pushlistener.code.architecture.communication.port.AsynchronousSenderCallback;
import com.jwa.pushlistener.code.architecture.communication.port.ReceiverHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class UdpServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UdpServer.class);
    private final int NUMBER_OF_CONCURRENT_PROCESSED_DATAGRAMS = 30;
    private final int port;
    private ReceiverHandler handler;
    private AsynchronousSenderCallback callback;
    private ExecutorService executorService;
    private DatagramSocket datagramSocket;

    UdpServer(final int port, final ReceiverHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    UdpServer(final int port, final AsynchronousSenderCallback callback) {
        this.port = port;
        this.callback = callback;
    }

    final void start() {
        final Thread t = new Thread(this, "UdpServer{port=" + port + "}");
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_CONCURRENT_PROCESSED_DATAGRAMS, runnable -> new Thread(runnable, "UdpRequestWorkers"));
        t.start();
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public final void run() {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            LOGGER.error("Datagram-socket creation failed: " + e.getMessage(), e);
            return;
        }
        try {
            while (true) {
                final byte[] buffer = new byte[UdpUtils.DATAGRAM_BUFFER_SIZE];
                final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                executorService.execute(new UdpRequestWorker(datagramPacket));
            }
        } catch (SocketException ignored) {
            // gets thrown when socket is closed
        } catch (IOException e) {
            LOGGER.error("Datagram-packet receiving failed: " + e.getMessage(), e);
        }
    }

    final void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
        UdpUtils.close(datagramSocket);
    }

    final DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    final void setHandler(final ReceiverHandler handler) {
        this.handler = handler;
    }

    final void setCallback(final AsynchronousSenderCallback callback) {
        this.callback = callback;
    }

    private final class UdpRequestWorker implements Runnable {
        private final DatagramPacket datagramPacket;

        private UdpRequestWorker(final DatagramPacket datagramPacket) {
            this.datagramPacket = datagramPacket;
        }

        @Override
        public final void run() {
            try {
                final Message request = UdpUtils.deserialize(datagramPacket.getData());
                if (handler != null) {
                    final Optional<Message> response = handler.handle(request);
                    final SocketAddress recipient = datagramPacket.getSocketAddress();
                    UdpUtils.send(response.or(new UdpAckMessage()), recipient, datagramSocket);
                } else {
                    callback.process(request);
                }
            } catch (IOException e) {
                LOGGER.error("UDP-request handling failed: " + e.getMessage(), e);
            }
        }
    }
}
