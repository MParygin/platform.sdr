package sdrN2;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import platform.sdr.Transceiver;
import platform.sdr.TransceiverRXListener;
import platform.sdr.util.TCP_IO;
import platform.sdr.util.UDPListener;
import platform.sdr.util.UDP_IO;

/**
 *
 */
public class SDRN2Old implements Transceiver, UDPListener {

    long freq = 7000000000L;

    private TCP_IO tcp;
    private UDP_IO datagramm;
    private int bandwith;

    private long packetSeq = -1L;
    private byte[] packetBuffer = null;

    private List<TransceiverRXListener> rxs = new ArrayList<TransceiverRXListener>();

    public SDRN2Old(String ip) {
        try {
            this.tcp = new TCP_IO(InetAddress.getByName(ip), 11000);

            // connect
            String resp = this.tcp.exchangeB("attach 0");
            if (parseResponse(resp)) {
                // get bandwith
                this.bandwith = Integer.parseInt(parseResponseValue(resp));

                // listener
                int udp = 9000;
                this.datagramm = new UDP_IO(udp);
                this.datagramm.addListener(this);

                // start
                String res = this.tcp.exchangeB("start iq " + udp);

            } else {
                System.err.println("Unable connect to server");
            }
        } catch (Exception ex) {
            Logger.getLogger(SDRN2Old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean parseResponse(String resp) {
        if (resp == null) return false;
        return resp.startsWith("OK ");
    }

    private String parseResponseValue(String resp) {
        if (resp == null) return null;
        if (resp.startsWith("OK ")) {
            return resp.substring(3).trim();
        } else {
            return null;
        }
    }

    @Override
    public void setFrequency(long freq) {
        System.out.println("Freq: " + freq);

        this.freq = freq;
        try {
            this.tcp.exchangeB("frequency " + (this.freq / 1000L));
        } catch (IOException ex) {
            Logger.getLogger(SDRN2Old.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long getFrequency() {
        return this.freq;
    }

    @Override
    public void detach() {
        //todo
    }

    @Override
    public void addRXListener(TransceiverRXListener listener) {
        this.rxs.add(listener);
    }


    int[] I = new int[1024];
    int[] Q = new int[1024];

    @Override
    public void recieve(byte[] buffer, int offset, int length) {
        //System.out.println("RP : " + length);


//        System.out.println(Arrays.toString(buffer));


        // accept new UDP packet (512)
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        // parse
        long seq = bb.getLong();
        if (seq != this.packetSeq) {
            // new seq
            this.packetBuffer = new byte[8192];
            this.packetSeq = seq;
        }
        //
        int off = bb.getShort();
        int len = bb.getShort();

        //System.out.println("seq: " + seq + " " + off + " " + len);

        // copy
        System.arraycopy(buffer, 12, this.packetBuffer, off, len);

        // make packet
        if ((len + off) == 8192) {
            //System.out.println("New packet");

//            System.out.println(Arrays.toString(this.packetBuffer));

            // reorder
            ByteBuffer bc = ByteBuffer.wrap(this.packetBuffer);
            bc.order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < 1024; i++) {
                float f = bc.getFloat() * 8388607.0f;
//                if (i == 10) System.err.println("F: " + f);
                I[i] = (int)(f);// + 32768;
            }
            for (int i = 0; i < 1024; i++) {
                float f = bc.getFloat() * 8388607.0f;
                Q[i] = (int)(f);// + 32768;
            }

            // notify
            //System.out.println("Point : " + Thread.currentThread());
            for (TransceiverRXListener listener : this.rxs) {
                listener.recieveIQ(1024, I, Q);
            }

        }

    }

    @Override
    public int getBandwith() {
        return this.bandwith;
    }

}
