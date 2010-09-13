package to.networld.cyberagent.communication.common;

/**
 * Interface for the action URIs that are used in the SOAPAction field in the
 * HTTP header.
 * 
 * @author Alex Oberhauser
 */
public interface ActionURIHandler {
	public static final String ACTION_PREFIX = OntologyHandler.PCA_ACTIONS_NS;
	
	public static final String REQUEST_ACTION = ACTION_PREFIX + "Request";
	public static final String RESPONSE_ACTION = ACTION_PREFIX + "Response";
	public static final String STATUS_ACTION = ACTION_PREFIX + "Status";
}
