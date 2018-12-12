package fi.hip.gb.disk.transport.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import fi.hip.gb.disk.conf.Config;
import fi.hip.gb.disk.transport.Transport;

/**
 * @author mjpitka2
 * 
 */
public class FtpTransport implements Transport {

    private Log log = LogFactory.getLog(FtpTransport.class);

    private String endpointURL = "";

    /**
     * Sets Ftp connection parameters to the endpoint
     * 
     * @param endpointURL
     *            Endopoint for client connection
     */
    public FtpTransport(String endpointURL) {
        if (endpointURL.charAt(endpointURL.length() - 1) == '/') {
            endpointURL = endpointURL.substring(0, endpointURL.length() - 1);
        }
        if (endpointURL.startsWith("ftp://")) {
            endpointURL = endpointURL.substring(6);
        }
        this.endpointURL = endpointURL;
    }

    public void put(File fileToPut) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(this.endpointURL, Config.getFtpPort());
            log.debug("Ftp put reply: " + ftp.getReplyString());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("Ftp put server refused connection.");
            }
            if (!ftp.login("anonymous", "")) {
                ftp.logout();
                throw new IOException("FTP: server wrong passwd");
            }
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            InputStream input = new FileInputStream(fileToPut);
            if (ftp.storeFile(fileToPut.getName(), input) != true) {
                ftp.logout();
                input.close();
                throw new IOException("FTP put exception");
            }
            input.close();
            ftp.logout();
        } catch (Exception e) {
            log.error("Ftp client exception: " + e.getMessage(), e);
            throw new IOException(e.getMessage());
        }
    }

    public void get(File fileToGet) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            int reply = 0;
            ftp.connect(this.endpointURL, Config.getFtpPort());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("Ftp get server refused connection.");
            }
            if (!ftp.login("anonymous", "")) {
                ftp.logout();
                throw new IOException("FTP: server wrong passwd");
            }
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            OutputStream output = new FileOutputStream(fileToGet.getName());
            if (ftp.retrieveFile(fileToGet.getName(), output) != true) {
                ftp.logout();
                output.close();
                throw new IOException("FTP get exception, maybe file not found");
            }
            ftp.logout();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public void delete(String fileToDelete) throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            int reply = 0;
            ftp.connect(this.endpointURL, Config.getFtpPort());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("Ftp delete server refused connection.");
            }
            if (!ftp.login("anonymous", "")) {
                ftp.logout();
                throw new IOException("FTP: server wrong passwd");
            }
            ftp.enterLocalPassiveMode();
            log.debug("Deleted: " + ftp.deleteFile(fileToDelete));
            ftp.logout();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public boolean exists(String fileToCheck) throws IOException {
        FTPClient ftp = new FTPClient();
        boolean found = false;
        try {
            int reply = 0;
            ftp.connect(this.endpointURL, Config.getFtpPort());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new IOException("Ftp exists server refused connection.");
            }
            if (!ftp.login("anonymous", "")) {
                ftp.logout();
                throw new IOException("FTP: server wrong passwd");
            }
            ftp.enterLocalPassiveMode();
            if (ftp.listNames(fileToCheck) != null) {
                found = true;
            }
            ftp.logout();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return found;
    }
}
