package to.networld.cyberagent.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class HttpsServer {
	
	private static HttpsServer instance = null;
	
	private HttpsServer() {}
	
	public static HttpsServer newInstance() {
		if ( instance == null )	instance = new HttpsServer();
		return instance; 
	}
	
	public void start() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException {
		Properties config = new Properties();
		config.load(HttpsServer.class.getResourceAsStream("/properties.cfg"));
		
		SSLContext sslContext = SSLContext.getInstance(config.getProperty("ssl.type"));
		
		KeyStore keystore = KeyStore.getInstance(config.getProperty("keystore.type"));
		keystore.load(HttpsServer.class.getResourceAsStream(config.getProperty("keystore.file")), config.getProperty("keystore.password").toCharArray());
		
		KeyManagerFactory keyManFactory = KeyManagerFactory.getInstance(config.getProperty("keymanager.type"));
		keyManFactory.init(keystore, config.getProperty("keymanager.password").toCharArray());
		sslContext.init(keyManFactory.getKeyManagers(), null, null);
		
		SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
		
		ServerSocket sslServerSocket = sslFactory.createServerSocket(new Integer(config.getProperty("ssl.port")), 10, InetAddress.getByName(config.getProperty("ssl.host")));
		
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		while ( true ) {
			SSLSocket socket = (SSLSocket) sslServerSocket.accept();
			threadPool.execute(new ConnectionHandler(socket));
		}
	}
}
