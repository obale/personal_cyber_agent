/**
 * PCA Reasoning
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

package to.networld.cyberagent.reasoning;

import javax.xml.soap.SOAPMessage;

/**
 * This class handles the incoming messages. The purpose
 * is only to parse the messages and not to reason about it.
 * 
 * @author Alex Oberhauser
 */
public class SOAPEvaluator implements Runnable {
	private final SOAPMessage message;
	
	public SOAPEvaluator(SOAPMessage _message) {
		this.message = _message;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		/*
		 * TODO: Implement here the SOAP message evaluation part. 
		 */
	}
	

}
