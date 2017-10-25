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
	public int getCode() {
		int code = -1;
		if (this == RequestNodes){
			code = 0;
		}
		if (this == RequestMavens){
			code = 1;
		}
		if (this == AnswerRequest){
			code = 2;
		}
		if (this == NodeAdditions){
			code = 3;
		}
		if ( this == MavenAdditions){
			code = 4;
		}
		if (this == NodeDeletions){
			code = 5;
		}
		if (this == MavenDeletions){
			code = 6;
		}
		return code;
	}
	
	/**
	 * Get cmd for type
	 * 
	 * @return type cmd
	 */
	public String getCmd() {

		String cmd = null;

		if (this == RequestNodes){
			cmd = "RN";
		}
		if (this == RequestMavens){
			cmd = "RM";
		}
		if (this == AnswerRequest){
			cmd = "AR";
		}
		if (this == NodeAdditions){
			cmd = "NA";
		}
		if ( this == MavenAdditions){
			cmd = "MA";
		}
		if (this == NodeDeletions){
			cmd = "ND";
		}
		if (this == MavenDeletions){
			cmd = "MD";
		}

		return cmd;
	}

	
	
	/**
	 * Get type for given code
	 * 
	 * @param code code of type
	 * @return type corresponding to code or null if bad code
	 */
	public static PacketType getByCode(int code) {
		PacketType packetType = null;

		if(code == 0){
			packetType = RequestNodes;
		}
		if(code == 1){
			packetType = RequestMavens;
		}
		if(code == 2){
			packetType = AnswerRequest;
		}
		if(code == 3){
			packetType = NodeAdditions;
		}
		if(code == 4){
			packetType = MavenAdditions;
		}
		if(code == 5){
			packetType = NodeAdditions;
		}
		if(code == 6){
			packetType = NodeDeletions;
		}

		return packetType;
	}
	
	
	/**
	 * Get type for given cmd
	 * 
	 * @param cmd cmd to find type cmd
	 * 
	 * @return cmd corresponding to code or null if bad cmd
	 */
	public static PacketType getByCmd(String cmd) {
		PacketType packetType = null;

		if (cmd.equals("RN")) {
			packetType = RequestNodes;
		}
		if (cmd.equals("RM")) {
			packetType = RequestMavens;
		}
		if (cmd.equals("AR")) {
			packetType = AnswerRequest;
		}
		if (cmd.equals("NA")) {
			packetType = NodeAdditions;
		}
		if (cmd.equals("MA")) {
			packetType = MavenAdditions;
		}
		if (cmd.equals("NA")) {
			packetType = NodeAdditions;
		}
		if (cmd.equals("ND")) {
			packetType = NodeDeletions;
		}

		return packetType;
	}
}

