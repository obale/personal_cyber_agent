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

package to.networld.cyberagent.reasoning.infogathering;

import java.util.Iterator;

import javax.xml.soap.SOAPMessage;

/**
 * TODO: This class should extract the attachments from the
 *       SOAP message. 
 * 
 * @author Alex Oberhauser
 */
public class AttachmentHandler {
	private final SOAPMessage mesage;
	
	public AttachmentHandler(SOAPMessage _message) {
		this.mesage = _message;
	}
	
	public void printInformation() {
		Iterator<?> iter = this.mesage.getAttachments();
		while ( iter.hasNext() ) {
			Object obj = iter.next();
			System.err.println("AttachementHandler::printEPUBInformation(): " + obj);
		}
	}
	
	public SOAPMessage getMessage() { return this.mesage; }
}
