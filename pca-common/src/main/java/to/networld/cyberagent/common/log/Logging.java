/**
 * PCA API/Common
 *
 * Copyright (C) 2010 by Networld Project
 * Written by Corneliu Valentin Stanciu <stanciucorneliu@networld.to>
 * Written by Alex Oberhauser <alexoberhauser@networld.to>
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

package to.networld.cyberagent.common.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Logging on the base of log4j for the whole pca. Useful to handle
 * the logging in a central way to be consistent and to have a unique
 * logging file output format that could be parsed easily.
 * 
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 */
public abstract class Logging {

	private static final InputStream configIS = 
		Logging.class.getClassLoader().getResourceAsStream("to/networld/cyberagent/common/log/log4j.properties");
	
	public static Logger getLogger(String _componentName) {
		Logger log = Logger.getLogger("to.networld.cyberagent." + _componentName);
		if ( configIS != null ) {
		    Properties props = new Properties();
		    try {
				props.load(configIS);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    PropertyConfigurator.configure(props);
		}
		return log;
	}
}