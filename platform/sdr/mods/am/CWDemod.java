package platform.sdr.mods.am;

/**
 *
 */
public class CWDemod {

    public StringBuilder chars = new StringBuilder();
    //public List<Character> chars = new ArrayList<Character>(); // chars . _ P Z W

    public double avg = 0.01;
    public double max;
    public double lmg;
    public double phase;

    public int[] hist = new int[1024]; // 48
    int cl = -0;
    boolean cv = false;

    public int BASE = 201;




    public void demodulate(double[] IQ) {
        double result = 0;
    int IL = BASE * 13;
    int ILM = IL * 3 / 4;
    int IL2 = IL * 2;
    int IL5 = IL * 5;

        double mx = 0;
        for (int i = 0; i < IQ.length; i+=2) {
            double mag = Math.sqrt(IQ[i] * IQ[i] + IQ[i+1] * IQ[i+1]);
            mx = Math.max(mag, mx);
            lmg = lmg *0.99 + mag * 0.01;
            boolean accpt = (lmg > ((max + avg) / 2d)) && ((max / avg) > 3) && (max > 0.35);
            if (accpt) {
                IQ[i] = Math.sin(phase) * 100;
                IQ[i+1] = IQ[i];
            } else {
                IQ[i] = 0;
                IQ[i+1] = 0;
            }

            if (cv != accpt) {
                // insert symbol
                if (cv) {
                    // was 0 (PWZ)
                    if (cl < IL2) {
                        //chars.add('P');
                    } else if (cl > IL5) {
                        chars.append('Z');
                    } else {
                        chars.append(' ');
                    }
                } else {
                    // was 1 (._)
                    if ((cl < IL2) && (cl > ILM)) {
                        chars.append('.');
                    } else {
                        chars.append('_');
                    }
                }


                cl = cl / 13;
                if (cl > 1023) cl = 1023;
                hist[cl]++;
                cl = 0;
                cv = accpt;
            } else {
                cl = cl + 1;
            }

            result += mag;
            phase += Math.PI / 30d;
        }
        result = (result / IQ.length / 2);

        this.avg = this.avg * 0.95d + result * 0.05d;
        this.max = this.max * 0.95d + mx * 0.05d;
        // cut off

    }



    public static String replc(String sw) {
       sw = sw.replace("_____", "0");
       sw = sw.replace("____.", "9");
       sw = sw.replace("___..", "8");
       sw = sw.replace("__...", "7");
       sw = sw.replace("_....", "6");
       sw = sw.replace(".....", "5");
       sw = sw.replace("...._", "4");
       sw = sw.replace("...__", "3");
       sw = sw.replace("..___", "2");
       sw = sw.replace(".____", "1");

       sw = sw.replace("....", "H");
       sw = sw.replace("..._", "V");
       sw = sw.replace(".._.", "F");
       sw = sw.replace("._..", "L");
       sw = sw.replace(".___", "J");
       sw = sw.replace(".__.", "P");
       sw = sw.replace("_...", "B");
       sw = sw.replace("_.._", "X");
       sw = sw.replace("_._.", "C");
       sw = sw.replace("_.__", "Y");
       sw = sw.replace("__..", "Z");
       sw = sw.replace("__._", "Q");

       sw = sw.replace("...", "S");
       sw = sw.replace(".._", "U");
       sw = sw.replace("._.", "R");
       sw = sw.replace(".__", "W");
       sw = sw.replace("_..", "D");
       sw = sw.replace("_._", "K");
       sw = sw.replace("__.", "G");
       sw = sw.replace("___", "O");

       sw = sw.replace("..", "I");
       sw = sw.replace("._", "A");
       sw = sw.replace("_.", "N");
       sw = sw.replace("__", "M");

       sw = sw.replace(".", "E");
       sw = sw.replace("_", "T");

       return sw;
    }

    public static void main(String[] args) {
        System.out.println(replc("._.. _.._ __"));
    }

}
