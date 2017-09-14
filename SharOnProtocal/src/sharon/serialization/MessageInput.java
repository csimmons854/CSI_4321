/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package sharon.serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Serialization input for messages
 */
public class MessageInput {
    private DataInputStream data;

    /**
     *  Constructs that creates a new input source from an InputStream
     *
     * @param in is the byte input source
     *
     * @throws java.lang.NullPointerException
     *     if in is a null pointer
     */
    public MessageInput(InputStream in)
            throws NullPointerException, IOException {
        if(in == null) {
            throw new NullPointerException();
        }
        data = new DataInputStream(in);
    }

    public MessageInput()
    {

    }

    public String getString() throws IOException {
        String newString = "";
        char tmp;
        while ( (tmp = ((char) data.readByte()))!= '\n')
        {
            newString += tmp;
        }
        return newString;
    }

    public int getInt() throws IOException
    {
        return data.readInt();
    }

    public byte getByte() throws IOException
    {
        return data.readByte();
    }

    public byte [] getByteArray(int count) throws IOException {
        byte [] buffer = new byte[count];
        data.read(buffer,0,count);
        return buffer;
    }

    public short getShort() throws IOException
    {
        return data.readShort();
    }



}
