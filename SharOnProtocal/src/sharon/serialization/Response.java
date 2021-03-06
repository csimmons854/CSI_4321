/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.serialization;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class Response extends Message {

	private byte[] id;
	private int ttl;
	private RoutingService routingService;
	private byte[] sourceSharOnAddress;
	private byte[] destinationSharOnAddress;
	private java.net.InetSocketAddress responseHost;
	private java.util.List<Result> resultList = new ArrayList<Result>();
	
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
		super(in);
		int payload = 0x0000FFFF;
		int matches = 1;
		int port = 1;
		byte [] address;
		payload = payload & in.getShort();

		matches = in.getByte();
		port = in.getShort() & 0x0000FFFF;
		address = in.getByteArray(4);
		responseHost = new InetSocketAddress(
				InetAddress.getByAddress(address),port);
		for(int i = 0; i < matches; i++)
		{
			addResult(new Result(
					in.getInt() & 0x00000000FFFFFFFFL,
					in.getInt() & 0x00000000FFFFFFFFL,
					in.getString()));
		}
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
		super(id,ttl,routingService,sourceSharOnAddress,destinationSharOnAddress);
		if(responseHost == null)
        {
            throw new BadAttributeValueException("Response host is null",
                    "null");
        }
		this.responseHost = responseHost;
    }
	
	
	/**
	 * Get address and port of responding host
	 * 
	 * @return responding host address and port
	 */
	public java.net.InetSocketAddress getResponseHost()
	{
		return responseHost;
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
    	if(responseHost == null)
		{
			throw new BadAttributeValueException("Response Host is Null","null");
		}
		this.responseHost = responseHost;
    }
	
	/**
	 * Get list of results
	 * 
	 * @return result list
	 */
	public java.util.List<Result> getResultList()
	{
		return resultList;
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
        if(result == null)
        {
            throw new BadAttributeValueException("Result is null","null");
        }
        if((resultList.size() + 1) > 255)
		{
			throw new BadAttributeValueException("Result list is full",result.toString());
		}
		resultList.add(result);
    }

    /**
     * @param o object to be compared
     * @return boolean based on the equivalency of the two objects
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        if (!super.equals(o)) return false;

        Response response = (Response) o;

        if (ttl != response.ttl) return false;
        if (!Arrays.equals(id, response.id)) return false;
        if (routingService != response.routingService) return false;
        if (!Arrays.equals(sourceSharOnAddress, response.sourceSharOnAddress))
            return false;
        if (!Arrays.equals(destinationSharOnAddress, response.destinationSharOnAddress))
            return false;
        if (!responseHost.equals(response.responseHost)) return false;
        return resultList.equals(response.resultList);
    }

    /**
     * @return hashCode of Response
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + responseHost.hashCode();
        result = 31 * result + resultList.hashCode();
        return result;
    }

    /**
     * @return String Representation of result
     */
    @Override
    public String toString() {
        return "Response{" +
                "id=" + Arrays.toString(this.getID()) +
                ", ttl=" + this.getTtl() +
                ", routingService=" + this.getRoutingService() +
                ", sourceSharOnAddress=" + Arrays.toString(this.getSourceAddress()) +
                ", destinationSharOnAddress=" + Arrays.toString(this.getDestinationAddress()) +
                ", responseHost=" + responseHost +
                ", resultList=" + resultList +
                '}';
    }
}
