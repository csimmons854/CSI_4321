/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package mvn.serialization;

public enum PacketType {
	
	/*
	 * AnswerResquest - answers to requests
	 * CleacCache - clears cache
	 * MavenAdditions - add mavens
	 * Maven Deletions - delete mavens
	 * NodeAdditions - add nodes
	 * NodeDeletions - Delete nodes
	 * RequestMavens - request mavens
	 * RequestNodes - request nodes
	 */
	AnswerRequest, ClearCache, MavenAdditions, MavenDeletions, NodeAdditions,
	NodeDeletions, RequestMavens, RequestNodes;
	
	
	/**
	 * Get code for type
	 * 
	 * @return type code
	 */
	public int getCode()
	{
		return 0;
	}
	
	/**
	 * Get cmd for type
	 * 
	 * @return type cmd
	 */
	public String getCmd() 
	{
		return null;
	}
	
	
	/**
	 * Get type for given code
	 * 
	 * @param code code of type
	 * @return type corresponding to code or null if bad code
	 */
	public static PacketType getByCode(int code) 
	{
		return null;
	}
	
	
	/**
	 * Get type for given cmd
	 * 
	 * @param cmd cmd to find type cmd
	 * 
	 * @return cmd corresponding to code or null if bad cmd
	 */
	public static PacketType getByCmd(String cmd)
	{
		return null;
	}
}
