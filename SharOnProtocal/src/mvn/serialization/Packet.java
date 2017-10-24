/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package mvn.serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Created by Chris on 10/23/2017.
 */
public class Packet {

    private PacketType type;
    private ErrorType error;
    private int sessionID;
    private Set<InetSocketAddress> addrList;

    /**
     * Construct new packet from byte array
     * @param buf byte [] to create packet
     * @throws IOException if byte [] is too long/short of buf is null
     * @throws IllegalArgumentException if bad attribute value
     */
    public Packet(byte [] buf) throws IOException, IllegalArgumentException{
        ErrorType error = null;
        PacketType type = null;
        int sessionID = -1;
        if(buf == null){
            throw new IOException("Buffer is null");
        }
        if(buf.length > 1562) {
            throw new IOException("Buffer is too long");
        }
        if(buf.length < 32) {
            throw new IOException("Buffer is too short");
        }

        //get data from buffer

        if(type == null){
            throw new IllegalArgumentException("Packet type is Invalid");
        }
        if(error == null){
            throw new IllegalArgumentException("Error type is Invalid");
        }


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
            throw  new IllegalArgumentException("Invalid sessionID: Out of " +
                    "Range [0-255]");
        }

    }

    /**
     * Return encoded message in byte array
     * @return encoded message byte array
     */
    public byte[] encode(){
        byte[] messageByteArray = null;

        return messageByteArray;
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

        //check is session id is valid

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
    }

    /**
     * Get list of addresses
     * @return list of addresses
     */
    public Set<InetSocketAddress> getAddrList(){
        return addrList;
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
