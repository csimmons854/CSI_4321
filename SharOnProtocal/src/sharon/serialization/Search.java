package sharon.serialization;

import java.io.IOException;
import java.util.Arrays;

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
        super(in);        int payload = 0x0000FFFF;

        String temp;
		payload = payload & in.getShort();
		temp = in.getString();


		if(temp.length() != payload - 1)
		{
			throw new IOException(Arrays.toString(getID())+ " " +  getTtl() + temp + " " + payload);
		}
		else
        {
            searchString = temp;
        }

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

        if (searchString == null)
        {
            throw new BadAttributeValueException("Search is Null","null");
        }

        if (!searchString.matches("^[a-zA-Z0-9._-]*$"))
        {
            throw new BadAttributeValueException(
                    "Search uses invalid characters", searchString);
        }

        if(searchString.length() > 65535)
        {
            throw new BadAttributeValueException("String is to long",
                    searchString);
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
		if(searchString == null)
		{
			throw new BadAttributeValueException("Search is null","null");
		}

        if (!searchString.matches("^[a-zA-Z0-9._-]+$"))
        {
            throw new BadAttributeValueException(
                    "Search uses invalid characters", searchString);
        }

        if(searchString.length() > 65535)
        {
            throw new BadAttributeValueException("String is to long",
                    searchString);
        }

        this.searchString = searchString;
    }

    /**
     * Test for equivalency of 2 Search objects
     * @param o object to be compared
     * @return boolean
     */
    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Search)) return false;
		if (!super.equals(o)) return false;

		Search search = (Search) o;

		return searchString != null ? searchString.equals(search.searchString)
                : search.searchString == null;
	}

    /**
     * @return hash code of the Search object
     */
    @Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result +
                (searchString != null ? searchString.hashCode() : 0);
		return result;
	}

    /**
     * @return String representation of the Search object
     */
    @Override
    public String toString() {
        return "Search{" +
        "id=" + Arrays.toString(this.getID()) +
                ", ttl=" + this.getTtl() +
                ", routingService=" + this.getRoutingService() +
                ", sourceSharOnAddress=" +
                Arrays.toString(this.getSourceAddress()) +
                ", destinationSharOnAddress=" +
                Arrays.toString(this.getDestinationAddress()) +
                ", searchString='" + searchString + '\'' +
                '}';
    }
}

