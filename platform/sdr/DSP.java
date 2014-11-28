package platform.sdr;

import platform.sdr.mods.Demodulator;

/**
 * DSP блок
 */
public interface DSP {

    int getLeft();

    int getRight();

    void setLeft(int freq);

    void setRight(int freq);

    /**
     * Получить текущий демодулятор
     *
     * @return Демодулятор
     */
    Demodulator getDemodulator();

    /**
     * Установить демодулятор
     *
     * @param demodulator Демодулятор
     */
    void setDemodulator(Demodulator demodulator);

}
