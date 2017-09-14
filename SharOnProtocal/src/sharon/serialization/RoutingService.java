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
	    int code = -1;
		if(this == BREADTHFIRSTBROADCAST)
		{
		    code = 0;
		}
		if(this == DEPTHFIRSTSEARCH)
		{
			code = 1;
		}

		return code;
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
		if(code == 1)
        {
            return DEPTHFIRSTSEARCH;
        }
        else if(code == 0)
        {
            return BREADTHFIRSTBROADCAST;
        }
        else
        {
            throw new BadAttributeValueException("Invalid Code",("" + code));
        }

    }
}
