/**
 * PCA Communication
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
 * Written by Alex Oberhauser <alexoberhauser@networld.to>
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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import to.networld.cyberagent.common.log.Logging;
import to.networld.cyberagent.communication.common.ComponentConfig;
import to.networld.cyberagent.communication.security.SecurityHandler;

/**
 * SSL server implementation that uses part of the HTTP/1.1 specification.
 * 
 * @author Corneliu Stanciu
 * @author Alex Oberhauser
 */
public class SSLServer extends Thread {
	private static SSLServer instance = null;
	private final SecurityHandler secHandler;
	private SSLServerSocket sslServerSocket = null;
	private Properties config = null;
	private boolean running = true;
	
	private SSLServer() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		this.setName("SSLServer");
		this.secHandler = SecurityHandler.newInstance();
		this.config = new Properties();
		this.config.load(SSLServer.class.getResourceAsStream("default.properties"));
	}
	
	public static SSLServer newInstance() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		if ( instance == null )	instance = new SSLServer();
		return instance; 
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
	 * @throws InterruptedException 
	 */
	public void startServer() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException, InterruptedException {
		this.running = true;
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		this.sslServerSocket = this.secHandler.createSSLServerSocket();		
		
		Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Listening on https://" + this.config.getProperty("ssl.host") + ":"
				+ this.config.getProperty("ssl.port") + "...");
		
		Responder.newInstance().start();
		
		while ( this.running ) {
			try {
				SSLSocket socket = (SSLSocket) this.sslServerSocket.accept();
				threadPool.execute(new ConnectionHandler(socket));
			} catch (IOException e) {
				if ( this.running == false)
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Closing SSL server socket...");
				else 
					throw new IOException();
			}
		}
		threadPool.shutdown();
	}
	
	/**
	 * Stops the currently running server.
	 * 
	 * @throws IOException 
	 */
	public void stopServer() throws IOException {
		this.sslServerSocket.close();
		this.running = false;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			this.startServer();
		} catch (KeyManagementException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (UnrecoverableKeyException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (NoSuchAlgorithmException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (KeyStoreException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (CertificateException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).info(e.getLocalizedMessage());
		} catch (IOException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (InterruptedException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		}
	}
}
