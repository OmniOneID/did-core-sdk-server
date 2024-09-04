/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.data.rest;

import java.util.List;

import org.omnione.did.data.model.enums.did.AuthType;
import org.omnione.did.data.model.enums.did.ProofPurpose;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DidKeyInfo {

    /**
     * The controller of the DID key.
     */
	@SerializedName("controller")
	private String controller;

    /**
     * The unique identifier for the key.
     */
	@SerializedName("keyId")
	private String keyId;

    /**
     * The public Key MulitBase value
     */
	@SerializedName("publicKey")
	private String publicKey;

    /**
     * This is the algorithm for that key.
     */
	@SerializedName("algoType")
	private String algoType;
	
    /**
     * Authentication type to use the key
     */
	@SerializedName("authType")
	private AuthType authType;

    /**
     * The list of purposes for which the key can be used.
     */
	@SerializedName("keyPurpose")
	private List<ProofPurpose> keyPurpose;
	
}
