package sharon.serialization;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
	    //initialize variables to be used to actually decode a messageInput
	    int type;
	    byte [] id;
	    int ttl;
	    int routingCode;
        byte [] source;
        byte [] destination;
        String searchString;
        int payload = 0x0000FFFF;
        int matches = 1;
        int port = 1;
        byte [] address;
        InetSocketAddress socketAddress;
        Message msg;

	    if(in == null)
        {
            throw new IOException("Null Value");
        }

        //get type of message
        type = in.getByte();

        if(type == 1) //decode Search Message
        {
            id = in.getByteArray(15);

            ttl = in.getByte();

            //convert to unsigned long
            if(ttl < 0)
            {
                ttl = ttl & 0x000000FF;
            }

            routingCode =in.getByte();
            source = in.getByteArray(5);
            destination = in.getByteArray(5);

            //create a int from two bytes
            payload = payload & in.getShort();
            searchString = in.getString();


            if(searchString.length() != payload - 1)
            {
                throw new IOException("Invalid Payload " + searchString + " " + payload);
            }

            msg = new Search(id,ttl,
                    RoutingService.getRoutingService(routingCode),
                    source,destination,searchString);

        }
        else if(type == 2) //response object
        {

            id = in.getByteArray(15);

            ttl = in.getByte();
            if(ttl < 0)
            {
                ttl = ttl & 0x000000FF;
            }
            routingCode = in.getByte();
            source = in.getByteArray(5);
            destination = in.getByteArray(5);
            payload = payload & in.getShort();
            matches = in.getByte();
            port = in.getShort();
            address = in.getByteArray(4);
            //System.err.println(payload + " " + Arrays.toString(address));
            socketAddress = new InetSocketAddress(
                    InetAddress.getByAddress(address),port);
            if(routingCode > 1)
            {
                throw new IOException(type + " " + Arrays.toString(id) +
                        " " + ttl + " " + routingCode + " " + Arrays.toString(source));
            }

            msg = new Response(id,ttl,
                    RoutingService.getRoutingService(routingCode),source,
                    destination,socketAddress);
            for(int i = 0; i < matches; i++)
            {
                ((Response)msg).addResult(new Result(
                        in.getInt() & 0x00000000FFFFFFFFL,
                        in.getInt() & 0x00000000FFFFFFFFL,
                        in.getString()));
            }
        }
        else
        {
            throw new BadAttributeValueException("Invalid Message Type " + type,
                    "" + type);
        }

		return msg;
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
	    if(id == null)
        {
            throw new BadAttributeValueException("ID is Null", "null");
        }

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
        System.err.println(sourceAddress.length);
        if(sourceAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large sourceAddress",
                    sourceAddress.toString());
        }

        if(sourceAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small sourceAddress",
                    sourceAddress.toString());
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
        if(destinationAddress.length > 5)
        {
            throw new BadAttributeValueException("Too large destinationAddress",
                    sourceSharOnAddress.toString());
        }

        if(destinationAddress.length < 5)
        {
            throw new BadAttributeValueException("Too small destinationAddress",
                    destinationAddress.toString());
        }

        this.destinationSharOnAddress = destinationAddress;
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
