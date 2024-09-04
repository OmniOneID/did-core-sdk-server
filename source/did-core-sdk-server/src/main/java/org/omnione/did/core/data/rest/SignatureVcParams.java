/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.data.rest;

import org.omnione.did.data.model.util.json.GsonWrapper;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SignatureVcParams extends SignatureParams{
    
	/**
     * A boolean value used to distinguish between overall claim and individual claims.
     * 
     * <p>This field indicates whether the claim is a single claim or not.</p>
     */
    @SerializedName("isSingleClaim")
    private Boolean isSingleClaim;
    
    /**
     * A code used to verify the order of individual claims in their raw form.
     * 
     * <p>This field stores the code of the claim.</p>
     */
    @SerializedName("claimCode")
    private String claimCode;   

	@Override
	public void fromJson(String val) {
	    super.fromJson(val);
	    
	  	GsonWrapper gson = new GsonWrapper();
	  	SignatureVcParams params = gson.fromJson(val, SignatureVcParams.class);
	  	
	  	isSingleClaim = params.getIsSingleClaim();
	  	claimCode = params.getClaimCode();		
	}
}
