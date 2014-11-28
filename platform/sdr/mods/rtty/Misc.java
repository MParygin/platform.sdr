package platform.sdr.mods.rtty;

/**
 *
 */
public class Misc {

    /* ---------------------------------------------------------------------- */

    /*
     * Hamming weight (number of bits that are ones).
     */
    public static int hweight32(int w) {
        w = (w & 0x55555555) + ((w >> 1) & 0x55555555);
        w = (w & 0x33333333) + ((w >> 2) & 0x33333333);
        w = (w & 0x0F0F0F0F) + ((w >> 4) & 0x0F0F0F0F);
        w = (w & 0x00FF00FF) + ((w >> 8) & 0x00FF00FF);
        w = (w & 0x0000FFFF) + ((w >> 16) & 0x0000FFFF);
        return w;
    }

    public static int hweight16(int w) {
        w = (w & 0x5555) + ((w >> 1) & 0x5555);
        w = (w & 0x3333) + ((w >> 2) & 0x3333);
        w = (w & 0x0F0F) + ((w >> 4) & 0x0F0F);
        w = (w & 0x00FF) + ((w >> 8) & 0x00FF);
        return w;
    }

    public static int hweight8(int w) {
        w = (w & 0x55) + ((w >> 1) & 0x55);
        w = (w & 0x33) + ((w >> 2) & 0x33);
        w = (w & 0x0F) + ((w >> 4) & 0x0F);
        return w;
    }

    /* ---------------------------------------------------------------------- */

    /*
     * Parity function. Return one if `w' has odd number of ones, zero otherwise.
     */
    public static int parity(int w) {
        return hweight32(w) & 1;
    }
}
