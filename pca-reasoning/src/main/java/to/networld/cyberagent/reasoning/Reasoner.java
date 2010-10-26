/**
 * pca-reasoning - to.networld.cyberagent.reasoning
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

package to.networld.cyberagent.reasoning;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import to.networld.cyberagent.common.data.IPPackage;
import to.networld.cyberagent.common.queues.RequestQueueHandler;
import to.networld.cyberagent.common.queues.QueueHandler;
import to.networld.cyberagent.reasoning.common.ComponentConfig;

/**
 * The handler class that triggers the reasoning for each SOAP message. The implementation
 * is similar than the SSL server.
 * 
 * @author Alex Oberhauser
 */
public class Reasoner extends Thread {
	private static Reasoner instance = null;
	private boolean running = true;
	private final QueueHandler<IPPackage> inputQueue;
	
	public static Reasoner newInstance() {
		if ( instance == null ) instance = new Reasoner();
		return instance;
	}
	
	private Reasoner() {
		this.setName("Reasoner");
		this.inputQueue = RequestQueueHandler.newInstance();
	}
	
	@Override
	public void run() {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("Reasoner started...");
		while ( this.running ) {
			try {
				IPPackage message = this.inputQueue.takeFirst();
				threadPool.execute(new SOAPHandler(message));
			} catch (InterruptedException e) {
				if ( this.running == false )
					Logger.getLogger(ComponentConfig.COMPONENT_NAME).info("Interrupting reading from queue...");
				else
					Logger.getLogger(ComponentConfig.COMPONENT_NAME).error("Reading from queue was interrupted!");
			}
		}
		threadPool.shutdown();
	}
	
	public void stopReasoner() throws IOException, InterruptedException {
		this.running = false;
		Thread.sleep(1500);
		this.interrupt();
	}
}
