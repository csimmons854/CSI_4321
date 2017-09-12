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
    long msgFileID;
    long msgFileSize;
    String msgFileName;
    DataInputStream data;

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

    /**
     * @return the msgFileName
     */
    public String getMsgFileName() {
        return msgFileName;
    }

    /**
     * @param msgFileName
     */
    public void setMsgFileName(String msgFileName) {
        this.msgFileName = msgFileName;
    }

    public long getMsgFileSize() {
        return msgFileSize;
    }

    public void setMsgFileSize(long msgFileSize) {
        this.msgFileSize = msgFileSize;
    }


    public long getMsgFileID() {
        return msgFileID;
    }

    public void setMsgFileID(long msgFileID) {
        this.msgFileID = msgFileID;
    }
}
