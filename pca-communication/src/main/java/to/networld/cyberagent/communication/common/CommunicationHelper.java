/**
 * pca-communication - to.networld.cyberagent.communication.common
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

package to.networld.cyberagent.communication.common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;

/**
 * @author Alex Oberhauser
 */
public abstract class CommunicationHelper {
	
	public static void sendLine(BufferedWriter _writer, String _line) throws IOException {
		_writer.write(_line);
		_writer.newLine();
		_writer.flush();
	}
	
	public static String getClientID(Socket _socket) {
		return _socket.getInetAddress().getHostAddress() + ":" + _socket.getPort();
	}
	
	public static void sendSOAPStatus(BufferedWriter _writer, String _conversationID, String _status) throws IOException {
		try {
			String soapMessage = SOAPBuilder.createStatusMessage(_conversationID, _status);
			sendLine(_writer, "HTTP/1.1 200 OK");
			sendLine(_writer, "Content-Type: application/soap+xml; charset=utf-8");
			sendLine(_writer, "Content-Length: " + soapMessage.length());
			sendLine(_writer, "SOAPAction: \"" + ActionURIHandler.STATUS_ACTION + "\"");
			sendLine(_writer, "");
			sendLine(_writer, soapMessage);
		} catch (SOAPException e) {
			sendLine(_writer, "HTTP/1.1 200 OK");
			sendLine(_writer, "Content-Type: text/plain; charset=utf-8");
			sendLine(_writer, "");
			sendLine(_writer, _status);
		}
	}
	
	public static void closeConnection(Socket _socket, BufferedWriter _writer) {
		try {
			if ( _writer != null )
				_writer.close();
			if ( _socket != null ) {
				_socket.close();
				Logger.getLogger(ComponentConfig.COMPONENT_NAME).debug("[" + CommunicationHelper.getClientID(_socket) + "] Connection closed!");
			}
		} catch (IOException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		}
	}
}
