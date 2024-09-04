/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.exception;

public interface CoreErrorCodeInterface {
	
	/**
	 * Error Message
	 * @return
	 */
	public String getMsg();
	
	/**
	 * Error Code
	 * @return
	 */
	public String getCode();
}
