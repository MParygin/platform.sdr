package platform.sdr.misc;

/**
 *
 */
public class Filter {

    private int length;
    private int decimateratio;
    private int pointer;
    private int counter;
    private double[] ifilter;
    private double[] qfilter;

    private Filter() {
    }

    /*
     * Sinc done properly.
     */
    private static double sinc(double x) {
        if (Math.abs(x) < 1e-10) {
            return 1.0;
        } else {
            return Math.sin(Math.PI * x) / (Math.PI * x);
        }
    }

    /*
     * Don't ask...
     */
    private static double cosc(double x) {
        if (Math.abs(x) < 1e-10) {
            return 0.0;
        } else {
            return (1.0 - Math.cos(Math.PI * x)) / (Math.PI * x);
        }
    }

    /*
     * Hamming window function.
     */
    private static double hamming(double x) {
        return 0.54 - 0.46 * Math.cos(2 * Math.PI * x);
    }

    /*
     * Create a band pass FIR filter with 6 dB corner frequencies
     * of 'f1' and 'f2'. (0 <= f1 < f2 <= 0.5)
     */
    private static double[] mk_filter(int len, boolean hilbert, double f1, double f2) {
        double[] fir;
        double t, h, x;
        int i;

        fir = new double[len];

        for (i = 0; i < len; i++) {
            t = i - (len - 1.0) / 2.0;
            h = i * (1.0 / (len - 1.0));

            if (!hilbert) {
                x = (2 * f2 * sinc(2 * f2 * t)
                        - 2 * f1 * sinc(2 * f1 * t)) * hamming(h);
            } else {
                x = (2 * f2 * cosc(2 * f2 * t)
                        - 2 * f1 * cosc(2 * f1 * t)) * hamming(h);
                /*
                 * The actual filter code assumes the impulse response
                 * is in time reversed order. This will be anti-
                 * symmetric so the minus sign handles that for us.
                 */
                x = -x;
            }

            fir[i] = x;
        }

        return fir;
    }

    public static Filter filter_init(int len, int dec, double[] itaps, double[] qtaps) {
        Filter f = new Filter();
        f.length = len;
        f.decimateratio = dec;
        if (itaps != null) {
            f.ifilter = new double[len];
            System.arraycopy(itaps, 0, f.ifilter, 0, len);
        }
        if (qtaps != null) {
            f.qfilter = new double[len];
            System.arraycopy(qtaps, 0, f.qfilter, 0, len);
        }
        f.pointer = len;
        f.counter = 0;
        return f;
    }

    public Filter filter_init_hilbert(int len, int dec) {
        return filter_init(len, dec, mk_filter(len, false, 0.05, 0.45), mk_filter(len, true, 0.05, 0.45));
    }
}
