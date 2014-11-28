package sdrAtlys;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import platform.sdr.Transceiver;
import platform.sdr.TransceiverRXListener;

/**
 *
 */
public class SDRAtlysNet implements Transceiver, Runnable{

    private List<TransceiverRXListener> rxs = new ArrayList<TransceiverRXListener>();
    
    long freq = 7000000000L;
    long freqNew = this.freq;

    public SDRAtlysNet() {
        new Thread(this).start();
    }


    @Override
    public void setFrequency(long freq) {
        this.freqNew = freq;
    }

    @Override
    public long getFrequency() {
        return this.freqNew;
    }

    @Override
    public int getBandwith() {
        return 250000; // 80000
    }

    @Override
    public void detach() {
    }

    @Override
    public void addRXListener(TransceiverRXListener listener) {
        this.rxs.add(listener);
    }
    
    private void sendFR(DatagramSocket socket, int pos) throws Exception {
        byte[] buffer = new byte[6];
        buffer[0] = 0x01;
        buffer[1] = 0x02;
        buffer[2] = (byte) (pos >> 24);
        buffer[3] = (byte) (pos >> 16);
        buffer[4] = (byte) (pos >> 8);
        buffer[5] = (byte) (pos);

        DatagramPacket packet = new DatagramPacket(buffer, 6, InetAddress.getByName("192.168.0.1"), 8200);
        socket.send(packet);

    }
    
    @Override
    public void run() {
        int[] I = new int[1024];
        int[] Q = new int[1024];
        int[] _I = new int[1024];
        int[] _Q = new int[1024];
        
        int pos = 0;
        try {
            DatagramSocket socket = new DatagramSocket(8200, InetAddress.getByName("192.168.0.100"));
            
            byte[] data = new byte[1296];
            
            int pck = 0;
            
            while (true) {
                
                if (this.freqNew != this.freq) {
                    sendFR(socket, (int)(this.freqNew * 0.0536870) );
                    this.freq = this.freqNew;
                }
                
                
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);
                socket.receive(receivePacket);        

               int js = 16; 
               for (int i = 0; i < 213; i++, js += 6) {

                    int wi = (data[js+3]) & 0xFF | (data[js+4] << 8) & 0xFF00  | (data[js+5] << 16) & 0xFF0000;
                    wi = wi << 8;
                    wi = wi >> 8;
                    if (pck < 1024) {
                        I[pck] = wi;
                    } else {
                        _I[pck-1024] = wi;
                    }

                    int wq = (data[js+0]) &0xFF | (data[js+1] << 8) &0xFF00 | (data[js+2] << 16) & 0xFF0000;
                    wq = wq << 8;
                    wq = wq >> 8;
                    if (pck < 1024) {
                        Q[pck] = wq;
                    } else {
                        _Q[pck-1024] = wq;
                    }
                   pck++;
               }
               
               if (pck >= 1024) {
                   
                   //System.out.println("IQ " + Integer.toHexString(I[0]) + " " +  Integer.toHexString(Q[0]));
                   
                   // send 
                    for (TransceiverRXListener listener : this.rxs) {
                        listener.recieveIQ(1024, I, Q);
                    }
                   
                   // swap
                  int[] x = I; I = _I; _I = x;
                  x = Q; Q = _Q; _Q = x;
                   
                   // --
                   pck = pck - 1024;
               }

                
            }
        } catch (Exception ex) {
            Logger.getLogger(SDRAtlysNet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public static void main2(String[] args) throws SocketException, IOException {
        
//        MulticastSocket socket = new MulticastSocket(8200);
        
        DatagramSocket socket = new DatagramSocket(8200, InetAddress.getByName("192.168.0.100"));
        socket.setBroadcast(true);
//        socket.setSoTimeout(2000);
        
        
        System.out.println("Init " + socket.getBroadcast());
        System.out.println("Init " + socket.getLocalPort());
        System.out.println("Init " + socket.getReceiveBufferSize());
        
        byte[] receiveData = new byte[1400];
        
        int pck = 0;
        
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);        
            pck++;
            if (pck % 100 == 0) System.out.println(pck);
            
           System.out.println(Arrays.toString(receiveData));
            System.out.println("--");
            
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(8200, InetAddress.getByName("192.168.0.100"));

        byte[] buffer = new byte[6];
        buffer[0] = 0x01;
        buffer[1] = 0x02;
        int pos = 0;
        while (true) {
            buffer[2] = (byte) (pos >> 24);
            buffer[3] = (byte) (pos >> 16);
            buffer[4] = (byte) (pos >> 8);
            buffer[5] = (byte) (pos);
            
            
            DatagramPacket packet = new DatagramPacket(buffer, 6, InetAddress.getByName("192.168.0.1"), 8200);
            socket.send(packet);
            Thread.sleep(50);
            
            pos++;
        }
        
    }
    
}
