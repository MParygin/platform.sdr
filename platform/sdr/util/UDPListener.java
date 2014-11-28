package platform.sdr.util;

/**
 *
 */
public interface UDPListener {

    void recieve(byte[] buffer, int offset, int length);
}
