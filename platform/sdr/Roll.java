package platform.sdr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Rolled Image
 */
public class Roll {

    private int width;
    private int height;
    private int pos;
    private BufferedImage img;

    public Roll(int width, int height) {
        this.width = width;
        this.height = height;
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public int getHeight() {
        return height;
    }

    public void draw(Graphics2D g2, int x, int y, int w) {
        // draw halfs
        int ps = this.height - ((pos - 1) % this.height) - 1;
        int pq = this.height - ps - 1;

        g2.drawImage(this.img,
                x, y,                   // dest A
                x + w, y + this.height -1 - ps,      // dest B
                0, ps,
                this.width, this.height - 1,
                null);
        g2.drawImage(this.img,
                x, y + pq,                   // dest A
                x + w, y + this.height - 1,      // dest B
                0, 0,
                this.width, ps,
                null);
    }

    public void fill(int[] rgb, int offset) {
        this.img.setRGB(0, this.height - (pos++ % this.height) - 1, this.width, 1, rgb, offset, this.width);
    }

}
