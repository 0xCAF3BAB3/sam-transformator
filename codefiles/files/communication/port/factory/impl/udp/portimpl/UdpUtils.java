package {{communicationPackageName}}.port.factory.impl.udp.portimpl;

import {{communicationPackageName}}.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

final class UdpUtils {
    private static final int MAX_SIZE_DATAGRAM_PAYLOAD = 65507; // source: http://openbook.rheinwerk-verlag.de/java7/1507_11_011.html#dodtp497f87ed-dd23-48d4-80c7-7e11b3ec99d6
    static final int DATAGRAM_BUFFER_SIZE = 1024; // 1 KiB

    private static byte[] serialize(final Message msg) throws IOException {
        final ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        final ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(msg);
        oo.close();
        return bStream.toByteArray();
    }

    static Message deserialize(final byte[] data) throws IOException {
        try {
            final ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
            final Message msg = (Message) iStream.readObject();
            iStream.close();
            return msg;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    static void close(final DatagramSocket datagramSocket) {
        if (datagramSocket != null && !datagramSocket.isClosed()) {
            datagramSocket.close();
        }
    }

    static void send(final Message msg, final SocketAddress recipient, final DatagramSocket datagramSocket) throws IOException {
        final byte[] byteMessage = serialize(msg);
        if (byteMessage.length > MAX_SIZE_DATAGRAM_PAYLOAD) {
            throw new IOException("Message too large, exceeds " + MAX_SIZE_DATAGRAM_PAYLOAD + " bytes");
        }
        final DatagramPacket datagramPacket = new DatagramPacket(byteMessage, byteMessage.length, recipient);
        datagramSocket.send(datagramPacket);
    }
}
