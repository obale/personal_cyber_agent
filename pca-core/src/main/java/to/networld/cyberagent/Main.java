package to.networld.cyberagent;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import to.networld.cyberagent.communication.HttpsServer;
import to.networld.cyberagent.monitoring.Logging;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws CertificateException 
	 * @throws KeyStoreException 
	 * @throws KeyManagementException 
	 * @throws UnrecoverableKeyException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, KeyManagementException, KeyStoreException, CertificateException, UnrecoverableKeyException {
		HttpsServer server = HttpsServer.newInstance();
		Logging.getLogger().info("Starting HTTPS Server....");
		server.start();
	}
}
