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
