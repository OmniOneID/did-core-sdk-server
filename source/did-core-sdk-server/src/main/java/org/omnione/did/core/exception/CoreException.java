/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.core.exception;

public class CoreException extends Exception{

	private static final long serialVersionUID = 6285544788893947401L;
	
	/**
	 * Error Code - Use the int range
	 */
	protected String errorCode;

	/**
	 * Error code message
	 */
	protected String errorMsg;

	/**
	 * Error reason
	 */
	protected String errorReason;
	
	public CoreException(CoreErrorCodeInterface ErrorEnum) {
		super("ErrorCode: " + ErrorEnum.getCode() + ", Message: " + ErrorEnum.getMsg());
		this.errorCode = ErrorEnum.getCode();
		this.errorMsg = ErrorEnum.getMsg();
	}
	
	public CoreException(CoreErrorCodeInterface ErrorEnum, String errorReason) {
		super("ErrorCode: " + ErrorEnum.getCode() + ", Message: " + ErrorEnum.getMsg() + ", Reason: " + errorReason);
		this.errorCode = ErrorEnum.getCode();
		this.errorMsg = ErrorEnum.getMsg();
		this.errorReason = errorReason;
	}
	
	public CoreException(String errorCode, String errorMsg) {
		super("ErrorCode: " + errorCode + ", Message: " + errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public CoreException(String iwErrorCode, Throwable throwable)  {
		super(iwErrorCode, throwable);
	}

	
	public CoreException(String errorCode, String errorMsg, String errorReason) {
		super("ErrorCode: " + errorCode + ", Message: " + errorMsg + ", Reason: " + errorReason);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.errorReason = errorReason;
	}
	
	public CoreException(CoreErrorCode errorCode, Throwable throwable) {
		super("ErrorCode: " + errorCode +  ", Reason: " + throwable);
		this.errorCode = errorCode.getCode();
		this.errorMsg = errorCode.getMsg();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
