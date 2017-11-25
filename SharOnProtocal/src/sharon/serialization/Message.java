/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.serialization;

import java.io.IOException;
import java.util.Arrays;

public abstract class Message {
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
	Message(MessageInput in) throws IOException, BadAttributeValueException {
	    if(in == null)
        {
            throw new IOException("Null Value");
        }
	    this.setID(in.getByteArray(15));
        System.out.println(Arrays.toString(id));

        int tempTtl = in.getByte();
        //convert to unsigned long
        if(tempTtl < 0)
        {
            tempTtl = tempTtl & 0x000000FF;
        }
        this.setTtl(tempTtl);
        this.setRoutingService(RoutingService.getRoutingService(in.getByte()));

        this.setSourceAddress(in.getByteArray(5));
        this.setDestinationAddress(in.getByteArray(5));

    }
	
	/**
	 * Constructs a Message
	 * 
	 * @param id message id
	 * @param ttl message ttl
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
            throw new BadAttributeValueException("Too Short ID", Arrays.toString(id));
        }

        if (id.length > 15) {
            throw new BadAttributeValueException("Too Large ID", Arrays.toString(id));
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

        if (sourceSharOnAddress == null) {
            throw new BadAttributeValueException("Null sourceAddress",
                    "null");
        }

        if (sourceSharOnAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large sourceAddress",
                    Arrays.toString(sourceSharOnAddress));
        }

        if (sourceSharOnAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small sourceAddress",
                    Arrays.toString(sourceSharOnAddress));
        }

        if (destinationSharOnAddress == null)
        {
            throw new BadAttributeValueException("Null destinationAddress", "null");
        }

        if (destinationSharOnAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large destinationAddress",
                    Arrays.toString(sourceSharOnAddress));
        }

        if (destinationSharOnAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small destinationAddress",
                    Arrays.toString(destinationSharOnAddress));
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

        if(this.getMessageType() == 1) //encode Search Message
        {
            out.writeByte(1);
            out.writeByteArray(this.getID());
            out.writeByte(this.ttl);
            out.writeByte(this.getRoutingService().getServiceCode());
            out.writeByteArray(this.sourceSharOnAddress);
            out.writeByteArray(this.destinationSharOnAddress);
            out.writeIntTo2Bytes(((Search)this).getSearchString().length() + 1);
            out.writeString(((Search)this).getSearchString());
        }
        else if(this.getMessageType() == 2) //encode Response Message
        {
            out.writeByte(2);
            out.writeByteArray(this.getID());
            out.writeByte(this.ttl);
            out.writeByte(this.getRoutingService().getServiceCode());
            out.writeByteArray(this.sourceSharOnAddress);
            out.writeByteArray(this.destinationSharOnAddress);

            //calculate the length of the ResultList
            int resultListLength = 0;
            for(int i = 0; i < ((Response)this).getResultList().size(); i++)
            {
                resultListLength +=
                        ((Response)this).getResultList().get(i).length() + 1;
            }

            out.writeIntTo2Bytes(7 + resultListLength);
            out.writeByte(((Response)this).getResultList().size());
            out.writeIntTo2Bytes(((Response)this).getResponseHost().getPort());
            out.writeByteArray(
                ((Response)this).getResponseHost().getAddress().getAddress());
            //encode each individual result of ResultList
            for(int i = 0; i < ((Response)this).getResultList().size(); i++)
            {
                ((Response)this).getResultList().get(i).encode(out);
            }

        }
        else
            throw new IOException();
    }
	
	/**
	 * Deserializes message from input source
	 * 
	 * @param in deserialization input source
	 * 
	 * @return deserialize message
	 * @throws java.io.IOException if deserialization fails
	 * @throws BadAttributeValueException if bad attribute value
	 */
	public static Message decode(MessageInput in)
            throws IOException,
                   BadAttributeValueException
	{
	    //initialize variables to be used to actually decode a messageInput
	    int type;

	    if(in == null)
        {
            throw new IOException("Null Value");
        }

        //get type of message
        type = in.getByte();

        if(type == 1) //decode Search Message
        {
            return new Search(in);
        }
        else if(type == 2) //response object
        {
            return new Response(in);
        }
        else
        {
            throw new BadAttributeValueException("Invalid Message Type " + type,
                    "" + type);
        }
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
	    return id.clone();
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
	    if(id == null)
        {
            throw new BadAttributeValueException("ID is Null", "null");
        }

        if(id.length < 15)
        {
            throw new BadAttributeValueException("Too Short ID", Arrays.toString(id));
        }

        if(id.length > 15)
        {
            throw new BadAttributeValueException("Too Large ID", Arrays.toString(id));
        }

        this.id = id.clone();
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
            throw new BadAttributeValueException("RoutingService is null","null");
        }

        if(routingService != RoutingService.BREADTHFIRSTBROADCAST &&
                routingService != RoutingService.DEPTHFIRSTSEARCH)
        {
            throw new BadAttributeValueException("invalid RoutingService "
                    + routingService.toString(),
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
		return sourceSharOnAddress.clone();
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
        if(sourceAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large sourceAddress",
                    Arrays.toString(sourceAddress));
        }

        if(sourceAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small sourceAddress",
                    Arrays.toString(sourceAddress));
        }

        this.sourceSharOnAddress = sourceAddress.clone();
    }
	
	/**
	 * Get destination address
	 * 
	 * @return destination address
	 */
	public byte[] getDestinationAddress()
	{
		return destinationSharOnAddress.clone();
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
        if(destinationAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large destinationAddress",
                    Arrays.toString(sourceSharOnAddress));
        }

        if(destinationAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small destinationAddress",
                    Arrays.toString(destinationAddress));
        }

        this.destinationSharOnAddress = destinationAddress.clone();
    }

    /**
     * Test for equivalency of two Message objects
     *
     * @param o object to be compared
     * @return a boolean based on the equivalency
     */
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

    @Override
    public String toString() {
        return "Message{" +
                "id=" + Arrays.toString(id) +
                ", ttl=" + ttl +
                ", routingService=" + routingService +
                ", sourceSharOnAddress=" + Arrays.toString(sourceSharOnAddress) +
                ", destinationSharOnAddress=" + Arrays.toString(destinationSharOnAddress) +
                '}';
    }
}
