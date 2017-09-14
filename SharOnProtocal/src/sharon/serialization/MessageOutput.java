/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package sharon.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * Serialization output for messages
 */
public class MessageOutput {

    private OutputStream msgOut;
    /**
     * Constructs a new output source from an OutputStream
     *
     * @param out is the byte output sink
     */
    public MessageOutput(OutputStream out)  throws NullPointerException
    {
        if(out == null)
        {
            throw new NullPointerException();
        }
        msgOut = out;
    }

    /**
     * Default Constructor that initialize a new ByteArrayOutputStream
     */
    public MessageOutput()
    {
        msgOut = new ByteArrayOutputStream();
    }

    /**
     * @return the outputStream
     */
    public OutputStream getMsgOut() {
        return msgOut;
    }

    /**
     * @param data int to be written to the outputStream
     * @throws IOException
     */
    public void writeInt(int data) throws IOException
    {
        msgOut.write(ByteBuffer.allocate(4).putInt(data).array());
        msgOut.flush();
    }

    /**
     * @param newString String to be written to the outputStream
     * @throws IOException
     */
    public void writeString(String newString) throws IOException
    {
        msgOut.write((newString + '\n').getBytes());
        msgOut.flush();
    }

    /**
     * @param data int to be written to 1 byte of the outputStream
     * @throws IOException
     */
    public void writeByte(int data) throws IOException {
        byte [] newByte = {(byte) data};
        //System.err.println(Arrays.toString(newByte));
        msgOut.write(newByte);
        msgOut.flush();

    }

    /**
     * @param data Byte [] to be written to the outputStream
     * @throws IOException
     */
    public void writeByteArray(byte [] data) throws IOException {
        //System.err.println(Arrays.toString(data));
        msgOut.write(data);
        msgOut.flush();

    }

    /**
     * @param data Integer to be written to 2 bytes of the outputStream
     * @throws IOException
     */
    public void writeIntTo2Bytes(int data) throws IOException {
        byte [] bytes = {0,0};
        {
            bytes[1] = (byte)data;
            bytes[0] = (byte)(data >> 8);
            msgOut.write(bytes);
            msgOut.flush();
        }
    }

}
