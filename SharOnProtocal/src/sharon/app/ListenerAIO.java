/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import sharon.app.GUI.SearchResultsListener;
import sharon.serialization.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sharon.app.Utilities.createRsp;


/**
 * Listens for messages
 */
public class ListenerAIO {
    private Logger log;
    private int downloadPort;
    String directory;
    private ArrayList<Byte> temp = new ArrayList<>();
    private List<Result> resultList;
    private HashMap<String, String> searchMap;
    private SearchResultsListener searchResultsListener;

    public ListenerAIO(AsynchronousSocketChannel clientChannel, Logger log,
                       int downloadPort, String directory, ArrayList<Byte> buffer,HashMap<String, String> searchMap,
                       SearchResultsListener searchResultsListener){
        this.downloadPort = downloadPort;
        this.log = log;
        this.directory = directory;
        temp.addAll(buffer);
        this.searchMap = searchMap;
        this.searchResultsListener = searchResultsListener;

        read(clientChannel);
    }

    public void handleRead(AsynchronousSocketChannel clientChannel, ByteBuffer buf, Integer bytesRead) throws IOException, BadAttributeValueException {
        for(int i = 0; i < bytesRead; i++ ){
            temp.add(buf.get(i));
        }
        byte [] bytes = new byte[temp.size()];
        for(int i = 0; i < temp.size(); i++){
            bytes[i] = temp.get(i);
        }
        Message message = getMessage(ByteBuffer.wrap(bytes));
        if(message != null){
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
            if (message instanceof Response) {
                System.out.println("Search Response for " +
                        searchMap.get(Arrays.toString(message.getID())));
                System.out.println("Download host: " + ((Response) message).getResponseHost());
                resultList = ((Response) message).getResultList();
                for (int i = 0; i < resultList.size(); i++) {
                    searchResultsListener.foundResult(resultList.get(i).getFileName(),message.getID(),
                            resultList.get(i).getFileID(),((Response) message).getResponseHost());
                    System.out.println("\t" + resultList.get(i).getFileName()
                            + ": ID " + resultList.get(i).getFileID()
                            + " (" + resultList.get(i).getFileSize()
                            + " bytes)");
                }
            }
            read(clientChannel);
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
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });
    }

    /**
     * @param buf buffer to be parsed
     * @return Message if message was able to formed return it, else returns nurse
     * @throws IOException
     */
    public Message getMessage(ByteBuffer buf) throws IOException {
        temp.clear();
        Message message = null;
        ArrayList<Byte> bytes = new ArrayList<Byte>();

        if(buf.remaining() > 30){
            for(int i = 0; i < 28; i++){
                bytes.add(buf.get());
            }
            int payloadLength = buf.getShort() & 0x0000FFFF;
            bytes.add((byte) (payloadLength >> 16));
            bytes.add((byte)payloadLength);
            if(buf.remaining() >= payloadLength){
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
                    for(byte b : byteArray){
                        temp.add(b);
                    }
                    message = null;
                }
            }else{
                temp.addAll(bytes);
            }

        }
        while(buf.remaining() > 0){
            temp.add(buf.get());
        }

        return message;
    }

}
