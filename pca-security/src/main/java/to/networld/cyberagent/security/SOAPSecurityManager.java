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

import to.networld.cyberagent.common.log.Logging;
import to.networld.cyberagent.common.queues.CommunicationRequestQueueHandler;
import to.networld.cyberagent.common.queues.QueueHandler;
import to.networld.cyberagent.common.queues.SecurityQueueHandler;
import to.networld.cyberagent.security.common.ComponentConfig;
import to.networld.soap.security.interfaces.ISecSOAPMessage;
import to.networld.soap.security.security.SOAPSecMessageFactory;

/**
 * 
 * @author Corneliu Valentin Stanciu
 * @author Alex Oberhauser
 *
 */
public class SOAPSecurityManager extends Thread {
	private static SOAPSecurityManager instance = null;
	private QueueHandler<SOAPMessage> inputQueue = null;
	private QueueHandler<SOAPMessage> outputQueue = null;
	private boolean running = true;

	private SOAPSecurityManager() {
		this.setName("SecurityManager");
		this.outputQueue = SecurityQueueHandler.newInstance();
		this.inputQueue = CommunicationRequestQueueHandler.newInstance();
	}
	
	public static SOAPSecurityManager newInstance() {
		if ( instance == null ) instance = new SOAPSecurityManager();
		return instance;
	}
	
	@Override
	public void run() {
	
		while ( this.running ) {
			try {
				ISecSOAPMessage secMessage = SOAPSecMessageFactory.newInstance(this.inputQueue.takeFirst());
				this.outputQueue.addLast(SecSOAPMessageHandler.newInstance().getSOAPMessage(secMessage));
			} catch (InterruptedException e) {
				if ( this.running == false )
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Interrupting reading from queue...");
				else
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("Reading from queue was interrupted!");
			} catch (KeyStoreException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			} catch (NoSuchAlgorithmException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			} catch (CertificateException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			} catch (SOAPException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			} catch (CredentialException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			} catch (IOException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			}
		}
	}
	
	public void stopManager() throws IOException, InterruptedException {
		this.running = false;
		Thread.sleep(1000);
		this.interrupt();
	}
}