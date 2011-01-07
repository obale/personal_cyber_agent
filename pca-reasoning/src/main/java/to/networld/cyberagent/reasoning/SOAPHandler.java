/**
 * PCA Reasoning
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;

import to.networld.cyberagent.common.data.IPPackage;
import to.networld.cyberagent.common.queues.ResponseQueueHandler;
import to.networld.cyberagent.reasoning.common.ComponentConfig;
//import to.networld.cyberagent.reasoning.infogathering.AttachmentHandler;
import to.networld.cyberagent.reasoning.infogathering.MetaInformation;

/**
 * The class that is responsible for the handling of the SOAP message received from
 * the clients.
 * 
 * @author Corneliu Valentin Stanciu
 * @author Alex Oberhauser
 */
public class SOAPHandler extends Thread {
	private SOAPMessage message = null;
	private IPPackage ipp = null;
	
	public SOAPHandler(IPPackage _ipp) {
		this.ipp = _ipp;
		this.message = _ipp.getSOAPMessage();
	}
	
	@Override
	public void run() {
//		new AttachmentHandler(this.message).printInformation();
		try {
			SOAPHeader header = this.message.getSOAPHeader();
			MetaInformation meta = new MetaInformation(header);
			meta.store();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			this.message.writeTo(os);
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).debug(os.toString().replace("\n", "\\n") + "'");
				
			ResponseQueueHandler.newInstance().addLast(this.ipp);
		} catch (IOException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		} catch (SOAPException e) {
			Logger.getLogger(ComponentConfig.COMPONENT_NAME).error(e.getLocalizedMessage());
		}		
	}
}
