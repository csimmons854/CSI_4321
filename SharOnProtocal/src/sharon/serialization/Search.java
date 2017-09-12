package sharon.serialization;

import java.io.IOException;

public class Search extends Message {
	
	/**
	 * Constructs a new search with deserialization
	 * 
	 * @param in deserialization input source
	 * 
	 * @throws java.io.IOException if I/O problem, including null
	 * @throws BadAttributeValueException if bad data value
	 */
	public Search(MessageInput in)
			throws java.io.IOException,
		    		BadAttributeValueException
	{
		super(null);
	}
		              
	/**
	 * Constructs new search with user input
	 * 
	 * @param id message id
	 * @param ttl message TTL
	 * @param routingService message routing service
	 * @param sourceAddress message source address
	 * @param destinationAddress message destination address
	 * @param searchString search string
	 * 
	 * @throws BadAttributeValueException if bad or null data value
	 */
	public Search(byte[] id,
            int ttl,
            RoutingService routingService,
            byte[] sourceAddress,
            byte[] destinationAddress,
            java.lang.String searchString)
     throws BadAttributeValueException
	{
		super(null);
	}
	
	/**
	 * Get search string
	 * 
	 * @return search string
	 */
	public java.lang.String getSearchString()
	{
		return null;
	}
	
	/**
	 * Set search string
	 * 
	 * @param searchString new search string
	 * @throws BadAttributeValueException if bad search value
	 */
	public void setSearchString(java.lang.String searchString)
            throws BadAttributeValueException
    {
		
    }
}
