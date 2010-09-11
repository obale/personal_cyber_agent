package to.networld.cyberagent.communication;

import java.util.HashMap;

/**
 * @author Alex Oberhauser
 *
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
			String[] parts = lines[count].split(":");
			if ( parts.length < 2 )
				continue;
			this.headerFields.put(parts[0].trim().toLowerCase(), parts[1].trim().toLowerCase());
		}
	}
	
	public String getCommandLine() { return this.commandLine; }
	public String getCommand() { return this.commandLine.split(" ")[0].toLowerCase(); }
	public String getContentLength() { return this.headerFields.get("content-length"); }
	public String getContentType() { return this.headerFields.get("content-type"); }
}
