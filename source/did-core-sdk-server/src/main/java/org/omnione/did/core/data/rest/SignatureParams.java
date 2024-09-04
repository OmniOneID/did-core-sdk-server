/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.data.rest;

import org.omnione.did.data.model.DataObject;
import org.omnione.did.data.model.util.json.GsonWrapper;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SignatureParams extends DataObject{
	

	/**
     * The identifier of the key used for signing.
     */
	@SerializedName("keyId")
	private String keyId;
	

	/**
     * The purpose of the key for signature.
     */ 
	@SerializedName("keyPurpose")
	private String keyPurpose;
	
	/**
     * The hash of the data to be signed.
     */
	@SerializedName("hashedData")
	private String hashedData;
	
	/**
     * The public key used for signature verification.
     */
	@SerializedName("publicKey")
	private String publicKey;
    
	/**
     * The original data that was signed.
     */
	@SerializedName("originData")
	private String originData;
	
	
    /**
     * The cryptographic algorithm used for signing.
     */
	@SerializedName("algorithm")
	private String algorithm;
	
    /**
     * The signature value.
     */
	@SerializedName("signatureValue")
	private String signatureValue;

	@Override
	public void fromJson(String val) {
	  	GsonWrapper gson = new GsonWrapper();
	  	SignatureParams params = gson.fromJson(val, SignatureParams.class);
	  	
	  	keyId = params.getKeyId();
	  	keyPurpose = params.getKeyPurpose();
	  	hashedData = params.getHashedData();
        publicKey = params.getHashedData();
        originData = params.getOriginData();
        algorithm = params.getAlgorithm();
        signatureValue = params.getSignatureValue();
	}
}
