/**
 * PCA Communication
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

package to.networld.cyberagent.communication.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

/**
 * Helper functions that abstracts the lower parts of the SOAP message
 * creation.
 * 
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 * 
 */
public abstract class SOAPBuilder {
	
	/**
	 * Creates a standard SOAP status message.
	 * 
	 * @param _status The status as string.
	 * @return The SOAP message as string representation
	 * @throws SOAPException
	 * @throws IOException
	 */
	public static String createStatusMessage(String _conversationID, String _status) throws SOAPException, IOException {
		SOAPMessage okMessage = MessageFactory.newInstance().createMessage();
		SOAPBody soapBody = okMessage.getSOAPBody();
		SOAPHeader soapHeader = okMessage.getSOAPHeader();
		if ( _conversationID != null ) {
			SOAPElement sessionID = soapHeader.addChildElement(new QName(OntologyHandler.PCA_NETWORK_NS, "session-id", OntologyHandler.PCA_NETWORK_PREFIX));
			sessionID.addTextNode(_conversationID);
		}
		okMessage.getSOAPPart().getEnvelope().addNamespaceDeclaration(OntologyHandler.PCA_NETWORK_PREFIX, OntologyHandler.PCA_NETWORK_NS);
		SOAPElement element = soapBody.addChildElement(new QName(OntologyHandler.PCA_NETWORK_NS, "status", OntologyHandler.PCA_NETWORK_PREFIX));
		element.addTextNode(_status);
		okMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "utf-8");
		okMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		okMessage.saveChanges();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		okMessage.writeTo(outputStream);
		return new String(outputStream.toByteArray()).trim();
	}
	
	/**
	 * Converts a String to a SOAPMessage object.
	 * 
	 * @param _soapMessage The SOAP message to convert.
	 * @return The SOAP message representation as SOAPMessage object.
	 * @throws IOException
	 * @throws SOAPException
	 */
	public static SOAPMessage convertStringToSOAP(String _soapMessage) throws IOException, SOAPException {
		InputStream inputStream = new ByteArrayInputStream(_soapMessage.getBytes());
		return MessageFactory.newInstance().createMessage(null, inputStream);
	}
}
