/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.serialization.MessageOutput;
import sharon.serialization.Search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.logging.Logger;

/**
 * Sends new messages
 */
public class SenderAIO {
    private Logger log;
    private Search search;

    public SenderAIO(AsynchronousSocketChannel clientChannel, Search search, Logger log) throws IOException {
        this.search = search;
        this.log = log;
        send(clientChannel);
    }

    public void send(AsynchronousSocketChannel clientChannel) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        search.encode(new MessageOutput(baos));
        ByteBuffer outBuf = ByteBuffer.wrap(baos.toByteArray());
        clientChannel.write(outBuf);
    }
}
