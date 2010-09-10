/**
 * PCA Communication
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

package to.networld.cyberagent.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLSocket;

import to.networld.cyberagent.monitoring.Logging;

public class ConnectionHandler extends Thread {
	private final SSLSocket socket;
	private final String clientID;

	protected ConnectionHandler(SSLSocket _socket) {
		this.socket = _socket;
		this.clientID = this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}

	@Override
	public void run() {
		Logging.getLogger().debug("[" + this.clientID + "] Connection established!");
		try {
			this.socket.startHandshake();
			Logging.getLogger().debug("[" + this.clientID + "] Handshake successful!");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			int size = Integer.valueOf(reader.readLine());
			
			StringBuffer message = new StringBuffer();
			String line = reader.readLine();						
			
			while ( (message.length() + line.length()) <= size ) {
				message.append(line + "\n");
				line = reader.readLine();
			}
		} catch (IOException e) {
			Logging.getLogger().error("[" + this.clientID + "] " + e.getLocalizedMessage());
		} catch (NullPointerException e) {
			Logging.getLogger().error("[" + this.clientID + "] " + e.getLocalizedMessage());
		} catch (NumberFormatException e) {
			Logging.getLogger().error("[" + this.clientID + "] NumberFormatException : " + e.getLocalizedMessage());
		} finally {
			try {
				this.socket.close();
				Logging.getLogger().debug("[" + clientID + "] Connection closed!");
			} catch (IOException e) {
				Logging.getLogger().error(e.getLocalizedMessage());
			}
		}
	}
}
