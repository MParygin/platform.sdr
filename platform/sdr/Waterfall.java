package platform.sdr;

import platform.sdr.mods.Demodulator;
import platform.sdr.mods.DemodulatorFactory;
import platform.sdr.mods.am.SAMDem;
import platform.sdr.util.FrequencyInfo;
import platform.sdr.util.Palette;
import platform.sdr.util.Spectr;
import platform.sdr.util.UtilsMath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Formatter;

/**
 *
 */
public class Waterfall extends JPanel implements TransceiverRXListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private JFrame frame;
    private Transceiver transceiver;
    private Roll rlMain;
    private Roll rlLens;

    private BufferedImage pi;

    private static final int HPAD = 20;
    private static final int VPAD = 20;
    private static final int BLOCK = 4096;
    private static final int LENS = BLOCK >> 3;

    // Colors
    private static Color CLR_GRID = new Color(0x302000);
    private static Color CLR_GRID_FREQ = new Color(0x90A000);
    private static Color CLR_CENTER = new Color(0x400000);

    // fonts
    private static Font FNT_GRID = new Font("Lucida Sans", Font.PLAIN, 14);
    private static Font FNT_FREQ = new Font("Lucida Sans", Font.PLAIN, 32);

    private Spectr spectr = new Spectr(BLOCK, 2);
    private FFLine line;

    int pos;
    int posLens;
    int posView;

    private long freq;
    private int pressX;

    static int[] palette = Palette.createStatic();
    static int[] paletteStatic = Palette.createStatic();
    private final DSP dsp;

    private Dumper dumper;

    public Waterfall(Transceiver transceiver, DSP dsp) {
        this.transceiver = transceiver;
        this.dsp = dsp;
        this.dumper = new Dumper();

        //
        setFocusable(true);
        requestFocus();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        this.line = new FFLine(BLOCK);

        // Buffer & Lens
        checkMainRoll();
        this.rlLens = new Roll(LENS, 80);

        // Palette
        int ips = 512;
        int fps = palette.length;

        this.pi = new BufferedImage(1, ips, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < ips; i++) {
            this.pi.setRGB(0, 511 - i, paletteStatic[i * fps / 512]);
        }
    }

    private void checkMainRoll() {
        int h = getHeight() - 512;
        if ((this.rlMain == null || this.rlMain.getHeight() != h) && (h > 0)) {
            this.rlMain = new Roll(BLOCK, h);
        }
    }

    @Deprecated
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Отрезок на экране в частоту
     *
     * @param DX Отрезок на экране
     */
    private int screen2freq(int DX) {
        return DX * this.transceiver.getBandwith() / getWidth();
    }

    private int freq2screen(int DF) {
        return DF * getWidth() / this.transceiver.getBandwith();
    }

    private void drawStringRight(Graphics2D g2, String str, int x, int y) {
        double w = g2.getFontMetrics().getStringBounds(str, g2).getWidth();
        g2.drawString(str, (int)(x - w), y);
    }

    private void drawStrings(Graphics2D g2, String[] strs, int x, int y, int w, int h) {
        // calc widths
        g2.setColor(new Color(80, 80, 80, 80));
        g2.fillRect(x, y, w, h);
        g2.setColor(Color.green);

        for (int i = 0, j = y + 10; i < strs.length; i++) {
            Rectangle2D rect = g2.getFontMetrics().getStringBounds(strs[i], g2);
            j += rect.getHeight() + 2;;
            g2.drawString(strs[i], x + 10, j);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        checkMainRoll();

        // hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // local
        int lw = getWidth();
        int lw2 = lw >> 1;
        int bn = this.transceiver.getBandwith();
        long tf = this.transceiver.getFrequency();
        int WFY = 512;

        // fill
        g2.setColor(Color.black);
        g2.fillRect(0, 0, lw, WFY);

        // fill dsp
        g2.setColor(Color.DARK_GRAY);
        {
            int xleft = freq2screen(this.dsp.getLeft()) + lw2;
            int xright = freq2screen(this.dsp.getRight()) + lw2;
            g2.fillRect(xleft, 0, xright - xleft, WFY);
        }

        // grid
        g2.setFont(FNT_GRID);
        long f = (tf/ 1000L) - (bn / 2);
        f = (f / 10000L) * 10000L;
        int ss = bn / 10000 + 2;
        for (int i = 0; i < ss; i++, f = f + 10000L) {
            long ff = f - (tf/ 1000L);
            int x = freq2screen((int)ff) + lw2;
            if (x >= 0 && x < lw) {
                g2.setColor(CLR_GRID);
                g.drawLine(x, 0, x, 512);
                g2.setColor(CLR_GRID_FREQ);
                String str = Double.toString(f / 1000000.0d);
                g.drawString(str, x + 3, 11);
            }
        }

        // grig hor (dB)
        for (int i = 32; i <= 512; i += 32) {
            g2.setColor(CLR_GRID);
            g2.drawLine(0, i, lw, i);
            g2.setColor(CLR_GRID_FREQ);
            String str = Integer.toString(-i / 32 * 9) + " dB";
            g2.drawString(str, 7, i - 3);
        }

        // center
        g2.setColor(CLR_CENTER);
        g2.drawLine(lw2, 0, lw2, 512);

        // draw spectr
        for (int i = 0; i < lw; i++) {
            // interpolation
            int ic0 = i * BLOCK / lw;
            int ic1 = (i + 1) * BLOCK / lw;
            int v = 0;
            for (int j = ic0; j < ic1; j++) {
                v += spectr.get(j);
            }
            v /= (ic1 - ic0);

            //System.out.format("F: %d %d %d\n", ic0, ic1, v);

            g.drawImage(this.pi,
                    i, 512,
                    i + 1, 512 - v,
                    0, 511,
                    1, 511 - v,
                    null);
//            g.drawLine(i, WFY - 1, i, WFY - () - 1);

        }
        g2.drawImage(this.pi, 0, 0, null);
        g2.drawImage(this.pi, 1, 0, null);
        g2.drawImage(this.pi, 2, 0, null);
        g2.drawImage(this.pi, 3, 0, null);

        // draw freq
        g2.setColor(Color.green);
        g2.setFont(FNT_FREQ);

        Formatter formatter = new Formatter();
        formatter.format("%4.6f MHz", tf / 1000000000.0);

        g2.drawString(formatter.toString(), 55, 60);

        // cw
//        g2.drawString(Double.toString(Main.morse.unit_elem), 55, 88);
//        g2.drawString(Integer.toString(Main.morse.mark_cnt), 55, 118);
//        g2.drawString(Integer.toString(Main.morse.space_cnt), 55, 148);
        //g2.drawString(Double.toString(((DSPImpl)dsp).cw.max), 55, 118);
        //g2.drawString(Double.toString(((DSPImpl)dsp).cw.lmg), 55, 148);



        g2.setColor(Color.black);
        g2.setFont(FNT_GRID);
//        g2.drawString(CWDemod.replc(((DSPImpl)dsp).cw.chars.toString()), 0, 510);

        // rolls
        this.rlMain.draw(g2, 0, WFY, lw);
        this.rlLens.draw(g2, lw - LENS - HPAD, VPAD, LENS);


        if (this.dsp.getDemodulator() != null) {
            Demodulator demodulator = this.dsp.getDemodulator();
            if (demodulator instanceof SAMDem) {
                SAMDem sam = (SAMDem) demodulator;
                g2.setColor(Color.yellow);
                long ff = (long) sam.getFrequency();
                int x = freq2screen((int)-ff) + (lw / 2);
                g2.drawLine(x, 0, x, 512);
            }

            if (demodulator instanceof SynchroDetector) {
                SynchroDetector det = (SynchroDetector) demodulator;
                g2.setColor(Color.yellow);
                double[] amp = det.amp;
                for (int i = 0; i < amp.length; i++) {
                    int x = lw / 2 - i;
                    g2.drawLine(x, 512, x, 512 - (int)(amp[i] * 100));
                }
            }
        }


        String[] strs = {
            "Диапазон: 40M",
            "Полоса: "+transceiver.getBandwith()+"Гц",
            "Частота: 0Гц",
            "Детектор: нет",
            "Дамп: нет",
        };

        // draw band
        FrequencyInfo info = FrequencyInfo.get(tf / 1000L);
        if (info != null) {
            strs[0] = "Диапазон: " + info.getLabel();
//            drawStringRight(g2, info.getLabel(), lw - 20, 60);
        }
        strs[1] = "Полоса: " + bn + "Гц";
        strs[2] = "Частота: " + ((DSPImpl)this.dsp).getPhysicRate() + "Гц";
        {
            StringBuilder str = new StringBuilder("Демодулятор: ");
            Demodulator d = this.dsp.getDemodulator();
            if (d == null) {
                str.append("нет");
            } else {
                str.append(d.name());
            }
//            drawStringRight(g2, str.toString(), lw - 20, 118);
            strs[3] = str.toString();
        }
        {
            String d = this.dumper.getFileName();
            if (d == null) {
                strs[4] = "Дамп: нет";
            } else {
                strs[4] = "Дамп: " + d;
            }
        }


        g2.setFont(FNT_GRID);
        drawStrings(g2, strs, HPAD * 3, VPAD * 4, 240, 130);


//        for (int i = 0; i < 1024; i++) {
//            g2.drawLine(i, 512, i, 512 + ((DSPImpl)dsp).cw.hist[i]);
//        }

//        g2.setColor(Color.yellow);
//        g2.drawLine(0, 511, ((DSPImpl)dsp).cw.BASE, 511);



        this.posView++;
    }


    int [] r = new int[BLOCK];

    @Override
    public void recieveIQ(int length, int[] I, int[] Q) {
        //System.out.println("Packet: "+length);
        double[] rw = this.line.recieveIQ(length, I, Q);
        if (rw != null) {

            // copy spectrs
            this.spectr.push();

            int mask = (BLOCK >> 1) - 1;
            // dirthy calcs
            for (int i = 0; i < BLOCK; i++) {
                double re = rw[i << 1];
                double im = rw[(i << 1) + 1];
                int p = (int) Math.sqrt(re * re + im * im);
                //int p = (int) Math.abs(re + im);
                int lp = UtilsMath.ln24(p, 512);
                this.spectr.set(i ^ mask, lp);
                //if (p > 1023) p = 1023;
                r[i ^ mask] = palette[lp];
            }

            // buffer
            if (this.rlMain != null) this.rlMain.fill(r, 0);
            // lens
            this.rlLens.fill(r, (BLOCK - LENS) >> 1);
            //this.li.setRGB(0, LH - (posLens++ % LH) - 1, LENS, 1, r, (BLOCK + LENS) >> 1, LENS);


           // async repaint
            repaint(1);
        }

        // dumper
        this.dumper.recieveIQ(length, I, Q);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Dragged " + e);
        int x = e.getX();
        long corr = screen2freq(this.pressX - x);
        long f = this.freq + (corr * 1000);
        if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
            f = (f / 25000) * 25000;
        }
        if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
            f = (f / 250000) * 250000;
        }
        this.transceiver.setFrequency(f);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("Moved " + e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.freq = this.transceiver.getFrequency();
        this.pressX = e.getX();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int bw = this.transceiver.getBandwith();
        this.transceiver.setFrequency(this.transceiver.getFrequency() + ((e.getWheelRotation() == -1) ? (bw * 1000L) : (-bw * 1000L)));
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e);
        // switch of keys
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_F12) {
            toggleFullScreen();
            return;
        }
        if (code == KeyEvent.VK_F7) {
            prevBand();
            return;
        }
        if (code == KeyEvent.VK_F8) {
            nextBand();
            return;
        }
        // none
        if (code == KeyEvent.VK_F1) {
            this.dsp.setDemodulator(null);
            return;
        }
        // am
        if (code == KeyEvent.VK_F2) {
            this.dsp.setDemodulator(DemodulatorFactory.get("AM"));
            return;
        }
        // am
        if (code == KeyEvent.VK_F3) {
            this.dsp.setDemodulator(DemodulatorFactory.get("SAM"));
            return;
        }
        if (code == KeyEvent.VK_L) {
            this.dsp.setLeft(-3000);
            this.dsp.setRight(0);
            return;
        }
        if (code == KeyEvent.VK_U) {
            this.dsp.setLeft(0);
            this.dsp.setRight(3000);
            return;
        }
        if (code == KeyEvent.VK_A) {
            this.dsp.setLeft(-3000);
            this.dsp.setRight(3000);
            //this.dsp.setDemodulator(DemodulatorFactory.get("SAM"));
            //this.dsp.setDemodulator(DemodulatorFactory.get("PhaseDetector"));
            return;
        }
        if (code == KeyEvent.VK_Q) {
            this.dsp.setLeft(-4900);
            this.dsp.setRight(4900);
            //this.dsp.setDemodulator(DemodulatorFactory.get("SAM"));
            //this.dsp.setDemodulator(DemodulatorFactory.get("PhaseDetector"));
            return;
        }
        if (code == KeyEvent.VK_W) {
            this.dsp.setLeft(0);
            this.dsp.setRight(500);
            return;
        }
        if (code == KeyEvent.VK_D) {
            this.dumper.toggle();
            return;
        }
        if (code == KeyEvent.VK_F) {
            ((DSPImpl)this.dsp).toggle();
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private boolean fs = false;

    private void toggleFullScreen() {
        System.out.println("Toggle FS");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        try {
            frame.setVisible(fs);
            gd.setFullScreenWindow((fs) ? null : frame);
            //frame.setVisible(true);
        } catch (Exception e) {
        }
        fs = !fs;
    }

    private void prevBand() {
        FrequencyInfo info = FrequencyInfo.get(this.transceiver.getFrequency() / 1000L);
        if (info != null) {
            info = FrequencyInfo.prev(info);
            if (info != null)
                this.transceiver.setFrequency(info.getStart() * 1000L);
        }
    }

    private void nextBand() {
        FrequencyInfo info = FrequencyInfo.get(this.transceiver.getFrequency() / 1000L);
        if (info != null) {
            info = FrequencyInfo.next(info);
            if (info != null)
                this.transceiver.setFrequency(info.getStart() * 1000L);
        }
    }

    public static void main(String[] args) {
        Main.main(args);
    }

}
