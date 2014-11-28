package platform.sdr;

import net.miginfocom.swing.MigLayout;
import sdrAtlys.SDRAtlysNet;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        
//        System.out.println(System.currentTimeMillis());

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        MigLayout mig = new MigLayout(
                "insets 0 0 0 0",
                "[grow]",
                "[]1[]1[grow]"
        );

        // dsp
        DSPImpl dsp = new DSPImpl();
        dsp.setLeft(400);
        dsp.setRight(600);

        // center
        //morse = new Morse();
        //Transceiver transceiver = new SDRN2Old(ip);
        
        Transceiver transceiver = new SDRAtlysNet();
        
        Waterfall waterfall = new Waterfall(transceiver, dsp);
        transceiver.addRXListener(waterfall);
        transceiver.addRXListener(dsp);
        //transceiver.addRXListener(morse);

        // root
        JPanel root = new JPanel(mig);
//        root.add(bands(transceiver), "cell 0 0");
//        root.add(modulations(), "cell 0 1");
        root.add(waterfall, "grow, cell 0 0 1 3");

        // frame
        JFrame frame = new JFrame("SDR");
        frame.setSize(1800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //  frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        // layout
        frame.getContentPane().add(root);

        // addons
        waterfall.setFrame(frame);
    }

}
