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
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Properties;

import to.networld.soap.security.common.Credential;
import to.networld.soap.security.interfaces.ICredential;

/**
 * Class that handles the keys. This part is critical for the application, so please
 * be cautious what you change here. This class should be the single part that handles
 * the keys (specially the private keys). 
 * 
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 */
public class CredentialHandler {
	private static CredentialHandler instance = null;
	private final Properties config;
	private final ICredential credential;
	
	private CredentialHandler() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		this.config = new Properties();
		this.config.load(CredentialHandler.class.getResourceAsStream("security.properties"));
		
		this.credential = new Credential(CredentialHandler.class.getResource(this.config.getProperty("security.pkcs12file")).getFile(),
				this.config.getProperty("security.pkcs12alias"),
				this.config.getProperty("security.pkcs12password"),
				CredentialHandler.class.getResource(this.config.getProperty("security.keystore")).getFile(),
				this.config.getProperty("security.password"));
	}
	
	protected static CredentialHandler newInstance() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		if ( instance == null ) instance = new CredentialHandler();
		return instance;
	}
	
	protected PublicKey getPublicRootCertificate() throws KeyStoreException { 
		return this.credential.getPublicKeystore().getCertificate(this.config.getProperty("security.cacert")).getPublicKey();
	}
	
	protected ICredential getCredential() { return this.credential; }
}
