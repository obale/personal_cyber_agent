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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import to.networld.cyberagent.common.data.IPPackage;
import to.networld.cyberagent.common.log.Logging;
import to.networld.cyberagent.common.queues.ReasoningQueueHandler;
import to.networld.cyberagent.common.queues.QueueHandler;
import to.networld.cyberagent.communication.common.ComponentConfig;

/**
 * The handler class that takes the response packages from the queue and executes
 * for each package a ResponseHandler thread.
 * 
 * @author Alex Oberhauser
 */
public class Responder extends Thread {
	private static Responder instance = null;
	private boolean running = true;
	private final QueueHandler<IPPackage> inputQueue;
	
	public static Responder newInstance() {
		if ( instance == null ) instance = new Responder();
		return instance;
	}
	
	private Responder() {
		this.setName("Responder");
		this.inputQueue = ReasoningQueueHandler.newInstance();
	}
	
	@Override
	public void run() {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Responder started...");
		while ( this.running ) {
			try {
				IPPackage message = this.inputQueue.takeFirst();
				threadPool.execute(new ResponseHandler(message));
			} catch (InterruptedException e) {
				if ( this.running == false )
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).info("Interrupting reading from queue...");
				else
					Logging.getLogger(ComponentConfig.COMPONENT_NAME).error("Reading from queue was interrupted!");
			}
		}
		threadPool.shutdown();
	}
	
	public void stopResponder() throws IOException, InterruptedException {
		this.running = false;
		Thread.sleep(1500);
		this.interrupt();
	}
}
