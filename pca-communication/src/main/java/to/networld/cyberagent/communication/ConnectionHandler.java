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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.apache.ws.security.components.crypto.CredentialException;

import to.networld.cyberagent.common.data.IPPackage;
import to.networld.cyberagent.common.queues.CommunicationRequestQueueHandler;
import to.networld.cyberagent.communication.common.ComponentConfig;
import to.networld.cyberagent.communication.common.CommunicationHelper;
import to.networld.cyberagent.communication.common.HTTPHeader;
import to.networld.cyberagent.communication.common.SOAPBuilder;
import to.networld.cyberagent.communication.security.SecurityHandler;
import to.networld.soap.security.interfaces.ISecSOAPMessage;
import to.networld.soap.security.security.SOAPSecMessageFactory;

/**
 * The class that handles a single connection to the client. The purpose of this
 * part is only to handle the raw stream and to assure the right identity.
 * 
 * @author Corneliu Stanciu
 * @author Alex Oberhauser
 */
public class ConnectionHandler extends Thread {
	private final SSLSocket socket;
	private final String clientID;
	private final BufferedReader reader;
	private final BufferedWriter writer;

	protected ConnectionHandler(SSLSocket _socket) throws IOException {
		this.socket = _socket;
		this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		this.clientID = this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}
	
	/**
	 * Read n chars from the request. n should be the size of the
	 * message.
	 * 
	 * @param _size The size of the received message.
	 * @return The client request.
	 * @throws IOException
	 */
	private StringBuffer readRequest(int _size) throws IOException {
		StringBuffer message = new StringBuffer();
		char ch;
		while ( (ch = (char)this.reader.read()) != -1 ) {
			message.append(ch);
			if ( message.length() >= _size ) break;
		}
		return message;
	}

	@Override
	public void run() {
		Logger.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + this.clientID + "] Connection established!");
		try {
			SSLSession session = this.socket.getSession();
			try {
				X509Certificate clientCert = (X509Certificate) session.getPeerCertificates()[0];
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + this.clientID + "] Session started for user '" + 
						clientCert.getSubjectDN() + "'");
			} catch (SSLPeerUnverifiedException e) {
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] Authentication failed!");
			}
			
			String hline = null;
			StringBuffer rawHeader = new StringBuffer();
			while ( !(hline = this.reader.readLine()).equals("") ) {
				rawHeader.append(hline + "\n");
			}
			HTTPHeader header = new HTTPHeader(rawHeader);
			
			if ( header.getSOAPAction() != null ) {
				int size = Integer.valueOf(header.getContentLength());

				StringBuffer request = this.readRequest(size);
				
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + this.clientID + "] Message of type '" + 
						header.getContentType() +  "' received from client '" + 
						header.getUserAgent() + "': '" + request.toString().replace("\n", "\\n") + "'");
				
				try {
					SOAPMessage soapRequest = SOAPBuilder.convertStringToSOAP(request.toString());
					ISecSOAPMessage secMessage = SOAPSecMessageFactory.newInstance(soapRequest);
					soapRequest = SecurityHandler.newInstance().getSOAPMessage(secMessage);
					
					IPPackage ipp = new IPPackage(soapRequest);
					ipp.setSSLSocket(this.socket, session);
					CommunicationRequestQueueHandler.newInstance().addLast(ipp);
				} catch (SOAPException e) {
					Logger.getLogger(ComponentConfig.COMPONENT_NAME).info(e.getLocalizedMessage());
				} catch (CredentialException e) {
					Logger.getLogger(ComponentConfig.COMPONENT_NAME).info(e.getLocalizedMessage());
				} catch (GeneralSecurityException e) {
					Logger.getLogger(ComponentConfig.COMPONENT_NAME).info(e.getLocalizedMessage());
				}
			} else {
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("[" + this.clientID + "] Unknown request with User-Agent '" + header.getUserAgent() + "'");
				CommunicationHelper.sendLine(this.writer, "HTTP/1.1 412 Precondition Failed");
				CommunicationHelper.sendLine(this.writer, "Content-Type: text/plain; charset=utf-8");
				CommunicationHelper.sendLine(this.writer, "");
				CommunicationHelper.sendLine(this.writer, "Are you sure that you know what you are doing?");
			}
		} catch (IOException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] " + e.getLocalizedMessage());
		} catch (NullPointerException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] " + e.getLocalizedMessage());
		} catch (NumberFormatException e) {
			try {
				CommunicationHelper.sendLine(this.writer, "HTTP/1.1 411 Length Required");
				CommunicationHelper.sendLine(this.writer, "");
			} catch (IOException e1) {
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] " + e1.getLocalizedMessage());
			}
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("[" + this.clientID + "] NumberFormatException : " + e.getLocalizedMessage());
		}
	}
}
