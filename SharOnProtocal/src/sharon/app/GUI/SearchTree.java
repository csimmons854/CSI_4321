package sharon.app.GUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Search results tree panel
 * 
 * @author donahoo
 * @version 0.2
 */
public class SearchTree extends JPanel {
    /**
     * Root node of search tree
     */
    protected DefaultMutableTreeNode rootNode;
    /**
     * Model backing JTree
     */
    protected DefaultTreeModel treeModel;
    /**
     * JTree of search results
     */
    protected JTree tree;
    /**
     * Single listener to user interaction with tree
     */
    protected SharOnListener sharOnListener;

    public SearchTree() {
        super(new GridLayout(1, 0));

        // Build search panel with tree
        rootNode = new DefaultMutableTreeNode("Searches");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.setRootVisible(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.addMouseListener(makeMouseListener());

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    /**
     * Make a listener to mouse events on tree
     * 
     * @return new mouse listener
     */
    private MouseListener makeMouseListener() {
        return new MouseAdapter() {
            public void mousePressed(final MouseEvent e) {
                // Find element clicked on
                final int selRow = tree.getRowForLocation(e.getX(), e.getY());
                final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    // Is double click?
                    if (e.getClickCount() == 2) {
                        final Object c = selPath.getLastPathComponent();
                        // Get DownloadNode and call listener
                        if (c instanceof DownloadNode) {
                            final DownloadNode dn = (DownloadNode) c;
                            if (sharOnListener != null) {
                                sharOnListener.download(dn.getFileID(), dn.getDownloadHost(),dn.getDownlaodName());
                            }
                        }
                    }
                }
            }
        };
    }
    
    /**
     * Set the SharOn listener
     * 
     * @param sharOnListener new SharOn listener
     */
    public void setSharOnListener(final SharOnListener sharOnListener) {
        this.sharOnListener = sharOnListener;
    }
    
    /**
     * Add download node
     * 
     * @param filename name of file
     * @param searchID ID of search
     * @param fileID ID of file within search
     * @param downloadHost address/port of download server
     * 
     * @return new download node
     */
    public DefaultMutableTreeNode addDownload(final String filename, final byte[] searchID, final long fileID, final InetSocketAddress downloadHost) {
        DefaultMutableTreeNode childNode = new DownloadNode(filename, fileID, downloadHost);

        for (@SuppressWarnings("unchecked")
        Enumeration<SearchNode> children = rootNode.children();children.hasMoreElements();) {
            SearchNode srchNode = children.nextElement();
            if (Arrays.equals(srchNode.getID(), searchID)) {
                treeModel.insertNodeInto(childNode, srchNode, srchNode.getChildCount());
            }
        }
        // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode

        // Make sure the user can see the lovely new node.
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        return childNode;
    }
    
    /**
     * Add search node
     * @param srch new search string
     * 
     * @return new search node
     */
    public DefaultMutableTreeNode addSearch(final String srch) {
        byte[] srchID = sharOnListener.getSearchID(srch);
        SearchNode childNode = new SearchNode(srch, srchID);
        DefaultMutableTreeNode parent = rootNode;

        // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        // Make sure the user can see the lovely new node.
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        // Assumes SharOnListener is not null
        sharOnListener.search(srch, srchID);

        return childNode;
    }
}

/**
 * Node representing a search
 */
class SearchNode extends DefaultMutableTreeNode {
    /**
     * ID of search for this node
     */
    private byte[] id;
    /**
     * Construct new search node
     * 
     * @param name name of search node (corresponds to search string)
     * @param id id of search node
     */
    public SearchNode(final String name, final byte[] id) {
        super(name);
        this.id = id;
    }
    
    /**
     * Get search ID
     * 
     * @return search ID
     */
    public byte[] getID() {
        return id.clone();
    }
}

/**
 * Node representing download
 */
class DownloadNode extends DefaultMutableTreeNode {
    /**
     * File ID for download
     */
    private long fileID;
    /**
     * Host of download
     */
    private InetSocketAddress downloadHost;
    private String downlaodName;
    
    /**
     * Construct new download node
     * 
     * @param name name of download node
     * @param fileID ID of download file
     * @param downloadHost address/port of download host
     */
    public DownloadNode(final String name, final long fileID, final InetSocketAddress downloadHost) {
        super(name);
        this.fileID = fileID;
        this.downloadHost = downloadHost;
        this.downlaodName = name;
    }
    
    /**
     * Get host for download
     * 
     * @return host for download
     */
    public InetSocketAddress getDownloadHost() {
        return downloadHost;
    }

    public String getDownlaodName() {return  downlaodName;}
    /**
     * Get File ID for download
     * 
     * @return file ID
     */
    public long getFileID() {
        return fileID;
    }
}
