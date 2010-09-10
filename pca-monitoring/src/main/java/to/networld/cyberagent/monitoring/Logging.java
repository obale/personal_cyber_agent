package to.networld.cyberagent.monitoring;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public abstract class Logging {

	private static final String LOG_CONFIG = Logging.class.getResource("/log4j.cfg").getPath();
	
	public static Logger getLogger() {
		Logger log = Logger.getLogger("pcaLogger");
		PropertyConfigurator.configure(LOG_CONFIG);
		return log;
	}
}