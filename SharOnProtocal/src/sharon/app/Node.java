/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.serialization.*;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 * Implementation of the Node class for sending and receiving searches and
 * responses
 */
public class Node{
    static private ExecutorService downloadExecutor = null;
    static private Logger log = Logger.getLogger("SharOnProtocol.log");

    /**
     * @param args args[0] address name
     *             args[1] port
     *             args[2] directory
     * @throws IOException if an input or output exception has occurred
     * @throws BadAttributeValueException if a bad value was attempted to be inputed
     */
    public static void main(String [] args) throws IOException, BadAttributeValueException {
        System.out.println("Node v1.03");
        if ((args.length < 3) ) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Port> <Directory> <Download Port>");
        }
        //set neighbor address and ports
        String neighborName;
        int neighborPort;
        int serverPort =  Integer.parseInt(args[0]);
        int downloadPort = Integer.parseInt(args[2]);
        String dir = args[1];

        FileHandler fh;
        fh = new FileHandler("SharOnProtocol.log");
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);

        //set up initialization string for the handshake
        byte [] initMsg = "INIT SharOn/1.0\n\n".getBytes();

        //set default values for sent packets
        int ttl = 1;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};

        //flag to end program;
        Boolean exitFlag = false;

        CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();

        //string to hold the response of the Node for the handshake
        String response;

        //HashMap to store what ID's are associated with what search strings
        HashMap<String, String> searchMap = new HashMap<>();


        //set up the socket to be used for communication between nodes
        ServerSocket serverSocket = new ServerSocket(serverPort);
        ServerSocket downloadSocket = new ServerSocket(downloadPort);

        IncomingConnections incomingConnections = new IncomingConnections(serverSocket,
                                                        connections, searchMap, dir, downloadPort, log);
        incomingConnections.start();

        DownloadConnections downloadConnections = new DownloadConnections(downloadSocket, dir, log);
        downloadConnections.start();

        while(!exitFlag) {
            System.out.println("Enter a Command");
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            String getLine = reader.nextLine();
            String command = "";
            StringTokenizer commandTokenizer = new StringTokenizer(getLine);

            if(!getLine.equals("")) {
                command = commandTokenizer.nextToken();
            }

            switch (command) {
                case "exit":
                    exitFlag = true;
                    break;
                case "connect":
                    if (commandTokenizer.countTokens() >= 2) {
                        neighborName = commandTokenizer.nextToken();
                        neighborPort = Integer.parseInt(commandTokenizer.nextToken());

                        try {
                            Connection newConnection = new Connection(new Socket(neighborName, neighborPort));
                            System.out.println("Connection Established to: " + newConnection.getClientSocket().getInetAddress());
                            newConnection.getClientSocket().getOutputStream().write(initMsg);
                            response = newConnection.getInData().getNodeResponse();
                            if (response.equals("OK SharOn\n\n")) {
                                connections.add(newConnection);
                                log.info("Connection Established too: " + newConnection.getClientSocket().getInetAddress());
                                Listener newListener = new Listener(newConnection, searchMap, dir, downloadPort, log);
                                newListener.start();
                            } else {
                                System.out.println("HandShake Rejected\n" + response);
                            }
                        } catch (IOException e) {
                            log.warning(e.getMessage());
                        }

                    } else {
                        System.out.println("Invalid connect format connect " +
                                "<connector Node> <connector port>");
                    }
                    break;
                case "download":
                    if (commandTokenizer.countTokens() == 4) {
                        String outDownloadName = commandTokenizer.nextToken();
                        int outDownloadPort = Integer.parseInt(commandTokenizer.nextToken());
                        String fileID = commandTokenizer.nextToken();
                        String fileName = commandTokenizer.nextToken();

                        new Download(dir,log).startDownload(outDownloadName,outDownloadPort,fileID,fileName);

                    } else {
                        System.out.println("Invalid download format: download " +
                                "<download Node> <download port> <File ID> " +
                                "<File Name>");
                    }
                    break;
                default:
                    try {
                        Search srch = new Search(Utilities.randomID(), ttl, RoutingService.BREADTHFIRSTBROADCAST,
                                sourceAddress, destinationAddress, command);
                        searchMap.put(Arrays.toString(srch.getID()), command);
                        System.out.println("Searching for: " + command);
                        for (Connection connection : connections) {
                            if (!connection.getClientSocket().isClosed()) {
                                Sender newSender = new Sender(srch, connection, log);
                                newSender.start();
                            } else {
                                connections.remove(connection);
                            }
                        }
                    } catch (BadAttributeValueException e) {
                        log.warning(e.getMessage());
                    }
                    break;
            }
        }
        System.exit(1);
    }
}
