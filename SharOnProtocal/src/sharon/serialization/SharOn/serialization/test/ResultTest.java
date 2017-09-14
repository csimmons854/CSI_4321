/************************************************
 *
 * Author: Chris Simmons, Mitchell Shannon
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import sharon.serialization.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 *  JUnit test suite for the result class
 */
public class ResultTest
{
    private Result rslt;
    private MessageInput in;
    private MessageOutput out;

    @Test
    public void resultMessageConstructorSuccessTest() throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(1); //fileID
        buffer.putInt(2); //fileSize
        buffer.put("test\n".getBytes(StandardCharsets.US_ASCII));
        ByteArrayInputStream newIn = new ByteArrayInputStream(buffer.array());
        MessageInput newMsg = new MessageInput(newIn);
        rslt = new Result(newMsg);
        assertEquals(1, rslt.getFileID());
        assertEquals(2, rslt.getFileSize());
        assertEquals("test", rslt.getFileName());

    }

    @Test
    public void resultMessageConstructorConvertToUnsignedTest() throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(-1); //fileID
        buffer.putInt(2);
        buffer.put("test\n".getBytes(StandardCharsets.US_ASCII));
        ByteArrayInputStream newIn = new ByteArrayInputStream(buffer.array());
        MessageInput newMsg = new MessageInput(newIn);
        rslt = new Result(newMsg);
        assertEquals(4294967295L, rslt.getFileID());
    }


    @Test (expected = BadAttributeValueException.class)
    public void resultMessageConstructorExceptionTest() throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(-1);
        buffer.putInt(2);
        buffer.put("test++\n".getBytes(StandardCharsets.US_ASCII));
        ByteArrayInputStream newIn = new ByteArrayInputStream(buffer.array());
        MessageInput newMsg = new MessageInput(newIn);
        rslt = new Result(newMsg);
    }

    @Test
    public void resultConstructorParametersSuccessTest() throws Exception
    {
        rslt = new Result(1,1,"test");
    }

    @Test (expected = BadAttributeValueException.class)
    public void resultConstructorFileIdExceptionTest() throws Exception
    {
        rslt = new Result(-1,1,"test");
    }

    @Test (expected = BadAttributeValueException.class)
    public void resultConstructorFileSizeExceptionTest() throws Exception
    {
        rslt = new Result(1,-1,"test");
    }

    @Test (expected = BadAttributeValueException.class)
    public void resultConstructorFileNameExceptionTest() throws Exception
    {
        rslt = new Result(1,1,null);
    }


	@Test
	public void encodeSuccessTest() throws Exception {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        temp.write(ByteBuffer.allocate(4).putInt((int)10).array());
        temp.write(ByteBuffer.allocate(4).putInt((int)20).array());
        temp.write("Test.txt\n".getBytes());
        out = new MessageOutput(temp);
        MessageOutput test = new MessageOutput();
        Result rslt1 = new Result(10,20,"Test.txt");
        rslt1.encode(test);
        //System.err.println(test.getMsgOut().toString());
        assertEquals(out.getMsgOut().toString(),test.getMsgOut().toString());
	}

	@Test
	public void toStringSuccessTest() throws Exception {
        rslt = new Result(892145433,12,"test");
		assertEquals(rslt.toString(),"892145433, 12, test");
	}

	@Test
	public void getFileIDTest() throws Exception {
	    Result testRslt = new Result(10,10,"10");
	    assertEquals(testRslt.getFileID(),10);
	}

	@Test
	public void setFileIDTest() throws Exception {
        rslt = new Result(1,1,"test");
		rslt.setFileID(10);
		assertEquals(10L,rslt.getFileID());
	}

    @Test (expected = BadAttributeValueException.class)
    public void setFileIDExceptionTest() throws Exception {
        rslt = new Result(1,1,"test");
        rslt.setFileID(-1);
    }

	@Test
	public void getFileSizeTest() throws Exception {
        Result testRslt = new Result(10,10,"10");
        assertEquals(testRslt.getFileSize(),10);
	}

	@Test
	public void setFileSizeTest() throws Exception {
	    rslt = new Result(1,1,"test");
		rslt.setFileSize(10);
		assertEquals(10L,rslt.getFileSize());
	}

    @Test (expected = BadAttributeValueException.class)
    public void setFileSizeExceptionTest() throws Exception {
        rslt = new Result(1,1,"test");
        rslt.setFileSize(-1);
    }

	@Test
	public void getFileNameTest() throws Exception {
        Result testRslt = new Result(10,10,"10");
        assertEquals(testRslt.getFileName(),"10");
	}

	@Test
	public void setFileNameTest() throws Exception {
        rslt = new Result(1,1,"test");
		rslt.setFileName("new");
		assertEquals(rslt.getFileName(),"new");
	}

    @Test (expected = BadAttributeValueException.class)
    public void setFileNameExceptionTest() throws Exception {
        rslt = new Result(1,1,"test");
        rslt.setFileName(null);
    }

	@Test
	public void hashCodeConsistencyTest() throws Exception {
        rslt = new Result(1,1,"test");
	    assertEquals(rslt.hashCode(),rslt.hashCode());
	}

    @Test
    public void hashCodeEqualsTest() throws Exception {
	    Result rslt1 = new Result(1,1,"test");
        Result rslt2 = new Result(1,1,"test");
        assertEquals(rslt1.hashCode(),rslt2.hashCode());
    }

	@Test
	public void equalsSuccessTest() throws Exception {
	    Result rslt1 = new Result(1, 1, "test");
        Result rslt2 = new Result(1,1,"test");
        assertTrue(rslt1.equals(rslt2));
	}

    @Test
    public void equalsKnownFalseTest() throws Exception {
        Result rslt1 = new Result(1, 1, "test");
        Result rslt2 = new Result(2,2,"quiz");
        assertFalse(rslt1.equals(rslt2));
    }


}