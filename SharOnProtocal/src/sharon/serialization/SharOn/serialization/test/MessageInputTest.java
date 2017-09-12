import sharon.serialization.MessageInput;
import sharon.serialization.Result;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Created by Chris on 9/4/2017.
 */
public class MessageInputTest {
    byte [] buff;
    InputStream in;

    @Test
    public void MessageInputSuccessTest() throws Exception
    {
        String fileName = "file.txt\n";
        Integer fileID = 1;
        Integer fileSize = 2;
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        temp.write(ByteBuffer.allocate(4).putInt(fileID).array());
        temp.write(ByteBuffer.allocate(4).putInt(fileSize).array());
        temp.write(fileName.getBytes());
        buff = temp.toByteArray();
        in = new ByteArrayInputStream(buff);
        MessageInput newMsg = new MessageInput(in);
        assertEquals(newMsg.getMsgFileID(),1);
        assertEquals(newMsg.getMsgFileSize(),2);
        assertEquals(newMsg.getMsgFileName(),"file.txt");
    }

}