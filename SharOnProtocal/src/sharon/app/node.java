/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import com.sun.org.apache.xpath.internal.operations.Bool;
import sharon.serialization.*;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static sharon.serialization.Message.decode;

/**
 * Implementation of the node class for sending and receiving searches and
 * responses
 */
public class node implements Runnable{

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
            throw new IllegalArgumentException("Parameter(s): <Port> <Directory> <Download Port>");
        }
        //set neighbor address and ports
        String neighborName;
        int neighborPort = 0;
        int serverPort =  Integer.parseInt(args[0]);
        int downloadPort = Integer.parseInt(args[2]);
        String dir = args[1];

        //set up initialization string for the handshake
        byte [] initMsg = "INIT SharOn/1.0\n\n".getBytes();

        //set default values for sent packets
        int id = 0;
        int ttl = 1;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};

        //flag to end program;
        Boolean exitFlag = false;

        CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();

        //string to hold the response of the node for the handshake
        String response;

        //HashMap to store what ID's are associated with what search strings
        HashMap<String, String> searchMap = new HashMap<>();


        //set up the socket to be used for communication between nodes
        Socket clientSocket;
        ServerSocket serverSocket = new ServerSocket(serverPort);

        IncomingConnections incomingConnections = new IncomingConnections(serverSocket,
                                                        connections, searchMap,dir, serverPort);
        incomingConnections.start();

        while(!exitFlag) {
            System.out.println("Enter a Command");
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            String getLine = reader.nextLine();
            String command = "";
            StringTokenizer commandTokenizer = new StringTokenizer(getLine);

            if(!getLine.equals("")) {
                command = commandTokenizer.nextToken();
            }

            if(command == "exit") {
                exitFlag = true;
            }else if(command.equals("connect")) {
                neighborName = commandTokenizer.nextToken();
                neighborPort = Integer.parseInt(commandTokenizer.nextToken());
                Connection newConnection = new Connection(new Socket(neighborName,neighborPort));
                newConnection.getClientSocket().getOutputStream().write(initMsg);
                response = newConnection.getInData().getNodeResponse();


                if(response.equals("OK SharOn\n\n")) {
                    connections.add(newConnection);

                    Listener newListener = new Listener(newConnection, searchMap, dir, serverPort);
                    newListener.start();
                }else {
                    System.out.println("HandShake Rejected\n" + response);
                }
            }else if (command.equals("download")){

            }else{
                System.out.println("# of connections " + connections.size());
                Search srch = new Search(intToByteArray(id),ttl, RoutingService.BREADTHFIRSTBROADCAST,
                        sourceAddress,destinationAddress,command);
                searchMap.put(Arrays.toString(srch.getID()), command);
                System.out.println("Searching for: " + command);
                    for (Connection connection : connections) {
                        if(!connection.getClientSocket().isClosed()) {
                            Sender newSender = new Sender(srch, connection);
                            newSender.start();
                        }else{
                            connections.remove(connection);
                        }

                    }

            }
            id++;
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
        private Connection connection;

        Sender(Message msg, Connection newConnection) {
            this.msg = msg;
            connection = newConnection;
        }

        public void run() {
            try {
                synchronized (connection.getOutData()) {
                    msg.encode(connection.getOutData());
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

    static class IncomingConnections implements Runnable {
        private Thread t;
        private ServerSocket server;
        private CopyOnWriteArrayList<Connection> connections;
        private HashMap<String,String> searchMap;
        private String dir;
        private int port;

        IncomingConnections(ServerSocket newServer, CopyOnWriteArrayList<Connection> newConnections,
                            HashMap<String,String> searchMap, String dir, int newPort) {
            server = newServer;
            connections = newConnections;
            this.searchMap = searchMap;
            this.dir = dir;
            port = newPort;
        }

        public void run() {
            try {
                synchronized (connections) {
                    Socket neighborNodeConnection = server.accept();
                    Connection newConnection = new Connection(neighborNodeConnection);
                    if(newConnection.getInData().getNodeResponse().equals("INIT SharOn/1.0\n\n")) {
                        newConnection.getOutData().writeByteArray("OK SharOn\n\n".getBytes("ASCII"));
                        connections.add(newConnection);
                        Listener newListener = new Listener(newConnection, searchMap, dir, port);
                        newListener.start();
                    }

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
        private Connection connection;
        private int port;
        private String directory;


        Listener(Connection newConnection, HashMap<String,String> searchMap, String dir, int newPort) {
            inData = newConnection.getInData();
            outData = newConnection.getOutData();
            connection = newConnection;
            this.searchMap = searchMap;
            port = newPort;
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
                                             new InetSocketAddress(InetAddress.getLocalHost(),port));

                            File dir = new File(directory);
                            File[] foundFiles = dir.listFiles((dir1, name) ->
                                    name.contains(((Search) msg).getSearchString()));
                            long fileID;
                            if(foundFiles != null) {
                                for(File item : foundFiles) {
                                    fileID = item.getName().hashCode() & 0x00000000FFFFFFFFL;
                                    outResponse.addResult(new Result(fileID,item.length(),item.getName()));
                                }
                            }
                            outResponse.encode(outData);
                        }
                        if (msg instanceof Response) {
                            System.out.println("Search Response for " +
                                    searchMap.get(Arrays.toString(msg.getID())));
                            System.out.println("Download host: " + ((Response) msg).getResponseHost());
                            resultList = ((Response) msg).getResultList();
                            for(int i = 0; i < resultList.size(); i++) {
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
                try {
                    connection.getClientSocket().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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
