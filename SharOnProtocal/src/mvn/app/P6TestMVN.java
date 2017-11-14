package mvn.app;

import static java.util.Arrays.copyOf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.xml.bind.DatatypeConverter;

public class P6TestMVN {
    
    private static final int QUERYMAX = 65535;
    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            throw new IllegalArgumentException( "Parameter(s): <Server> <Port>");

        InetAddress server = InetAddress.getByName(args[0]);
        int servPort = Integer.parseInt(args[1]);

        DatagramSocket socket = new DatagramSocket();
        socket.connect(server, servPort);
        
        // Test 1 Node
        testNode(socket);
        
        // Test 1 Maven
        testMaven(socket);
        
        // Test 2
        testAR(socket);
        
        // Test 3
        testMaxQueryID(socket);
        
        // Test 4
        testIncorrectVersion(socket);
        
        // Test 5
        testShortPacket(socket);
        
        socket.close();
    }
    
    private static void testNode(DatagramSocket socket) throws IOException {
        System.out.println("Test 1 - Node");
        System.out.println("**********");
        // RN – expect no results
        System.out.println("Sending RN");
        send(socket, "4000C100");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200C100");
        
        // NA 1.1.1.1:1
        System.out.println("Sending NA 1.1.1.1:1");
        send(socket, "43003101010101010001");
        
        // RN – expect 1.1.1.1:1
        System.out.println("Sending RN");
        send(socket, "4000C200");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200C201010101010001");
        
        // NA 255.255.255.255:64000
        System.out.println("Sending NA 1.1.1.1:1");
        send(socket, "43001B01FFFFFFFFFA00");
        
        // RN – expect 1.1.1.1:1, 255.255.255.255:64000
        System.out.println("Sending RN");
        send(socket, "4000C300");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200C302010101010001FFFFFFFFFA00");
        
        // NA 1.1.1.1:1
        System.out.println("Sending NA 1.1.1.1:1");
        send(socket, "43003201010101010001");
        
        // RN – expect 1.1.1.1:1, 255.255.255.255:64000
        System.out.println("Sending RN");
        send(socket, "4000C400");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200C402010101010001FFFFFFFFFA00");
        
        // ND 255.255.255.255:64000
        System.out.println("Sending ND 255.255.255.255:64000");
        send(socket, "45008301FFFFFFFFFA00");
        
        // RN – expect 1.1.1.1:1
        System.out.println("Sending RN");
        send(socket, "4000C500");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200C501010101010001");
        
        // ND 1.1.1.1:2
        System.out.println("Sending ND 1.1.1.1:2");
        send(socket, "45003301010101010002");
        
        // RN – expect 1.1.1.1:1
        System.out.println("Sending RN");
        send(socket, "4000C600");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200C601010101010001");
        
        // ND 1.1.1.1:1
        System.out.println("Sending ND 1.1.1.1:1");
        send(socket, "45005201010101010001");
        
        // RN – expect no results
        System.out.println("Sending RN");
        send(socket, "40003500");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "42003500");
        
        // NA 2.2.2.2:2 5.5.5.5:5
        System.out.println("Sending NA 2.2.2.2:2 5.5.5.5:5");
        send(socket, "4300C702020202020002050505050005");
        
        // RN – expect 2.2.2.2:2, 5.5.5.5:5
        System.out.println("Sending RN");
        send(socket, "4000A100");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200A102020202020002050505050005");
    }
    
    private static void testMaven(DatagramSocket socket) throws IOException {
        System.out.println("Test 1 - Maven");
        System.out.println("**********");
        // RM – expect no results
        System.out.println("Sending RM");
        send(socket, "41007600");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "42007600");
        
        // MD 1.1.1.1:1
        System.out.println("Sending MD 1.1.1.1:1");
        send(socket, "46007701010101010001");
        
        // RM – expect no results
        System.out.println("Sending RM");
        send(socket, "41007700");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "42007700");
        
        // MA 3.3.3.3:3
        System.out.println("Sending MA 3.3.3.3:3");
        send(socket, "44001301030303030003");
        
        // MA 4.4.4.4:4
        System.out.println("Sending MA 4.4.4.4:4");
        send(socket, "4400F801040404040004");
        
        // RM – expect 3.3.3.3:3, 4.4.4.4:4
        System.out.println("Sending RM");
        send(socket, "41003300");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "42003302030303030003040404040004");
        
        // MD 3.3.3.3:3
        System.out.println("Sending MD 3.3.3.3:3");
        send(socket, "46004E01030303030003");
        
        // RM – expect 4.4.4.4:4
        System.out.println("Sending RM");
        send(socket, "41006A00");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "42006A01040404040004");
        
        // RN – expect 2.2.2.2:2, 5.5.5.5:5
        System.out.println("Sending RN");
        send(socket, "4000B100");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200B102020202020002050505050005");
    }
    
    private static void testAR(DatagramSocket socket) throws IOException {
        System.out.println("Test 2");
        System.out.println("**********");
        // RM – expect no results
        System.out.println("Sending AR 3.3.3.3:3");
        send(socket, "4200DF01030303030003");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4214DF00");
    }
    
    private static void testMaxQueryID(DatagramSocket socket) throws IOException {
        System.out.println("Test 3");
        System.out.println("**********");
        // RN – expect no results
        System.out.println("Sending RN with Max session ID");
        send(socket, "4000FF00");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "4200FF02020202020002050505050005");
    }
    
   
    private static void testIncorrectVersion(DatagramSocket socket) throws IOException {
        System.out.println("Test 4");
        System.out.println("**********");
        // RN – expect no results
        System.out.println("Sending RN with bad version");
        send(socket, "7000FF00");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "42140000");
    }
    
    private static void testShortPacket(DatagramSocket socket) throws IOException {
        System.out.println("Test 5");
        System.out.println("**********");
        // RN – expect no results
        System.out.println("Sending RN");
        send(socket, "4000");
        System.out.println("RCVD: " + receive(socket));
        System.out.println("EXPD: " + "420A0000");
    }
    
    private static void send(DatagramSocket socket, String hex) throws IOException {
        byte[] sndBuffer = DatatypeConverter.parseHexBinary(hex);
        DatagramPacket sendPacket = new DatagramPacket(sndBuffer, sndBuffer.length);
        socket.send(sendPacket);
    }
    
    private static String receive(DatagramSocket socket) throws IOException {
        DatagramPacket rcvDatagram = new DatagramPacket(new byte[QUERYMAX], QUERYMAX);
        socket.receive(rcvDatagram);
        return DatatypeConverter.printHexBinary(copyOf(rcvDatagram.getData(), rcvDatagram.getLength()));
    }
}
