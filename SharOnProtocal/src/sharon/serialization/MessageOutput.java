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

    public MessageOutput()
    {
        msgOut = new ByteArrayOutputStream();
    }

    public OutputStream getMsgOut() {
        return msgOut;
    }

    public void setMsgOut(OutputStream msgOut) {
        this.msgOut = msgOut;
    }

    public void writeInt(int data) throws IOException
    {
        msgOut.write(ByteBuffer.allocate(4).putInt(data).array());
        msgOut.flush();
    }

    public void writeString(String newString) throws IOException
    {
        msgOut.write((newString + '\n').getBytes());
        msgOut.flush();
    }

}
