package org.example.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import to.networld.cyberagent.communication.SSLServer;
import to.networld.cyberagent.communication.common.OntologyHandler;

/**
 * @author Alex Oberhauser
 */
public class MainSSLClient {
	
	public static void printStringWithPrefix(String _prefix, String _string) {
		String[] lines = _string.split("\n");
		for ( int count=0; count < lines.length; count++ ) {
			System.out.println(_prefix + lines[count]);
		}
	}

	public static String createRequest() throws SOAPException, IOException {
		SOAPMessage message = MessageFactory.newInstance().createMessage();
		message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "utf-8");
		message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

		SOAPHeader header = message.getSOAPHeader();
		header.addNamespaceDeclaration(OntologyHandler.FOAF_PREFIX, OntologyHandler.FOAF_NS);
		header.addNamespaceDeclaration(OntologyHandler.RDF_PREFIX, OntologyHandler.RDF_NS);

		SOAPElement element = header.addHeaderElement(new QName(OntologyHandler.FOAF_NS, "Agent", OntologyHandler.FOAF_PREFIX));
		element.addAttribute(new QName(OntologyHandler.RDF_NS, "resource", OntologyHandler.RDF_PREFIX),
				"http://devnull.networld.to/foaf.rdf#me");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		message.writeTo(outputStream);
		return new String(outputStream.toByteArray());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		config.load(SSLServer.class.getResourceAsStream("default.properties"));

		URL trustedURL = SSLServer.class.getResource(config.getProperty("keystore.trusted"));
		System.setProperty("javax.net.ssl.trustStore", trustedURL.getPath());
//		System.setProperty("javax.net.ssl.trustStorePassword", "1234567890");

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket(config.getProperty("ssl.host"), new Integer(config.getProperty("ssl.port")));

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		socket.startHandshake();

		String messageToSend = createRequest();
		StringBuffer headerToSend = new StringBuffer();
		headerToSend.append("POST / HTTP/1.1\r\n");
		headerToSend.append("User-Agent: PCA DEV Client v0.1-SNAPSHOT\r\n");
		headerToSend.append("Content-Length: " + messageToSend.length() + "\r\n");
		headerToSend.append("Content-Type: application/soap+xml; charset=utf-8\r\n");
		headerToSend.append("SOAPAction: \"" + OntologyHandler.PCA_ACTIONS_NS + "Request\"\r\n");
		
		writer.write(headerToSend.toString());
		writer.newLine();
		writer.write(messageToSend);
		writer.flush();
		
		printStringWithPrefix(">> ", headerToSend.toString());
		System.out.println(">> ");
		printStringWithPrefix(">> ", messageToSend);
		
		System.out.println();
		
		String header;
		while ( !(header = reader.readLine()).equalsIgnoreCase("")) {
			System.out.println("<< " + header);
		}
		 
		System.out.println("<< ");
		String line;
		while ( (line = reader.readLine()) != null ) {
			System.out.println("<< " + line);
		}
		 
		writer.close();
		socket.close();
	}
}
