package to.networld.cyberagent.communication.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
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
	public static String createStatusMessage(String _status) throws SOAPException, IOException {
		SOAPMessage okMessage = MessageFactory.newInstance().createMessage();
		SOAPBody soapBody = okMessage.getSOAPBody();
		SOAPElement element = soapBody.addChildElement(new QName("status"));
		element.addTextNode(_status);
		okMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		okMessage.saveChanges();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		okMessage.writeTo(outputStream);
		return new String(outputStream.toByteArray());
	}
}
