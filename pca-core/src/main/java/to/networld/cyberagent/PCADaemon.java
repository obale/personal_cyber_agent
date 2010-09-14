/**
 * PCA Core
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

package to.networld.cyberagent;

import java.io.IOException;

import to.networld.cyberagent.communication.SSLServer;
import to.networld.cyberagent.reasoning.ReasonerManager;
import to.networld.cyberagent.security.SOAPSecurityManager;

/**
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 *
 */
public class PCADaemon extends Thread {
	private SSLServer server = null;
	private SOAPSecurityManager secManager = null;
	private ReasonerManager resManager = null;
	
	private PCADaemon() {
		this.setName("PCADaemon");
	}
	
	@Override
	public void run() {
		try {
			this.server = SSLServer.newInstance();
			this.server.start();
		
			this.secManager = SOAPSecurityManager.newInstance();
			this.secManager.start();
		
			this.resManager = ReasonerManager.newInstance();
			this.resManager.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void shutdownDaemon() throws IOException, InterruptedException {
		if ( this.server != null )
			this.server.stopServer();
		if ( this.secManager != null )
			this.secManager.stopManager();
		if ( this.resManager != null )
			this.resManager.stopReasoner();
	}

	public static void main(String[] args) throws Exception {
		PCADaemon daemon = new PCADaemon();
		daemon.start();
		
		Thread.sleep(15000);
		
		daemon.shutdownDaemon();
	}
}
