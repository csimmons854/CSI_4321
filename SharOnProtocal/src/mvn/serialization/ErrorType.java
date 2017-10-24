/************************************************
 *
 * Author: Chris Simmons
 * Assignment: Program0Test
 * Class: CSI 4321 Data Communications
 *
 ************************************************/
package mvn.serialization;

/**
 * MVN error type
 */
public enum ErrorType {

    /**
     * IncorrectPacket - Unexpected packet type
     * None - No error
     * System - System error
     */
    IncorrectPacket, None, System;
		
	
	/**
	 * Gets code for error 
	 * 
	 * @return the error code
	 */
	public int getCode() {

		int errorCode = -1;

		if (this == IncorrectPacket) {
			errorCode = 20;
		}

		if (this == None){
			errorCode = 0;
		}

		if (this == System){
			errorCode = 10;
		}

		return errorCode;
	}
	
	
	/**
	 * Get error for given code
	 * 
	 * @param code - code of error
	 * @return error corresponding to code or null if bad code
	 */
	public static ErrorType getByCode(int code) {
		ErrorType errorType = null;

		if(code == 20){
			errorType = IncorrectPacket;
		}
		if(code == 10){
			errorType = System;
		}
		if(code == 0){
			errorType = None;
		}

		return errorType;
	}
}
