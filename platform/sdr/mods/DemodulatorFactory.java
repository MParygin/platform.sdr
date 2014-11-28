package platform.sdr.mods;

import platform.sdr.SynchroDetector;
import platform.sdr.mods.am.AMDem;
import platform.sdr.mods.am.SAMDem;

import java.util.HashMap;
import java.util.Map;

/**
 * Фабрика демодуляторов
 */
public final class DemodulatorFactory {

    static Map<String, Demodulator> dems = new HashMap<String, Demodulator>();

    private static void add(Demodulator demodulator) {
        dems.put(demodulator.name(), demodulator);
    }

    static {
        add(new AMDem());
        add(new SAMDem());
        add(new SynchroDetector());
    }

    public static Demodulator get(String name) {
        return dems.get(name);
    }
}
