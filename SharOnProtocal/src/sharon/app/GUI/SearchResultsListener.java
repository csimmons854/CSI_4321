package sharon.app.GUI;

import java.net.InetSocketAddress;

/**
 * Interface for listener to asynchronously-received search results
 * 
 * @author donahoo
 * @version 0.2
 */
public interface SearchResultsListener {
    /**
     * Called when a search result is found
     *  @param filename search result file name
     * @param searchID ID of original search
     * @param fileID ID of search result file
     * @param downloadHost Address/Port of search result file
     */
    void foundResult(String filename, byte[] searchID, long fileID, InetSocketAddress downloadHost);
}
