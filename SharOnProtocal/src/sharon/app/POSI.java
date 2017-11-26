package sharon.app;

import mvn.serialization.ErrorType;
import mvn.serialization.Packet;
import mvn.serialization.PacketType;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class POSI {
    final private int MAXMVNS = 5;
    final private int NGHBRS = 10;
    final private int RFSH = 10;

    private Packet mavens;
    private Packet nodes;

    public POSI (InetSocketAddress startingNode){
        mavens = new Packet(PacketType.AnswerRequest, ErrorType.None,0);
        nodes = new Packet(PacketType.AnswerRequest, ErrorType.None,0);
        nodes.addAddress(startingNode);
    }

    public void addMaven(InetSocketAddress newMaven) throws IOException {
        Set<InetSocketAddress> addrList;
        Set<InetSocketAddress> mavenAddrList = mavens.getAddrList();
        if (mavenAddrList.size() < MAXMVNS && !mavenAddrList.contains(newMaven)) {
            mavens.addAddress(newMaven);
            addrList = requestInfo(newMaven, PacketType.RequestNodes);
            if(addrList != null){
                sendUnknownInfo(newMaven, addrList,PacketType.NodeAdditions);
                for(InetSocketAddress node: addrList){
                    nodes.addAddress(node);
                }
            }
            addrList = requestInfo(newMaven,PacketType.RequestMavens);
            if(addrList != null){
                sendUnknownInfo(newMaven, addrList,PacketType.MavenAdditions);
                for(InetSocketAddress maven : addrList){
                    addMaven(maven);
                }
            }
            else{
                System.out.println("Maven List was empty");
            }
        }
    }

    private Set<InetSocketAddress> requestInfo(InetSocketAddress dest,PacketType type) throws IOException {
        int sessionID = new Random().nextInt(256 - 1) + 1;
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
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if(newPacket.getAddrList().size() > 1){
            return newPacket.getAddrList();
        }
        else{
            System.out.println("returning null");
            return null;
        }
    }

    private void sendUnknownInfo(InetSocketAddress dest, Set<InetSocketAddress> destList, PacketType type) throws IOException {
        int sessionID = new Random().nextInt(256 - 1) + 1;

        Packet packet = new Packet(type, ErrorType.None, sessionID);
        Set<InetSocketAddress> addrList = null;

        if(type == PacketType.NodeAdditions){
            addrList = nodes.getAddrList();
        }else if (type == PacketType.MavenAdditions){
            addrList = mavens.getAddrList();
        }else{
            return;
        }

        for(InetSocketAddress addr : addrList){
            if(!destList.contains(addr)){
                packet.addAddress(addr);
            }
        }

        if(packet.getAddrList().size() > 0){
            byte [] packetByteArray = packet.encode();
            DatagramSocket udpSocket = new DatagramSocket();
            DatagramPacket udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length,
                    dest.getAddress(), dest.getPort());
            udpSocket.send(udpPacket);
            udpSocket.close();
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

    public Set<InetSocketAddress> getMavens(){
        return mavens.getAddrList();
    }

    public Set<InetSocketAddress> getNodes(){
        return nodes.getAddrList();
    }

}
