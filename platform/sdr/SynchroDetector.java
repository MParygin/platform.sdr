package platform.sdr;

import platform.sdr.mods.Demodulator;

/**
 *
 */
public class SynchroDetector implements Demodulator {

    private static final int DEPTH = 100;
    private static final double PHI = Math.PI * 2.0 / 48000.0;
    private static final double FR = 25.0; // частота шага
    private static final double FACTOR = 0.05;
    private static final double FACTOR2 = 1 - FACTOR;


    public double[] amp = new double[DEPTH];
    private double[] phase = new double[DEPTH];

    @Override
    public String name() {
        return "PhaseDetector";
    }

    @Override
    public void process(double[] IQ) {
        for (int i = 0; i < IQ.length; i+=2) {

            // каждый блок по 25 Гц
            for (int j = 0; j < DEPTH; j++) {
                // PLL цепь
                double sre = IQ[i];
                double sim = IQ[i+1];

                // Z
                double zre = Math.cos(this.phase[j]);
                double zim = Math.sin(this.phase[j]);

                // Магнитуда
                //double mag = Math.sqrt(sre * sre + sim * sim);

                // Задержка
                double dre = zre * sre + zim * sim;
                double dim = -zim * sre + zre * sim;

               // double diffm = Math.sqrt(dim * dim + dre * dre);
                this.amp[j] =  this.amp[j] * FACTOR2 +  dre * FACTOR;

                // phase
                this.phase[j] += j * FR * PHI;
            }

        }
    }

}
