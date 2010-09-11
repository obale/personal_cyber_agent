/**
 * PCA Communication
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Alex Oberhauser <oberhauseralex@networld.to>
 * All Rights Reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>
 */

package to.networld.cyberagent.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import to.networld.cyberagent.monitoring.Logging;

/**
 * 
 * @author Corneliu Stanciu
 * @author Alex Oberhauser
 *
 */
public class SSLServer {
	private static SSLServer instance = null;
	private Properties config = null;
	
	private SSLServer() throws IOException {
		this.config = new Properties();
		config.load(SSLServer.class.getResourceAsStream("default.properties"));
	}
	
	public static SSLServer newInstance() throws IOException {
		if ( instance == null )	instance = new SSLServer();
		return instance; 
	}
	
	/**
	 * Creates a server side SSL socket.
	 * 
	 * @return The SSLServerSocket that are listening to the port specified in the configuration file.
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	private SSLServerSocket createSSLServerSocket() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance(this.config.getProperty("ssl.type"));
		
		KeyStore keystore = KeyStore.getInstance(this.config.getProperty("keystore.type"));
		keystore.load(SSLServer.class.getResourceAsStream(this.config.getProperty("keystore.file")), 
				this.config.getProperty("keystore.password").toCharArray());
		
		KeyManagerFactory keyManFactory = KeyManagerFactory.getInstance(config.getProperty("keymanager.type"));
		keyManFactory.init(keystore, this.config.getProperty("keymanager.password").toCharArray());
		sslContext.init(keyManFactory.getKeyManagers(), null, null);
		
		SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
		
		SSLServerSocket socket = (SSLServerSocket) sslFactory.createServerSocket(new Integer(this.config.getProperty("ssl.port")), 
				10, 
				InetAddress.getByName(this.config.getProperty("ssl.host")));
		
		try {
			X509Certificate cert = (X509Certificate) keystore.getCertificate(this.config.getProperty("certificate.alias"));
			Logging.getLogger().info("SSL Server X.509 certificate: " + cert.getIssuerDN());
		} catch (NullPointerException e) {
			Logging.getLogger().error("Information for SSL certificate not found!");
		}
		
		return socket;
	}
	
	/**
	 * Start the server that are listen to a SSL socket. 
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 */
	public void start() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		SSLServerSocket sslServerSocket = this.createSSLServerSocket();
		Logging.getLogger().info("SSL Server listening on socket://" + this.config.getProperty("ssl.host") + ":"
				+ this.config.getProperty("ssl.port") + "...");
		while ( true ) {
			SSLSocket socket = (SSLSocket) sslServerSocket.accept();
			threadPool.execute(new ConnectionHandler(socket));
		}
	}
}
