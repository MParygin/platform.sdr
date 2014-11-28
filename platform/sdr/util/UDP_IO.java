package platform.sdr.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class UDP_IO implements Runnable {

    private DatagramSocket socket;
    private List<UDPListener> listeners = new ArrayList<UDPListener>();

    public UDP_IO(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        //listener
        Thread thread = new Thread(this);
        thread.start();
    }

    public void addListener(UDPListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[16384];
        DatagramPacket packet = new DatagramPacket(buffer, 16384);
        while (true) {
            try {
                this.socket.receive(packet);
                // notify
                int length = packet.getLength();
                int offset = packet.getOffset();
                for (UDPListener listener : this.listeners) listener.recieve(buffer, offset, length);
            } catch (IOException ex) {
                Logger.getLogger(UDP_IO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
