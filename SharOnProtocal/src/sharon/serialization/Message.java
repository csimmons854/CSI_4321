package sharon.serialization;

import java.io.IOException;
import java.util.Arrays;

public class Message {

	byte[] id;
	int ttl;
	RoutingService routingService;
	byte[] sourceSharOnAddress;
	byte[] destinationSharOnAddress;

	
	/**
	 * Constructs a message
	 * 
	 * @param in deserialization input source
	 */
	Message(MessageInput in)
	{
		
	}
	
	/**
	 * Constructs a Message
	 * 
	 * @param id message id
	 * @param invalid message ttl
	 * @param routingService message routing service 
	 * @param sourceSharOnAddress message source address
	 * @param destinationSharOnAddress message destination address
	 */
	Message(byte[] id, int ttl, RoutingService routingService, 
			byte[] sourceSharOnAddress, byte[] destinationSharOnAddress)
	{
		
	}
	
	/**
	 * Serialize message
	 * 
	 * @param out serialization output message
	 * 
	 * @throws java.io.IOException if serialization fails
	 */
	public void encode(MessageOutput out)
            throws java.io.IOException
    {
	          
    }
	
	/**
	 * Deserializes message from input source
	 * 
	 * @param in deserialization input source
	 * 
	 * @return
	 * @throws java.io.IOException if deserialization fails
	 * @throws BadAttributeValueException if bad attribute value
	 */
	public static Message decode(MessageInput in)
            throws IOException,
                   BadAttributeValueException
	{
		return null;
    }
	
	/**
	 * Get type of message
	 * 
	 * @return message type
	 */
	public int getMessageType()
	{
		return 0;
	}
	
	/**
	 * Get message id
	 * 
	 * @return message id
	 */
	public byte[] getID()
	{
		return null;
	}
	
	/**
	 * Set message id
	 * 
	 * @param id new ID
	 * 
	 * @throws BadAttributeValueException if bad or null attribute
	 */
	public void setID(byte[] id)
	           throws BadAttributeValueException
	{
		
	}
	
	/**
	 * Get message ttl
	 * 
	 * @return message ttl
	 */
	public int getTtl()
	{
		return 0;
	}
	
	/**
	 * Get message routing service
	 * 
	 * @param ttl new ttl
	 * 
	 * @throws BadAttributeValueException if bad ttl value
	 */
	public void setTtl(int ttl)
            throws BadAttributeValueException
    {
		
    }
	
	/**
	 * Get message routing service
	 * 
	 * @return routing service
	 */
	public RoutingService getRoutingService()
	{
		return null;
	}
	
	/**
	 * Set message routing service
	 * 
	 * @param routingService new routing service
	 * 
	 * @throws BadAttributeValueException if null routing service value
	 */
	public void setRoutingService(RoutingService routingService)
            throws BadAttributeValueException
    {
		
    }
	
	/**
	 * Get source address
	 * 
	 * @return source address
	 */
	public byte[] getSourceAddress()
	{
		return null;
	}
	
	/**
	 * Set source address
	 * 
	 * @param sourceAddress source address
	 * 
	 * @throws BadAttributeValueException if bad or null address value
	 */
	public void setSourceAddress(byte[] sourceAddress)
            throws BadAttributeValueException
    {
		
    }
	
	/**
	 * Get destination address
	 * 
	 * @return destination address
	 */
	public byte[] getDestinationAddress()
	{
		return null;
	}
	
	/**
	 * Set destination address
	 * 
	 * @param destinationAddress destination address
	 * 
	 * @throws BadAttributeValueException if bad or null address value
	 */
	public void setDestinationAddress(byte[] destinationAddress)
            throws BadAttributeValueException
    {
		
    }
}
