/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package sharon.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        ArrayList<Byte> byteList = new ArrayList<Byte>();
        byte tmp;
        while ( (tmp = ( data.readByte()))!= '\n')
        {
            byteList.add(tmp);
        }
        byte [] byteArray = new byte [byteList.toArray().length];
        for(int i = 0; i < byteArray.length; i++)
        {
            byteArray[i] = (byte)byteList.toArray()[i];
        }
        return new String(byteArray,"ASCII");
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
        data.readFully(buffer,0,count);
        return buffer;
    }

    public short getShort() throws IOException
    {
        return data.readShort();
    }



}
