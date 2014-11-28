package platform.sdr.util;

/**
 *
 */
public class Spectr {

    private int length;
    private int depth;

    private int[][] arrs;

    public Spectr(int length, int depth) {
        this.length = length;
        this.depth = depth;

        this.arrs = new int[depth][];
        for (int i = 0; i < depth; i++) this.arrs[i] = new int[length];
    }

    public void push() {
        for (int i = this.depth - 1; i >= 1; i--) {
            System.arraycopy(this.arrs[i - 1], 0, this.arrs[i], 0, this.length);
        }
    }

    public void set(int index, int value) {
        this.arrs[0][index] = value;
    }

    public int get(int index) {
        int result = 0;
        for (int i = 0; i < this.depth; i++) result += this.arrs[i][index];
        return result / this.depth;
    }
}
