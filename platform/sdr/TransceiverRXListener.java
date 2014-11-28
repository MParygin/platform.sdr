package platform.sdr;

/**
 * Слушатель принимаемых данных
 */
public interface TransceiverRXListener {

    public void recieveIQ(int length, int[] I, int[] Q);

}
