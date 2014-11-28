package platform.sdr.mods.am;

import platform.sdr.mods.Demodulator;

/**
 * AM демодулятор (кваратурная выборка в 2 канала амплитудного значения)
 */
public final class AMDem implements Demodulator {

    private double dc;
    private double smooth;

    /**
     * Получить наименование
     *
     * @return Наименование
     */
    @Override
    public String name() {
        return "AM";
    }

    /**
     * Обработка квадратурной выборки
     *
     * @param IQ Квадратурная выборка в обычном формате IQ
     */
    @Override
    public void process(double[] IQ) {
        for (int i = 0; i < IQ.length; i+=2) {
            double lock_curr = Math.sqrt(IQ[i] * IQ[i] + IQ[i+1] * IQ[i+1]);
            this.dc = 0.9999f * this.dc + 0.0001f * lock_curr;
            this.smooth = 0.5f * this.smooth + 0.5f * (lock_curr - this.dc);
            IQ[i] = this.smooth;
            IQ[i+1] = this.smooth;
        }
    }

}
