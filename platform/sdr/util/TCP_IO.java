package platform.sdr.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * TCP IO wrapper
 */
public class TCP_IO {

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public TCP_IO(InetAddress address, int port) throws Exception {
        this.socket = new Socket(address, port);
        this.is = this.socket.getInputStream();
        this.os = this.socket.getOutputStream();
    }

    public void send(String str) throws IOException {
        this.os.write(str.getBytes());
    }

    public String recieveNB() throws IOException {
        int av = this.is.available();
        if (av == 0) return null;
        byte[] data = new byte[av];
        this.is.read(data);
        return new String(data);
    }

    public String receiveB() throws IOException {
        while (true) {
            String str = recieveNB();
            if (str != null) return str;
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                return null;
            }
        }
    }

    public String exchangeB(String str) throws IOException {
        send(str);
        return receiveB();
    }
}
