/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package sharon.serialization;

import java.io.OutputStream;


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
        msgOut = out;
    }

    public MessageOutput()
    {

    }

    public OutputStream getMsgOut() {
        return msgOut;
    }

    public void setMsgOut(OutputStream msgOut) {
        this.msgOut = msgOut;
    }
}
