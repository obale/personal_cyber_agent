/**
 * PCA Reasoning
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

package to.networld.cyberagent.reasoning;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import to.networld.cyberagent.common.log.Logging;
import to.networld.cyberagent.common.queues.QueueHandler;
import to.networld.cyberagent.common.queues.SecurityQueueHandler;
import to.networld.cyberagent.reasoning.common.ComponentConfig;

/**
 * 
 * @author Corneliu Valentin Stanciu
 * @author Alex Oberhauser
 */
public class ReasonerManager extends Thread{
	private static ReasonerManager instance = null;
	private final QueueHandler<SOAPMessage> inputQueue;
//	private final QueueHandler<SOAPMessage> outputQueue = null;
	private boolean running = true;
	
	public static ReasonerManager newInstance() {
		if ( instance == null ) instance = new ReasonerManager();
		return instance;
	}
	
	public ReasonerManager() {
		this.setName("Reasoner");
		this.inputQueue = SecurityQueueHandler.newInstance();
//		this.outputQueue = ReasoningQueueHandler.newInstance();
	}
	
	@Override
	public void run() {
		
		while ( this.running ) {
			try {
				/**
				 * TODO: Implementing the reasoning part.
				 */
				SOAPMessage message = this.inputQueue.takeFirst();
				
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				message.writeTo(os);
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).debug(os.toString().replace("\n", "\\n") + "'");
			} catch (InterruptedException e) {
				if ( this.running == false )
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Interrupting reading from queue...");
				else
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("Reading from queue was interrupted!");
			} catch (SOAPException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			} catch (IOException e) {
				Logging.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
			}
		}
	}
	
	public void stopReasoner() throws IOException, InterruptedException {
		this.running = false;
		Thread.sleep(1500);
		this.interrupt();
	}
}