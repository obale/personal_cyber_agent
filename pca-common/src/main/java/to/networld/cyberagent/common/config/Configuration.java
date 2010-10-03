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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Central configuration handler.
 * 
 * @author Alex Oberhauser
 */
public class Configuration {
	private static Configuration instance = null;
	private Properties prop = null;
	protected static final String configDir = Configuration.class.getResource("/").toExternalForm().replaceFirst("file:", "");
	
	public static Configuration newInstance() throws IOException {
		if ( instance == null ) instance = new Configuration();
		return instance;
	}
	
	private Configuration() throws IOException {
		this.prop = new Properties();
		this.prop.load(Configuration.class.getResourceAsStream("/default.properties"));
	}
	
	public String getValue(String _key) {
		return this.prop.getProperty(_key);
	}
	
	public String getPassword(String _key) throws GeneralSecurityException, IOException {
		return CryptoUtils.decrypt(this.prop.getProperty(_key));
	}
	
}
