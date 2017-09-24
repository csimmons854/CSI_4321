/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program1Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
import org.junit.Test;
import sharon.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static sharon.serialization.Message.decode;

/**
 * Created by Chris on 9/11/2017.
 */
public class ResponseTest {

    @Test
    /*
        Test that the constructor actually assigns the values correctly
     */
    public void ResponseConstructorSuccessTest() throws Exception
    {
        byte [] idTest = "000000000000000".getBytes();
        int ttlTest = 100;
        RoutingService routingServiceTest = RoutingService.BREADTHFIRSTBROADCAST;
        byte [] srcTest = "00000".getBytes();
        byte [] destTest = "00000".getBytes();
        int port = 256;
        byte[] iNetAddress = {15,15,15,15};
        java.net.InetSocketAddress responseHost =
                new InetSocketAddress(
                        InetAddress.getByAddress(iNetAddress),port);

        Response responseTest = new Response("000000000000000".getBytes(), 100,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),responseHost);
        assertEquals(new String(idTest),new String (responseTest.getID()));
        assertEquals(ttlTest,responseTest.getTtl());
        assertEquals(routingServiceTest,responseTest.getRoutingService());
        assertEquals(new String(srcTest),new String (responseTest.getSourceAddress()));
        assertEquals(new String(destTest),new String (responseTest.getDestinationAddress()));
        assertEquals(responseHost,responseTest.getResponseHost());
    }

    /*
        Test that an exception is thrown if TTL is negative
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseNegativeTTLTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), -1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test that an exception is thrown if TTL is larger than 255
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToLargeTTLTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 256,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test for if the the ID length is small
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToSmallIDTest() throws Exception
    {
        Response responseTest = new Response("0".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test for if the the ID length is larger than 15 bytes
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToLargeIDTest() throws Exception
    {
        Response responseTest = new Response("0000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
        System.err.println("000000000000000".getBytes().length);
    }

    /*
        Test for if the the ID length is null
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseNullIDTest() throws Exception
    {
        Response responseTest = new Response(null, 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test for if the the RoutingService is null
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseBadRoutingServiceTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                null, "00000".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test for if the the Src is larger than 5 bytes
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToLargeSrcTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "000000".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test for if the the Src is smaller than 5 bytes
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToSmallSrcTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "0".getBytes(),
                "00000".getBytes(),null);
    }

    /*
        Test for if the the Src is null
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseNullSrcTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, null,
                "00000".getBytes(),null);
    }

    /*
        Test for if the the dest is to small
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToSmallDestTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "0".getBytes(),null);
    }

    /*
        Test for if the the dest is too large
    */
    @Test (expected = BadAttributeValueException.class)
    public void ResponseToLargeDestTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "000000".getBytes(),null);
    }

    @Test (expected = BadAttributeValueException.class)
    public void ResponseNullDestTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                null,null);
    }

    @Test (expected = BadAttributeValueException.class)
    public void ResponseNullResponseTest() throws Exception
    {
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
    }

    @Test
    public void ResponseMessageInputConstructorSuccessTest() throws Exception
    {
        byte [] idTest = "000000000000000".getBytes();
        int ttlTest = 100;
        RoutingService routingServiceTest = RoutingService.BREADTHFIRSTBROADCAST;
        byte [] srcTest = "00000".getBytes();
        byte [] destTest = "00000".getBytes();
        java.net.InetSocketAddress testResponseHost = null;

        Response responseTest = new Response(null); //this will change

        assertEquals(idTest,responseTest.getID());
        assertEquals(ttlTest,responseTest.getTtl());
        assertEquals(routingServiceTest,responseTest.getRoutingService());
        assertEquals(srcTest,responseTest.getSourceAddress());
        assertEquals(destTest,responseTest.getDestinationAddress());
        assertEquals(testResponseHost,responseTest.getResponseHost());
    }

    @Test
    public void decodeTest() throws BadAttributeValueException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 100;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        byte matches = 2;
        int port = 256;
        byte[] iNetAddress = {15,15,15,15};
        java.net.InetSocketAddress responseHost =
                new InetSocketAddress(
                InetAddress.getByAddress(iNetAddress),port);

        MessageOutput msgOut = new MessageOutput();
        MessageOutput msgOut1 = new MessageOutput();
        ;
        Result rslt = new Result(2,2,"test");
        Result rslt1 = new Result(1,2,"test");

        List<Result> rsltList = new ArrayList<Result>();
        rsltList.add(rslt);
        rsltList.add(rslt1);

        rsltList.get(0).encode(msgOut);
        rsltList.get(1).encode(msgOut1);

        buffer.put((byte)2);
        buffer.put(id);
        buffer.put((byte)ttl);
        buffer.put((byte)0);
        buffer.put(sourceAddress);
        buffer.put(destinationAddress);
        buffer.putShort((short) 15);
        buffer.put(matches);
        buffer.putShort((short)port);
        buffer.put(iNetAddress);
        buffer.put(((ByteArrayOutputStream)msgOut.getMsgOut()).toByteArray());
        buffer.put(((ByteArrayOutputStream)msgOut1.getMsgOut()).toByteArray());
        ByteArrayInputStream newIn2 = new ByteArrayInputStream(buffer.array());
        MessageInput newMsg2 = new MessageInput(newIn2);

        Response response = (Response) decode(newMsg2);
        Response response1 = new Response(id,ttl,
                RoutingService.BREADTHFIRSTBROADCAST,
                sourceAddress,destinationAddress,responseHost);
        response1.addResult(rslt);
        response1.addResult(rslt1);
        assertEquals(response1,response);
    }

    @Test
    public void encodeTest() throws BadAttributeValueException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MessageOutput msgOut = new MessageOutput();

        byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 100;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        int port = 256;
        byte[] iNetAddress = {15,15,15,15};
        java.net.InetSocketAddress responseHost =
                new InetSocketAddress(
                        InetAddress.getByAddress(iNetAddress),port);


        Result rslt = new Result(69,233,"test");
        Result rslt1 = new Result(214,233,"test");


        Response response1 = new Response(id,ttl,
                RoutingService.BREADTHFIRSTBROADCAST,
                sourceAddress,destinationAddress,responseHost);
        response1.addResult(rslt);
        response1.addResult(rslt1);

        response1.encode(msgOut);
        //System.err.println(Arrays.toString(((ByteArrayOutputStream) msgOut.getMsgOut()).toByteArray()));

    }

    @Test (expected = BadAttributeValueException.class)
    public void setterTest() throws BadAttributeValueException, IOException
    {
        int port = 256;
        byte[] iNetAddress = {15,15,15,15};
        byte[] sourceTest = {15,15,15,15};
        java.net.InetSocketAddress responseHost =
                new InetSocketAddress(InetAddress.getByAddress(iNetAddress),port);

        Response responseTest = new Response("000000000000000".getBytes(), 100,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),responseHost);
        responseTest.setSourceAddress(sourceTest);
    }
}