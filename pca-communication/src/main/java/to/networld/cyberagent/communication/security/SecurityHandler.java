/**
 * PCA Security
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

package to.networld.cyberagent.communication.security;

import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.apache.ws.security.components.crypto.CredentialException;

import to.networld.cyberagent.common.config.Configuration;
import to.networld.cyberagent.communication.SSLServer;
import to.networld.cyberagent.communication.common.ComponentConfig;
import to.networld.soap.security.interfaces.ISecSOAPMessage;

/**
 * 1. Handles the SSL socket creation and the authentication mechanism (X509 certificates).
 * 2. Handles the checks of security constraints of incoming SOAP messages.
 * 
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 */
public class SecurityHandler {
	private static SecurityHandler instance;
	private Configuration config = null;
	private final CredentialHandler credentialHandler;
	
	private SecurityHandler() throws IOException, GeneralSecurityException {
		this.credentialHandler = CredentialHandler.newInstance();
		this.config = Configuration.newInstance();
	}
	
	public static SecurityHandler newInstance() throws IOException, GeneralSecurityException {
		if ( instance == null ) instance = new SecurityHandler();
		return instance;
	}
	
	/**
	 * Creates a server side SSL socket.
	 * 
	 * @return The SSLServerSocket that are listening to the port specified in the configuration file.
	 * @throws IOException
	 * @throws GeneralSecurityException 
	 */
	public SSLServerSocket createSSLServerSocket() throws IOException, GeneralSecurityException {
		SSLContext sslContext = SSLContext.getInstance(this.config.getValue("communication.ssl.type"));

		KeyStore keystore = KeyStore.getInstance(this.config.getValue("communication.keystore.type"));
		keystore.load(SSLServer.class.getResourceAsStream(this.config.getValue("communication.keystore.file")), 
				this.config.getPassword("communication.keystore.password").toCharArray());
		
		KeyManagerFactory keyManFactory = KeyManagerFactory.getInstance(config.getValue("communication.keymanager.type"));
		keyManFactory.init(keystore, this.config.getPassword("communication.keymanager.password").toCharArray());
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this.config.getValue("communication.keymanager.type"));
		CredentialHandler credential = CredentialHandler.newInstance();
		trustManagerFactory.init(credential.getPublicKeyStore());
		
		sslContext.init(keyManFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		
		SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
		
		SSLServerSocket socket = (SSLServerSocket) sslFactory.createServerSocket(new Integer(this.config.getValue("communication.ssl.port")), 
				10, 
				InetAddress.getByName(this.config.getValue("communication.ssl.host")));

		try {
			X509Certificate cert = (X509Certificate) keystore.getCertificate(this.config.getValue("communication.certificate.alias"));
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("Used X.509 certificate: " + cert.getIssuerDN());
		} catch (NullPointerException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error("Information for SSL certificate not found!");
		}
		
		/*
		 *  TODO: In productive use "socket.setNeedClientAuth(true);", active this line to assure that ALL clients have to authenticate.
		 */
//		socket.setNeedClientAuth(true);
		socket.setWantClientAuth(true);
		
		return socket;
	}
	
	/**
	 * Checks the security constraints and decrypts the message.
	 * 
	 * TODO: Check the X.509 certificate, that was used as authorization in the communication layer, against
	 *       the X.509 certificate that was used for the signing part.
	 * 
	 * @param _secMessage The secure message received over the wire.
	 * @return The clear text and checked SOAP message.
	 * @throws SOAPException
	 * @throws CredentialException
	 * @throws IOException
	 */
	public SOAPMessage getSOAPMessage(ISecSOAPMessage _secMessage) throws SOAPException, CredentialException, IOException {
		_secMessage.checkSecurityConstraints(this.credentialHandler.getCredential());
		return _secMessage.getSOAPMessage();
	}
}
