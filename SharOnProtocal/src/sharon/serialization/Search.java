package sharon.serialization;

import java.io.IOException;

public class Search extends Message {
    private String searchString;
	
	/**
	 * Constructs a new search with deserialization
	 * 
	 * @param in deserialization input source
	 * 
	 * @throws java.io.IOException if I/O problem, including null
	 * @throws BadAttributeValueException if bad data value
	 */
	public Search(MessageInput in)
			throws IOException,
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
            String searchString)
     throws BadAttributeValueException
	{
		super(id,ttl,routingService,sourceAddress,destinationAddress);

        if (!searchString.matches("^[a-zA-Z0-9._-]*$"))
        {
            throw new BadAttributeValueException("Search uses invalid characters", searchString);
        }

        if (searchString == null)
        {
            throw new BadAttributeValueException("Search is Null",searchString);
        }


        this.searchString = searchString;

	}
	
	/**
	 * Get search string
	 * 
	 * @return search string
	 */
	public String getSearchString()
	{
		return searchString;
	}
	
	/**
	 * Set search string
	 * 
	 * @param searchString new search string
	 * @throws BadAttributeValueException if bad search value
	 */
	public void setSearchString(String searchString)
            throws BadAttributeValueException
    {
        if (!searchString.matches("^[a-zA-Z0-9._-]+$"))
        {
            throw new BadAttributeValueException("Search uses invalid characters", searchString);
        }

        if(searchString == null)
        {
            throw new BadAttributeValueException("Search is null",searchString);
        }

        this.searchString = searchString;
    }
}
