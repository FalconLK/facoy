import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;

public class ExecuteExternalProcessAndReadInput {
	// start AND ProcessBuilder AND (redirect)|(getInputStream|getOutputStream)
	public static void execute1() throws IOException, InterruptedException {
		//Create Process Builder with Command and Arguments
		ProcessBuilder pb = new ProcessBuilder("myCommand", "myArg1", "myArg2");
		
		//Setup Execution Environment
		Map<String, String> env = pb.environment();
		env.put("VAR1", "myValue");
		env.remove("OTHERVAR");
		env.put("VAR2", env.get("VAR1") + "suffix");
		
		//Setup execution directory
		pb.directory(new File("myDir"));
		
		//Handle Output Streams
		File log = new File("log");
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.appendTo(log));
		
		// Start Process
		Process p = pb.start();
		
		//Wait for process to complete
		p.waitFor();
		
		//Cleanup
		p.destroy();
	}
	// exec AND (getInputStream)|(getErrorStream)
	public static void execute2() throws IOException, InterruptedException {
		// Get Runtime
		Runtime rt = Runtime.getRuntime();
		
		//Execute Process
		Process p = rt.exec("myCommand");
		
		//Redirect external process stdout to this program's stdout
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while((line = in.readLine()) != null)
			System.out.println(line);
		
		//Wait for process to complete and cleanup
		p.waitFor();
		p.destroy();
	}
}
