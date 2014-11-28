package platform.sdr.mods.psk;

import platform.sdr.TransceiverRXListener;

/**
 *
 */

class trellis_state_t {
  int      distance; /* Total excess distance to this state  */
  long     estimate; /* Estimate of transmitted bit sequence */
}


public class PSKDemod  implements TransceiverRXListener, Runnable {

    /* Control flags */
    public static final int  NO_SQUELCH           = 0x000001; /* Suppress squelch */
    public static final int  SQUELCH_OPEN         = 0x000002; /* Squelch is open  */
    public static final int  READ_FILE            = 0x000004; /* Read samples from file */
    public static final int  WRITE_FILE           = 0x000008; /* Write samples to file  */
    public static final int  MODE_BPSK            = 0x000010; /* Mode is BPSK     */
    public static final int  MODE_QPSK_USB        = 0x000020; /* Mode is QPSK-USB */
    public static final int  MODE_QPSK_LSB        = 0x000040; /* Mode is QPSK-LSB */
    public static final int  ENABLE_CAT           = 0x000080; /* Enable Trcvr CAT */
    public static final int  AUTO_QSO_DATA        = 0x000100; /* Auto collection of qso data */
    public static final int  RECORD_QSO           = 0x000200; /* Enable recording of QSO's   */
    public static final int  NCURSES_INIT         = 0x000400; /* Curses inteface initialized */
    public static final int  TRANSMIT_MACRO       = 0x000800; /* Start transmission of Macro */
    public static final int  STAY_IN_TX_MODE      = 0x001000; /* Remain in Transmisiion mode */
    public static final int  CAPITAL_LETTERS      = 0x002000; /* Capitalize letters in Trans */
    public static final int  CHANNEL_A_SELECT     = 0x004000; /* Detector channel A selected */
    public static final int  MODE_TRANSMIT        = 0x008000; /* lpsk31 is in Transmit mode  */
    public static final int  RECORD_EDITED        = 0x010000; /* Some data entered in record */
    public static final int  RECORD_SAVED         = 0x020000; /* Record was saved to file    */



    private static final int DSP_SPEED              = 48000; /* dsp sampling speed in samples/sec */
    private static final int BUFFER_SIZE            = 1024; /* Size of buffer for signal samples  */
    private static final int AUDIO_FREQUENCY        = 500; /* Nominal detector frequency (Hz)  */
    private static final int SQUELCH_WINDOW         = 10; /* Length of squelch averager window  */
    private static final int PHASE_AVE_WINDOW       = 5; /* Length of phase averager window    */
    private static final int NUM_VARICODE_CHARS     = 128;
    private static final int CR                     = 0x0D;
    private static final int LF                     = 0x0A;
    private static final int NO_CHARACTER           = 0x80;


    /* Varicode alphabet in HEX */
    int[] varicode_table = {
      /* ASCII   VARICODE       HEX */
      /* NUL   10 1010 1011 */  0x2ab,
      /* SOH   10 1101 1011 */  0x2db,
      /* STX   10 1110 1101 */  0x2ed,
      /* ETX   11 0111 0111 */  0x377,
      /* EOT   10 1110 1011 */  0x2eb,
      /* ENQ   11 0101 1111 */  0x35f,
      /* ACK   10 1110 1111 */  0x2ef,
      /* BEL   10 1111 1101 */  0x2fd,
      /* BS    10 1111 1111 */  0x2ff,
      /* TAB      1110 1111 */  0x0ef,
      /* LF          1 1101 */  0x01d,
      /* VT    11 0110 1111 */  0x36f,
      /* FF    10 1101 1101 */  0x2dd,
      /* CR          1 1111 */  0x01f,
      /* SO    11 0111 0101 */  0x375,
      /* SI    11 1010 1011 */  0x3ab,
      /* DLE   10 1111 0111 */  0x2f7,
      /* DC1   10 1111 0101 */  0x2f5,
      /* DC2   11 1010 1101 */  0x3ad,
      /* DC3   11 1010 1111 */  0x3af,
      /* DC4   11 0101 1011 */  0x35b,
      /* NAK   11 0110 1011 */  0x36b,
      /* SYN   11 0110 1101 */  0x36d,
      /* ETB   11 0101 0111 */  0x357,
      /* CAN   11 0111 1011 */  0x37b,
      /* EM    11 0111 1101 */  0x37d,
      /* SUB   11 1011 0111 */  0x3b7,
      /* ESC   11 0101 0101 */  0x355,
      /* FS    11 0101 1101 */  0x35d,
      /* GS    11 1011 1011 */  0x3bb,
      /* RS    10 1111 1011 */  0x2fb,
      /* US    11 0111 1111 */  0x37f,
      /*                  1 */  0x001,
      /* !      1 1111 1111 */  0x1ff,
      /* "      1 0101 1111 */  0x15f,
      /* #      1 1111 0101 */  0x1f5,
      /* $      1 1101 1011 */  0x1db,
      /* %     10 1101 0101 */  0x2d5,
      /* &     10 1011 1011 */  0x2bb,
      /* '      1 0111 1111 */  0x17f,
      /* (        1111 1011 */  0x0fb,
      /* )        1111 0111 */  0x0f7,
      /* *      1 0110 1111 */  0x16f,
      /* +      1 1101 1111 */  0x1df,
      /* ,         111 0101 */  0x075,
      /* -          11 0101 */  0x035,
      /* .         101 0111 */  0x057,
      /* /      1 1010 1111 */  0x1af,
      /* 0        1011 0111 */  0x0b7,
      /* 1        1011 1101 */  0x0bd,
      /* 2        1110 1101 */  0x0ed,
      /* 3        1111 1111 */  0x0ff,
      /* 4      1 0111 0111 */  0x177,
      /* 5      1 0101 1011 */  0x15b,
      /* 6      1 0110 1011 */  0x16b,
      /* 7      1 1010 1101 */  0x1ad,
      /* 8      1 1010 1011 */  0x1ab,
      /* 9      1 1011 0111 */  0x1b7,
      /* :        1111 0101 */  0x0f5,
      /* ;      1 1011 1101 */  0x1bd,
      /* <      1 1110 1101 */  0x1ed,
      /* =         101 0101 */  0x055,
      /* >      1 1101 0111 */  0x1d7,
      /* ?     10 1010 1111 */  0x2af,
      /* @     10 1011 1101 */  0x2bd,
      /* A         111 1101 */  0x07d,
      /* B        1110 1011 */  0x0eb,
      /* C        1010 1101 */  0x0ad,
      /* D        1011 0101 */  0x0b5,
      /* E         111 0111 */  0x077,
      /* F        1101 1011 */  0x0db,
      /* G        1111 1101 */  0x0fd,
      /* H      1 0101 0101 */  0x155,
      /* I         111 1111 */  0x07f,
      /* J      1 1111 1101 */  0x1fd,
      /* K      1 0111 1101 */  0x17d,
      /* L        1101 0111 */  0x0d7,
      /* M        1011 1011 */  0x0bb,
      /* N        1101 1101 */  0x0dd,
      /* O        1010 1011 */  0x0ab,
      /* P        1101 0101 */  0x0d5,
      /* Q      1 1101 1101 */  0x1dd,
      /* R        1010 1111 */  0x0af,
      /* S         110 1111 */  0x06f,
      /* T         110 1101 */  0x06d,
      /* U      1 0101 0111 */  0x157,
      /* V      1 1011 0101 */  0x1b5,
      /* W      1 0101 1101 */  0x15d,
      /* X      1 0111 0101 */  0x175,
      /* Y      1 0111 1011 */  0x17b,
      /* Z     10 1010 1101 */  0x2ad,
      /* [      1 1111 0111 */  0x1f7,
      /*       1 1110 1111 */  0x1ef,
      /* ]      1 1111 1011 */  0x1fb,
      /* ^     10 1011 1111 */  0x2bf,
      /* _      1 0110 1101 */  0x16d,
      /* `     10 1101 1111 */  0x2df,
      /* a             1011 */  0x00b,
      /* b         101 1111 */  0x05f,
      /* c          10 1111 */  0x02f,
      /* d          10 1101 */  0x02d,
      /* e               11 */  0x003,
      /* f          11 1101 */  0x03d,
      /* g         101 1011 */  0x05b,
      /* h          10 1011 */  0x02b,
      /* i             1101 */  0x00d,
      /* j      1 1110 1011 */  0x1eb,
      /* k        1011 1111 */  0x0bf,
      /* l           1 1011 */  0x01b,
      /* m          11 1011 */  0x03b,
      /* n             1111 */  0x00f,
      /* o              111 */  0x007,
      /* p          11 1111 */  0x03f,
      /* q      1 1011 1111 */  0x1bf,
      /* r           1 0101 */  0x015,
      /* s           1 0111 */  0x017,
      /* t              101 */  0x005,
      /* u          11 0111 */  0x037,
      /* v         111 1011 */  0x07b,
      /* w         110 1011 */  0x06b,
      /* x        1101 1111 */  0x0df,
      /* y         101 1101 */  0x05d,
      /* z      1 1101 0101 */  0x1d5,
      /* {     10 1011 0111 */  0x2b7,
      /* |      1 1011 1011 */  0x1bb,
      /* }     10 1011 0101 */  0x2b5,
      /* ~     10 1101 0111 */  0x2d7,
      /* DEL   11 1011 0101 */  0x3b5
    };

    /*------------------------------------------------------------------------*/

    /*  Decode_PSK_Character()
     *
     *  Decodes a PSK31-coded character from
     *  the sequence of phase reversals in PSK31
     *  signal, producing an Ascii equivalent
     */
    /* Assembles varicode character */
    int varicode = 0;

    int Decode_PSK_Character() {
        int varicode_bit, /* One bit of a Varicode character */
                ascii_char;   /* New decoded ASCII character     */

        /* Get next Varicode bit */
        varicode_bit = Varicode_Bit();

        /* Enter new varicode bit */
        varicode <<= 1;
        varicode |= varicode_bit;

        /* Return char on two consecutive 0's (char space) */
        if ((varicode & 0x03) == 0) {
            /* Dump trailing 0's of SPACE */
            varicode >>= 2;

            /* Convert varicode to ascii */
            /* Look up varicode hex code in table */
            for (ascii_char = 0; ascii_char < NUM_VARICODE_CHARS; ascii_char++) {
                if (varicode_table[ascii_char] == varicode) {
                    break;
                }
            }

            varicode = 0;

            /* Change CR to LF, seems needed */
            if (ascii_char == CR) {
                ascii_char = LF;
            }

            return (ascii_char);

        } /* if( (varicode & 0x03) == 0 ) */ else {
            return (NO_CHARACTER);
        }

    } /* Decode_PSK_Character() */

    /*------------------------------------------------------------------------*/

    /*  Varicode_Bit()
     *
     *  Returns Varicode bits decoded from carrier phase shifts
     */
    int Varicode_Bit() {
        /* Carrier phase changes */
        int phase_chg;

        /* Get phase PSK31 carrier phase change */
        phase_chg = PSK31_Phase();

        /* If mode is BPSK, a phase change of */
        /* 100-180 deg is considered reversal */
        if (isFlagSet(MODE_BPSK)) {
            if ((Math.abs(phase_chg) > 100) && (Math.abs(phase_chg) < 260)) {
                return (0);
            } else {
                return (1);
            }
        } else /* If in QPSK mode */ {
            return (Viterbi_Decoder(phase_chg)) ? 1 : 0;
        }

    } /* Varicode_Bit( void ) */

    int sqlch_thr;  /* Squelch threshold    */

    int old_phase, /* Old carrier phase from detector o/p */
            ave_phs_chg, /* Ave. value of carrier phase changes */
            chA_squelch, /* Channel A squelch (signal) level    */
            chB_squelch, /* Channel B squelch (signal) level    */
            last_lev_i, /* Last phase detector in-phase signal level   */
            last_lev_q, /* Last phase detector quadrature signal level */
            /* Four quadrants of a signal cycle (in dsp samples) */
            quadrant_1 = DSP_SPEED / AUDIO_FREQUENCY / 4,
            quadrant_2 = DSP_SPEED / AUDIO_FREQUENCY / 2,
            quadrant_3 = (3 * DSP_SPEED / AUDIO_FREQUENCY) / 4,
            quadrant_4 = DSP_SPEED / AUDIO_FREQUENCY,
            /* PSK31 element period in DSP samples */
            elem_half_period = BUFFER_SIZE / 2;


    /*------------------------------------------------------------------------*/

    /*  PSK31_Phase()
     *
     *  Detects phase changes of the PSK carrier
     */
    int PSK31_Phase() {

        int elem_timer, /* Counter used for producing the baud rate  */
                sample_lev, /* Sample level calculated from raw sample   */
                det_phase, /* Phase in samples of phase detector        */
                det_level_i, /* Phase detector in-phase signal level      */
                det_level_q, /* Phase detector quadrature signal level    */
                chA_level_i, /* 'A' channel in-phase signal level         */
                chA_level_q, /* 'A' channel quadrature signal level       */
                chB_level_i, /* 'B' channel in-phase signal level         */
                chB_level_q, /* 'B' channel quadrature signal level       */
                sel_level_i, /* In-phase level from selected channel      */
                sel_level_q, /* Quadrature level from selected channel    */
                carrier_phase, /* Phase angle of audio signal from receiver */
                phase_chg, /* Difference between consecutive phase val. */
                phase_slip, /* Average and normalized carrier phase slip */
                max_slip, /* Maximum phase shift taken as phase slip   */
                slice_cnt, /* Counts slices (= 1/2 PSK31 elements) done */
                /* Normalized values of detector */
                /* channels and selected squelch */
                chA_norm_lev_i,
                chA_norm_lev_q,
                chB_norm_lev_i,
                chB_norm_lev_q,
                norm_squelch;

        /* Initialize levels and psk phase */
        chA_level_i = chA_level_q = 0;
        chB_level_i = chB_level_q = 0;
        det_phase = 0;

        /*** Following code acts like a synchronous detector,   ***/
        /*** working at a nominal 500 Hz. To avoid the nulling  ***/
        /*** of the o/p levels when a phase reversal occurs in  ***/
        /*** the middle of a PSK31 element, two 'channels' are  ***/
        /*** used separated by a 1/2 element time delay and the ***/
        /*** one with the best squelch level is selected in use ***/

        /* Return psk phase after two PSK31 'slices' eg 1 element period */
        for (slice_cnt = 0; slice_cnt < 2; slice_cnt++) {
            /* Initialize levels and psk phase */
            det_level_i = det_level_q = 0;

            /* Summate samples for each half element */
            for (elem_timer = 0; elem_timer < elem_half_period; elem_timer++) {
                /* Get next signal sample from buffer */
                sample_lev = Get_Signal_Sample();

                /*** Calculate phase detector in-phase level. ***/
                /* Samples are accumulated over a PSK31 element */

                /* From 0 to 180 deg use sample level as is */
                if (det_phase < quadrant_2) {
                    det_level_i += sample_lev;
                } else /* From 180 to 360 deg use negated (rectified) sample */ {
                    det_level_i -= sample_lev;
                }

                /*** Calculate phase detector quadrature level. ***/
                /* Samples are accumulated over a PSK31 element   */

                /* From 90 to 270 deg use sample level as is */
                if ((det_phase >= quadrant_1)
                        && (det_phase < quadrant_3)) {
                    det_level_q += sample_lev;
                } else /* From 270 to 90 deg use negated (rectified) sample */ {
                    det_level_q -= sample_lev;
                }

                /* Advance detector phase by one DSP sample count */
                det_phase++;

                /* At a full signal cycle, reset the */
                /* phase count of the phase detector */
                if (det_phase == quadrant_4) {
                    det_phase = 0;
                }

            } /* for(elem_timer=0;elem_timer<elem_half_period;elem_timer++) */

            /* Channel A sums current detector levels */
            chA_level_i += det_level_i;
            chA_level_q += det_level_q;

            /* Channel B sums last detector levels */
            chB_level_i += last_lev_i;
            chB_level_q += last_lev_q;

            /* Save current levels */
            last_lev_i = det_level_i;
            last_lev_q = det_level_q;

        } /* for( slice_cnt = 0; slice_cnt < 2; slice_cnt++ ) */

        /* Normalize detector channel levels */
        chA_norm_lev_i = chA_level_i / BUFFER_SIZE;
        chA_norm_lev_q = chA_level_q / BUFFER_SIZE;
        chB_norm_lev_i = chB_level_i / BUFFER_SIZE;
        chB_norm_lev_q = chB_level_q / BUFFER_SIZE;

        /* Calculate sliding window average values for the channel */
        /* squelch. This is the vector magnitude of in-phase and   */
        /* quadrature signals from the phase detector channels     */
        chA_squelch = (chA_squelch * (SQUELCH_WINDOW - 1) + SQUELCH_WINDOW
                * Scalar_Mag(chA_norm_lev_i, chA_norm_lev_q)) / SQUELCH_WINDOW;

        chB_squelch = (chB_squelch * (SQUELCH_WINDOW - 1) + SQUELCH_WINDOW
                * Scalar_Mag(chB_norm_lev_i, chB_norm_lev_q)) / SQUELCH_WINDOW;

        /* Set best channel select flag according to squelch */
        if (chA_squelch > 2 * chB_squelch) {
            Set_Flag(CHANNEL_A_SELECT);
        } else if (chB_squelch > 2 * chA_squelch) {
            Clear_Flag(CHANNEL_A_SELECT);
        }

        /* Select best channel data according to flag */
        if (isFlagSet(CHANNEL_A_SELECT)) {
            /* Normalize squelch value and select level */
            norm_squelch = chA_squelch / SQUELCH_WINDOW;
            sel_level_i = chA_level_i;
            sel_level_q = chA_level_q;
        } else {
            /* Normalize squelch value and select level */
            norm_squelch = chB_squelch / SQUELCH_WINDOW;
            sel_level_i = chB_level_i;
            sel_level_q = chB_level_q;
        }

        /* Set squelch open flag */
        if (norm_squelch > this.sqlch_thr) {
            Set_Flag(SQUELCH_OPEN);
        } else {
            Clear_Flag(SQUELCH_OPEN);
        }

        /* Calculate new carrier phase if squelch is  */
        /* open. Calculate carrier phase from arctan  */
        /* of in-phase and quadrature detector levels */
        if (isFlagSet(SQUELCH_OPEN) || isFlagSet(NO_SQUELCH)) {
            carrier_phase = Arc_Tan(sel_level_q, sel_level_i);
        } else {
            carrier_phase = 0;
        }

        /* Phase change between consecutive phase values */
        phase_chg = old_phase - carrier_phase;
        old_phase = carrier_phase;

        /* For BPSK phase slip limit = 90 */
        /* For QPSK phase slip limit = 45 */
        if (isFlagSet(MODE_BPSK)) {
            max_slip = 90;
        } else {
            max_slip = 45;
        }

        /* Limit phase changes to +-max_slip for slip rate calculations. */
        /* Caclulate average phase change with a sliding window averager */
        if (Math.abs(phase_chg) < max_slip) {
            ave_phs_chg = ((ave_phs_chg * (PHASE_AVE_WINDOW - 1))
                    + (PHASE_AVE_WINDOW * phase_chg)) / PHASE_AVE_WINDOW;
        }

        /* Phase slip = normalized value of average phase change */
        phase_slip = ave_phs_chg / PHASE_AVE_WINDOW;

        /* Keep phase change in range */
        if (phase_chg < 0) {
            phase_chg += 360;
        }

        /* Plot carrier phase on 'scope' */
//  Screen_Phase_Window( carrier_phase, phase_slip, norm_squelch );

        return (phase_chg);

    } /* End of PSK31_Phase() */



    /*------------------------------------------------------------------------*/

    /* Viterbi decoder trellis */
    trellis_state_t[] trellis = new trellis_state_t[16];

    /* Convolutional code symbols */
    int[] symbols = new int[32];


    /*  Viterbi_Decoder()
     *
     *  The Viterbi Decoder
     */
    boolean Viterbi_Decoder(int phase_chg) {
        int min_dist;  /* Tracks minimum distance in trellis */
        int[] dists = new int[32]; /* Distance metrics for the Viterbi   */
        int idx;

        /* Bit sequence esimates */
        long[] ests = new long[32];

        int select, /* Select smallest distance   */
                vote;   /* Vote counter for bit guess */


        /* Reverse phase for receiving on LSB */
        if (isFlagSet(MODE_QPSK_LSB)) {
            phase_chg = 360 - phase_chg;
        }

        min_dist = 65535;
        /* Calculate distances for all states & both current data values */
        for (idx = 0; idx < 32; idx++) {
            /* Added distance = distance between rcvd phase and predicted symbol */
            dists[idx] = trellis[idx / 2].distance + Distance(phase_chg, symbols[idx]);

            /* Keep track of the smallest distance */
            if (dists[idx] < min_dist) {
                min_dist = dists[idx];
            }

            /* New bit sequence estimate */
            ests[idx] = (trellis[idx / 2].estimate << 1) + (idx & 1);

        } /* for( idx = 0; idx < 32; idx++ ) */

        /* For each state in the new trellis array */
        for (idx = 0; idx < 16; idx++) {
            /* Select lowest distance */
            if (dists[idx] < dists[16 + idx]) {
                select = 0;
            } else {
                select = 16;
            }

            /* Update excess distances */
            trellis[idx].distance = dists[select + idx] - min_dist;

            /* Keep the new estimate */
            trellis[idx].estimate = ests[select + idx];

        } /* for( idx = 0; idx < 16; idx++ ) */

        /* Take a vote of the 20th bits */
        vote = 0;
        for (idx = 0; idx < 16; idx++) {
            if ((trellis[idx].estimate & (1 << 20)) > 0) {
                vote++;
            }
        }

        /* 'phase_chg' is used as a random number */
        if (vote == 8) {
            return ((phase_chg & 1) != 0);
        } else {
            return (vote < 8);
        }

    } /* Viterbi_Decoder() */



    /*------------------------------------------------------------------------*/

    /*  Distance()
     *
     *  Distance meteric for the Viterbi decoder
     */
    int Distance(int A, int B) {
        /* Shortest distance around the circumference */
        /* (phase circle) between two points A and B  */
        int dist = Math.abs(A - B);
        if (dist > 180) {
            dist = 360 - dist;
        }
        return (dist);
    } /* Distance() */


    /*------------------------------------------------------------------------*/

    /*  Parity()
     *
     *  Returns odd (?) parity
     */
    int Parity(int data) {
        int count = 0;
        while (data > 0) {
            if ((data & 1) != 0) {
                count++;
            }
            data >>= 1;
        }
        return (count & 1);
    } /* Parity() */



    /* Arc tan correction table */
    int[] table = {
        0, 1, 3, 4, 5, 6, 8, 9, 10, 11, 13, 14,
        15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27,
        28, 29, 30, 31, 32, 33, 34, 35, 35, 36, 37, 38,
        39, 39, 40, 41, 42, 42, 43, 44, 44, 45
    };

    /*------------------------------------------------------------------------*/

    /*  Arc_Tan()
     *
     *  Calculates an aproximate integer arc tan (in deg)
     *  for a given pair of integer parameters (y,x). The
     *  result is in range 0 - +-180 deg, max error ~.5 deg.
     *  The first approximation used is arctan = 45*y/x for
     *  y<x and for y>x arctan = 90-45*x/y. This is used in
     *  a table to improve the accuracy. The signs of x
     *  and y are used to find arc tan in all quadrants.
     */
    int Arc_Tan(int y, int x) {
        int angle, /* Inermediate result  */
                arc_tan; /* Final arctan result */

        /* Avoid divide by zero */
        if ((x == 0) && (y == 0)) {
            return (0);
        }

        /* Angle +-(0-45) deg or (135-225) */
        if (Math.abs(y) <= Math.abs(x)) {
            /* First approximation */
            angle = (45 * y) / x;

            /* Use correction table for more accuracy */
            if (angle < 0) {
                angle = -(int) table[-angle];
            } else {
                angle = (int) table[angle];
            }

            /* Set result in correct quadrant */
            if (x < 0) {
                arc_tan = 180 + angle;
            } else if (y < 0) {
                arc_tan = 360 + angle;
            } else {
                arc_tan = angle;
            }

        } /* if( abs(y) <= abs(x) ) */ else /* Angle (45-135) or (225-315) */ {
            /* First approximation */
            angle = (45 * x) / y;

            /* Use correction table for more accuracy */
            if (angle < 0) {
                angle = -(int) table[-angle];
            } else {
                angle = (int) table[angle];
            }

            /* Set result in correct quadrant */
            if (y < 0) {
                arc_tan = 270 - angle;
            } else {
                arc_tan = 90 - angle;
            }

        } /* else */

        return (arc_tan);

    } /* End of Arc_Tan() */

    /*------------------------------------------------------------------------*/

    /*  Scalar_Mag()
     *
     *  sqrt(x^2 + y^2). This is approximated by a Taylor
     *  series expansion in (x+y) e.g: ((x+y)^2-x*y)/(x+y)
     *  to first order, and refined by one iteration of the
     *  Newton-Raphson method. This is rather wierd but
     *  seems to give the most accurate approximation.
     */
    int Scalar_Mag(int x, int y) {
        /* Vector magnitude of two values at 90 deg */
        int vector_mag;

        int sum, /* Sum of two consecutive samples    */
                prod, /* Product of two cnsecutive samples */
                aprox;  /* First approxim. to sqrt(x^2+y^2)  */

        /* Remove sign */
        x = Math.abs(x);
        y = Math.abs(y);

        /* Calculate first approximation to sqrt(x^2+y^2) */
        sum = x + y;
        prod = x * y;
        aprox = (sum == 0 ? 0 : (sum * sum - prod) / sum);

        /* Refine by one iteration using Newton-Raphson method */
        vector_mag = (aprox == 0 ? 0
                : ((aprox * aprox + sum * sum - (2 * prod)) / aprox) / 2);

        return (vector_mag);

    } /* End of Scalar_Mag() */

    /* An int variable holding the single-bit flags */
    static int Flags = 0;

    boolean isFlagSet(int flag) {
        return ((Flags & flag) == flag);
    }

    boolean isFlagClear(int flag) {
        return ((~Flags & flag) == flag);
    }

    void Set_Flag(int flag) {
        Flags |= flag;
    }

    void Clear_Flag(int flag) {
        Flags &= ~flag;
    }

    void Toggle_Flag(int flag) {
        Flags ^= flag;
    }


    /*------------------------------------------------------------------------*/
    private volatile int[] swap = null;
    /*  Get_Signal_Sample()
     *
     *  Gets the next DSP sample of the signal input
     */

    /* Signal/dsp samples buffer */
    private int[] buffer = new int[BUFFER_SIZE];
    static int /* Index to signal samples buffer */ buffer_idx = BUFFER_SIZE,
            /* Buffer size according to stereo/mono mode */
            buffer_size = BUFFER_SIZE;

    int Get_Signal_Sample() {
        int sample_cnt;  /* Number of samples read from dsp */



        /* New DSP sample of audio input  */
        int sample;


        /* Refill dsp samples buffer when needed */
        if (buffer_idx >= buffer_size) {
            /* Start buffer index according to stereo/mono mode */
            buffer_idx = 0;

            /* Read carrier samples from dsp, abort on error */
            while (true) {
                if (this.swap == null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(Morse.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(Morse.class.getName()).log(Level.SEVERE, null, ex);
                }


                if (this.swap != null) {
                    this.buffer = this.swap;
                    this.swap = null;
                    //System.err.println("Rec buffer: " + this.buffer[0] + " " + this.buffer[1] + " " + this.buffer[2] + " " + this.buffer[3] + " " + this.buffer[4]);
                    break;
                }
            }

//	if( (sample_cnt = read(dsp_fd, buffer, buffer_size)) == -1 )
//	{
//	  perror( "demorse: dsp read()" );
//	  exit(-1);
//	}

        } /* End of if( buffer_idx == buffer_size ) */

        /* Remove neutral value (128) from samples */
        sample = buffer[ buffer_idx]; //- 128;

        /* Increment according to mono/stereo mode */
        buffer_idx += 1;

        return (sample);

    } /* End of Get_Signal_Sample() */



    @Override
    public void recieveIQ(int length, int[] I, int[] Q) {
       int[] r = new int[length];
       System.arraycopy(I, 0, r, 0, length);
       this.swap = r;
    }


    public void run() {
	while (true) {
	  /* Just print chars if no word wrap */
	  System.out.print(Decode_PSK_Character());
	}
    }


    public PSKDemod() {
        //Set_Flag(ADAPT_SPEED);

        // ?
        Thread thread = new Thread(this);
        thread.start();
    }

}
