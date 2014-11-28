package platform.sdr;

/**
 * Интерфейс трансивера
 */
public interface Transceiver {

    /**
     * Установить частоту
     *
     * @param freq Частота, миллиГерц
     */
    public void setFrequency(long freq);

    public long getFrequency();

    /**
     * Получить ширину принимаемой полосы
     *
     * @return Полоса, Герц
     */
    public int getBandwith();

    public void detach();

    public void addRXListener(TransceiverRXListener listener);
}
