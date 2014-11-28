package platform.sdr.mods.am;

import platform.sdr.mods.Demodulator;

/**
 * AM синхронный демодулятор (кваратурная выборка в 2 канала амплитудного значения)
 */
public final class SAMDem implements Demodulator {

    private static final double TWOPI = Math.PI * 2d;

    private double phs; // Фаза
    private double f;
    private double alpha;
    private double beta;

    private double h;
    private double l;
    private double dre;
    private double dim;

    private double dc;
    private double curr;

    private double amag = 1;

    public SAMDem() {
        double fac = (TWOPI / 48000);

        this.h = 3000 * fac;
        this.f = 0 * fac;
        this.l = -3000 * fac;

        double iir = 10000 * fac;
        this.alpha = iir * 0.3;
        this.beta = this.alpha * this.alpha * 0.25;

    }

    /**
     * Получить наименование
     *
     * @return Наименование
     */
    @Override
    public String name() {
        return "SAM";
    }

    /**
     * Обработка квадратурной выборки
     *
     * @param IQ Квадратурная выборка в обычном формате IQ
     */
    @Override
    public void process(double[] IQ) {
        double _mag = 0;

        for (int i = 0; i < IQ.length; i+=2) {
            // PLL цепь
            double sre = IQ[i];
            double sim = IQ[i+1];

            // Z
            double zre = Math.cos(this.phs);
            double zim = Math.sin(this.phs);

            // Магнитуда
            double mag = Math.sqrt(sre * sre + sim * sim);
            _mag += mag;

            // Задержка
            this.dre = zre * sre + zim * sim;
            this.dim = -zim * sre + zre * sim;

            // Тангенс
            double diff = Math.atan2(this.dim, this.dre);
            double diffm = Math.sqrt(this.dim * this.dim + this.dre * this.dre);

            //diff = diff * diffm * diffm * diffm * diffm;
            diff = diff / 100;

//            if ((mag / this.amag) < 1.2) diff = 0;
            if (i == 10) System.out.println("-- " + diff + "    " + diffm);
//            diff = diff * mag / amag / 100d;


            // Корректировка частоты
            this.f += this.beta * diff;

            this.f = Math.max(this.f, this.l);
            this.f = Math.min(this.f, this.h);

            // Фаза
            this.phs += this.f + this.alpha * diff;

            while (this.phs >= TWOPI) this.phs -= TWOPI;
            while (this.phs < 0) this.phs += TWOPI;

            // demodulate
            this.curr = 0.999d * this.curr + 0.001d * Math.abs(this.dim);

            //am->lock.prev = am->lock.curr;
            this.dc = 0.99999d * this.dc + 0.00001d * this.dre;
            double demout = this.dre - this.dc;

            // вывод
            IQ[i] = demout;
            IQ[i+1] = demout;
        }

        _mag /= 1024.0;
        this.amag = this.amag * 0.9 + _mag * 0.1;

        System.out.println("MAG " + this.amag);

//        double fac = (TWOPI / 48000);
//        System.out.println("SAM: " + (this.f / fac));
    }


    public double getFrequency() {
        double fac = (TWOPI / 48000);
        return this.f / fac;
    }
}
