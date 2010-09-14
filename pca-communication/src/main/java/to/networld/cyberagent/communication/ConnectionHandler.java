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
import java.util.UUID;

import javax.net.ssl.SSLSocket;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import to.networld.cyberagent.common.log.Logging;
import to.networld.cyberagent.common.queues.CommunicationRequestQueueHandler;
import to.networld.cyberagent.communication.common.ActionURIHandler;
import to.networld.cyberagent.communication.common.ComponentConfig;
import to.networld.cyberagent.communication.common.SOAPBuilder;

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
	
	private void sendSOAPStatus(String _conversationID, String _status) throws IOException {
		try {
			String soapMessage = SOAPBuilder.createStatusMessage(_conversationID, _status);
			this.sendLine("HTTP/1.1 200 OK");
			this.sendLine("Content-Type: application/soap+xml; charset=utf-8");
			this.sendLine("Content-Length: " + soapMessage.length());
			this.sendLine("SOAPAction: \"" + ActionURIHandler.STATUS_ACTION + "\"");
			this.sendLine("");
			this.sendLine(soapMessage);
		} catch (SOAPException e) {
			this.sendLine("HTTP/1.1 200 OK");
			this.sendLine("Content-Type: text/plain; charset=utf-8");
			this.sendLine("");
			this.sendLine(_status);
		}
		
	}
	
	private void sendLine(String _line) throws IOException {
		this.writer.write(_line);
		this.writer.newLine();
		this.writer.flush();
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
		Logging.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + this.clientID + "] Connection established!");
		try {
			this.socket.startHandshake();
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + this.clientID + "] Handshake successful!");
			
			/*
			 * Parse the received HTTP header.
			 */
			String hline = null;
			StringBuffer rawHeader = new StringBuffer();
			while ( !(hline = this.reader.readLine()).equals("") ) {
				rawHeader.append(hline + "\n");
			}
			HTTPHeader header = new HTTPHeader(rawHeader);
			if ( header.getSOAPAction() != null ) {
				int size = Integer.valueOf(header.getContentLength());

				/*
				 * Parse the received request.
				 */
				StringBuffer request = this.readRequest(size);
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + this.clientID + "] Message of type '" + 
						header.getContentType() +  "' received from client '" + 
						header.getUserAgent() + "': '" + request.toString().replace("\n", "\\n") + "'");
				
				/**
				 * TODO: The StringBuffer response is the message from the client.
				 *       Handle that message!!! For example call the classes that checks
				 *       the security constraints.
				 */
				
				try {
					SOAPMessage soapRequest = SOAPBuilder.convertStringToSOAP(request.toString());
					CommunicationRequestQueueHandler.newInstance().addLast(soapRequest);
				} catch (SOAPException e) {
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
				}
				
				/*
				 * Send OK to the client.
				 */
				this.sendSOAPStatus(UUID.randomUUID().toString(), "OK");
			} else {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] Unauthorized access with User-Agent '" + header.getUserAgent() + "'");
				this.sendLine("HTTP/1.1 412 Precondition Failed");
				this.sendLine("Content-Type: text/plain; charset=utf-8");
				this.sendLine("");
				this.sendLine("Are you sure that you know what you are doing?");
			}
		} catch (IOException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] " + e.getLocalizedMessage());
		} catch (NullPointerException e) {
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] " + e.getLocalizedMessage());
		} catch (NumberFormatException e) {
			try {
				this.sendLine("HTTP/1.1 411 Length Required");
				this.sendLine("");
			} catch (IOException e1) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] " + e1.getLocalizedMessage());
			}
			Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + this.clientID + "] NumberFormatException : " + e.getLocalizedMessage());
		} finally {
			try {
				if ( reader != null )
					this.reader.close();
				if ( writer != null )
					this.writer.close();
				this.socket.close();
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + clientID + "] Connection closed!");
			} catch (IOException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("[" + clientID + "] " + e.getLocalizedMessage());
			}
		}
	}
}
