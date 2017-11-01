/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package mvn.serialization;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

import static mvn.serialization.ErrorType.*;
import static mvn.serialization.PacketType.*;

public class Packet {

    private PacketType type;
    private ErrorType error;
    private int sessionID;
    private Set<InetSocketAddress> addrList = new HashSet<>();

    /**
     * Construct new packet from byte array
     * @param buf byte [] to create packet
     * @throws IOException if byte [] is too long/short of buf is null
     * @throws IllegalArgumentException if bad attribute value
     */
    public Packet(byte [] buf) throws IOException, IllegalArgumentException{
        if(buf == null){
            throw new IOException("Buffer is null");
        }
        if(buf.length > 1534) {
            throw new IOException("Buffer is too long");
        }
        if(buf.length < 4) {
            throw new IOException("Buffer is too short");
        }
        if(buf[0] >> 4 != 4){
            throw new IllegalArgumentException("Invalid Version Number: " +
                    (buf[0] >> 4));
        }
        type = PacketType.getByCode((buf[0] & 0x0F));
        error = ErrorType.getByCode(buf[1]);
        int count = buf[3] & 0x000000FF;

        if(type != AnswerRequest && error != None){
            throw new IllegalArgumentException("Invalid Packet Type and Error " +
                    "Type combination\n Note: Only Answer Request Packets " +
                    "can have Error Types other than None");
        }

        if((type == RequestMavens || type == RequestNodes) && count > 0){
            throw new IllegalArgumentException("Invalid Type/count\n Request" +
                    "Mavens and RequestNodes cannot have addresses stored");
        }
        

        if(count*6 != buf.length - 4){
            throw new IOException("Count and number of Addresses" +
                                               " mismatch");
        }

        if(type == null){
            throw new IllegalArgumentException("Packet type is Invalid");
        }
        if(error == null){
            throw new IllegalArgumentException("Error type is Invalid");
        }
        for(int i = 0; i < count; i++){
            InetAddress address;
            int start = 4 + i*6;
            address = InetAddress.getByAddress(Arrays.copyOfRange(buf,start,start + 4));
            int firstHalf = (buf[start + 4] & 0xff) << 8;
            int secondHalf = buf[start+ 5] & 0xFF;
            int port = firstHalf | secondHalf;
            InetSocketAddress socketAddress = new InetSocketAddress(address,port);
            addrList.add(socketAddress);
        }

        sessionID = buf[2] & 0x000000FF;
    }

    /**
     * Construct new packet from attributes
     * @param type type of message
     * @param error error type if any
     * @param sessionID session ID of message
     * @throws IllegalArgumentException if bad attribute value given.
     * Note that only an Answer Request may have a non-zero error
     */
    public Packet(PacketType type, ErrorType error, int sessionID) throws
             IllegalArgumentException{
        if(sessionID < 0 || sessionID > 255){
            throw new IllegalArgumentException("Invalid sessionID: Out of " +
                    "Range [0-255]");
        }
        if(type != AnswerRequest && error != None){
            throw new IllegalArgumentException("Invalid Packet Type and Error " +
                    "Type combination\n Note: Only Answer Request Packets " +
                    "can have Error Types other than None");
        }

        if(type == null){
            throw new IllegalArgumentException("Packet type is Invalid");
        }

        if(error == null){
            throw new IllegalArgumentException("Error type is Invalid");
        }

        this.type = type;
        this.error = error;
        this.sessionID = sessionID;
    }

    /**
     * Return encoded message in byte array
     * @return encoded message byte array
     */
    public byte[] encode() throws IOException {
        byte[] messageByteArray = new byte[1534];



        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte)(this.getType().getCode() | 0x00000040));
        bytes.add((byte)this.getError().getCode());
        bytes.add((byte)this.getSessionID());
        bytes.add((byte)addrList.size());

        for (InetSocketAddress address:addrList) {
            for (byte addressByte:address.getAddress().getAddress()){
                bytes.add(addressByte);
            }
            bytes.add((byte)(address.getPort() >>> 8));
            bytes.add((byte)address.getPort());
        }
        for (int i = 0; i < bytes.size();i++) {
            messageByteArray[i] = bytes.get(i);
        }
        return Arrays.copyOf(messageByteArray,bytes.size());
    }

    /**
     * Get packet type
     * @return Packet type
     */
    public PacketType getType(){
        return type;
    }

    /**
     * Get error
     * @return error
     */
    public ErrorType getError(){
        return error;
    }

    /**
     * Set session ID
     * @param sessionID new session ID
     * @throws IllegalArgumentException if sessionID invalid
     */
    public void setSessionID(int sessionID) throws IllegalArgumentException{

        if(sessionID < 0 || sessionID > 255){
            throw  new IllegalArgumentException("Invalid sessionID: Out of " +
                    "Range [0-255]");
        }

        this.sessionID = sessionID;
    }

    /**
     * Get session ID
     * @return session ID
     */
    public int getSessionID(){
        return sessionID;
    }

    /**
     * Add new address
     * @param newAddress new address to add.
     *                   If the Packet already contains the given address,
     *                   the list of addresses remains unchanged.
     * @throws IllegalArgumentException if newAddress is null,
     *                                  this type of MVN packet does not
     *                                  have addresses, or if too many addresses
     */
    public void addAddress(InetSocketAddress newAddress)
            throws IllegalArgumentException{
        //check for Illegal Argument
        if(newAddress == null){
            throw new IllegalArgumentException("Address is Null");
        }
        if(this.getType() == RequestNodes || this.getType() == RequestMavens){
            throw new IllegalArgumentException("This Maven packet does not" +
                                               "allow address");
        }
        if(this.getAddrList().size() == 255){
            throw new IllegalArgumentException("This maven packet has the " +
                    "maximum amount of addresses stored");
        }
        this.addrList.add(newAddress);
    }

    /**
     * Get list of addresses
     * @return list of addresses
     */
    public Set getAddrList(){
        return new HashSet<>(addrList);
    }

    /**
     * Human-readable string representation
     * @return String representation of Packet
     */
    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", error=" + error +
                ", sessionID=" + sessionID +
                ", addrList=" + addrList +
                '}';
    }

    /**
     * Test for equivalency of two Packet objects
     * Must override to satisfy contract
     * @param o object (Packet) to be compared
     * @return a boolean based on the equivalency
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet)) return false;

        Packet packet = (Packet) o;

        if (sessionID != packet.sessionID) return false;
        if (type != null ? !type.equals(packet.type) : packet.type != null)
            return false;
        if (error != null ? !error.equals(packet.error) : packet.error != null)
            return false;
        return addrList != null ? addrList.equals(packet.addrList) : packet.addrList == null;
    }

    /**
     * Generates hashcode for Packet
     * Must override to satisfy contract
     * @return the hash code of the Packet
     */
    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + sessionID;
        result = 31 * result + (addrList != null ? addrList.hashCode() : 0);
        return result;
    }
}
