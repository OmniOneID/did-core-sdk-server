/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.manager;

import java.util.ArrayList;
import java.util.List;

import org.omnione.did.core.data.rest.SignatureParams;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.core.data.rest.VpVerifyParam;
import org.omnione.did.core.exception.CoreErrorCode;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.util.VerifyUtil;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.profile.Filter;
import org.omnione.did.data.model.vc.Claim;
import org.omnione.did.data.model.vc.CredentialSchema;
import org.omnione.did.data.model.vc.CredentialSubject;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.data.model.vp.VpProof;
import org.omnione.did.data.model.vp.VerifiablePresentation;


public class VpManager {

	public VerifiablePresentation verifiablePresentation;
	private static boolean isProofs = false;
	
	/**
	 * Verifies the provided VerifiablePresentation.
	 *
	 * @param verifiablePresentation The VerifiablePresentation to verify.
	 * @param verifyParam The parameters for verification, including filter, DidDocument etc...
	 * @throws CoreException
	 */
	public void verifyPresentation(VerifiablePresentation verifiablePresentation, VpVerifyParam verifyParam) throws CoreException {

		this.verifiablePresentation = verifiablePresentation;
		
		boolean isExpired = VerifyUtil.isExpired(verifiablePresentation.getValidUntil());
		if(isExpired) {
			throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_EXPIRED_VP);
		}

		checkFilter(verifyParam.getFilter(), verifiablePresentation.getVerifiableCredential());
		
        verifyHolderSignature(verifiablePresentation, verifyParam.getHolderDidDocument());
        verifyIssuerSignature(verifiablePresentation.getVerifiableCredential(), verifyParam.getIssuerDidDocument(),
                verifyParam.isCheckVcExpirationDate());
	}

    /**
	 * Gets the list of claims from the VerifiablePresentation.
	 *
	 * @return The list of claims.
	 */
	public List<Claim> getClaimList() {

		List<Claim> claimListResult = new ArrayList<Claim>();
		
		for (VerifiableCredential verifiableCredential : verifiablePresentation.getVerifiableCredential()) {
			CredentialSubject tmpCredentialSubject = new CredentialSubject();
			tmpCredentialSubject.fromJson(verifiableCredential.getCredentialSubject().toJson());
			claimListResult.addAll(tmpCredentialSubject.getClaims());
		}

		return claimListResult;
	}
	
	/**
	 * Checks if the VerifiableCredentials match the filter criteria.
	 *
	 * @param filter Thㄷ filter specifies the conditions that the submitted VerifiableCredentials must meet.
	 * @param verifiableCredentials The list of VerifiableCredentials to check.
	 * @throws CoreException
	 */
	private void checkFilter(Filter filter, List<VerifiableCredential> verifiableCredentials) throws CoreException {

		if(filter == null) {
			return;
		}
		for(CredentialSchema credentialSchema : filter.getCredentialSchemas()) {
			VerifiableCredential filterVc = findVerifiableCredential(verifiableCredentials, credentialSchema);

			if (filterVc != null ) {
				if (isIssuerAllowed(filterVc, credentialSchema)) {

					List<Claim> claimList = filterVc.getCredentialSubject().getClaims();
					List<String> requiredClaimCodeList = credentialSchema.getRequiredClaims();

					boolean allRequiredClaimsPresent = requiredClaimCodeList.stream()
							.allMatch(requiredClaimCode -> claimList.stream()
									.anyMatch(claim -> claim.getCode().equals(requiredClaimCode)));

					if (!allRequiredClaimsPresent) {
					    throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_NOT_CONTAIN_CLAIM);
					}
				} else {
				    throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_NOT_ALLOW_ISSUER);
				}
			} else {
			    throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_NOT_MATCHED_SCHEMA);
			}
		}
	}

	/**
	 * Finds the VerifiableCredential that matches the CredentialSchema.
	 *
	 * @param verifiableCredentials The list of VerifiableCredentials to search.
	 * @param credentialSchema The CredentialSchema to match.
	 * @return The matching VerifiableCredential, or null if not found.
	 */
	private VerifiableCredential findVerifiableCredential(List<VerifiableCredential> verifiableCredentials, CredentialSchema credentialSchema) {
	    return verifiableCredentials.stream()
	            .filter(vc -> credentialSchema.getId().equals(vc.getCredentialSchema().getId())
	                    && credentialSchema.getType().equals(vc.getCredentialSchema().getType()))
	            .findFirst()
	            .orElse(null);
	}
	
	/**
	 * Checks if the issuer of the VerifiableCredential is allowed by the CredentialSchema.
	 *
	 * @param vc The VerifiableCredential to check.
	 * @param credentialSchema The CredentialSchema containing information about allowed issuers.
	 * @return True if the issuer is allowed, false otherwise.
	 */
	private boolean isIssuerAllowed(VerifiableCredential vc, CredentialSchema credentialSchema) {
	    String issuer = vc.getIssuer().getId();
	    return issuer != null && credentialSchema.getAllowedIssuers().contains(issuer);
	}
	
	/**
	 * Verifies the signature of a Verifiable Presentation (VP, Holder Signature).
	 * This method first checks if the Verifiable Presentation (VP) contains a single proof.
	 * If a single proof is present, it verifies the signature using that proof.
	 * If multiple proofs are present, it iterates through each proof and verifies each one.
	 * 
	 * @param verifiablePresentation The Verifiable Presentation to be verified.
	 * @param holderDidDocument The DID document of the holder of the Verifiable Presentation.
	 * @throws CoreException
	 * @throws CryptoException
	 */
    private void verifyHolderSignature(VerifiablePresentation verifiablePresentation, DidDocument holderDidDocument) throws CoreException {
        
        SignatureParams vpSigParams = new SignatureParams();
        
        if(verifiablePresentation.getProof() != null) {
            vpSigParams = getHolderSignatureParams(verifiablePresentation, holderDidDocument, verifiablePresentation.getProof());
            VerifyUtil.verifySignature(vpSigParams);
        } else {
            isProofs=true;
            List<VpProof> proofList = verifiablePresentation.getProofs();
            for (VpProof proof : proofList) {
                vpSigParams = getHolderSignatureParams(verifiablePresentation, holderDidDocument, proof);
                VerifyUtil.verifySignature(vpSigParams);
            }
        }   
    }

    /**
     * Verifies the signature of claims within a list of Verifiable Credentials (VCs, Issuer Signature).
     * This method iterates through each Verifiable Credential (VC) and checks its expiration date if required.
     * If the expiration date is valid, it then verifies the signature of each claim within the VC.
     * 
     * @param verifiableCredentials The list of Verifiable Credentials to be verified.
     * @param issuerDidDocument The DID document of the issuer of the Verifiable Credentials.
     * @param isCheckVcExpirationDate A boolean flag indicating whether to check the expiration date of the VCs.
     * @throws CoreException
     */
    private void verifyIssuerSignature(List<VerifiableCredential> verifiableCredentials, DidDocument issuerDidDocument, boolean isCheckVcExpirationDate) throws CoreException {
        
        for (VerifiableCredential verifiableCredential : verifiableCredentials) {
            if(isCheckVcExpirationDate) {
                boolean isExpired = VerifyUtil.isExpired(verifiableCredential.getValidUntil());
                if(isExpired) {
                    throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_EXPIRED_VC, verifiableCredential.getId());
                }
            }

            if (verifiableCredential.getCredentialSubject() != null) {
                List<Claim> claimList = verifiableCredential.getCredentialSubject().getClaims();

                if (claimList == null || claimList.size() == 0) {
                    throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_PRIVACY_NOT_EXIST, verifiableCredential.getId());
                }

                for (int i = 0; i < claimList.size(); i++) {
                    VerifiableCredential tmpVerifiableCredential = new VerifiableCredential();
                    tmpVerifiableCredential.fromJson(verifiableCredential.toJson());

                    List<Claim> tmpClaimList = new ArrayList<Claim>();
                    tmpClaimList.add(claimList.get(i));

                    tmpVerifiableCredential.getCredentialSubject().setClaims(tmpClaimList);

                    SignatureVcParams sigVcParamsByClaims = new SignatureVcParams();
                    sigVcParamsByClaims = VerifyUtil.getSignatureVcParams(tmpVerifiableCredential, issuerDidDocument, true
                            , verifiableCredential.getProof().getProofValueList().get(i));
                    sigVcParamsByClaims.setClaimCode(claimList.get(i).getCode());

                    VerifyUtil.verifySignature(sigVcParamsByClaims);
                }
            }
        }
    }
    
	/**
     * Gets the SignatureParams for verifying a VerifiablePresentation proof.
     *
     * @param verifiablePresentation The VerifiablePresentation to verify.
     * @param holderDidDocument The DID document of the holder.
     * @param proof The proof to verify.
     * @return The SignatureParams for verifying Signature
     * @throws CoreException
     */
    private SignatureParams getHolderSignatureParams(VerifiablePresentation verifiablePresentation, DidDocument holderDidDocument, VpProof proof) throws CoreException {
        VerifiablePresentation tmpVerifiablePresentation = new VerifiablePresentation();
        tmpVerifiablePresentation.fromJson(verifiablePresentation.toJson());

        VpProof tmpProof = new VpProof();
        tmpProof.setType(proof.getType());
        tmpProof.setCreated(proof.getCreated());
        tmpProof.setVerificationMethod(proof.getVerificationMethod());
        tmpProof.setProofPurpose(proof.getProofPurpose());

        tmpVerifiablePresentation.setProof(tmpProof);

        if(isProofs) {
            tmpVerifiablePresentation.setProofs(null);
        }
        
        SignatureParams sigParams = new SignatureParams();
        sigParams.setOriginData(tmpVerifiablePresentation.toJson());
        sigParams.setSignatureValue(proof.getProofValue());

        DidManager didManager = new DidManager();
        didManager.parse(holderDidDocument.toJson());

        VerificationMethod publicKey = didManager.getVerificationMethodByKeyId(tmpProof.getVerificationMethod().split("\\#")[1]);
        if (publicKey == null) {
            throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_NOT_EXIST_SIGNING_KEY);
        }

        sigParams.setPublicKey(publicKey.getPublicKeyMultibase());
        sigParams.setAlgorithm(tmpProof.getType());
        sigParams.setKeyPurpose(tmpProof.getProofPurpose());

        return sigParams;
    }
}
