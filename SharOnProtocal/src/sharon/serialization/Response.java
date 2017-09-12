package sharon.serialization;

import java.io.IOException;
import java.util.Arrays;

public class Response extends Message {

	byte[] id;
    int ttl;
    RoutingService routingService;
    byte[] sourceSharOnAddress;
    byte[] destinationSharOnAddress;
    java.net.InetSocketAddress responseHost;
	
	/**
	 * Constructs new response with deserialization
	 * 
	 * @param in deserialization input source
	 * 
	 * @throws java.io.IOException if I/O problem, including null
	 * @throws BadAttributeValueException if bad data value
	 */
    public Response(MessageInput in)
	         throws IOException,
	                BadAttributeValueException
	{
		super(null);
	}
	
	/**
	 * Constructs new response with user input
	 * 
	 * @param id message id
	 * @param ttl message TTL
	 * @param routingService message routing service
	 * @param sourceSharOnAddress message source address
	 * @param destinationSharOnAddress message destination address
	 * @param responseHost Address and port of responding host
	 * 
	 * @throws BadAttributeValueException if bad or null attribute value
	 */
	public Response(byte[] id,
            int ttl,
            RoutingService routingService,
            byte[] sourceSharOnAddress,
            byte[] destinationSharOnAddress,
            java.net.InetSocketAddress responseHost)
     throws BadAttributeValueException
	{
		super(null);
    }
	
	
	/**
	 * Get address and port of responding host
	 * 
	 * @return responding host address and port
	 */
	public java.net.InetSocketAddress getResponseHost()
	{
		return null;
	}
	
	
	/**
	 * Set address and port of responding host
	 * 
	 * @param responseHost responding host address and port
	 * 
	 * @throws BadAttributeValueException if bad attribute value
	 */
	public void setResponseHost(java.net.InetSocketAddress responseHost)
            throws BadAttributeValueException
    {
		
    }
	
	/**
	 * Get list of results
	 * 
	 * @return result list
	 */
	public java.util.List<Result> getResultList()
	{
		return null;
	}
	
	/**
	 * Add result to list
	 * 
	 * @param result new result to add to result list
	 * 
	 * @throws BadAttributeValueException if result is null or would make result 
	 * 										list too long to encode
	 */
	public void addResult(Result result)
            throws BadAttributeValueException
    {
		
    }
}
