import java.io.*;

public class CommandWaitThread implements Runnable {

    private String command = "";

    private Process process = null;

    private boolean finished = false;

    private StringBuffer out = new StringBuffer();

    private StringBuffer err = new StringBuffer();

    private Integer exitCode = null;

    public CommandWaitThread(String com) {
        command = com;
    }

    public String getCommand() {
        return command;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public String getInputStream() {
        return out.toString();
    }

    public String getErrorStream() {
        return err.toString();
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void stop() {
        try {
            if (process != null) process.destroy();
        } catch (Exception e) {
        }
        this.finished = true;
    }

    public void run() {
        try {
            if (this.finished == false) {
                Runtime runner = Runtime.getRuntime();
                process = runner.exec(command);
                new ProcessInputReaderThread(process.getInputStream(), out);
                new ProcessInputReaderThread(process.getErrorStream(), err);
                exitCode = new Integer(process.waitFor());
            }
        } catch (Exception e) {
            err.append("Error running command.\n");
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            PrintWriter errData = new PrintWriter(ba);
            e.printStackTrace(errData);
            errData.flush();
            err.append(ba.toString());
            e.printStackTrace();
        }
        this.finished = true;
    }

    private class ProcessInputReaderThread implements Runnable {

        InputStream stream = null;

        StringBuffer data = null;

        public ProcessInputReaderThread(InputStream in, StringBuffer buff) {
            stream = in;
            data = buff;
            new Thread(Thread.currentThread().getThreadGroup(), this, this.getClass().getName()).start();
        }

        public void run() {
            try {
                int ch;
                while ((ch = stream.read()) != -1) {
                    data.append((char) ch);
                }
            } catch (Exception e) {
            }
        }
    }
}
