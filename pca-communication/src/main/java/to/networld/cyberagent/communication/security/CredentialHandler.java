/**
 * PCA Security
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
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

package to.networld.cyberagent.communication.security;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;

import to.networld.cyberagent.common.config.Configuration;
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
	private final Configuration config;
	private final ICredential credential;
	
	private CredentialHandler() throws IOException, GeneralSecurityException {
		this.config = Configuration.newInstance();
		
		this.credential = new Credential(CredentialHandler.class.getResource(this.config.getValue("communication.security.pkcs12file")).getFile(),
				this.config.getValue("communication.security.pkcs12alias"),
				this.config.getPassword("communication.security.pkcs12password"),
				CredentialHandler.class.getResource(this.config.getValue("communication.security.keystore")).getFile(),
				this.config.getPassword("communication.security.password"));
	}
	
	protected static CredentialHandler newInstance() throws IOException, GeneralSecurityException {
		if ( instance == null ) instance = new CredentialHandler();
		return instance;
	}
	
	protected PublicKey getPublicRootCertificate() throws KeyStoreException { 
		return this.credential.getPublicKeystore().getCertificate(this.config.getValue("communication.security.cacert")).getPublicKey();
	}
	
	protected KeyStore getPublicKeyStore() {
		return this.credential.getPublicKeystore();
	}
	
	protected ICredential getCredential() { return this.credential; }
}
