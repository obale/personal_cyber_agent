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

/**
 * @author Alex Oberhauser
 */
public class MainSSLClient {

	public static String createRequest() throws SOAPException, IOException {
		SOAPMessage message = MessageFactory.newInstance().createMessage();
		message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "utf-8");
		message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

		SOAPHeader header = message.getSOAPHeader();
		header.addNamespaceDeclaration("foaf", "http://xmlns.com/foaf/0.1/");
		header.addNamespaceDeclaration("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

		SOAPElement element = header.addHeaderElement(new QName("http://xmlns.com/foaf/0.1/", "Agent", "foaf"));
		element.addAttribute(new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource", "rdf"),
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
//		 System.setProperty("javax.net.ssl.trustStorePassword", "1234567890");

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket(config.getProperty("ssl.host"), new Integer(config.getProperty("ssl.port")));

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		socket.startHandshake();

		String messageToSend = createRequest();

		writer.write("POST / HTTP/1.1");
		writer.newLine();
		writer.write("Content-Length: " + messageToSend.length());
		writer.newLine();
		writer.write("Content-Type: application/soap+xml; charset=utf-8");
		writer.newLine();
		writer.newLine();
		writer.write(messageToSend);
		writer.flush();
		 
		String header;
		System.out.println("--- HEADER   ---");
		while ( !(header = reader.readLine()).equalsIgnoreCase("")) {
			System.out.println(header);
		}
		 
		System.out.println("--- Response ---");
		String line;
		while ( (line = reader.readLine()) != null ) {
			System.out.println(line);
		}
		 
		writer.close();
		socket.close();
	}
}
