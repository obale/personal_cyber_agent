/**
 * PCA Security
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

package to.networld.cyberagent.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import javax.security.cert.Certificate;

/**
 * Component that assures that the client has authorization to access the system
 * or special components. For this purpose a certificate authentication is needed.
 * 
 * @author Alex Oberhauser
 */
public class AccessHandler {
	private static AccessHandler instance = null;
	private final KeyHandler keyHandler;
	
	private AccessHandler() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		this.keyHandler = KeyHandler.newInstance();
	}
	
	public static AccessHandler newInstance() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		if ( instance == null ) instance = new AccessHandler();
		return instance;
	}
	
	/**
	 * Checks if the certificate was signed with the root certificate. If the
	 * verification fails the methods throws one of the exceptions below. In
	 * general that means that the client is not authorized by the framework.
	 * 
	 * @param _clientCert The certificate to check.
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws KeyStoreException
	 * @throws javax.security.cert.CertificateException
	 */
	public void verifyCertificate(Certificate _clientCert) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, KeyStoreException, javax.security.cert.CertificateException {
		_clientCert.verify(this.keyHandler.getPublicRootCertificate());
	}
	
	public void printRootCA() {
		try {
			System.out.println(this.keyHandler.getPublicRootCertificate());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
}
