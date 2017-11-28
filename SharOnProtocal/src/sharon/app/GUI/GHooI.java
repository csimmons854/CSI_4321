package sharon.app.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

/**
 * Simple GUI for search and results
 * 
 * @author donahoo
 * @version 0.2
 */
public class GHooI extends JPanel implements Runnable {
    /**
     * Name of application
     */
    private final static String APPNAME = "SharOn";
    /**
     * Search result tree
     */
    private final SearchTree treePanel;

    /**
     * Construct simple GUI panel for search and results
     */
    public GHooI() {
        super(new BorderLayout());

        // Create the components
        treePanel = new SearchTree();

        // Lay everything out
        treePanel.setPreferredSize(new Dimension(300, 150));
        add(treePanel, BorderLayout.CENTER);

        // Add text search box
        final JTextField searchText = new JTextField();
        searchText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               treePanel.addSearch(searchText.getText().trim());
               searchText.setText("");
            }
        });
        add(searchText, BorderLayout.SOUTH);
    }

    /**
     * Set SharOn listener to be notified when GUI user initiates
     * 1) a search
     * 2) a download request
     * 
     * @param listener new SharOn listener
     */
    public void setSharOnListener(final SharOnListener listener) {
        treePanel.setSharOnListener(listener);
    }
    
    /**
     * Add node to specified search (by searchID) for download
     *  @param filename name of file
     * @param searchID ID of search
     * @param fileID ID of file within search
     * @param downloadHost address/port of download server
     */
    public void addDownloadNode(final String filename, final byte[] searchID, final long fileID, final InetSocketAddress downloadHost) {
        treePanel.addDownload(filename, searchID, fileID, downloadHost);
    }
    
    /**
     * Listener for asynchronously-received search results
     * 
     * @return search results listener
     */
    public SearchResultsListener getSearchResultsListener() {
        return new SearchResultsListener() {
            @Override
            public void foundResult(final String filename, final byte[] searchID, final long fileID, final InetSocketAddress downloadHost) {
                addDownloadNode(filename, searchID, fileID, downloadHost);
            }
        };
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked
     * from the event-dispatching thread.
     */
    public void run() {
        // Create and set up the window
        final JFrame frame = new JFrame(APPNAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane
        this.setOpaque(true); // content panes must be opaque
        frame.setContentPane(this);

        // Display the window
        frame.pack();
        frame.setVisible(true);
    }
}
