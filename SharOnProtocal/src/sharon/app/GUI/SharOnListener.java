package sharon.app.GUI;

import java.net.InetSocketAddress;

/**
 * Interface for GUI to inform node of user actions
 * 
 * @author donahoo
 * @version 0.2
 */
public interface SharOnListener {
    /**
     * Get the next search ID
     * 
     * @param srch search string
     * 
     * @return next search ID
     */
    byte[] getSearchID(String srch);
    /**
     * Initiate search
     * 
     * @param srch search string
     * @param srchID search ID
     */
    void search(String srch, byte[] srchID);
    /**
     * Initiate download
     * 
     * @param fileID file ID
     * @param downloadHost download host address/port
     */
    void download(long fileID, InetSocketAddress downloadHost, String name);
}
