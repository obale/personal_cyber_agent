package to.networld.cyberagent.reasoning;

/**
 * This class handles the incoming messages. The purpose
 * is only to parse the messages and not to reason about it.
 * 
 * @author Alex Oberhauser
 */
public class SOAPEvaluator {
	private static SOAPEvaluator instance = null;
	
	private SOAPEvaluator() {}
	
	public static SOAPEvaluator newInstance() {
		if ( instance == null ) instance = new SOAPEvaluator();
		return instance;
	}
}
