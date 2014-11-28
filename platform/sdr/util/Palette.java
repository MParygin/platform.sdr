package platform.sdr.util;

/**
 *
 */
public class Palette {

    public static int[] create() {
        int[] result = new int[1024];
        int pos = 0;
        for (int i = 0; i < 256; i++) {
            result[pos++] = i;
        }
        for (int i = 0; i < 256; i++) {
            result[pos++] = (i << 8) | (254-i);
        }
        for (int i = 0; i < 256; i++) {
            result[pos++] = (i << 16) | 0xFF00;
        }
        for (int i = 0; i < 256; i++) {
            result[pos++] = ((254-i) << 8) | 0xFF0000;
        }
        return result;
    }

    public static int[] createStatic() {
        int[] result = new int[1024];
        int pos = 0;
        for (int i = 0; i < 256; i++) {
            result[pos++] = (i << 8);
        }
        for (int i = 0; i < 256; i++) {
            result[pos++] = (i << 16) | 0xFF00;
        }
        for (int i = 0; i < 256; i++) {
            result[pos++] = ((254-i) << 8) | 0xFF0000;
        }
//        for (int i = 0; i < 256; i++) {
//            result[pos++] = 0xFF00 | (254-i);
//        }
        for (int i = 0; i < 256; i++) {
            result[pos++] = (i << 16) | (i << 8) | 0xFF0000;
        }
        return result;
    }

}
