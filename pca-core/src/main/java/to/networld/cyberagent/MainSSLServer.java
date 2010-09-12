/**
 * PCA Core
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

package to.networld.cyberagent;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import to.networld.cyberagent.communication.SSLServer;
//import to.networld.cyberagent.security.AccessHandler;

public class MainSSLServer {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, KeyManagementException, KeyStoreException, CertificateException, UnrecoverableKeyException {
		SSLServer server = SSLServer.newInstance();
		server.start();
		
		/*
		 * XXX: The following part is only for testing purpose during the
		 *      development phase. If you are unsure please comment
		 *      the next lines out.
		 */
//		AccessHandler accessHandler = AccessHandler.newInstance();
//		accessHandler.printRootCA();
	}
}
