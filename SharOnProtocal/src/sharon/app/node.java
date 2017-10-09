/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.serialization.*;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

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
        String dir = args[2];

        //set up initialization string for the handshake
        byte [] initMsg = "INIT SharOn/1.0\n\n".getBytes();

        //set default values for sent packets
        int id = 0;
        int ttl = 100;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};

        //string to hold the response of the node for the handshake
        String response;

        //HashMap to store what ID's are associated with what search strings
        HashMap<String, String> searchMap = new HashMap<>();


        //set up the socket to be used for communication between nodes
        Socket clientSocket = new Socket(neighborName, neighborPort);

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
            Listener newListener = new Listener(data,outData,searchMap, dir);
            newListener.start();

            //begin prompting for search messages
            while(true)
            {
                System.out.println("Enter a search");
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                String search = reader.nextLine();
                Search srch = new Search(intToByteArray(id),ttl, RoutingService.BREADTHFIRSTBROADCAST,
                        sourceAddress,destinationAddress,search);
                searchMap.put(Arrays.toString(srch.getID()), search);
                System.out.println("Searching for: " + search);

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
        private HashMap<String, String> searchMap;
        private String directory;


        Listener(MessageInput in, MessageOutput out,HashMap<String,String> searchMap, String dir) {
            inData = in;
            outData = out;
            this.searchMap = searchMap;
            directory = dir;

        }

        public void run() {
            List<Result> resultList;
            try {
                synchronized (inData) {
                    while (true) {
                        msg = decode(inData);
                        if (msg instanceof Search) {
                            Response outResponse =
                                new Response(msg.getID(),msg.getTtl(),msg.getRoutingService(),
                                             msg.getSourceAddress(),msg.getDestinationAddress(),
                                             new InetSocketAddress(InetAddress.getLocalHost(),8080));

                            File dir = new File(directory);
                            File[] foundFiles = dir.listFiles((dir1, name) ->
                                    name.contains(((Search) msg).getSearchString()));
                            byte fileID = 0;
                            if(foundFiles != null)
                            {
                                for(File item : foundFiles)
                                {
                                    fileID++;
                                    outResponse.addResult(new Result(fileID,item.length(),item.getName()));
                                }
                            }
                            outResponse.encode(outData);
                        }
                        if (msg instanceof Response) {
                            System.out.println("Search Response for " +
                                    searchMap.get(Arrays.toString(((Response) msg).getID())));
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
