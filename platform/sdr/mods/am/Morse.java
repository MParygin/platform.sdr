package platform.sdr.mods.am;

import platform.sdr.TransceiverRXListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Morse implements TransceiverRXListener, Runnable {


    private static final int AUDIO_FREQUENCY        = 500; /* Nominal detector frequency (Hz)  */
    private static final int CYCLES_PER_FRAG        = 2; /* Cycles of signal/signal fragment */
    private static final int INITIAL_WPM            = 20;
    private static final int MAX_UNIT_LEN           = 30; /* Max length of Morse unit, fragments */
    private static final int MIN_UNIT_LEN           = 10; /* Max length of Morse unit, fragments */
    private static final int DSP_SPEED              = 48000; /* dsp sampling speed in samples/sec */
    private static final int STEP_THRESHOLD         = 105; /* % diff between successive sig levels */

    private static final int MARK_SIGNAL            = 0x0001; /* Count fragments of a mark element   */
    private static final int ELEM_SPACE             = 0x0002; /* Count frag. of inter-element space  */
    private static final int CHAR_SPACE             = 0x0004; /* Count fragments of inter-char space */
    private static final int WAIT_WORD_SPACE        = 0x0008; /* Wait for an inter-word space        */
    private static final int WORD_SPACE             = 0x0010; /* Count fragments of inter-word space */
    private static final int WAIT_FOR_MARK          = 0x0020; /* Count fragments of no-signal space  */
    private static final int WAIT_FOR_SPACE         = 0x0040; /* Wait for a space after a long dash  */
    private static final int MARK_TONE              = 0x0080; /* Fragment is a mark (key down)       */
    private static final int ADAPT_SPEED            = 0x0100;  /* Enable speed tracking */

    public int unit_elem = (60*AUDIO_FREQUENCY) / (50*CYCLES_PER_FRAG*INITIAL_WPM);

    private int det_thr = 60;

    public int space_elem_cnt = 0; /* Number of space elements processed  */
    private int space_frag_cnt = 0; /* Number of space fragments processed */
    public int mark_elem_cnt  = 0; /* Number of mark elements processed   */
    private int mark_frag_cnt  = 0; /* Number of mark fragments processed  */
    public int mark_cnt    = 0; /* Count of Mark fragments detected  */
    public int space_cnt   = 0; /* Count of Space fragments detected */
    private int new_context = 0; /* What context Morse decoding is in */
    private int hex_code = 0x01; /* Hex equivalent of Morse character */


    public Morse() {
        Set_Flag(ADAPT_SPEED);

        // ?
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
	while (true) {
	  /* Just print chars if no word wrap */
	  System.out.print(Get_Character());
	}
    }


/*------------------------------------------------------------------------*/

/*  Get_Fragment()
 *
 *  Detects the cw beat frequency signal o/p from the radio
 *  receiver and determines the status, (mark or space) of a
 *  'fragment' (a small fraction, ~1/8) of a Morse code element.
 *  Signal detection is done by using a Goertzel algorithm.
 */

  /* Variables for the Goertzel algorithm */
  private double cosw, sinw, coeff;

  private int
	/* Fragment length in DSP samples */
	frag_len		 = (DSP_SPEED*CYCLES_PER_FRAG)/AUDIO_FREQUENCY,
	last_block		 = 0, /* Previous value of Goertzel block size  */
	samples_buff_len = (CYCLES_PER_FRAG*MAX_UNIT_LEN*DSP_SPEED)/AUDIO_FREQUENCY,
	samples_buff_idx = 0; /* Sample buffer index */

  /* Circular signal samples buffer for Goertzel detector */
  private int[] samples_buff = new int[(CYCLES_PER_FRAG * MAX_UNIT_LEN * DSP_SPEED)/AUDIO_FREQUENCY];

  /* Circular signal level buffer and index for edge detector */
  private int sig_level_idx = 0;
  private int[] sig_level_buff = new int[MAX_UNIT_LEN];

  int Get_Fragment() {

      int
            frag_lev,    /* Level of the Morse signal 'fragment'     */
            frag_timer,  /* Counter for timing duration of fragment  */
            block_size,  /* Block size (N) of the Goertzel algorithm */
            up_steps,	 /* Num of consecutive increasing signal levels */
            dn_steps,	 /* Num of consecutive decreasing signal levels */
            idx;

      double q0, q1, q2;


      /* Goertzel block size depends on Morse speed */
      block_size = frag_len * unit_elem;

      /* Recalculate Goertzel parameters in block size changes */
      if( block_size != last_block ) {
            double w = 2.0 * Math.PI * (double)AUDIO_FREQUENCY / (double)DSP_SPEED;
            cosw = Math.cos(w);
            sinw = Math.sin(w);
            coeff = 2.0 * Math.cos(w);
            last_block = block_size;
      }

      /* Buffer dsp samples of input signal for a fragment */
      for(frag_timer = 0; frag_timer < frag_len; frag_timer++ ) {
            /* Get next signal sample from buffer, abort on error */
            samples_buff[samples_buff_idx] = Get_Signal_Sample();

            /* Advance/Reset circular buffers' index */
            if(++samples_buff_idx >= samples_buff_len ) samples_buff_idx = 0;

      } /* for( frag_timer = 0; frag_timer < frag_len ... */

      /** Calculate signal fragment level over a block **/
      /* Backstep buffer index for use of samples */
      samples_buff_idx -= block_size;
      if( samples_buff_idx < 0 ) samples_buff_idx += samples_buff_len;

      /* Calculate fragment level using Goertzel algorithm */
      q1 = q2 = 0.0;
      for( idx = 0; idx < block_size; idx++ ) {
            q0 = coeff * q1 - q2 + samples_buff[samples_buff_idx];
            q2 = q1;
            q1 = q0;

            /* Reset circular buffers' index */
            if( ++samples_buff_idx >= samples_buff_len )
              samples_buff_idx = 0;
      }

      /* Scalar magnitude of input signal scaled by block size */
      q1 /= (double)block_size;
      q2 /= (double)block_size;
      frag_lev = (int)(q1*q1 + q2*q2 - q1*q2*coeff);

      /* Save signal power level to circular buffer */
      sig_level_buff[sig_level_idx] = frag_lev;
      if( ++sig_level_idx >= MAX_UNIT_LEN )
            sig_level_idx = 0;

      /* Backstep buffer index for use of fragment levels */
      sig_level_idx -= unit_elem;
      if( sig_level_idx < 0 )
            sig_level_idx += MAX_UNIT_LEN;

      /* Count the number of "steps" in the signal's edge that are
       * in the same direction (increasing or decreasing amplitude) */
      up_steps = dn_steps = 0;
      for( idx = 1; idx < unit_elem; idx++ ) {
            int tmp1, tmp2;

            /* Compare successive signal levels */
            tmp1 = sig_level_buff[sig_level_idx];
            if( ++sig_level_idx >= MAX_UNIT_LEN )
              sig_level_idx = 0;
            tmp2 = sig_level_buff[sig_level_idx];

            /* Successive levels are compared for more
             * than STEP_THRESHOLD difference up or down */
            if( STEP_THRESHOLD*tmp1 < 100*tmp2 )
              up_steps++;
            else if( 100*tmp1 > STEP_THRESHOLD*tmp2 )
              dn_steps++;
      }

      if( ++sig_level_idx >= MAX_UNIT_LEN ) sig_level_idx = 0;

      /* Set tone status. */
      if( 100*up_steps > det_thr * (unit_elem-1) )
            Set_Flag( MARK_TONE );
      else
            if( 100*dn_steps > det_thr*(unit_elem-1) )
              Clear_Flag( MARK_TONE );

      return( frag_lev );

} /* End of Get_Fragment() */


    /*------------------------------------------------------------------------*/

    /*  Adapt_Decoder()
     *
     *  Adjusts Morse speed from measurements on the incoming signal
     */
    void Adapt_Decoder() {
        int speed_err; /* Morse speed error  */

        /* Calculate Morse speed */
        if (mark_elem_cnt != 0 && mark_frag_cnt != 0 && space_elem_cnt != 0 && space_frag_cnt != 0) {
            if (isFlagSet(ADAPT_SPEED)) {
                /* Estimate Morse speed from space and mark counts */
                speed_err = (mark_frag_cnt + space_frag_cnt)
                        / (mark_elem_cnt + space_elem_cnt) - unit_elem;

                /* Morse speed limits (30-10 wpm) */
                if ((unit_elem > MIN_UNIT_LEN) && (speed_err < 0)) {
                    unit_elem--;
                }
                if ((unit_elem < MAX_UNIT_LEN) && (speed_err > 0)) {
                    unit_elem++;
                }

            } /* if( isFlagSet(ADAPT_SPEED) ) */

        } /* if( mark_elem_cnt && space_elem_cnt && space_frag_cnt ) */

        /* Clear counters */
        space_elem_cnt = space_frag_cnt = 0;
        mark_elem_cnt = mark_frag_cnt = 0;

    } /* Adapt_Decoder() */


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


/*  Get_Character()
 *
 *  Decodes a Morse code character from the
 *  sequence of marks (dits and dahs) and spaces
 */


    public char Get_Character() {



  /* Hex equivalent of Morse code is formed by left-shifting */
  /* 1 or 0 into hex_code. The 0x01 initial value marks the  */
  /* beginning of the bit field, 1 being a dit and 0 a dash. */

  /* Get the level of a fragment from PLL detector. A fragment    */
  /* is a small fraction of a morse code element, there are from  */
  /* 8 to 30 frags/element depending on Morse speed (30-10 w.p.m) */
  while(true) {
	/* Decide on a mark or space fragment */
	/* with a hysterisis on the threshold */
	Get_Fragment();

	/* Increment mark or space count */
	if( isFlagSet(MARK_TONE) )
	  mark_cnt++;
	else
	  space_cnt++;

	/* If a mark element is too long, limit count */
	if( mark_cnt > unit_elem * 8 ) mark_cnt = unit_elem * 8;

	/* If a space element is too long, limit count */
	if( space_cnt > unit_elem * 16 ) space_cnt = unit_elem * 16;

	/* Process mark and space element counts to decode Morse */
	switch( new_context )
	{
	  case MARK_SIGNAL: /* Process mark element */

		/* If fragment is a mark */
		if( isFlagSet(MARK_TONE) )
		{
		  /* If mark element is too long */
		  /* reset and wait for a space  */
		  if( mark_cnt >= (unit_elem * 8) )
		  {
			/* Clear space counter */
			space_cnt = 0;

			/* Clear hex character code */
			hex_code = 0x01;

			/* Wait for a space fragment */
			new_context = WAIT_FOR_SPACE;

		  } /* if( mark_cnt >= unit_elem * 8 ) */

		} /* if( isFlagSet(MARK_TONE) ) */
		else
		{
		  /* Clear space count to 1 */
		  space_cnt = 1;

		  /* Switch to processing inter-element space */
		  new_context = ELEM_SPACE;
		}

		break;

	  case ELEM_SPACE: /* Process inter-element space */

		/* If space reaches 1/2 units its an inter-element space */
		if( ((space_cnt * 2) >= unit_elem) ||
			isFlagSet(MARK_TONE) )
		{
		  /* If mark is < 2 units its a dit else a dash */
		  if( (mark_cnt < unit_elem * 2) )
		  {
			/* Insert dit and increment mark frag and elem count */
			hex_code = (hex_code << 1) | 0x01;
			mark_frag_cnt += mark_cnt;
			mark_elem_cnt += 1; /* A dit is 1 element long */
		  }
		  else
		  {
			/* Insert dash and increment mark frag and elem count */
			hex_code <<= 1;
			mark_frag_cnt += mark_cnt;
			mark_elem_cnt += 3; /* A dash is 3 elements long */
		  } /* if( mark_cnt < unit_elem * 2 ) */

		  /* Clear mark count */
		  mark_cnt = 0;

		  /* Wait for inter-char space count */
		  if( isFlagClear(MARK_TONE) )
			new_context = CHAR_SPACE;
		  else
		  {
			space_cnt = 0;
			new_context = MARK_SIGNAL;
		  }

		} /* if( (space_cnt * 2) >= unit_elem || ) */

		break;

	  case CHAR_SPACE: /* Wait for inter-char space */

		/* If fragment is space */
		if( isFlagClear(MARK_TONE) )
		{
		  /* If space reaches 2 units its inter-character */
		  if( space_cnt >= (unit_elem * 2) )
		  {
			/* Switch to waiting for inter-word space */
			new_context = WAIT_WORD_SPACE;

			/* Return decoded Morse char */
                        char c = Hex_to_Ascii(hex_code);

                        /* Clear hex character code */
			hex_code = 0x01;

                        return c;
		  }

		}  /* if( isFlagClear(MARK_TONE) ) */
		else /* Its the end of inter-element space */
		{
		  /* Count up space frags and elements */
		  space_frag_cnt += space_cnt;
		  space_elem_cnt++; /* Inter-element space */

		  /* Clear space cnt and process marks */
		  space_cnt = 0;
		  new_context = MARK_SIGNAL;
		}

		break;

	  case WAIT_WORD_SPACE: /* Wait for an inter-word space */

		/* If fragment is space */
		if( isFlagClear(MARK_TONE) )
		{
		  /* If space count reaches 5, its word space */
		  if( space_cnt >= (unit_elem * 5) )
			new_context = WORD_SPACE;

		}  /* if( isFlagClear(MARK_TONE) ) */
		else /* Its the end of inter-character space */
		{
		  /* Adapt to incoming signal */
		  Adapt_Decoder();

		  /* Switch to processing mark signal */
		  space_cnt = 0;
		  new_context = MARK_SIGNAL;
		}

		break;

	  case WORD_SPACE: /* Process Inter-word space */

		/* If fragment is space */
		if( isFlagClear(MARK_TONE) )
		{
		  if( space_cnt >= (unit_elem * 7) )
		  {
			new_context = WAIT_FOR_MARK;
			return( ' ' );
		  }
		}
		else
		{
		  /* Adapt to incoming signal */
		  Adapt_Decoder();

		  /* Switch to processing mark signal */
		  space_cnt = 0;
		  new_context = MARK_SIGNAL;
		  return( ' ' );
		}

	  case WAIT_FOR_MARK: /* Process no-signal space */

		/* If fragment is mark switch to processing marks */
		if( isFlagSet(MARK_TONE) )
		{
		  space_cnt = 0;
		  new_context = MARK_SIGNAL;
		}

		break;

	  case WAIT_FOR_SPACE: /* Wait for space after long dash */

		/* If fragment is space, switch to counting space */
		if( isFlagClear(MARK_TONE) )
		{
		  space_cnt = 1;
		  mark_cnt  = 0;
		  new_context = WAIT_FOR_MARK;
		}

		break;

	  default: /* Set context if none */
		if( isFlagSet(MARK_TONE) )
		  new_context = MARK_SIGNAL;
		else
		  new_context = WAIT_FOR_MARK;

	} /* End of switch( new_context ) */

  } /* while(1) */


    }

    /*------------------------------------------------------------------------*/

    private volatile int[] swap = null;

private static int BUFFER_SIZE            = 1024;

/*  Get_Signal_Sample()
 *
 *  Gets the next DSP sample of the signal input
 */

  /* Signal/dsp samples buffer */
  private int[] buffer = new int[BUFFER_SIZE];

  static int
	/* Index to signal samples buffer */
	buffer_idx = BUFFER_SIZE,
    /* Buffer size according to stereo/mono mode */
	buffer_size = BUFFER_SIZE;


  int Get_Signal_Sample() {
  int sample_cnt;  /* Number of samples read from dsp */



  /* New DSP sample of audio input  */
  int sample;


  /* Refill dsp samples buffer when needed */
  if( buffer_idx >= buffer_size )
  {
	/* Start buffer index according to stereo/mono mode */
        buffer_idx = 0;

	/* Read carrier samples from dsp, abort on error */
        while (true) {
            if (this.swap == null) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Morse.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Morse.class.getName()).log(Level.SEVERE, null, ex);
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
  sample = buffer[ buffer_idx ]; //- 128;

  /* Increment according to mono/stereo mode */
  buffer_idx += 1;

  return( sample );

} /* End of Get_Signal_Sample() */


    /*------------------------------------------------------------------------*/

    /*  Hex_to_Ascii()
     *
     *  Converts the hex equivalent of
     *  a Morse code character to ASCII
     */
    private static int NUMBER_OF_CHAR = 55;

    /* Table of ASCII characters available in Morse code */
    private char[] morse_ascii_char = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', ',', ':',
        '?', '\'', '-', '/', '(', '"', ';', '$', '#', '<', '!', '@', ']',
        '=', '~', '_', '*', 0
    };

    /* Table of hex equivalent of Morse characters */
    private int[] morse_hex_char = {
        0x06, 0x17, 0x15, 0x0b, 0x03, 0x1d, 0x09, 0x1f, 0x07, 0x18, 0x0a, 0x1b, 0x04,
        0x05, 0x08, 0x19, 0x12, 0x0d, 0x0f, 0x02, 0x0e, 0x1e, 0x0c, 0x16, 0x14, 0x13,
        0x30, 0x38, 0x3c, 0x3e, 0x3f, 0x2f, 0x27, 0x23, 0x21, 0x20, 0x6a, 0x4c, 0x47,
        0x73, 0x61, 0x5e, 0x2d, 0x52, 0x6d, 0x55, 0xf6, 0x35, 0x7a, 0x2a, 0x37, 0x29,
        0x2e, 0xff, 0x01
    };

    private char Hex_to_Ascii(int hex_code) {
        int idx; /* Loop index */

        /* Look for a match in hex table */
        for (idx = 0; idx < NUMBER_OF_CHAR; idx++) {
            if (hex_code == morse_hex_char[idx]) {
                break;
            }
        }

        /* Return ascii equivalent of hex code */
        return (morse_ascii_char[idx]);
    }


    @Override
    public void recieveIQ(int length, int[] I, int[] Q) {
       int[] r = new int[length];
       System.arraycopy(I, 0, r, 0, length);
       this.swap = r;
    }




}
