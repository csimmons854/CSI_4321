package sharon.app;

import sharon.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sharon.app.Utilities.createRsp;


public class ListenerAIO {
    private Logger log;
    private int downloadPort;
    String directory;
    private ArrayList<Byte> temp = new ArrayList<>();

    public ListenerAIO(AsynchronousSocketChannel clientChannel, Logger log, int downloadPort, String directory){
        this.downloadPort = downloadPort;
        this.log = log;
        this.directory = directory;

        read(clientChannel);
    }

    public void handleRead(AsynchronousSocketChannel clientChannel, ByteBuffer buf, Integer bytesRead) throws IOException, BadAttributeValueException {

        for(int i = 0; i < bytesRead; i++ ){
            temp.add(buf.get(i));
        }

        if(temp.size() > 30){
            byte [] bytes = new byte[temp.size()];
            for(int i = 0; i < temp.size(); i++){
                bytes[i] = temp.get(i);
            }
            Message message = getMessage(ByteBuffer.wrap(bytes));
            if(message != null){
                temp.clear();
                System.out.println("Message: " + message);
                if(message instanceof Search) {
                    Response rsp = createRsp((Search) message, directory, downloadPort, log);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rsp.encode(new MessageOutput(baos));
                    ByteBuffer outBuf = ByteBuffer.wrap(baos.toByteArray());
                    clientChannel.write(outBuf,outBuf,new CompletionHandler<Integer,ByteBuffer>(){
                        public void completed(Integer bytesWritten, ByteBuffer buf) {

                        }
                        public void failed(Throwable ex, ByteBuffer buf) {
                            try {
                                clientChannel.close();
                            } catch (IOException e) {
                                log.log(Level.WARNING, "Close Failed", e);
                            }
                        }
                    });
                }
                read(clientChannel);
            }

        } else {
            read(clientChannel);
        }
    }

    public void read(AsynchronousSocketChannel clientChannel){
        ByteBuffer buf = ByteBuffer.allocateDirect(65535);
        clientChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            public void completed(Integer bytesRead, ByteBuffer buf) {
                try {
                    handleRead(clientChannel, buf, bytesRead);
                } catch (IOException e) {
                    log.log(Level.WARNING, "Handle Read Failed", e);
                } catch (BadAttributeValueException e) {
                    log.warning(e.getMessage());
                }
            }

            public void failed(Throwable ex, ByteBuffer v) {
                System.out.println("Failed yo");
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });
    }
    public Message getMessage(ByteBuffer buf) throws IOException {
        Message message = null;
        ArrayList<Byte> bytes = new ArrayList<Byte>();

        if(buf.remaining() > 30){
            for(int i = 0; i < 28; i++){
                bytes.add(buf.get());
            }
            int payloadLength = buf.getShort() & 0x0000FFFF;
            bytes.add((byte) (payloadLength >> 16));
            bytes.add((byte)payloadLength);
            System.out.println(payloadLength);
            for(int i = 0; i < payloadLength; i++){
                bytes.add(buf.get());
            }
            byte[] byteArray = new byte[bytes.size()];
            for(int i = 0; i < byteArray.length; i++){
                byteArray[i] = bytes.get(i);
            }

            try {
                message = message.decode(new MessageInput(new ByteArrayInputStream(byteArray)));
            }catch (BadAttributeValueException e){
                System.out.println(e.getMessage());
                message = null;
            }

        }
        while(buf.remaining() > 0){
            temp.add(buf.get());
        }
        return message;
    }

}
