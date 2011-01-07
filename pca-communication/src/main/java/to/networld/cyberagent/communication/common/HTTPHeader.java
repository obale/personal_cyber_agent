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

import java.util.HashMap;

/**
 * Representation of a HTTP header.
 * 
 * @author Alex Oberhauser
 * @author Corneliu Valentin Stanciu
 */
public class HTTPHeader {
	private final StringBuffer rawHeader;
	private String commandLine = null;
	private HashMap<String, String> headerFields = new HashMap<String, String>();
	
	public HTTPHeader(StringBuffer _rawHeader) {
		this.rawHeader = _rawHeader;
		this.parseHeader();
	}
	
	private void parseHeader() {
		String [] lines = this.rawHeader.toString().split("\n");
		this.commandLine = lines[0];
		for ( int count=1; count < lines.length; count++ ) {
			String[] parts = lines[count].split(":", 2);
			if ( parts.length < 2 )
				continue;
			this.headerFields.put(parts[0].trim().toLowerCase(), parts[1].trim());
		}
	}
	
	public String getCommandLine() { return this.commandLine; }
	public String getCommand() { return this.commandLine.split(" ")[0].toLowerCase(); }
	
	public String getHeaderValue(String _value) { return this.headerFields.get(_value.toLowerCase()); }
	
	public String getContentLength() { return this.headerFields.get("content-length"); }
	public String getContentType() { return this.headerFields.get("content-type"); }
	public String getUserAgent() { return this.headerFields.get("user-agent"); }
	public String getSOAPAction() { return this.headerFields.get("soapaction"); }
	public String getHost() { return this.headerFields.get("host"); }
	public String getCacheControl() { return this.headerFields.get("cache-control"); }
	
	@Override
	public String toString() {
		return this.rawHeader.toString();
	}
	
}
