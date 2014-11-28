package platform.sdr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dumper
 */
public final class Dumper implements TransceiverRXListener{

    private String fileName = null;
    private FileOutputStream fos = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss'.iq'");
    private byte[] bf = new byte[8192];

    @Override
    public void recieveIQ(int length, int[] I, int[] Q) {
        if (this.fos != null) {
            ByteBuffer bb = ByteBuffer.wrap(this.bf);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < length; i++) {
                bb.putInt(I[i]);
                bb.putInt(Q[i]);
            }
            try {
                this.fos.write(this.bf);
            } catch (Exception ex) {
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void toggle() {
        if (fos == null) {
            try {
                this.fileName = this.format.format(new Date());
                FileOutputStream fos = new FileOutputStream(this.fileName);
                this.fos = fos;
            } catch (FileNotFoundException ex) {
            }
        } else {
            this.fileName = null;
            FileOutputStream fos = this.fos;
            this.fos = null;
            try {
                fos.flush();
                fos.close();
            } catch (IOException ex) {
            }
        }
    }

}
