package mvn.app;

import mvn.serialization.*;
import sharon.app.Connection;
import sharon.serialization.Message;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by Chris on 10/31/2017.
 */
public class Client {
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
        Random r = new Random();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket udpPacket;

        while (!command.equals("exit")) {
            try {
                packet = null;
                System.out.print("Please Enter a Command: \n>");
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                command = reader.nextLine();
                StringTokenizer commandTokenizer = new StringTokenizer(command);

                if (!command.equals("")) {
                    cmdType = commandTokenizer.nextToken();
                }

                sessionID = r.nextInt(255 - 1) + 1;

                if (cmdType.equals("RN")) {
                    packet = new Packet(PacketType.RequestNodes, ErrorType.None, sessionID);
                }
                if (cmdType.equals("RM")) {
                    packet = new Packet(PacketType.RequestMavens, ErrorType.None, sessionID);
                }
                if (cmdType.equals("NA")) {
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.NodeAdditions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("NA Command Expects at least one argument");
                    }
                }
                if (cmdType.equals("MA")) {
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.MavenAdditions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("MA Command Expects at least one argument");
                    }
                }
                if (cmdType.equals("ND")) {
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.NodeDeletions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("ND Command Expects at least one argument");
                    }
                }
                if (cmdType.equals("MD")) {
                    if (commandTokenizer.hasMoreTokens()) {
                        packet = new Packet(PacketType.MavenDeletions, ErrorType.None, sessionID);
                        while (commandTokenizer.hasMoreTokens()) {
                            packet.addAddress(createAddress(commandTokenizer.nextToken()));
                        }
                    } else {
                        System.err.println("MD Command Expects at least one argument");
                    }
                }

                if (packet != null) {
                    byte[] packetByteArray = packet.encode();
                    udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length, serverAddress, serverPort);
                    Sender newSender = new Sender(socket, udpPacket, packet.getSessionID());
                    newSender.start();
                }
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
    }

    static class Sender implements Runnable {
        private Thread t;
        private DatagramSocket socket;
        private DatagramPacket packet;
        private int sessionID;

        Sender(DatagramSocket socket, DatagramPacket packet, int sessionID) {
            this.socket = socket;
            this.packet = packet;
            this.sessionID = sessionID;
        }

        public void run() {
            try {
                int attempts = 0;
                Boolean receivedFlag = false;
                Packet newPacket = null;
                while(attempts < 3 && !receivedFlag) {
                    socket.setSoTimeout(3000);
                    socket.send(packet);
                    byte[] buffer = new byte[1534];
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    try {
                        socket.receive(responsePacket);
                        System.out.println(Arrays.toString(responsePacket.getData()));
                        System.out.println(responsePacket.getLength());
                        try {
                            newPacket = new Packet(Arrays.copyOf(responsePacket.getData(),responsePacket.getLength()));
                            if(newPacket.getType() != PacketType.AnswerRequest){
                                System.err.println("Unexpected Packet Type");
                                attempts++;
                            }
                            else if(newPacket.getSessionID() == sessionID || sessionID == 0){
                                receivedFlag = true;
                                System.out.println(newPacket);
                            }
                            else if(newPacket.getSessionID() != sessionID){
                                System.err.println("Unexpected Session ID: Expected: "
                                        + sessionID + " Received: " + newPacket.getSessionID());
                                attempts++;
                            }
                        }catch (IllegalArgumentException e){
                            System.err.println("Invalid Message: " + e.getMessage());
                            attempts++;
                        }catch (IOException e){
                            System.err.println("Communication problem: " + e.getMessage());
                            attempts++;
                        }
                    }catch (SocketTimeoutException e) {
                        attempts++;
                    }
                }
                if(!receivedFlag && newPacket.getType() == PacketType.RequestMavens ||
                        newPacket.getType() == PacketType.RequestNodes){
                    System.err.println("No Response from Server");
                }

            } catch (IOException e) {

            }
        }

        public void start() {
            if (t == null) {
                t = new Thread(this);
                t.start();
            }

        }
    }

    public static InetSocketAddress createAddress(String address){
        StringTokenizer addressTokenizer = new StringTokenizer(address);
        String newAddressIP = addressTokenizer.nextToken(":");
        int newAddressPort = Integer.parseInt(addressTokenizer.nextToken());

        return new InetSocketAddress(newAddressIP,newAddressPort);
    }
}


