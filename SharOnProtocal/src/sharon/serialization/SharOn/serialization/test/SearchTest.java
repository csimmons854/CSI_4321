/************************************************
 *
 * Author: Chris Simmons and Mitchell Shannon
 * Assignment: Program1Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.serialization.test;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import sharon.serialization.BadAttributeValueException;
import sharon.serialization.RoutingService;
import sharon.serialization.Search;

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
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        						   , destinationAddress, searchString);
        
        assertEquals(id, srch.getID());
        assertEquals(ttl, srch.getTtl());
        assertEquals(routingService, srch.getRoutingService());
        assertEquals(sourceAddress, srch.getSourceAddress());
        assertEquals(destinationAddress, srch.getDestinationAddress());
        assertEquals(searchString, srch.getSearchString());
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
	public void searchConstructorBadIdLittleTest() throws BadAttributeValueException {
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
	public void searchConstructorBadTtlTest() throws BadAttributeValueException {
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
	public void searchConstructorBadRoutingServiceTest() throws BadAttributeValueException {
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
	public void searchConstructorBadSourceAddressBigTest() throws BadAttributeValueException {
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
	public void searchConstructorBadSourceAddressLittleTest() throws BadAttributeValueException {
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
	public void searchConstructorBadDestinationAddressBigTest() throws BadAttributeValueException {
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
	public void searchConstructorBadDestinationAddressLittleTest() throws BadAttributeValueException {
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
	public void searchConstructorNullStringTest() throws BadAttributeValueException {
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
	public void searchConstructorBadStringTest() throws BadAttributeValueException {
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
	public void searchConstructorMessageValidTest() throws IOException, BadAttributeValueException {
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
	public void seachConstructorBadMessageBigTest() throws IOException, BadAttributeValueException {
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
	public void searchConstructorBadMessageSmallTest() throws IOException, BadAttributeValueException {
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
	@Test
	public void searchStringInvalidTest() throws BadAttributeValueException {
		String string = "{}()[]";
		byte[] id = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int ttl = 0;
        RoutingService routingService = RoutingService.BREADTHFIRSTBROADCAST;
        byte[] sourceAddress = {0,0,0,0,0};
        byte[] destinationAddress = {0,0,0,0,0};
        java.lang.String searchString = "";
        
        Search srch = new Search(id, ttl, routingService, sourceAddress
        						   , destinationAddress, searchString);
        srch.setSearchString(string);
	}
}
