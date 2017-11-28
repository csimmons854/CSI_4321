/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program 7
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package sharon.app;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

//Download thread to be executed to send files
class Download implements Runnable {
    private Connection clientCon = null;
    private String dir;
    private Logger log;

    Download(Socket downloadSocket, String directory, Logger logger) throws IOException {
        clientCon = new Connection(downloadSocket);
        dir = directory;
        log = logger;
    }

    Download(String directory, Logger logger) throws IOException {
        dir = directory;
        log = logger;
    }

    public void startDownload(String downloadAddress, int downloadPort, String fileID, String fileName) {
        if (!Utilities.checkForFile(fileName, dir)) {
            try {
                System.out.println("Download name: " + downloadAddress);
                System.out.println("Download Port: " + downloadPort);
                Connection newConnection = new Connection(new Socket(downloadAddress, downloadPort));
                System.out.println("Download Connection established");
                newConnection.writeMessage(fileID + "\n");
                System.out.println("File ID: " + fileID);
                StringBuilder rsp = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    rsp.append((char) newConnection.getInData().getByte());
                }
                if (rsp.toString().equals("OK\n\n")) {
                    File newFile = new File(dir + "\\" + fileName);
                    OutputStream out = new FileOutputStream(newFile);
                    InputStream in = newConnection.getClientSocket().getInputStream();
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.close();
                    in.close();
                } else {
                    InputStream in = newConnection.getClientSocket().getInputStream();
                    int read;
                    while ((read = in.read()) != -1) {
                        rsp.append((char) read);
                    }
                    System.out.println(rsp);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                log.warning(e.getMessage());
            }
        } else {
            System.out.println("File already exists in directory");
        }
    }

    public void run(){
        if(clientCon != null){
            try {
                String fileID = clientCon.getInData().getString();
                //ong fileID = Integer.parseInt(getID) & 0x00000000FFFFFFFFL;
                File newFile = Utilities.findFileByID(fileID,dir);
                FileInputStream fileInputStream;
                int data;
                if(newFile != null) {
                    fileInputStream = new FileInputStream(newFile);
                    clientCon.getOutData().writeByteArray("OK\n\n".getBytes());
                    System.out.println("Sending: " + newFile.getName());
                    log.info("Sending: " + newFile.getName());
                    while ((data = fileInputStream.read()) >= 0) {
                        clientCon.getClientSocket().getOutputStream().write(data);
                    }
                    System.out.println(newFile.getName() + " Sent");
                    log.info(newFile.getName() + "File Sent");
                }
                else
                {
                    clientCon.getOutData().writeByteArray(("ERROR ID (" + fileID + ") not found").getBytes());
                }
                clientCon.getClientSocket().close();
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
        }
    }
}
