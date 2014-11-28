package platform.sdr.mods;

/**
 * Демодулятор
 */
public interface Demodulator {

    /**
     * Получить наименование
     *
     * @return Наименование
     */
    String name();

    /**
     * Обработка квадратурной выборки
     * 
     * @param IQ Квадратурная выборка в обычном формате IQ
     */
    void process(double[] IQ);

}
