package sdrAtlys;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import platform.sdr.Transceiver;
import platform.sdr.TransceiverRXListener;

/**
 *
 */
public class SDRAtlys implements Transceiver, Runnable {
    
    public native static boolean open();
    
    public native static void close();
    
    public native static int read(byte[] array);
    
    public native static void set(int a, int b);
    
    static {
        System.loadLibrary("SDR");
    }
            
    
    private List<TransceiverRXListener> rxs = new ArrayList<TransceiverRXListener>();
    
    long freq = 7000000000L;
    long freqNew = this.freq;

    public SDRAtlys() {
        new Thread(this).start();
    }
    
    
    @Override
    public void setFrequency(long freq) {
        this.freqNew = freq;
    }

    @Override
    public long getFrequency() {
        return this.freq;
    }

    @Override
    public int getBandwith() {
        return 80000;
    }

    @Override
    public void detach() {
    }

    @Override
    public void addRXListener(TransceiverRXListener listener) {
        this.rxs.add(listener);
    }
    

    @Override
    public void run() {
        byte[] data = new byte[8192];
        int[] I = new int[1024];
        int[] Q = new int[1024];
        
        if (!open()) {
            
            System.exit(-1);
        }
        
        while (true) {
            
            if (this.freqNew != this.freq) {
                int factor = (int) (this.freqNew / 18.6310795);
//                set(0, factor);
                System.out.println("Set FR2 " + (int)(this.freqNew / 1000));
                this.freq = this.freqNew;
            }
            
            
            read(data);
            
            for (int j=0; j<1024; j++) {
               int js = j << 3;
               
               int wi = (data[js+2]) & 0xFF | (data[js+4] << 8) & 0xFF00  | (data[js+6] << 16) & 0xFF0000;
               wi = wi << 8;
               wi = wi >> 8;
               I[j] = wi;

               int wq = (data[js+1]) &0xFF | (data[js+3] << 8) &0xFF00 | (data[js+5] << 16) & 0xFF0000;
               wq = wq << 8;
               wq = wq >> 8;
               Q[j] = wq;
               
               //if (j == 0) System.out.println(wi + "   " + wq);
            }
            
//            System.out.println(Arrays.toString(data));
//            System.out.println(Arrays.toString(I));
//            System.out.println(Arrays.toString(Q));
//            System.out.println("--");
//            System.out.println("--");
//            System.out.println("--");
            
            
            // notify
            //System.out.println("Point : " + Thread.currentThread());
            for (TransceiverRXListener listener : this.rxs) {
                listener.recieveIQ(1024, I, Q);
            }
            
        }
    }
    
    
    public static void main(String[] args) {
        
        int js = 0;
        byte[] data = {0, 0, 122, 0, -2, 0, -1, 0};
        
        int wi = (data[js+2]) & 0xFF | (data[js+4] << 8) & 0xFF00  | (data[js+6] << 16) & 0xFF0000;
         wi = wi << 8;
         wi = wi >> 8;

        System.out.println(wi);
        System.out.println(Integer.toHexString(wi));
        
        System.out.println(0xfffffe7a);
    }
    
}
