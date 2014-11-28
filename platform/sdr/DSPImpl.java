package platform.sdr;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import platform.sdr.mods.Demodulator;
import platform.sdr.mods.am.AMDem;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class DSPImpl implements TransceiverRXListener, DSP, Runnable {

    private int left;
    private int rigth;

    private SourceDataLine line;

    private Demodulator demodulator;
    private boolean filter = true;

    //public CWDemod cw = new CWDemod();

    public DSPImpl() {
        try {
            this.line = AudioSystem.getSourceDataLine(new AudioFormat(48000, 16, 2, true, false));
        } catch (LineUnavailableException ex) {
            Logger.getLogger(DSPImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.line.open();
//           / this.line.start();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(DSPImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(this.line.getFormat());

        Thread thread = new Thread(this, "DSP");
        thread.start();
    }


    @Override
    public int getLeft() {
        return this.left;
    }

    @Override
    public int getRight() {
        return this.rigth;
    }

    @Override
    public void setLeft(int freq) {
        this.left = freq;
    }

    @Override
    public void setRight(int freq) {
        this.rigth = freq;
    }

    @Override
    public Demodulator getDemodulator() {
        return this.demodulator;
    }

    @Override
    public void setDemodulator(Demodulator demodulator) {
        this.demodulator = demodulator;
    }

    public void toggle() {
        this.filter = !this.filter;
    }

    //Queue<byte[]> queue = new ArrayBlockingQueue<byte[]> (100);
    //Resampler resampler = new Resampler(47860, 48002);
    Resampler resampler = new Resampler(250000, 48000);


    double[] raw = new double[2048];
    DoubleFFT_1D fft = new DoubleFFT_1D(1024);
    AMDem am = new AMDem();

    int[] raw2 = new int[1024];
    byte[] b = new byte[1024 * 2 * 2];

    @Override
    public void recieveIQ(int length, int[] I, int[] Q) {
        int ps = 0;
        for (int i = 0; i < length; i++) {
            raw[ps++] = I[i];
            raw[ps++] = Q[i];
        }

        if (this.filter) {

            // fft forward
            fft.complexForward(raw);

            // apply filter
            ps = 0;
            int _left = -this.left * 1024 / 250000; //48000;
            int _right = this.rigth * 1024 / 250000;//48000;
            for (int i = 0; i < length; i++) {
                if ((i > _left) && (i < (1023 - _right))) {
                    raw[ps++] = 0;
                    raw[ps++] = 0;
                } else {
                    ps += 2;
                }
            }

            // inverse fft
            fft.complexInverse(raw, true);
        }

        // demodulator phase
        Demodulator d = this.demodulator;
        if (d != null) {
            d.process(raw);
        }

     //   this.cw.demodulate(raw);

          ps = 0;



//        ByteBuffer bb = ByteBuffer.wrap(b);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        for (int i = 0; i < length; i ++) {
//            double Iv = raw[ps++];
//            ps++;
//            //raw2[i] = (int) (Iv * 256);
//            bb.putShort((short)(Iv * 256));
//            bb.putShort((short)(Iv * 256));
//        }
//        queue.add(b);

          double[] db = new double[1024];
          for (int i = 0; i < 1024; i++) db[i] = raw[i<<1];
          resampler.add(db);

//        Main.morse.recieveIQ(1024, raw2, raw2);

        //System.out.println("----- " + this.line.available() + "       " + this.line.getBufferSize() + " " + this.line.isOpen());
    }

    long _a = 0;
    int pckts = 0;
    int physic;

    @Override
    public void run() {
        this.line.start();
        while (true) {
//            byte[] b = queue.poll();
//            if (b != null) {
            double[] db = resampler.get(1024);
            if (db != null) {

                ByteBuffer bb = ByteBuffer.wrap(b);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                for (int i = 0; i < 1024; i ++) {
                    double Iv = db[i];
                    //raw2[i] = (int) (Iv * 256);
                    bb.putShort((short)(Iv * 256));
                    bb.putShort((short)(Iv * 256));
                }

                //System.err.println(line.getBufferSize( ) + "  " + line.available());
                int wr = this.line.write(b, 0, b.length);
                if (wr != 4096) System.err.println("WR: " + wr);
                if (_a == 0) {
                    _a = System.currentTimeMillis();
                } else {
                    pckts++;
                    if ((pckts % 100) == 0) {
                        long d = System.currentTimeMillis() - _a;
                        this.physic = (int) ((pckts * 1024.0) / (d / 1000.0));
                    }
                }

            } else {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DSPImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public int getPhysicRate() {
        return physic;
    }

    public static void main(String[] args) {

       // DSPImpl impl = new DSPImpl();
try {

        SourceDataLine line = AudioSystem.getSourceDataLine(new AudioFormat(48000, 16, 2, true, false));
        byte[] b = new byte[1024 * 2 * 2];
        for (int i = 0; i< b.length; i++) b[i] = (byte) i;
        int pckts = 0;

        Thread.sleep(150);
        line.open();
        line.start();

        long _a = 0;
        while (true) {
            Thread.sleep(15);
            int av = line.available();
            if (av >= 1024) {
//                System.out.println(av);

                int wr = line.write(b, 0, b.length);

                if (_a == 0) {
                    _a = System.currentTimeMillis();
                    pckts = 0;
                } else {
                    pckts++;
                    if ((pckts % 1000) == 0) {
                        long d = System.currentTimeMillis() - _a;
                        System.out.println(((pckts * 1024.0) / (d / 1000.0)));
                    }
                }
            }
        }


} catch (Exception e) {
    e.printStackTrace();

}
    }



}
