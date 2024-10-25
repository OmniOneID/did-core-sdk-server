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

import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.profile.Filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VpVerifyParam {
    
	/**
	 * Whether the expiration date of verifiable credentials is verified
	 */
	private boolean checkVcExpirationDate = true;

	/**
	 * Setting up VC submission conditions
	 */
	private Filter filter; 
	
	/**
	 * Holder's DidDocument
	 */
	private DidDocument holderDidDocument;

	/**
     * Issuer's DidDocument
     */
	private DidDocument issuerDidDocument;

	public VpVerifyParam(DidDocument holderDidDocument, DidDocument issuerDidDocument) {

		this.holderDidDocument = holderDidDocument;
		this.issuerDidDocument = issuerDidDocument;
	}
}
