package sharon.app;

import java.io.File;
import java.util.Random;

class Utilities {
     static File findFileByID(String id, String directory) {
        File dir = new File(directory);
        File foundFile = null;
        File[] foundFiles = dir.listFiles((dir1, name) ->
                name.contains(""));
        long fileID;
        System.err.println("ID: " + id);
        if(foundFiles != null) {
            for(File item : foundFiles) {
                fileID = item.getName().hashCode() & 0x00000000FFFFFFFFL;
                if(fileID == (Long.parseLong(id))) {
                    foundFile = item;
                }
            }
        }
        return foundFile;
    }

    static Boolean checkForFile(String fileName, String directory) {
        File dir = new File(directory);
        Boolean foundFlag = false;
        File[] foundFiles = dir.listFiles((dir1, name) ->
                name.contains(""));

        if(foundFiles != null) {
            for(File item : foundFiles) {
                if(fileName.equals(item.getName()))
                {
                    System.out.println(fileName +  " = " + item.getName());
                    foundFlag = true;
                }
            }
        }
        return foundFlag;
    }

    static byte [] randomID(){
         byte [] id = new byte[15];
        Random r = new Random();
        for(int i = 0; i < 15; i++){
            id[i] = (byte) r.nextInt(256);
        }
        return id;
    }
}
