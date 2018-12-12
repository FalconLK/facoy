import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPSClient;


public class FTP_ApacheCommonsNet_Samples {

	public FTPClient sample1a(String server, int port, String username, String password) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample1b(String server, String username, String password) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(server);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample1c(String server, int port, String username, String password) throws SocketException, IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setDefaultPort(port);
		ftpClient.connect(server);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample2a(String server, int port, String username, String password) throws SocketException, IOException {
		FTPSClient ftpClient = new FTPSClient();
		ftpClient.connect(server, port);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample2b(String server, String username, String password) throws SocketException, IOException {
		FTPSClient ftpClient = new FTPSClient();
		ftpClient.connect(server);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample2c(String server, int port, String username, String password) throws SocketException, IOException {
		FTPSClient ftpClient = new FTPSClient();
		ftpClient.setDefaultPort(port);
		ftpClient.connect(server);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample3a(String ftpserver, int ftpport, String proxyserver, int proxyport, String username, String password) throws SocketException, IOException {
		FTPHTTPClient ftpClient = new FTPHTTPClient(proxyserver, proxyport);
		ftpClient.connect(ftpserver, ftpport);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample3b(String ftpserver, String proxyserver, int proxyport, String username, String password) throws SocketException, IOException {
		FTPHTTPClient ftpClient = new FTPHTTPClient(proxyserver, proxyport);
		ftpClient.connect(ftpserver);
		ftpClient.login(username, password);
		return ftpClient;
	}
	
	public FTPClient sample3c(String ftpserver, int ftpport, String proxyserver, int proxyport, String username, String password) throws SocketException, IOException {
		FTPHTTPClient ftpClient = new FTPHTTPClient(proxyserver, proxyport);
		ftpClient.setDefaultPort(ftpport);
		ftpClient.connect(ftpserver);
		ftpClient.login(username, password);
		return ftpClient;
	}

}
