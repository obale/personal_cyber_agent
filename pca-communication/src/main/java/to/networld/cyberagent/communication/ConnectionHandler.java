package to.networld.cyberagent.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLSocket;

import to.networld.cyberagent.monitoring.Logging;


public class ConnectionHandler extends Thread {
	
	private final SSLSocket socket;

	public ConnectionHandler(SSLSocket _socket) {
		this.socket = _socket;
	}

	@Override
	public void run() {
		Logging.getLogger().debug("Connection from " + this.socket.getPort());
		try {
			this.socket.startHandshake();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			int size = Integer.valueOf(reader.readLine());
			
			StringBuffer message = new StringBuffer();
			String line = reader.readLine();						
			
			while ( (message.length() + line.length()) <= size ) {
				message.append(line + "\n");
				line = reader.readLine();
			}
			
			System.out.println(message);
			this.socket.close();
		} catch (IOException e) {
			Logging.getLogger().error(e.getLocalizedMessage());
		}
	}
}
