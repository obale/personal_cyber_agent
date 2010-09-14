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

package to.networld.cyberagent.security;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.ws.security.components.crypto.CredentialException;

import to.networld.soap.security.interfaces.ISecSOAPMessage;

/**
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 */
public class SecSOAPMessageHandler {
	private static SecSOAPMessageHandler instance;
	private final CredentialHandler credentialHandler;
	
	private SecSOAPMessageHandler() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		this.credentialHandler = CredentialHandler.newInstance();
	}
	
	public static SecSOAPMessageHandler newInstance() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if ( instance == null ) instance = new SecSOAPMessageHandler();
		return instance;
	}
	
	public SOAPMessage getSOAPMessage(ISecSOAPMessage _secMessage) throws SOAPException, CredentialException, IOException {
		_secMessage.checkSecurityConstraints(this.credentialHandler.getCredential());
		return _secMessage.getSOAPMessage();
	}
	
}
