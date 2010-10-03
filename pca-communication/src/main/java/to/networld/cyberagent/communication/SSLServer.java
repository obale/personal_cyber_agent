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
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import to.networld.cyberagent.common.config.Configuration;
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
	private final Responder responder;
	private SSLServerSocket sslServerSocket = null;
	private Configuration config = null;
	private boolean running = true;
	
	private SSLServer() throws IOException, GeneralSecurityException {
		this.setName("SSLServer");
		this.secHandler = SecurityHandler.newInstance();
		this.responder = Responder.newInstance();
		this.config = Configuration.newInstance();
	}
	
	public static SSLServer newInstance() throws IOException, GeneralSecurityException {
		if ( instance == null )	instance = new SSLServer();
		return instance; 
	}
	
	/**
	 * Start the server that are listen to a SSL socket. 
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws GeneralSecurityException 
	 */
	public void startServer() throws IOException, InterruptedException, GeneralSecurityException {
		this.running = true;
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		this.sslServerSocket = this.secHandler.createSSLServerSocket();		
		this.responder.start();
		
		Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("Listening on https://" + this.config.getValue("communication.ssl.host") + ":"
				+ this.config.getValue("communication.ssl.port") + "...");
		
		while ( this.running ) {
			try {
				SSLSocket socket = (SSLSocket) this.sslServerSocket.accept();
				threadPool.execute(new ConnectionHandler(socket));
			} catch (IOException e) {
				if ( this.running == false)
					Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("Closing SSL server socket...");
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
	 * @throws InterruptedException 
	 */
	public void stopServer() throws IOException, InterruptedException {
		this.responder.stopResponder();
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
		} catch (NoSuchAlgorithmException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (IOException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (InterruptedException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (GeneralSecurityException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		}
	}
}
