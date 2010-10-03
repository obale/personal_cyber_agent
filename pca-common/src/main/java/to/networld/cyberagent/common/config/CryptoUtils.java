/**
 * pca-common - to.networld.cyberagent.common.config
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

package to.networld.cyberagent.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Alex Oberhauser
 */
public class CryptoUtils {
	  protected static final String AES = "AES";
	  private static final File keyFile = new File(Configuration.configDir, "properties.key");
	  
	  public static String encrypt(String _value) throws GeneralSecurityException, IOException  {
		  if (!keyFile.exists()) {
			  KeyGenerator keyGen = KeyGenerator.getInstance(CryptoUtils.AES);
			  keyGen.init(128);
			  SecretKey sk = keyGen.generateKey();
			  FileWriter fw = new FileWriter(keyFile);
			  fw.write(byteArrayToHexString(sk.getEncoded()));
			  fw.flush();
			  fw.close();
		  }
	    
		  SecretKeySpec sks = getSecretKeySpec();
		  Cipher cipher = Cipher.getInstance(CryptoUtils.AES);
		  cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
		  byte[] encrypted = cipher.doFinal(_value.getBytes());
		  return byteArrayToHexString(encrypted);
	  }
	  
	  /**
	   * decrypt a value  
	   * @throws GeneralSecurityException 
	   * @throws IOException 
	   */
	  public static String decrypt(String message) throws GeneralSecurityException, IOException {
		  SecretKeySpec sks = getSecretKeySpec();
		  Cipher cipher = Cipher.getInstance(CryptoUtils.AES);
		  cipher.init(Cipher.DECRYPT_MODE, sks);
		  byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
		  return new String(decrypted);
	  }
	  
	  
	  
	  private static SecretKeySpec getSecretKeySpec() throws NoSuchAlgorithmException, IOException {
		  byte [] key = readKeyFile(keyFile);
		  SecretKeySpec sks = new SecretKeySpec(key, CryptoUtils.AES);
		  return sks;
	  }

	  private static byte [] readKeyFile(File keyFile) throws FileNotFoundException {
		  Scanner scanner = new Scanner(keyFile).useDelimiter("\\Z");
		  String keyValue = scanner.next();
		  scanner.close();
		  return hexStringToByteArray(keyValue);
	  }

	  private static String byteArrayToHexString(byte[] b){
		  StringBuffer sb = new StringBuffer(b.length * 2);
		  for (int i = 0; i < b.length; i++){
			  int v = b[i] & 0xff;
			  if (v < 16) {
				  sb.append('0');
			  }
			  sb.append(Integer.toHexString(v));
		  }
		  return sb.toString().toUpperCase();
	  }

	  private static byte[] hexStringToByteArray(String s) {
		  byte[] b = new byte[s.length() / 2];
		  for (int i = 0; i < b.length; i++){
			  int index = i * 2;
			  int v = Integer.parseInt(s.substring(index, index + 2), 16);
			  b[i] = (byte)v;
		  }
		  return b;
	  }

}