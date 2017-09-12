package sharon.serialization;

public enum RoutingService {

	/**
	 * Breadth first broadcast routing service
	 * Depth first search routing service
	 */
	BREADTHFIRSTBROADCAST, DEPTHFIRSTSEARCH;
	
	/**
	 * Get code for routing service
	 * 
	 * @return routing service corresponding to code
	 */
	public int getServiceCode()
	{
		return 0;
	}
	
	/**
	 * Get routing service for given code
	 * 
	 * @param code code of routing service	 
	 * 
	 * @return
	 * @throws BadAttributeValueException if bad code value
	 */
	public static RoutingService getRoutingService(int code)
            throws BadAttributeValueException
    {
		return null;
		
    }
}
