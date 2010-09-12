package to.networld.cyberagent.security;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Properties;

/**
 * Class that handles the keys. This part is critical for the application, so please
 * be cautious what you change here. This class should be the single part that handles
 * the keys (specially the private keys). 
 * 
 * @author Alex Oberhauser
 */
public class KeyHandler {
	private static KeyHandler instance = null;
	private final KeyStore keystore;
	private final Properties config;
	
	private KeyHandler() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		this.config = new Properties();
		config.load(KeyHandler.class.getResourceAsStream("security.properties"));
		
		this.keystore = KeyStore.getInstance(config.getProperty("security.type"));
		this.keystore.load(KeyHandler.class.getResourceAsStream("/" + config.getProperty("security.keystore")), 
				config.getProperty("security.password").toCharArray());
	}
	
	protected static KeyHandler newInstance() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		if ( instance == null ) instance = new KeyHandler();
		return instance;
	}
	
	protected PublicKey getPublicRootCertificate() throws KeyStoreException { 
		return this.keystore.getCertificate(config.getProperty("security.cacert")).getPublicKey();
	}
}
