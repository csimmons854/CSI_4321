/*

 Author: Chris Simmons
 Assignment: Program 7
 Class: CSI 4321 Data Communications

 */
package sharon.app;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Attempts to establish a connection to a new node
 */
class ConnectionAIO {
    private ArrayList<Byte> cache = new ArrayList<>();
    private Logger log;
    private AsynchronousSocketChannel clientChannel;

    ConnectionAIO(Logger log){
        this.log = log;
    }


    void initiateConnection(InetSocketAddress addr) throws IOException {
        clientChannel = AsynchronousSocketChannel.open();
        clientChannel.connect(addr, null, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void a, Void b) {
                try {
                    handleConnect(clientChannel);
                } catch (IOException e) {
                    failed(e, null);
                }

            }

            @Override
            public void failed(Throwable e, Void attachment) {
                try {
                    clientChannel.close();
                } catch (IOException e1) {
                    log.warning(e1.getMessage());
                }
                System.out.println("Failed to Connect");
            }
        });
    }

    private void handleConnect(AsynchronousSocketChannel clientChannel) throws IOException{
        ByteBuffer buf = ByteBuffer.wrap("INIT SharOn/1.0\n\n".getBytes());
        clientChannel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesWritten, ByteBuffer buf) {
                try {
                    handleHandshake(clientChannel);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buf) {
                try {
                    clientChannel.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });

    }

    private void handleHandshake(AsynchronousSocketChannel clntChannel) throws IOException{
        ByteBuffer buf = ByteBuffer.allocateDirect(100);
        clntChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer bytesRead, ByteBuffer buf) {
                try{
                    handleHandshakeRsp(clntChannel, buf, bytesRead);
                }catch (IOException e){
                    failed(e,null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    clntChannel.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, "Close Failed", e);
                }
            }
        });
    }

    private void handleHandshakeRsp(AsynchronousSocketChannel clientChannel,
                                   ByteBuffer buf, Integer bytesRead) throws IOException, InterruptedException {
        for(int i = 0; i < bytesRead; i++ ){
            cache.add(buf.get(i));
        }
        if(cache.size() >= ("OK SharOn\n\n".getBytes().length)) {
            byte[] bytes = new byte[cache.size()];
            for (int i = 0; i < cache.size(); i++) {
                bytes[i] = cache.get(i);
            }
            String rsp = frameHandshake(ByteBuffer.wrap(bytes));
            if (rsp.equals("OK SharOn\n\n")) {
                new ListenerAIO(clientChannel, log, downloadPort, directory, cache,
                        searchMap, searchResultsListener);
                log.info("Connected " + clientChannel.getRemoteAddress());
                System.out.println("Connected to " + clientChannel.getRemoteAddress());

            }
            else {
                System.out.println("Handshake rejected " + clientChannel.getRemoteAddress());
                clientChannel.close();
                throw new IOException("Handshake was rejected " + clientChannel.getRemoteAddress());
            }
        }else{
            handleHandshake(clientChannel);
        }
    }

    private String frameHandshake(ByteBuffer buffer){
        cache.clear();
        StringBuilder handshake = new StringBuilder();
        for(int i = 0; i < "OK SharOn\n\n".getBytes().length; i++){
            handshake.append((char) buffer.get());
        }
        while(buffer.remaining() > 0){
            cache.add(buffer.get());
        }
        return handshake.toString();
    }

    AsynchronousSocketChannel getClientChannel(){
        return clientChannel;
    }
}
