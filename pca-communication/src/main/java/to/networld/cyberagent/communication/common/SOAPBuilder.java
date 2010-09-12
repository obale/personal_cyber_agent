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
 */
public class SOAPBuilder {
	
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
		return new String(outputStream.toByteArray());
	}
	
	public static SOAPMessage convertStringToSOAP(String _soapMessage) throws IOException, SOAPException {
		InputStream inputStream = new ByteArrayInputStream(_soapMessage.getBytes());
		return MessageFactory.newInstance().createMessage(null, inputStream);
	}
}