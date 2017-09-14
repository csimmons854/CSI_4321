package sharon.serialization;

import java.io.IOException;
import java.util.Arrays;

public class Message {

    private byte[] id;
    private int ttl;
    private RoutingService routingService;
    private byte[] sourceSharOnAddress;
    private byte[] destinationSharOnAddress;

	
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
            throws BadAttributeValueException {

        if (id == null)
        {
            throw new BadAttributeValueException("ID was null","null");
        }

        if (id.length < 15) {
            throw new BadAttributeValueException("Too Short ID", id.toString());
        }

        if (id.length > 15) {
            throw new BadAttributeValueException("Too Large ID", id.toString());
        }


        if (routingService == null) {
            throw new BadAttributeValueException("RoutingService is null", "null");
        }

        if (!routingService.equals(RoutingService.BREADTHFIRSTBROADCAST) &&
                !routingService.equals(RoutingService.DEPTHFIRSTSEARCH)) {
            throw new BadAttributeValueException("invalid RoutingService",
                    routingService.toString());
        }

        if (ttl > 255) {
            throw new BadAttributeValueException("Too large ttl", "" + ttl);
        }

        if (ttl < 0) {
            throw new BadAttributeValueException("Too small ttl", "" + ttl);
        }

        if (sourceSharOnAddress == null)
        {
            throw new BadAttributeValueException("Null sourceAddress",
                    "null");
        }

        if (sourceSharOnAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large sourceAddress",
                    sourceSharOnAddress.toString());
        }

        if (sourceSharOnAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small sourceAddress",
                    sourceSharOnAddress.toString());
        }

        if (destinationSharOnAddress == null)
        {
            throw new BadAttributeValueException("Null destinationAddress", "null");
        }

        if (destinationSharOnAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large destinationAddress",
                    sourceSharOnAddress.toString());
        }

        if (destinationSharOnAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small destinationAddress",
                    destinationSharOnAddress.toString());
        }

        this.ttl = ttl;
		this.id = id;
		this.routingService = routingService;
		this.destinationSharOnAddress = destinationSharOnAddress;
		this.sourceSharOnAddress = sourceSharOnAddress;
	}
	
	/**
	 * Serialize message
	 * 
	 * @param out serialization output message
	 * 
	 * @throws java.io.IOException if serialization fails
	 */
	public void encode(MessageOutput out)
            throws IOException
    {
          if (out == null)
          {
              throw new IOException();
          }
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
	    if(in == null)
        {
            throw new IOException();
        }
		return null;
    }
	
	/**
	 * Get type of message
	 * 
	 * @return message type
	 */
	public int getMessageType()
	{
	    int type = 0;
		if(this instanceof Search)
        {
            type = 1;
        }

        if(this instanceof Response)
        {
            type = 2;
        }
	    return type;
	}
	
	/**
	 * Get message id
	 * 
	 * @return message id
	 */
	public byte[] getID()
	{

	    return id;
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
        if(id.length < 15)
        {
            throw new BadAttributeValueException("Too Short ID", id.toString());
        }

        if(id.length > 15)
        {
            throw new BadAttributeValueException("Too Large ID", id.toString());
        }

        this.id = id;
	}
	
	/**
	 * Get message ttl
	 * 
	 * @return message ttl
	 */
	public int getTtl()
	{
		return ttl;
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
        if(ttl > 255)
        {
            throw new BadAttributeValueException("Too large ttl", "" + ttl);
        }

        if(ttl < 0)
        {
            throw new BadAttributeValueException("Too small ttl", "" + ttl);
        }

        this.ttl = ttl;
    }
	
	/**
	 * Get message routing service
	 * 
	 * @return routing service
	 */
	public RoutingService getRoutingService()
	{
		return routingService;
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
        if(routingService == null)
        {
            throw new BadAttributeValueException("RoutingService is null",null);
        }

        if(routingService != RoutingService.BREADTHFIRSTBROADCAST ||
                routingService != RoutingService.DEPTHFIRSTSEARCH)
        {
            throw new BadAttributeValueException("invalid RoutingSerive",
                    routingService.toString());
        }

        this.routingService = routingService;
    }
	
	/**
	 * Get source address
	 * 
	 * @return source address
	 */
	public byte[] getSourceAddress()
	{
		return sourceSharOnAddress;
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
        if(sourceSharOnAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large sourceAddress",
                    sourceSharOnAddress.toString());
        }

        if(sourceSharOnAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small sourceAddress",
                    sourceSharOnAddress.toString());
        }

        this.sourceSharOnAddress = sourceAddress;
    }
	
	/**
	 * Get destination address
	 * 
	 * @return destination address
	 */
	public byte[] getDestinationAddress()
	{
		return destinationSharOnAddress;
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
        if(destinationSharOnAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large destinationAddress",
                    sourceSharOnAddress.toString());
        }

        if(destinationSharOnAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small destinationAddress",
                    destinationSharOnAddress.toString());
        }

        this.destinationSharOnAddress = destinationAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (ttl != message.ttl) return false;
        if (!Arrays.equals(id, message.id)) return false;
        if (routingService != message.routingService) return false;
        if (!Arrays.equals(sourceSharOnAddress, message.sourceSharOnAddress))
            return false;
        return Arrays.equals(destinationSharOnAddress, message.destinationSharOnAddress);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(id);
        result = 31 * result + ttl;
        result = 31 * result + (routingService != null ? routingService.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(sourceSharOnAddress);
        result = 31 * result + Arrays.hashCode(destinationSharOnAddress);
        return result;
    }
}
