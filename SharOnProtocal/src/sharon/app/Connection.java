package sharon.app;

import sharon.serialization.MessageInput;
import sharon.serialization.MessageOutput;

import java.io.IOException;
import java.net.Socket;

/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/


public class Connection {
    private Socket clientSocket;
    private MessageOutput outData;
    private MessageInput inData;

    /**
     * @param newSocket set the socket for the connections
     * @throws IOException Exception to be thrown
     */
    public Connection(Socket newSocket) throws IOException {
        clientSocket = newSocket;
        outData = new MessageOutput(clientSocket.getOutputStream());
        inData = new MessageInput(clientSocket.getInputStream());
    }

    /**
     * @param message writes amessage to the socket output stream
     * @throws IOException
     */
    public void writeMessage(String message) throws IOException {
        outData.writeByteArray(message.getBytes("ASCII"));
    }

    /**
     * @return the socket associated with this connection
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * @return returns the MessageOutput associated with this connections
     */
    public MessageOutput getOutData() {
        return outData;
    }

    /**
     * @return returns the MessageInput associated with this connection
     */
    public MessageInput getInData() {
        return inData;
    }
}
