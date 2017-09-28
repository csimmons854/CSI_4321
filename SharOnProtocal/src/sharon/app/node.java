/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.serialization.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static sharon.serialization.Message.decode;

/**
 * Implementation of the node class for sending and receiving searches and
 * responses
 */
public class node implements Runnable{
    private String searchString;


    /**
     * @param id in int form to be converted to the 15 byte array form for
     *           the protocol
     * @return Byte array of length 15 representing the newly converted Id
     */
    public static byte [] intToByteArray(int id)
    {
        String intToID = "";
        byte [] stringToID;

        intToID = Integer.toString(id);
        while(intToID.length() < 15)
        {
            intToID = '0' + intToID;
        }
        stringToID = intToID.getBytes();
        for(int i = 0; i < stringToID.length;i++)
        {
            stringToID[i] -= 48;
        }
        return stringToID;
    }

    /**
     * @param args args[0] address name
     *             args[1] port
     *             args[2] directory
     * @throws IOException if an input or output exception has occurred
     * @throws BadAttributeValueException if a bad value was attempted to be inputed
     */
    public static void main(String [] args) throws IOException, BadAttributeValueException {
        if ((args.length < 3) ) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Address/Name> <Port> <Directory>");
        }
        //set neighbor address and ports
        String neighborName = args[0];
        int neighborPort = Integer.parseInt(args[1]);

        //set up initialization string for the handshake
        byte [] initMsg = "INIT SharOn/1.0\n\n".getBytes();

        //set default values for sent packets
        int id = 0;
        int ttl = 100;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};

        //string to hold the response of the node for the handshake
        String response;

        //System.out.println("NeighborName: " + neighborName);
        //System.out.println("NeighborNode: " + neighborPort);

        //set up the socket to be used for communication between nodes
        Socket clientSocket = new Socket(neighborName, neighborPort);
        //System.out.println("Connected to server...sending echo string");

        //wrap the output and input streams in MessageInput and MessageOutput
        //classes
        OutputStream out = clientSocket.getOutputStream();
        MessageOutput outData = new MessageOutput(out);
        InputStream in = clientSocket.getInputStream();
        MessageInput data = new MessageInput(in);

        //send the initialization message
        out.write(initMsg);

        //store the response
        response = data.getNodeResponse();

        //if response is good then began waiting for packets or send packets
        if(response.equals("OK SharOn\n\n")) {
            //System.out.println("Handshake Established");

            //begin listener thread
            Listener newListener = new Listener(data,outData);
            newListener.start();

            //begin prompting for search messages
            while(true)
            {
                System.out.println("Enter a search");
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                String search = reader.nextLine();
                System.out.println("Searching for: " + search);

                Search srch = new Search(intToByteArray(id),ttl, RoutingService.BREADTHFIRSTBROADCAST,
                        sourceAddress,destinationAddress,search);
                id++;
                Sender newSender = new Sender(srch,outData);
                newSender.start();

            }
        }
        else {
            System.out.println("HandShake Rejected\n" + response);
        }

    }



    @Override
    public void run() {

    }


    /**
     * Sender thread to send searches to other nodes
     */
    static class Sender implements Runnable {
        private Thread t;
        private Message msg;
        private MessageOutput outData;

        Sender(Message msg, MessageOutput out) {
            this.msg = msg;
            outData = out;
        }

        public void run() {
            try {
                synchronized (outData) {
                    msg.encode(outData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }

        }
    }

    static class Listener implements Runnable {
        private Thread t;
        private Message msg;
        private MessageInput inData;
        private MessageOutput outData;


        Listener(MessageInput in, MessageOutput out) {
            inData = in;
            outData = out;
        }

        public void run() {
            List<Result> resultList = new ArrayList<Result>();
            try {
                synchronized (inData) {
                    while (true) {
                        msg = decode(inData);
                        if (msg instanceof Search) {

                        }
                        if (msg instanceof Response) {
                            System.out.println("Search Response for x");
                            System.out.println("Download host: " + ((Response) msg).getResponseHost());
                            resultList = ((Response) msg).getResultList();
                            for(int i = 0; i < resultList.size(); i++)
                            {

                                System.out.println("\t" + resultList.get(i).getFileName()
                                        + ": ID " + resultList.get(i).getFileID()
                                        + " (" + resultList.get(i).getFileSize()
                                        + " bytes)");
                            }

                        }
                    }
                }
            } catch (BadAttributeValueException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }

        }
    }

}
