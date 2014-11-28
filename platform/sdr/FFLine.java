package platform.sdr;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Fast Fourie Line
 */
public class FFLine {

    private int seq = 0;
    //private int size;
    private int blocks;
    private DoubleFFT_1D fft = null;
    private double[] raw = null; // Raw persist twice numbers (Re & Im)

    public FFLine(int size) {
        //this.size = size;
        this.blocks = (size / 1024) - 1; //todo
        this.fft = new DoubleFFT_1D(size);
        this.raw = new double[size << 1]; // Raw persist twice numbers (Re & Im)
    }

    public double[] recieveIQ(int length, int[] I, int[] Q) {
        double[] result = null;

        int ps = this.seq * 2048;

        //System.out.println("FF " + seq + " " + ps);

        for (int i = 0; i < 1024; i++) {
            raw[ps++] = I[i];
            raw[ps++] = Q[i];
        }

        if (this.seq != this.blocks) {
            // begin packet
        } else {
            // end packet
            // fft (raw -> raw)
            this.fft.complexForward(this.raw);
            // retu
            result = this.raw;
        }

        // next
        this.seq++;
        this.seq = this.blocks & this.seq;

        return result;
    }

}
