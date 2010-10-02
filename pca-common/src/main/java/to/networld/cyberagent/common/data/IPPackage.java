/**
 * pca-common - to.networld.cyberagent.common.data
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Alex Oberhauser <alexoberhauser@networld.to>
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
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

package to.networld.cyberagent.common.data;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.xml.soap.SOAPMessage;

/**
 * "Inter Process Package" is a wrapper around the received SOAPMessage.
 * This class is used to encapsulate all important information that are
 * related to one client.<p/>
 * 
 * Encapsulated parts:<p/>
 * 
 * <ul>
 * 		<li>SOAPMessage</li>
 * 		<li>SSLSocket</li>
 * 		<li>SSLSession</li>
 * </ul>
 * 
 * @author Alex Oberhauser
 */
public class IPPackage {
	private SOAPMessage message = null;
	private SSLSocket sslsocket = null;
	private SSLSession sslsession = null;
	
	public IPPackage(SOAPMessage _message) {
		this.message = _message;
	}
	
	public void setSOAPMessage(SOAPMessage _message) {
		this.message = _message;
	}
	
	public void setSSLSocket(SSLSocket _sslsocket, SSLSession _sslsession) {
		this.sslsocket = _sslsocket;
		this.sslsession = _sslsession;
	}
	
	public SOAPMessage getSOAPMessage() { return this.message; }
	public SSLSocket getSSLSocket() { return this.sslsocket; }
	public SSLSession getSSLSession() { return this.sslsession; }
}
