/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 5
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package mvn.app;

import mvn.serialization.*;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Client {

    /**
     * @param args main program arguments args[0] is address args[1] is port number
     * @throws IOException
     */
    public static void main(String [] args) throws IOException {
        if ((args.length < 2) ) { // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server Name/IP> <Port> ");
        }

        InetAddress serverAddress = InetAddress.getByName(args[0]);
        int serverPort = Integer.parseInt(args[1]);
        String command = "start";
        String cmdType = null;
        Packet packet;
        int sessionID;
        Boolean IOExceptionFlag = false;
        Random r = new Random();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket udpPacket;

        //Continue prompting user till command is exit
        while (!command.equals("exit") && !IOExceptionFlag) {
            try {
                packet = null;
                //prompt user
                System.out.print("Please Enter a Command: \n>");
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                command = reader.nextLine();
                StringTokenizer commandTokenizer = new StringTokenizer(command);

                //check if a command was entered
                if (!command.equals("")) {
                    cmdType = commandTokenizer.nextToken();
                }

                sessionID = r.nextInt(255 - 1) + 1;

                //if command is RN send a RequestNodes Packet
                if (cmdType.equals("RN")) {
                    packet = new Packet(PacketType.RequestNodes, ErrorType.None, sessionID);
                }
                //if command is RM send a RequestMavens Packet
                else if (cmdType.equals("RM")) {
                    packet = new Packet(PacketType.RequestMavens, ErrorType.None, sessionID);
                }
                //if command is NA send a NodeAdditions Packet
                else if (cmdType.equals("NA")) {
                    //check for valid arguments
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.NodeAdditions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("NA Command Expects at least one argument");
                    }
                }
                //if command is MA send a MavenAdditions Packet
                else if (cmdType.equals("MA")) {
                    //check for valid arguments
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.MavenAdditions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("MA Command Expects at least one argument");
                    }
                }
                //if command is ND send a NodeDeletions Packet
                else if (cmdType.equals("ND")) {
                    //check for valid arguments
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.NodeDeletions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("ND Command Expects at least one argument");
                    }
                }
                //if command is MD send a MavenDeletions Packet
                else if (cmdType.equals("MD")) {
                    //check for valid arguments
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.MavenDeletions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("MD Command Expects at least one argument");
                    }
                }
                else if (!cmdType.equals("exit")){
                    System.err.println("Invalid Command");
                }

                //if a packet was created send it
                if (packet != null) {
                    //create buffer to send via UDP
                    byte[] packetByteArray = packet.encode();
                    //create UDP packet
                    udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length, serverAddress, serverPort);
                    int attempts = 0;
                    Boolean receivedFlag = false;
                    Packet newPacket = null;
                    //send Packet
                    socket.send(udpPacket);
                    if((packet.getType() == PacketType.RequestMavens || packet.getType() == PacketType.RequestNodes)){
                        try {
                            while (attempts < 3 && !receivedFlag && !IOExceptionFlag) {
                                socket.setSoTimeout(3000);
                                byte[] buffer = new byte[1534];
                                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                                try {
                                    socket.receive(responsePacket);
                                    try {
                                        newPacket = new Packet(Arrays.copyOf(responsePacket.getData(), responsePacket.getLength()));
                                        if (newPacket.getType() != PacketType.AnswerRequest) {
                                            System.err.println("Unexpected Packet Type");
                                        } else if (newPacket.getSessionID() == sessionID || sessionID == 0) {
                                            receivedFlag = true;
                                            System.out.println(newPacket);
                                        } else if (newPacket.getSessionID() != sessionID) {
                                            System.err.println("Unexpected Session ID: Expected: "
                                                    + sessionID + " Received: " + newPacket.getSessionID());
                                        }
                                    } catch (IllegalArgumentException e) {
                                        System.err.println("Invalid Message: " + e.getMessage());
                                    } catch (IOException e) {
                                        System.err.println("Communication problem: " + e.getMessage());
                                        IOExceptionFlag = true;
                                    }
                                } catch (SocketTimeoutException e) {
                                    attempts++;
                                    socket.send(udpPacket);
                                    System.err.println("Retransmitting");
                                }
                            }
                            if (!receivedFlag && !IOExceptionFlag && (packet.getType() == PacketType.RequestMavens ||
                                    packet.getType() == PacketType.RequestNodes)) {
                                System.err.println("No Response from Server");
                            }
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
        System.out.println("Terminating Client");
    }


    /**
     * Creates an InetSocketAddress from a string following this format:
     * address:port
     * @param address a string ins address:port format
     * @return InetSocketAddress
     */
    public static InetSocketAddress createAddress(String address){
        StringTokenizer addressTokenizer = new StringTokenizer(address);
        String newAddressIP = addressTokenizer.nextToken(":");
        int newAddressPort = Integer.parseInt(addressTokenizer.nextToken());

        return new InetSocketAddress(newAddressIP,newAddressPort);
    }
}


