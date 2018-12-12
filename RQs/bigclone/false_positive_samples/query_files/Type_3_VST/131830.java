import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Uses the ping command native to Linux ( or Mac ) to determine average latency
 * and packetloss
 */
public class LinuxPing extends AbstractPing {

    public LinuxPing(HashMap paramMap) {
        super.target = (String) paramMap.get("target");
        if (paramMap.get("repeats") != null) {
            super.repeats = Integer.parseInt((String) paramMap.get("repeats"));
        }
        if (paramMap.get("packetsize") != null) {
            super.packetSize = (String) paramMap.get("packetsize");
        }
    }

    /**
     * Executes the test and sets the result in a HashMap super.result
     * retrievable with getResult() ( defined in the abstract class AbstractPing )
     */
    public void run() {
        try {
            InetAddress addr = InetAddress.getByName(super.target);
            String line = null;
            Debug.log("Linux Ping test", "Executing command '" + "ping -c" + super.repeats + " -s" + super.packetSize + " " + super.target + "'");
            Process p = Runtime.getRuntime().exec("ping -c" + super.repeats + " -s" + super.packetSize + " " + super.target);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = in.readLine()) != null) {
                Debug.log("Linux Ping test", "    " + line);
                super.output.add(line);
            }
            String packetlossLine = super.output.get(super.output.size() - 2);
            String latencyLine = super.output.get(super.output.size() - 1);
            Debug.log("Linux Ping test", "Probable packetloss line: " + packetlossLine);
            Debug.log("Linux Ping test", "Probable latency line:    " + latencyLine);
            String[] packetlossSplit = packetlossLine.split(",");
            String[] packetlossPercentage = packetlossSplit[2].split(" ");
            super.packetloss = packetlossPercentage[1].substring(0, packetlossPercentage[1].length() - 1);
            Debug.log("Linux Ping test", "Read packetloss: " + packetloss);
            String[] latencySplit = latencyLine.split("/");
            super.avgLatency = latencySplit[4];
            Debug.log("Linux Ping test", "Read latency: " + avgLatency);
            super.result.put("Packetloss", super.packetloss);
            super.result.put("Avg Latency", super.avgLatency);
            Debug.log("Linux Ping test", "Test OK");
        } catch (Exception e) {
            super.result.put("Packetloss", "FAIL");
            super.result.put("Avg Latency", "FAIL");
            Debug.log("Linux Ping test", "Test failed");
        }
    }
}
