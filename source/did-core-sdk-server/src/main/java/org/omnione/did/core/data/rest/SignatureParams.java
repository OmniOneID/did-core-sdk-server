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
