package sharon.app;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import mvn.serialization.*;

public class NodeAIO {
    private InetAddress mavenAddress;
    private int mavenPort;
    private int localPort;
    private int downloadPort;
    private Packet mavens;
    private Packet nodes;
    final private int MAXMVNS = 5;
    final private int NGHBRS = 10;
    final private int RFSH = 10;


    public NodeAIO(InetAddress mavenAddress, int mavenPort, int localPort, int downloadPort) {
        this.mavenAddress = mavenAddress;
        this.mavenPort = mavenPort;
        this.localPort = localPort;
        this.downloadPort = downloadPort;
    }

    public void start() {

        try {
            addMaven(new InetSocketAddress(mavenAddress, mavenPort));


        } catch (IOException e) {
            System.err.println("Failed to establish connection to maven");
        }

    }

    private void addMaven(InetSocketAddress newMaven) throws IOException {
        Set<InetSocketAddress> addrList;
        Set<InetSocketAddress> mavenAddrList = mavens.getAddrList();
        if (mavenAddrList.size() <= 5) {
            mavens.addAddress(newMaven);
            addrList = requestInfo(newMaven, PacketType.RequestNodes);
            sendUnknownInfo(newMaven, addrList,PacketType.NodeAdditions);
            if(addrList != null){
                for(InetSocketAddress node: addrList){
                    nodes.addAddress(node);
                }
            }
            addrList = requestInfo(newMaven,PacketType.RequestMavens);
            sendUnknownInfo(newMaven, addrList,PacketType.MavenAdditions);
            if(addrList != null){
                for(InetSocketAddress maven : addrList){
                    addMaven(maven);
                }
            }
        }
    }

    private Set<InetSocketAddress> requestInfo(InetSocketAddress dest,PacketType type) throws IOException {
        int sessionID = new Random().nextInt(256) + 1;
        Packet packet = new Packet(type, ErrorType.None, sessionID);
        boolean receivedFlag = false;
        boolean IOExceptionFlag = false;
        byte[] packetByteArray = packet.encode();
        DatagramSocket udpSocket = new DatagramSocket();
        DatagramPacket udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length,
                dest.getAddress(), dest.getPort());
        udpSocket.send(udpPacket);
        Packet newPacket = null;
        int attempts = 0;
        try {
            while (attempts < 3 && !receivedFlag && !IOExceptionFlag) {
                udpSocket.setSoTimeout(3000);
                byte[] buffer = new byte[1534];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                try {
                    udpSocket.receive(responsePacket);
                    try {
                        newPacket = new Packet(Arrays.copyOf(responsePacket.getData(), responsePacket.getLength()));
                        if (newPacket.getType() == PacketType.AnswerRequest &&
                                (newPacket.getSessionID() == sessionID || sessionID == 0)) {
                                receivedFlag = true;
                        }
                    } catch (IOException e) {
                        IOExceptionFlag = true;
                    }
                } catch (SocketTimeoutException e) {
                    attempts++;
                    udpSocket.send(udpPacket);
                }
            }
            if (!receivedFlag && !IOExceptionFlag && (packet.getType() == PacketType.RequestMavens ||
                    packet.getType() == PacketType.RequestNodes)) {
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if(newPacket != null){
            return newPacket.getAddrList();
        }
        else{
            return null;
        }
    }

    public void sendUnknownInfo(InetSocketAddress dest, Set<InetSocketAddress> destList, PacketType type) throws IOException {
        int sessionID = new Random().nextInt(256) + 1;

        Packet packet = new Packet(type, ErrorType.None, sessionID);
        for(InetSocketAddress addr : (Set<InetSocketAddress>) mavens.getAddrList()){
            if(destList.contains(addr)){
                packet.addAddress(addr);
            }
        }

        byte [] packetByteArray = packet.encode();
        DatagramSocket udpSocket = new DatagramSocket();
        DatagramPacket udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length,
                dest.getAddress(), dest.getPort());
        if(packet.getAddrList().size() > 0){
            udpSocket.send(udpPacket);
        }
    }

    private void removeNodeOrMaven(InetSocketAddress dest, InetSocketAddress addr, PacketType type) throws Exception {
        if(type == PacketType.MavenDeletions || type == PacketType.NodeDeletions) {
            int sessionID = new Random().nextInt(256) + 1;
            Packet packet = new Packet(type, ErrorType.None, sessionID);
            packet.addAddress(addr);
            byte[] packetByteArray = packet.encode();
            DatagramSocket udpSocket = new DatagramSocket();
            DatagramPacket udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length,
                    dest.getAddress(), dest.getPort());
            udpSocket.send(udpPacket);
        }else{
            throw new Exception("Invalid PacketType Argument");
        }
    }
}

