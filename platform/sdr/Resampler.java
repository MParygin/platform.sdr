package platform.sdr;

import java.util.Arrays;

/**
 * Ресэмплер
 */
public class Resampler {

    private int from;
    private int to;
    private int nlength;

    private double[] h = null;
    int posw;
    int posr;

    public Resampler(int from, int to) {
        this.from = from;
        this.to = to;
        //if (from != to)
            h = new double[100000000];
        this.nlength = 1024 * to / from;
        System.out.println(this.nlength);
    }

    public void add(double[] samples) {
        // add resampled
        for (int i = 0; i < nlength; i++) {
            double pos = i * 1023.0 / (double)nlength;
            double ipos = Math.floor(pos);
            double left = pos - ipos;
            double right = 1 - left;
            int iipos = (int) ipos;
            double d = samples[iipos + 1]  - samples[iipos];
            double v = samples[iipos] + d * left;
            this.h[posw++] = v;
            //System.out.println(pos + " " + ipos + " " + left + " " + right);
        }
    }

    public double[] get(int length) {
        if ((this.posw - this.posr) >= 1024) {
            double[] result = new double[1024];
            System.arraycopy(h, posr, result, 0, length);
            this.posr += length;
            return result;
        }
        return null;
    }


    public static void main(String[] args) {
        Resampler resampler = new Resampler(47850, 48005);
        //Resampler resampler = new Resampler(48000, 48000);

        double[] s = new double[1024];
        for (int i = 0; i < 1024; i++) s[i] = i;

        System.out.println(Arrays.toString(s));

        resampler.add(s);

        s = resampler.get(1024);

        System.out.println(Arrays.toString(s));
    }

}
