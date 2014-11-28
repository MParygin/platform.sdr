package platform.sdr.util;

/**
 *
 */
public class UtilsMath {

    static double F = Math.log(24);

    public static int ln24(int value, int scale) {
        return (int) (512 - ln24m(value, scale));
    }


    public static double ln24m(int value, int scale) {
        if (value < 0) return 0;
        if (value == 0) return 512;
        //return (Math.log((16777216.0d / value)) * scale / 16.635532333438686);
        return (Math.log((16777216.0d / value)) * scale / 17.328679513998633);
    }

/*
    public static void main(String[] args) {
        System.out.println(Math.log(33554432.0d));

        System.out.println(ln24(0, 512));
        System.out.println(ln24(1, 512));
        System.out.println(ln24(2, 512));
        System.out.println(ln24(4, 512));
        System.out.println(ln24(8, 512));
        System.out.println(ln24(16, 512));
        System.out.println(ln24(32, 512));
        System.out.println(ln24(64, 512));
        System.out.println(ln24(128, 512));
        System.out.println(ln24(256, 512));
        System.out.println(ln24(512, 512));
        System.out.println(ln24(1024, 512));
        System.out.println(ln24(2048, 512));
        System.out.println(ln24(4096, 512));
        System.out.println(ln24(8192, 512));
        System.out.println(ln24(16384, 512));
        System.out.println(ln24(32768, 512));
        System.out.println(ln24(65536, 512));
        System.out.println(ln24(16777216, 512));
    }
*/

}
