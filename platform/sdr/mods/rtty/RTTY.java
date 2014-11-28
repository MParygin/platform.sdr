package platform.sdr.mods.rtty;

/**
 *
 */
public class RTTY {

    // Const
    public static final int SampleRate = 48000;
    public static final int MaxSymLen = 1024;

    // Parity values
    public static final int PARITY_NONE = 0;
    public static final int PARITY_EVEN = 1;
    public static final int PARITY_ODD = 2;
    public static final int PARITY_ZERO = 3;
    public static final int PARITY_ONE = 4;

    // RX states
    public static final int RTTY_RX_STATE_IDLE = 0;
    public static final int RTTY_RX_STATE_START = 1;
    public static final int RTTY_RX_STATE_DATA = 2;
    public static final int RTTY_RX_STATE_PARITY = 3;
    public static final int RTTY_RX_STATE_STOP = 4;
    public static final int RTTY_RX_STATE_STOP2 = 5;

    // Baudot
    public static final int BAUDOT_LETS	= 0x100;
    public static final int BAUDOT_FIGS	= 0x200;

    static char letters[] = {
        '\0', 'E', '\n', 'A', ' ', 'S', 'I', 'U',
        '\r', 'D', 'R', 'J', 'N', 'F', 'C', 'K',
        'T', 'Z', 'L', 'W', 'H', 'Y', 'P', 'Q',
        'O', 'B', 'G', '·', 'M', 'X', 'V', '·'
    };

    static char figures[] = {
        '\0', '3', '\n', '-', ' ', '\'', '8', '7',
        '\n', '$', '4', '@', ',', '!', ':', '(', //FIX n->a
        '5', '+', ')', '2', 'H', '6', '0', '1',
        '9', '?', '&', '·', '.', '/', '=', '·'
    };

    /*
     * Common stuff
     */
    double shift;
    int symbollen;
    int nbits;
    int parity; //
    int stoplen;
    boolean reverse;
    boolean msb;

    double phaseacc;

    /*
     * RX related stuff
     */
    //struct filter *hilbert;
    //struct fftfilt *fftfilt;

    double[] pipe = new double[MaxSymLen];
    int pipeptr;

    double[] bbfilter = new double[MaxSymLen];
    int filterptr;

    int rxstate;

    int counter;
    int bitcntr;
    int rxdata;

    double prevsymbol;

    int rxmode;

    /*
     * TX related stuff
     */
    int txmode;
    int preamble;

    /**
     * Decode Baudot
     *
     * @param data Data
     * @return Char
     */
    char baudot_dec(int data) {
        char out = 0;

        switch (data) {
            case 0x1F:		/* letters */
                this.rxmode = BAUDOT_LETS;
                break;
            case 0x1B:		/* figures */
                this.rxmode = BAUDOT_FIGS;
                break;
            case 0x04:		/* unshift-on-space */
                this.rxmode = BAUDOT_LETS;
                return ' ';
            default:
                if (this.rxmode == BAUDOT_LETS) {
                    out = letters[data];
                } else {
                    out = figures[data];
                }
                break;
        }

        return out;
    }
    /*

    int rtty_parity(int c, int nbits, int par) {
	c &= (1 << nbits) - 1;

	switch (par) {
	default:
	case PARITY_NONE:
		return 0;

	case PARITY_ODD:
		return Misc.parity(c);

	case PARITY_EVEN:
		return !Misc.parity(c);

	case PARITY_ZERO:
		return 0;

	case PARITY_ONE:
		return 1;
	}
    }

    int bitreverse(int in, int n) {
            int out = 0;
            for (int i = 0; i < n; i++) {
                out = (out << 1) | ((in >> i) & 1);
            }
            return out;
        }


    char decode_char() {
	int parbit = (this.rxdata >> this.nbits) & 1;
	int par = rtty_parity(this.rxdata, this.nbits, this.parity);

	if (this.parity != PARITY_NONE && parbit != par) {
//		fprintf(stderr, "P");
		return 0;
	}

	char data = (char)(this.rxdata & ((1 << this.nbits) - 1));

	if (this.msb)
		data = (char) bitreverse(data, this.nbits);

	if (this.nbits == 5)
		return baudot_dec(data);

	return data;
    }



    int rttyrx(boolean bit) {
	int flag = 0;
	char c;

	switch (this.rxstate) {
	case RTTY_RX_STATE_IDLE:
		if (!bit) {
			this.rxstate = RTTY_RX_STATE_START;
			this.counter = this.symbollen / 2;
		}
		break;

	case RTTY_RX_STATE_START:
		if (--this.counter == 0) {
			if (!bit) {
				this.rxstate = RTTY_RX_STATE_DATA;
				this.counter = this.symbollen;
				this.bitcntr = 0;
				this.rxdata = 0;
				flag = 1;
			} else
				this.rxstate = RTTY_RX_STATE_IDLE;
		}
		break;

	case RTTY_RX_STATE_DATA:
		if (--this.counter == 0) {
			this.rxdata |= ((bit) ? 1 : 0) << this.bitcntr++;
			this.counter = this.symbollen;
			flag = 1;
		}

		if (this.bitcntr == this.nbits) {
			if (this.parity == PARITY_NONE)
				this.rxstate = RTTY_RX_STATE_STOP;
			else
				this.rxstate = RTTY_RX_STATE_PARITY;
		}
		break;

	case RTTY_RX_STATE_PARITY:
		if (--this.counter == 0) {
			this.rxstate = RTTY_RX_STATE_STOP;
			this.rxdata |= ((bit) ? 1 : 0) << this.bitcntr++;
			this.counter = this.symbollen;
			flag = 1;
		}
		break;

	case RTTY_RX_STATE_STOP:
		if (--this.counter == 0) {
			if (bit) {
				c = decode_char();
                                System.err.println("Char: " + c);
				flag = 1;
			} else {
//				fprintf(stderr, "F");
			}
			this.rxstate = RTTY_RX_STATE_STOP2;
			this.counter = this.symbollen / 2;
		}
		break;

	case RTTY_RX_STATE_STOP2:
		if (--this.counter == 0)
			this.rxstate = RTTY_RX_STATE_IDLE;
		break;
	}

	return flag;
    }

    double prevre;
    double previm;

    void rtty_rxprocess(struct trx *trx, double[] buf, int len) {
	struct rtty *s = (struct rtty *) trx->modem;
	complex *zp;

        double zre;
        double zim;

	int n, i;
	double f;
        boolean bit;

	boolean rev = (trx->reverse != 0) ^ (this.reverse);

	for (int k = 0; k < len; k++) {
		// create analytic signal...
		zre = zim = buf[k];

		filter_run(s->hilbert, z, &z);

		// ...so it can be shifted in frequency
		z = mixer(trx, z);

		n = fftfilt_run(s->fftfilt, z, &zp);

		for (i = 0; i < n; i++) {

			f = carg(ccor(prev, zp[i])) * SampleRate / (2 * Math.PI);
			prev = zp[i];

			f = bbfilt(s, f);
			this.pipe[this.pipeptr] = f;
			this.pipeptr = (this.pipeptr + 1) % this.symbollen;

			if (this.counter == this.symbollen / 2)
				update_syncscope(s);

//			f = bbfilt(s, f);
			if (rev)
				bit = (f > 0.0);
			else
				bit = (f < 0.0);

			if (rttyrx(bit) && trx->afcon) {
				if (f > 0.0)
					f = f - this.shift / 2;
				else
					f = f + this.shift / 2;

//				fprintf(stderr, "bit=%d f=% f\n", bit, f);

				if (Math.abs(f) < this.shift / 2)
					trx_set_freq(trx->frequency + f / 256);
			}
		}
	}

    } */
}
