/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program1Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
import org.junit.Test;
import sharon.serialization.BadAttributeValueException;
import sharon.serialization.MessageInput;
import sharon.serialization.Response;
import sharon.serialization.RoutingService;

import static org.junit.Assert.*;

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
        java.net.InetSocketAddress testResponseHost = null;

        Response responseTest = new Response("000000000000000".getBytes(), 100,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);

        assertEquals(idTest,responseTest.getID());
        assertEquals(ttlTest,responseTest.getTtl());
        assertEquals(routingServiceTest,responseTest.getRoutingService());
        assertEquals(srcTest,responseTest.getSourceAddress());
        assertEquals(destTest,responseTest.getDestinationAddress());
        assertEquals(testResponseHost,responseTest.getResponseHost());
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
        Response responseTest = new Response("000000000000000".getBytes(), 1,
                RoutingService.BREADTHFIRSTBROADCAST, "00000".getBytes(),
                "00000".getBytes(),null);
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
}