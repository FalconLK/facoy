package com.pcmsolutions.device.EMU.E4;

import com.pcmsolutions.device.EMU.E4.preset.IsolatedPreset;
import com.pcmsolutions.device.EMU.E4.preset.IsolatedSample;
import com.pcmsolutions.device.EMU.E4.remote.Remotable;
import com.pcmsolutions.device.EMU.database.AbstractDatabase;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * User: paulmeehan
 * Date: 28-Jan-2004
 * Time: 01:57:14
 */
public class SessionExternalization {

    public static final String LAST_SESSION_FILENAME = "last";

    public static final String SESSION_EXT = "zsession_e4";

    public static final String SESSION_CONTENT_ENTRY = "e4_session_contents";

    public static final String LAST_FLASH_SESSION_FILENAME = "last_flash";

    public static final String FLASH_SESSION_CONTENT_ENTRY = "e4_flash_session_contents";

    public static final String LAST_ROM_SESSION_FILENAME = "last_rom";

    public static final String ROM_SESSION_CONTENT_ENTRY = "e4_rom_session_contents";

    public static DeviceSession makeDeviceSession(E4Device device) {
        return new Impl_DeviceSession(device);
    }

    protected interface DeviceSession {

        public E4Device getDevice();

        public RomAndFlash getRomAndFlash();

        public interface RomAndFlash {

            public Map<Integer, Object> getRomMap();

            public Map<Integer, Object> getFlashMap();
        }
    }

    private static class Impl_DeviceSession implements DeviceSession, Serializable {

        private E4Device device;

        private Map<Integer, Object> romMap = new HashMap<Integer, Object>();

        private Map<Integer, Object> flashMap = new HashMap<Integer, Object>();

        public Impl_DeviceSession(E4Device device) {
            this.device = device;
            for (Iterator<Map.Entry<Integer, AbstractDatabase.DBO<DatabasePreset, IsolatedPreset>>> i = device.presetDB.getDBOEntrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Integer, AbstractDatabase.DBO<DatabasePreset, IsolatedPreset>> me = i.next();
                if (me.getKey().intValue() > DeviceContext.MAX_USER_PRESET) flashMap.put(me.getKey(), me.getValue().retrieveRawContent());
            }
            for (Iterator<Map.Entry<Integer, AbstractDatabase.DBO<DatabaseSample, IsolatedSample>>> i = device.sampleDB.getDBOEntrySet().iterator(); i.hasNext(); ) {
                Map.Entry<Integer, AbstractDatabase.DBO<DatabaseSample, IsolatedSample>> me = i.next();
                if (me.getKey().intValue() > DeviceContext.MAX_USER_SAMPLE) romMap.put(me.getKey(), me.getValue().retrieveRawContent());
            }
        }

        public E4Device getDevice() {
            return device;
        }

        private static class Impl_RomAndFlash implements RomAndFlash, Serializable {

            private Map<Integer, Object> romMap;

            private Map<Integer, Object> flashMap;

            public Impl_RomAndFlash(Map<Integer, Object> romMap, Map<Integer, Object> flashMap) {
                this.romMap = romMap;
                this.flashMap = flashMap;
            }

            public Map<Integer, Object> getRomMap() {
                return romMap;
            }

            public Map<Integer, Object> getFlashMap() {
                return flashMap;
            }
        }

        public DeviceSession.RomAndFlash getRomAndFlash() {
            return new Impl_RomAndFlash(romMap, flashMap);
        }
    }

    public static class ExternalizationException extends Exception {

        public ExternalizationException(String message) {
            super(message);
        }
    }

    protected static void saveAsLastSession(DeviceSession snap) throws IOException {
        try {
            saveSession(snap, getLastSessionFileForDevice(snap.getDevice()));
        } catch (IOException e) {
            getLastSessionFileForDevice(snap.getDevice()).delete();
            throw e;
        }
    }

    protected static File getLastSessionFileForDevice(DeviceContext device) {
        return new File(device.getDeviceLocalDir(), LAST_SESSION_FILENAME + "." + SESSION_EXT);
    }

    protected static File getLastSessionFileForDevice(Remotable r) {
        return new File(r.getDeviceLocalDir(), LAST_SESSION_FILENAME + "." + SESSION_EXT);
    }

    protected static void saveSession(DeviceSession session, File file) throws IOException {
        if (!(session instanceof Impl_DeviceSession)) session = new Impl_DeviceSession(session.getDevice());
        ZipOutputStream zos = null;
        ObjectOutputStream oos = null;
        zos = new ZipOutputStream(new FileOutputStream(file));
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(Deflater.BEST_SPEED);
        zos.putNextEntry(new ZipEntry(SESSION_CONTENT_ENTRY));
        oos = new ObjectOutputStream(zos);
        oos.writeObject(session.getRomAndFlash());
        oos.writeObject(session);
        oos.close();
    }

    protected static void saveFlash(E4Device device) throws IOException {
        File file = new File(device.remote.getDeviceLocalDir(), LAST_FLASH_SESSION_FILENAME + "." + SESSION_EXT);
        Object[] flash = device.presetDB.getFlashSnapshot();
        ZipOutputStream zos = null;
        ObjectOutputStream oos = null;
        zos = new ZipOutputStream(new FileOutputStream(file));
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(Deflater.BEST_SPEED);
        zos.putNextEntry(new ZipEntry(FLASH_SESSION_CONTENT_ENTRY));
        oos = new ObjectOutputStream(zos);
        oos.writeObject(flash);
        oos.close();
    }

    protected static void saveRom(E4Device device) throws IOException {
        File file = new File(device.remote.getDeviceLocalDir(), LAST_ROM_SESSION_FILENAME + "." + SESSION_EXT);
        Object[] rom = device.sampleDB.getRomSnapshot();
        ZipOutputStream zos = null;
        ObjectOutputStream oos = null;
        zos = new ZipOutputStream(new FileOutputStream(file));
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(Deflater.BEST_SPEED);
        zos.putNextEntry(new ZipEntry(FLASH_SESSION_CONTENT_ENTRY));
        oos = new ObjectOutputStream(zos);
        oos.writeObject(rom);
        oos.close();
    }

    protected static Object[] loadFlash(E4Device device) throws ExternalizationException {
        File file = new File(device.remote.getDeviceLocalDir(), LAST_FLASH_SESSION_FILENAME + "." + SESSION_EXT);
        ZipInputStream zis;
        ZipEntry ze;
        ObjectInputStream ois;
        try {
            zis = new ZipInputStream(new FileInputStream(file));
            ze = zis.getNextEntry();
            if (ze == null) throw new ExternalizationException("Invalid flash session file");
            ois = new ObjectInputStream(zis);
            Object o = ois.readObject();
            ois.close();
            if (!(o instanceof Object[])) throw new ExternalizationException("Invalid flash session file");
            return (Object[]) o;
        } catch (FileNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (IOException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        }
    }

    protected static Object[] loadRom(E4Device device) throws ExternalizationException {
        File file = new File(device.remote.getDeviceLocalDir(), LAST_ROM_SESSION_FILENAME + "." + SESSION_EXT);
        ZipInputStream zis;
        ZipEntry ze;
        ObjectInputStream ois;
        try {
            zis = new ZipInputStream(new FileInputStream(file));
            ze = zis.getNextEntry();
            if (ze == null) throw new ExternalizationException("Invalid rom session file");
            ois = new ObjectInputStream(zis);
            Object o = ois.readObject();
            ois.close();
            if (!(o instanceof Object[])) throw new ExternalizationException("Invalid rom session file");
            return (Object[]) o;
        } catch (FileNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (IOException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        }
    }

    public static DeviceSession.RomAndFlash loadDeviceRomAndFlash(File f) throws ExternalizationException {
        ZipInputStream zis;
        ZipEntry ze;
        ObjectInputStream ois;
        try {
            zis = new ZipInputStream(new FileInputStream(f));
            ze = zis.getNextEntry();
            if (ze == null) throw new ExternalizationException("Invalid device session file");
            ois = new ObjectInputStream(zis);
            Object o = ois.readObject();
            ois.close();
            if (!(o instanceof DeviceSession.RomAndFlash)) throw new ExternalizationException("Invalid device session file");
            return (DeviceSession.RomAndFlash) o;
        } catch (FileNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (IOException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        }
    }

    public static DeviceSession loadDeviceSession(File f) throws ExternalizationException {
        ZipInputStream zis;
        ZipEntry ze;
        ObjectInputStream ois;
        try {
            zis = new ZipInputStream(new FileInputStream(f));
            ze = zis.getNextEntry();
            if (ze == null) throw new ExternalizationException("Invalid device session file");
            ois = new ObjectInputStream(zis);
            Object o = ois.readObject();
            o = ois.readObject();
            ois.close();
            if (!(o instanceof Impl_DeviceSession)) throw new ExternalizationException("Invalid device session file");
            return (Impl_DeviceSession) o;
        } catch (FileNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (IOException e) {
            throw new ExternalizationException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new ExternalizationException(e.getMessage());
        }
    }
}
