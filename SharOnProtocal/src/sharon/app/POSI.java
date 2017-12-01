/*

 Author: Chris Simmons
 Assignment: Program 7
 Class: CSI 4321 Data Communications

 */
package sharon.app;

import mvn.serialization.ErrorType;
import mvn.serialization.Packet;
import mvn.serialization.PacketType;

import java.io.IOException;
import java.net.*;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * POSI class for managing mavens and nodes
 */
public class POSI implements Runnable {
    private Thread t;
    final private int NGHBRS = 10;

    private Packet mavens;
    private Packet nodes;
    private InetSocketAddress localAddress;
    private InetSocketAddress startingMaven;
    private Logger log;

    /**
     * @param startingNode first node to be added
     * @param log log file
     * @param startingMaven first Maven to be added
     * @throws IOException thwon if a bad packet was formed
     */
    POSI(InetSocketAddress startingNode, Logger log, InetSocketAddress startingMaven) throws IOException {
        this.log = log;
        localAddress = startingNode;
        mavens = new Packet(PacketType.AnswerRequest, ErrorType.None,0);
        nodes = new Packet(PacketType.AnswerRequest, ErrorType.None,0);
        this.startingMaven = startingMaven;
    }

    @Override
    public void run() {
        nodes.addAddress(localAddress);
        try {
            addMaven(startingMaven);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            int RFSH = 10;
            Thread.sleep(RFSH *60*1000);
        } catch (InterruptedException e) {
            log.severe(e.getMessage());
        }
        try {
            posiRefresh();
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

    /**
     * @param newMaven maven ot be added
     * @throws IOException thrown for bad packet formation
     */
    private void addMaven(InetSocketAddress newMaven) throws IOException {
        Set<InetSocketAddress> addrList;
        Set mavenAddrList = mavens.getAddrList();
        int MAXMVNS = 5;
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
        }
    }

    /**
     * @param dest destination to get information from
     * @param type type of packet to be sent
     * @return a set of the information retrieved
     * @throws IOException thrown for bad packet formation
     */
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
        if(attempts == 3){
            mavens.delAddress(dest);
            for(InetSocketAddress addr : mavens.getAddrList()){
                removeNodeOrMaven(addr,dest,PacketType.MavenDeletions);
            }
        }
        if(newPacket != null && newPacket.getAddrList().size() > 0){
            return newPacket.getAddrList();
        }
        else{
            return null;
        }

    }

    private void sendUnknownInfo(InetSocketAddress dest, Set<InetSocketAddress> destList, PacketType type) throws IOException {
        int sessionID = new Random().nextInt(256 - 1) + 1;

        Packet packet = new Packet(type, ErrorType.None, sessionID);
        Set<InetSocketAddress> addrList;

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

    public void removeNodeOrMaven(InetSocketAddress dest, InetSocketAddress addr, PacketType type) throws IOException {
        if(type == PacketType.MavenDeletions || type == PacketType.NodeDeletions) {
            if(type == PacketType.NodeDeletions){
                if(nodes.getAddrList().contains(addr)) {
                    nodes.delAddress(addr);
                }
            }

            int sessionID = new Random().nextInt(256) + 1;
            Packet packet = new Packet(type, ErrorType.None, sessionID);
            packet.addAddress(addr);
            byte[] packetByteArray = packet.encode();
            DatagramSocket udpSocket = new DatagramSocket();
            DatagramPacket udpPacket = new DatagramPacket(packetByteArray, packetByteArray.length,
                    dest.getAddress(), dest.getPort());
            udpSocket.send(udpPacket);
        }
    }

    Set<InetSocketAddress> getMavens(){
        return mavens.getAddrList();
    }

    Set<InetSocketAddress> getNodes(){
        return nodes.getAddrList();
    }

     ConcurrentHashMap<InetSocketAddress, AsynchronousSocketChannel> getChannelSet() throws IOException {
        ConcurrentHashMap<InetSocketAddress, AsynchronousSocketChannel> channelSet = new ConcurrentHashMap<>();
            Vector<InetSocketAddress> availableNodes = new Vector<>();
            for(InetSocketAddress address : nodes.getAddrList()){
                if(address != localAddress) {
                    availableNodes.add(address);
                }
            }
            while (channelSet.size() < NGHBRS && !availableNodes.isEmpty()){
                int r = new Random().nextInt(availableNodes.size());
                ConnectionAIO newCon = new ConnectionAIO(log);
                newCon.initiateConnection(availableNodes.get(r));
                channelSet.put(availableNodes.get(r), newCon.getClientChannel());
                availableNodes.remove(r);
            }
        return channelSet;
    }

    int getNGHBRS(){
        return NGHBRS;
    }

    private void posiRefresh() throws IOException {
        Vector<InetSocketAddress> temp = new Vector<>();
        nodes = new Packet(PacketType.AnswerRequest, ErrorType.None,0);
        for (AsynchronousSocketChannel socket: this.getChannelSet().values()) {
            nodes.addAddress((InetSocketAddress)socket.getRemoteAddress());
        }
        for(InetSocketAddress maven : mavens.getAddrList()){
            mavens.delAddress(maven);
            temp.add(maven);
        }
        for(InetSocketAddress newMaven: temp){
            addMaven(newMaven);
        }
    }


}
