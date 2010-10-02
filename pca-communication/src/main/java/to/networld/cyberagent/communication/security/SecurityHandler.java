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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.ws.security.components.crypto.CredentialException;

import to.networld.cyberagent.common.log.Logging;
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
	private Properties config = null;
	private final CredentialHandler credentialHandler;
	
	private SecurityHandler() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		this.credentialHandler = CredentialHandler.newInstance();
		this.config = new Properties();
		this.config.load(SSLServer.class.getResourceAsStream("default.properties"));
	}
	
	public static SecurityHandler newInstance() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if ( instance == null ) instance = new SecurityHandler();
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
	public SSLServerSocket createSSLServerSocket() throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance(this.config.getProperty("ssl.type"));

		KeyStore keystore = KeyStore.getInstance(this.config.getProperty("keystore.type"));
		keystore.load(SSLServer.class.getResourceAsStream(this.config.getProperty("keystore.file")), 
				this.config.getProperty("keystore.password").toCharArray());
		
		KeyManagerFactory keyManFactory = KeyManagerFactory.getInstance(config.getProperty("keymanager.type"));
		keyManFactory.init(keystore, this.config.getProperty("keymanager.password").toCharArray());
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
		CredentialHandler credential = CredentialHandler.newInstance();
		trustManagerFactory.init(credential.getPublicKeyStore());
		
		sslContext.init(keyManFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		
		SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
		
		SSLServerSocket socket = (SSLServerSocket) sslFactory.createServerSocket(new Integer(this.config.getProperty("ssl.port")), 
				10, 
				InetAddress.getByName(this.config.getProperty("ssl.host")));

		try {
			X509Certificate cert = (X509Certificate) keystore.getCertificate(this.config.getProperty("certificate.alias"));
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Used X.509 certificate: " + cert.getIssuerDN());
		} catch (NullPointerException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("Information for SSL certificate not found!");
		}
		
		/*
		 *  XXX, TODO: In productive use "socket.setNeedClientAuth(true);", active this line to assure that ALL clients have to authenticate.
		 */
		socket.setNeedClientAuth(true);
//		socket.setWantClientAuth(true);
		
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
