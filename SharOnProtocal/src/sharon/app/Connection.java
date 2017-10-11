package sharon.app;

import sharon.serialization.MessageInput;
import sharon.serialization.MessageOutput;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Chris on 10/9/2017.
 */
public class Connection {
    private Socket clientSocket;
    private MessageOutput outData;
    private MessageInput inData;

    public Connection(Socket newSocket) throws IOException {
        clientSocket = newSocket;
        outData = new MessageOutput(clientSocket.getOutputStream());
        inData = new MessageInput(clientSocket.getInputStream());
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public MessageOutput getOutData() {
        return outData;
    }

    public MessageInput getInData() {
        return inData;
    }
}
