/**
 * pca-communication - to.networld.cyberagent.communication
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

package to.networld.cyberagent.communication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import to.networld.cyberagent.common.data.IPPackage;
import to.networld.cyberagent.communication.common.ComponentConfig;
import to.networld.cyberagent.communication.common.CommunicationHelper;

/**
 * Receives a IPPackage (Inter Process Package) with the needed information to
 * send a response to the client.
 * 
 * @author Alex Oberhauser
 */
public class ResponseHandler extends Thread {
	private IPPackage ipp = null;
	
	public ResponseHandler(IPPackage _ipp) {
		this.ipp = _ipp;
	}
	
	@Override
	public void run() {
		BufferedWriter out = null;
		SSLSocket socket = null;
		try {
			socket = this.ipp.getSSLSocket();
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			/*
			 * XXX: Implement here the sending of the response message.
			 *      Maybe the response message should be read out from the IPPackage, to be able
			 *      to return the response from the Reasoner.
			 */
			CommunicationHelper.sendSOAPStatus(out, UUID.randomUUID().toString(), "Message stored successfully!");
		} catch (IOException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} finally {
			CommunicationHelper.closeConnection(socket, out);
		}
	}
}
