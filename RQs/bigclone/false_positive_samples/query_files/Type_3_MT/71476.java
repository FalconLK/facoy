import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class RegQuery {

    private static final String MEM_QUERY = "mem";

    private static final String IP_CONFIG_QUERY = "ipconfig /all";

    private static final String REGQUERY_UTIL = "reg query ";

    private static final String REGSTR_TOKEN = "REG_SZ";

    private static final String REGDWORD_TOKEN = "REG_DWORD";

    private static final String REGBINARY_TOKEN = "REG_BINARY";

    private static final String REGMULTISZ_TOKEN = "REG_MULTI_SZ";

    private static final String PERSONAL_FOLDER_CMD = REGQUERY_UTIL + "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v Personal";

    private static final String CPU_SPEED_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\" /v ~MHz";

    private static final String CPU_NAME_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0\" /v ProcessorNameString";

    private static final String MOUSE_NAME_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DESCRIPTION\\System\\" + "MultifunctionAdapter\\5\\PointerController\\0\\PointerPeripheral\\0\" /v Identifier";

    private static final String KEYBOARD_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\WOW\\boot.description\" /v keyboard.typ";

    private static final String COMPUTER_NAME_CMD = REGQUERY_UTIL + "\"HKLM\\SYSTEM\\ControlSet001\\Control\\ComputerName\\ActiveComputerName\" /v ComputerName";

    private static final String DEFAULT_USER_NAME_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon\" /v DefaultUserName";

    private static final String OS_NAME_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v ProductName";

    private static final String PRODUCT_ID_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v ProductId";

    private static final String OWNER_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v RegisteredOwner";

    private static final String SERVICE_PACK_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v CSDVersion";

    private static final String INSTALL_DATE_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v InstallDate";

    private static final String PRODUCT_KEY_CMD = REGQUERY_UTIL + "\"HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v DigitalProductId";

    private static final String BIOS_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DESCRIPTION\\System\" /v SystemBiosVersion";

    private static final String MODEM_CMD = REGQUERY_UTIL + "\"HKLM\\SYSTEM\\ControlSet001\\Services\\winachsf\" /v FriendlyName";

    private static final String HDD_CMD = REGQUERY_UTIL + "\"HKEY_LOCAL_MACHINE\\SYSTEM\\ControlSet001\\Services\\Disk\\Enum\" /v 0";

    private static final String SDD1_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DEVICEMAP\\Scsi\\Scsi Port 0\\Scsi Bus 0\\Target Id 0\\Logical Unit Id 0\" /v Identifier";

    private static final String SDD2_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DEVICEMAP\\Scsi\\Scsi Port 1\\Scsi Bus 0\\Target Id 0\\Logical Unit Id 0\" /v Identifier";

    private static final String LPT_CMD = REGQUERY_UTIL + "\"HKLM\\HARDWARE\\DEVICEMAP\\PARALLEL PORTS\" /v \\Device\\Parallel0";

    private static final String COM1_CMD = REGQUERY_UTIL + "\"HKEY_LOCAL_MACHINE\\HARDWARE\\DEVICEMAP\\SERIALCOMM\" /v \\Device\\Serial0";

    private static final String COM3_CMD = REGQUERY_UTIL + "\"HKEY_LOCAL_MACHINE\\HARDWARE\\DEVICEMAP\\SERIALCOMM\" /v Winachsf0";

    private static final String FIREWIRE_CMD = REGQUERY_UTIL + "\"HKLM\\SYSTEM\\ControlSet002\\Services\\NIC1394\" /v DisplayName";

    public static String queryRegistry(String cmd, String token) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String result = reader.getResult();
            int p = result.indexOf(token);
            if (p == -1) {
                return null;
            }
            return result.substring(p + token.length()).trim();
        } catch (Exception e) {
            System.out.println("Query of Registry failed.");
            return null;
        }
    }

    public static String getCurrentUserPersonalFolderPath() {
        String rval = "null";
        if (queryRegistry(PERSONAL_FOLDER_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(PERSONAL_FOLDER_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getCPUName() {
        String rval = "null";
        if (queryRegistry(CPU_NAME_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(CPU_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getCPUSpeed() {
        String temp = queryRegistry(CPU_SPEED_CMD, REGDWORD_TOKEN);
        try {
            String rval = "null";
            if (queryRegistry(CPU_NAME_CMD, REGSTR_TOKEN) != null) {
                rval = Integer.toString((Integer.parseInt(temp.substring("0x".length()), 16) + 1));
            }
            return rval;
        } catch (Exception e) {
            return "null";
        }
    }

    public static String getMAC() {
        try {
            Process process = Runtime.getRuntime().exec(IP_CONFIG_QUERY);
            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String result = reader.getResult();
            String rval = result;
            Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0-9a-fA-F]{2}-[0 -9a-fA-F]{2}-[0-9a-fA-F]{2}");
            Matcher m = macPattern.matcher(result);
            rval = m.group();
            return rval;
        } catch (Exception e) {
            return "null";
        }
    }

    public static String getIP() {
        String rval = "null";
        try {
            InetAddress local = InetAddress.getLocalHost();
            rval = "" + local.getHostAddress();
        } catch (UnknownHostException uhe) {
            rval = "null";
        }
        return rval;
    }

    public static String getMem() {
        try {
            return "null";
        } catch (Exception e) {
            return "null";
        }
    }

    public static String getMouse() {
        String rval = "null";
        if (queryRegistry(MOUSE_NAME_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(MOUSE_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getKeyboard() {
        String rval = "null";
        if (queryRegistry(KEYBOARD_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(KEYBOARD_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getOSName() {
        String rval = "null";
        if (queryRegistry(OS_NAME_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(OS_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getProductID() {
        String rval = "null";
        if (queryRegistry(PRODUCT_ID_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(PRODUCT_ID_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getOwner() {
        String rval = "null";
        if (queryRegistry(OWNER_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(OWNER_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getServicePack() {
        String rval = "null";
        if (queryRegistry(SERVICE_PACK_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(SERVICE_PACK_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getProductKey() {
        String fullPID = "";
        String encodedKey = "";
        if (queryRegistry(PRODUCT_KEY_CMD, REGBINARY_TOKEN) != null) {
            fullPID = queryRegistry(PRODUCT_KEY_CMD, REGBINARY_TOKEN);
        }
        encodedKey = fullPID.substring(104, 134);
        System.out.println(encodedKey);
        final char[] digits = { 'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'M', 'P', 'Q', 'R', 'T', 'V', 'W', 'X', 'Y', '2', '3', '4', '6', '7', '8', '9' };
        final int D_LEN = 29;
        final int S_LEN = 15;
        int[] hexDigitalPID = new int[D_LEN];
        char[] des = new char[D_LEN + 1];
        int i = 0;
        int n = 0;
        int tmp = 0;
        int hn = 0;
        int value = 0;
        String rval = "";
        for (i = 0; i <= 14; ++i) {
            hexDigitalPID[i] = Integer.decode("0x" + encodedKey.substring(i * 2, (i * 2) + 2)).intValue();
        }
        for (i = 0; i <= 14; ++i) {
            System.out.println("hexDigitalPID[" + i + "] = " + hexDigitalPID[i]);
        }
        for (i = D_LEN - 1; i >= 0; --i) {
            if (((i + 1) % 6) == 0) {
                des[i] = '-';
            } else {
                hn = 0;
                for (n = S_LEN - 1; n >= 0; --n) {
                    hn = ((hn << 8) + hexDigitalPID[n]);
                    hexDigitalPID[n] = (hn / 24);
                    hn = (hn % 24);
                }
                des[i] = digits[hn];
            }
        }
        des[D_LEN] = '\n';
        for (i = 0; des[i] != '\n'; ++i) {
            rval += des[i];
        }
        return rval;
    }

    public static String getComputerName() {
        String rval = "null";
        if (queryRegistry(COMPUTER_NAME_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(COMPUTER_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getUserName() {
        String rval = "null";
        if (queryRegistry(DEFAULT_USER_NAME_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(DEFAULT_USER_NAME_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getBios() {
        String rval = "null";
        if (queryRegistry(BIOS_CMD, REGMULTISZ_TOKEN) != null) {
            rval = queryRegistry(BIOS_CMD, REGMULTISZ_TOKEN);
        }
        return rval;
    }

    public static String getModem() {
        String rval = "null";
        if (queryRegistry(MODEM_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(MODEM_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getHDD() {
        String rval = "null";
        if (queryRegistry(HDD_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(HDD_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getSDD1() {
        String rval = "null";
        if (queryRegistry(SDD1_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(SDD1_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getSDD2() {
        String rval = "null";
        if (queryRegistry(SDD2_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(SDD2_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getLPT() {
        String rval = "null";
        if (queryRegistry(LPT_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(LPT_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getCOM1() {
        String rval = "null";
        if (queryRegistry(COM1_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(COM1_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String getCOM3() {
        String rval = "null";
        if (queryRegistry(COM3_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(COM3_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    public static String get1394() {
        String rval = "null";
        if (queryRegistry(FIREWIRE_CMD, REGSTR_TOKEN) != null) {
            rval = queryRegistry(FIREWIRE_CMD, REGSTR_TOKEN);
        }
        return rval;
    }

    static class StreamReader extends Thread {

        private InputStream is;

        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) sw.write(c);
            } catch (IOException e) {
                ;
                ;
                ;
            }
        }

        String getResult() {
            return sw.toString();
        }
    }
}
