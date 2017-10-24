/************************************************
 *
 * Author: Chris Simmons and Mitchell Shannon
 * Assignment: Program1Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package mvn.serialization.test;

import mvn.serialization.ErrorType;
import mvn.serialization.Packet;
import mvn.serialization.PacketType;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import static mvn.serialization.ErrorType.None;
import static mvn.serialization.PacketType.AnswerRequest;
import static org.junit.Assert.*;


public class packetTest {
    /**
     * Test for Packet(Byte [] buffer) constructor success
     */
    @Test
    public void packetByteArrayConstructorSuccessTest() throws IOException,
            IllegalArgumentException {
        byte [] buffer = null;
        PacketType type = AnswerRequest;
        ErrorType error = None;
        //Will build buffer later when I implement constructor

        Packet packet1 = new Packet(buffer);
        Packet packet2 = new Packet(type,error,0);

        assertEquals(packet2,packet1);
    }

    /**
     * Test for Packet(Byte [] buffer) buffer too large Exception
     */
    @Test (expected = IOException.class)
    public void packetByteArrayConstructorBufferToLongTest() throws IOException,
            IllegalArgumentException {
        byte [] buffer = new byte[1563];

        new Packet(buffer);
    }

    /**
     * Test for Packet(Byte [] buffer) buffer too small Exception
     */
    @Test (expected = IOException.class)
    public void packetByteArrayConstructorBufferToShortTest() throws IOException,
            IllegalArgumentException {
        byte [] buffer = new byte[0];

        new Packet(buffer);
    }

    /**
     * Test for Packet(Byte [] buffer) buffer null Exception
     */
    @Test (expected = IOException.class)
    public void packetByteArrayConstructorBufferNullTest() throws IOException,
            IllegalArgumentException {
        byte [] buffer = null;

        new Packet(buffer);
    }

    /**
     * Test for Packet(Byte [] buffer) invalid packet type exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void packetByteArrayConstructorBufferBadTypeTest() throws
            IOException, IllegalArgumentException {
        byte [] buffer = null;
        //Will build buffer later when I implement constructor

        new Packet(buffer);
    }

    /**
     * Test for Packet(Byte [] buffer) invalid error type exception
     */
    @Test (expected = IllegalArgumentException.class)
    public void packetByteArrayConstructorBufferBadErrorTest() throws
            IOException, IllegalArgumentException {
        byte [] buffer = null;
        //Will build buffer later when I implement constructor

        new Packet(buffer);
    }

    /**
     * Test for Packet(PacketType, ErrorType, Int) constructor success
     */
    @Test
    public void packetConstructorSuccessTest() throws
            IOException, IllegalArgumentException {
        Packet packet1 = new Packet(AnswerRequest,None,0);

        assertTrue(packet1.getError() == None &&
                    packet1.getType() == AnswerRequest &&
                    packet1.getSessionID() == 0);
    }

    /**
     * Test for Packet(PacketType, ErrorType, Int) invalid session ID
     * too small
     */
    @Test (expected = IllegalArgumentException.class)
    public void packetConstructorBadPacketTypeTooSmallTest() throws
            IOException, IllegalArgumentException {
        Packet packet1 = new Packet(AnswerRequest,None,-1);

    }


    /**
     * Test for Packet(PacketType, ErrorType, Int) invalid session ID
     * too large
     */
    @Test (expected = IllegalArgumentException.class)
    public void packetConstructorBadPacketTypeTooLargeTest() throws
            IOException, IllegalArgumentException {
        Packet packet1 = new Packet(AnswerRequest,None,256);

    }

    /**
     * Test that Encode returns the same byte [] given to the Packet constructor
     */
    @Test
    public void encodeSuccessTest() throws Exception {
        //Buffer to compare against
        //Will be implemented later
        byte [] buffer = null;
        Packet packet = new Packet(buffer);

        assertArrayEquals(packet.encode(),buffer);
    }

    /**
     * test that getType returns the same PacketType given to the Packet
     */
    @Test
    public void getType() throws Exception {
        PacketType type = AnswerRequest;
        Packet packet = new Packet(AnswerRequest,None,0);
        assertEquals(type,packet.getType());
    }

    /**
     * Tests for getError success
     */
    @Test
    public void getErrorMatchTest() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        Packet testPacket = new Packet (pType, eType, 0);
        assertEquals(testPacket.getError(), eType);
    }

    /**
     * Tests for getError failure
     */
    @Test
    public void getErrorMismatchTest() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        ErrorType eTest = ErrorType.System;
        Packet testPacket = new Packet (pType, eType, 0);
        assertNotEquals(testPacket.getError(), eTest);
    }

    /**
     * Tests for setSessionID success
     */
    @Test
    public void setSessionIDMatchTest() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        int sessionSet = 2;
        Packet testPacket = new Packet (pType, eType, sessionTest);
        testPacket.setSessionID(sessionSet);
        assertEquals(testPacket.getSessionID(), sessionSet);
    }

    /**
     *  Tests for setSessionID failure
     */
    @Test
    public void setSessionIDMismatchTest() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        int sessionSet = 2;
        Packet testPacket = new Packet (pType, eType, sessionTest);
        testPacket.setSessionID(sessionSet);
        assertNotEquals(testPacket.getSessionID(), sessionTest);
    }

    /**
     * Tests for setSessionID illegal session
     */
    @Test (expected = IllegalArgumentException.class)
    public void setSessionIDIllegalArgumentSmallTest() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);
        testPacket.setSessionID(-1);
    }

    /**
     * Tests for setSessionID illegal session
     */
    @Test (expected = IllegalArgumentException.class)
    public void setSessionIDIllegalArgumentLargeTest() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);
        testPacket.setSessionID(256);
    }

    /**
     * Tests for getSessionID success
     */
    @Test
    public void getSessionIDMatch() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);
        assertEquals(testPacket.getSessionID(), sessionTest);
    }

    /**
     * Test for getSessionID failure
     */
    @Test
    public void getSessionIDMismatch() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        int sessionSet = 2;
        Packet testPacket = new Packet (pType, eType, sessionTest);
        assertNotEquals(testPacket.getSessionID(), sessionSet);
    }

    /**
     * Tests for adAddress success
     */
    @Test
    public void addAddressSuccess() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);

        InetAddress addr = InetAddress.getLocalHost();
        int port = 80;
        InetSocketAddress sockAddr = new InetSocketAddress(addr, port);
        testPacket.addAddress(sockAddr);
        assertEquals(testPacket.getAddrList(), sockAddr);
    }

    /**
     * Tests for adAddress failure due to bad port
     */
    @Test (expected = IllegalArgumentException.class)
    public void addAddressFailureBadPort() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);

        InetAddress addr = InetAddress.getLocalHost();
        int port = -500;
        InetSocketAddress sockAddr = new InetSocketAddress(addr, port);
        testPacket.addAddress(sockAddr);
    }

    /**
     * Tests for addAddress failure due to too many addr
     */
    @Test (expected = IllegalArgumentException.class)
    public void addAddressFailureTooManyAddr() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);

        InetAddress addr = InetAddress.getLocalHost();
        for(int i = 1; i <= 256; i++) {
            InetSocketAddress sockAddr = new InetSocketAddress(addr, i);
            testPacket.addAddress(sockAddr);
        }
    }

    /**
     * Tests for addAddress failure due to Request Maven type
     */
    @Test (expected = IllegalArgumentException.class)
    public void addAddressFailureRequestMavens() throws Exception {
        PacketType pType = PacketType.RequestMavens;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);

        InetAddress addr = InetAddress.getLocalHost();
        int port = 10;
        InetSocketAddress sockAddr = new InetSocketAddress(addr, port);
        testPacket.addAddress(sockAddr);
    }

    /**
     * Tests addAddress failure due to RequestNode type
     */
    @Test (expected = IllegalArgumentException.class)
    public void addAddressFailureRequestNodes() throws Exception {
        PacketType pType = PacketType.RequestNodes;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);

        InetAddress addr = InetAddress.getLocalHost();
        int port = 10;
        InetSocketAddress sockAddr = new InetSocketAddress(addr, port);
        testPacket.addAddress(sockAddr);
    }

    /**
     * Tests for getAddrList success
     */
    @Test
    public void getAddrList() throws Exception {
        PacketType pType = PacketType.AnswerRequest;
        ErrorType eType = ErrorType.None;
        int sessionTest = 3;
        Packet testPacket = new Packet (pType, eType, sessionTest);

        InetAddress addr = InetAddress.getLocalHost();
        int port = 80;
        int port2 = 90;
        InetSocketAddress sockAddr = new InetSocketAddress(addr, port);
        InetSocketAddress sockAddr2 = new InetSocketAddress(addr, port2);
        Set<InetSocketAddress> check = new HashSet<InetSocketAddress>();

        check.add(sockAddr);
        check.add(sockAddr2);
        testPacket.addAddress(sockAddr);
        testPacket.addAddress(sockAddr2);

        assertEquals(testPacket.getAddrList(), check);
    }

}