package to.networld.cyberagent.security;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.ws.security.components.crypto.CredentialException;

import to.networld.soap.security.interfaces.ISecSOAPMessage;

/**
 * @author Alex Oberhauser
 */
public class SecSOAPMessageHandler {
	private static SecSOAPMessageHandler instance;
	private final CredentialHandler credentialHandler;
	
	private SecSOAPMessageHandler() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		this.credentialHandler = CredentialHandler.newInstance();
	}
	
	public static SecSOAPMessageHandler newInstance() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if ( instance == null ) instance = new SecSOAPMessageHandler();
		return instance;
	}
	
	public SOAPMessage getSOAPMessage(ISecSOAPMessage _secMessage) throws SOAPException, CredentialException, IOException {
		_secMessage.checkSecurityConstraints(this.credentialHandler.getCredential());
		return _secMessage.getSOAPMessage();
	}
	
}
