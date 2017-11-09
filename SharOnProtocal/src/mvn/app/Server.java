/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 6
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

package mvn.app;

import mvn.serialization.*;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.*;



public class Server {
    /**
     * @param args main program arguments args[0] is port number
     * @throws IOException
     */
    public static void main(String [] args) throws IOException {
        // Test for correct # of args
        if ((args.length < 1)) {
            throw new IllegalArgumentException("Parameter: <Port> ");
        }

        //assign serverPort
        int serverPort = Integer.parseInt(args[0]);

        //Initialize address and port of the client
        int packetPort;
        InetAddress packetAddress;

        //default the incoming packets from client to null
        Packet receivedPacket = null;

        //default the packet to be sent back when receiving RN or RM
        Packet responseNodePacket = new Packet(PacketType.AnswerRequest,
                ErrorType.None,0);
        Packet responseMavenPacket = new Packet(PacketType.AnswerRequest,
                ErrorType.None,0);

        //default the various AR packets to be sent to the client when error
        //occurred
        Packet commError = new Packet(PacketType.AnswerRequest,
                ErrorType.System,0);
        Packet invalMsgError = new Packet(PacketType.AnswerRequest,
                ErrorType.IncorrectPacket,0);
        Packet errorPacket = new Packet(PacketType.AnswerRequest,
                ErrorType.IncorrectPacket,0);

        //set up the server socket and the DatagramPacket that is to store
        //incoming UDP packets
        byte[] buffer = new byte[1534];
        DatagramPacket clientPacket = new DatagramPacket(buffer,buffer.length);
        DatagramSocket serverSocket = new DatagramSocket(serverPort);

        //set up logger
        Logger log = Logger.getLogger("SharOnMaven.log");
        FileHandler fh;
        fh = new FileHandler(".\\SharOnProtocol.log");
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);

        //begin to listen for packets
        while(true) {
            //accept received packets
            serverSocket.receive(clientPacket);
            //store the address and port of the received packet
            packetAddress = clientPacket.getAddress();
            packetPort = clientPacket.getPort();

            //attempt to form a packet and log any errors that occur
            try {
                receivedPacket = new Packet(Arrays.copyOf(clientPacket.getData()
                        , clientPacket.getLength()));

            } catch (IOException e) {
                receivedPacket = null;
                log.warning("Communication problem: " + e.getMessage());

                serverSocket.send(new DatagramPacket(commError.encode(),
                        commError.encode().length,
                        packetAddress, packetPort));

            } catch (IllegalArgumentException e) {
                receivedPacket = null;
                log.warning("Invalid Message: " + e.getMessage());

                serverSocket.send(new DatagramPacket(invalMsgError.encode(),
                        invalMsgError.encode().length,
                        packetAddress, packetPort));

            }

            //if the packet was successfully made begin to decode packet
            if (receivedPacket != null) {
                //if the packet was a Answer Request log the packet and
                //reply that an error had occurred
                if (receivedPacket.getType() == PacketType.AnswerRequest) {
                    log.warning("Unexpected Message Type: " + receivedPacket);

                    errorPacket.setSessionID(receivedPacket.getSessionID());
                    serverSocket.send(new DatagramPacket(errorPacket.encode(),
                            errorPacket.encode().length,
                            packetAddress, packetPort));

                }
                //if the packet had a non-zero error log the packet and
                //reply that an error had occurred
                else if (receivedPacket.getError() != ErrorType.None) {
                    log.warning("Unexpected Error: " + receivedPacket);

                    errorPacket.setSessionID(receivedPacket.getSessionID());
                    serverSocket.send(new DatagramPacket(errorPacket.encode(),
                            errorPacket.encode().length,
                            packetAddress, packetPort));

                }
                //if the packet is a RM or RN reply with the corresponding
                //node or maven packet and log the packet
                else if (receivedPacket.getType() == PacketType.RequestMavens ||
                        receivedPacket.getType() == PacketType.RequestNodes) {
                    log.info("Received: " + receivedPacket);

                    if (receivedPacket.getType() == PacketType.RequestNodes) {
                        responseNodePacket.setSessionID(receivedPacket.getSessionID());
                        serverSocket.send(new DatagramPacket(responseNodePacket.encode(),
                                responseNodePacket.encode().length,
                                packetAddress, packetPort));

                    } else {
                        responseMavenPacket.setSessionID(receivedPacket.getSessionID());
                        serverSocket.send(new DatagramPacket(responseMavenPacket.encode(),
                                responseNodePacket.encode().length,
                                packetAddress, packetPort));
                    }

                }
                //if the packet is a ND, NA, MD, MA reply the corresponding
                //perform the corresponding operation on either the maven response
                //packet or node response packet
                else {
                    try {
                        log.info("Received: " + receivedPacket);

                        if (receivedPacket.getType() == PacketType.NodeAdditions) {
                            for (InetSocketAddress address :
                                    (HashSet<InetSocketAddress>) receivedPacket.getAddrList()) {
                                responseNodePacket.addAddress(address);
                            }
                        }
                        if (receivedPacket.getType() == PacketType.MavenAdditions) {
                            for (InetSocketAddress address :
                                    (HashSet<InetSocketAddress>) receivedPacket.getAddrList()) {
                                responseMavenPacket.addAddress(address);
                            }

                        }
                        if (receivedPacket.getType() == PacketType.MavenDeletions) {
                            for (InetSocketAddress address :
                                    (HashSet<InetSocketAddress>) receivedPacket.getAddrList()) {
                                responseMavenPacket.delAddress(address);
                            }
                        }
                        if (receivedPacket.getType() == PacketType.NodeDeletions) {
                            for (InetSocketAddress address :
                                    (HashSet<InetSocketAddress>) receivedPacket.getAddrList()) {
                                responseNodePacket.delAddress(address);
                            }
                        }

                    } catch (IllegalArgumentException e){
                        //do nothing
                    }
                }
            }
        }
    }
}
