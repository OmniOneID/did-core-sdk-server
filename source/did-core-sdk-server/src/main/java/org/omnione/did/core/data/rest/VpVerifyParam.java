/* 
 * Copyright 2024 Raonsecure
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
