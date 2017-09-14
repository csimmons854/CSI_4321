/************************************************
 *
 * Author: Chris Simmons and Mitchell Shannon
 * Assignment: Program1Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

import static org.junit.Assert.*;
import static sharon.serialization.Message.decode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import sharon.serialization.*;

public class SearchTest {

	/**
	 * Tests search() success
	 */
	@Test
	public void searchConstructorValidTest() throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        						   , destinationAddress, searchString);
        Search srch1 = new Search(new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, 0,
				RoutingService.BREADTHFIRSTBROADCAST, new byte[] {0,0,0,0,0},
				new byte[] {0,0,0,0,0}, "" );
        
        assertEquals(srch, srch1);
	}
	
	/**
	 * Tests search() fail with id too big
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadIdBigTest() throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with id too small
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadIdLittleTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with bad ttl
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadTtlTest() throws BadAttributeValueException{
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with bad routing service
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadRoutingServiceTest() throws
            BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = null;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with source address too big
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadSourceAddressBigTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with source address too little
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadSourceAddressLittleTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with dest address too large
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadDestinationAddressBigTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for search() fail with dest address too small
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadDestinationAddressLittleTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests search() for fail with null string
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorNullStringTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = null;
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests search() for fail with invalid string
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadStringTest()
            throws BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = ")([] {}";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
				, destinationAddress, searchString);  
	}
	
	/**
	 * Tests for valid search(MessageInput)
	 */
	@Test
	public void searchConstructorMessageValidTest()
            throws IOException, BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(null);
	}
	
	/**
	 * Tests for search(MessageInput) when in is too large
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchConstructorBadMessageStringTest()
            throws IOException, BadAttributeValueException {
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = -1;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0,0};
        java.lang.String searchString = null;
        
        Search srch = new Search(null);
	}
	
	/**
	 * Tests for search(MessageInput) when in is too small
	 */
	@Test
	public void searchConstructorBadMessageSmallTest()
            throws IOException, BadAttributeValueException {
		byte[] id = {0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0};
        byte[] destinationAddress = {0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(null);
	}
	
	/**
	 * Tests for setting a valid string 
	 */
	@Test
	public void searchValidStringTest() throws BadAttributeValueException {
		String string = "good";
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        						   , destinationAddress, searchString);
        srch.setSearchString(string);
        
        assertEquals(string, srch.getSearchString());
	}
	
	/**
	 * Tests for a BadAttributeException when invalid chars are given
	 */
	@Test (expected = BadAttributeValueException.class)
	public void searchStringInvalidTest() throws BadAttributeValueException {
		String string = "{}()[]";
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        						   , destinationAddress, searchString);
        srch.setSearchString(string);
	}


	@Test
    public void encodeTest() throws BadAttributeValueException, IOException {
        byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        String searchString = "test";

        Search srch = new Search(id, ttl, routingService, sourceAddress
                , destinationAddress, searchString);
        srch.encode(new MessageOutput());
        System.err.println();
    }

	@Test
	public void decodeTest() throws BadAttributeValueException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 100;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};

        buffer.put((byte)0);
        buffer.put(id);
        buffer.put((byte)ttl);
        buffer.put((byte)0);
        buffer.put(sourceAddress);
        buffer.put(destinationAddress);
        buffer.put((byte)0);
        buffer.put((byte)5);
        buffer.put("test\n".getBytes(StandardCharsets.US_ASCII));
        ByteArrayInputStream newIn = new ByteArrayInputStream(buffer.array());
        MessageInput newMsg = new MessageInput(newIn);

		Search srch = (Search)decode(newMsg);
		assertEquals(new Search(id,ttl,RoutingService.BREADTHFIRSTBROADCAST,
                sourceAddress,destinationAddress,"test"),srch);
	}

}
