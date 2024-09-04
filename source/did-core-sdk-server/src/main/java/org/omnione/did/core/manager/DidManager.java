/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.manager;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.omnione.did.core.data.rest.DidKeyInfo;
import org.omnione.did.core.data.rest.SignatureParams;
import org.omnione.did.core.exception.CoreErrorCode;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.util.VerifyUtil;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.Proof;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.did.Service;
import org.omnione.did.data.model.enums.did.DidServiceType;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.did.ProofType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class DidManager {
	
	
	public DidDocument didDocument;
	
	public DidDocument tmpDidDocument;
	
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
	
	public DidDocument getDocument() {
		return this.didDocument;
	}
	
	/**
     * Create a new DID document.
     *
     * @param did The owner's did
     * @param controller The controller's did
     * @param didKeyInfos The list of key information to save in the DID Document
     * @return created DID document
     * @throws CoreException
     */
	public DidDocument createDocument(String did, String controller, List<DidKeyInfo> didKeyInfos) throws CoreException {
		if (didKeyInfos == null || didKeyInfos.size() == 0) {
			throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_ADD_KEY_FAIL);
		}
		if (did == null || did.isEmpty()) {
			throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_ID_IS_NULL);
		}
		// init()
		didDocument = new DidDocument();
		
		List<String> contextList = new ArrayList<String>();
		contextList.add("https://www.w3.org/ns/did/v1");
		
		didDocument.setContext(contextList);
		didDocument.setId(did);
		didDocument.setController(controller);
		didDocument.setCreated(dateToString(ZonedDateTime.now(ZoneId.of("UTC"))));
		didDocument.setUpdated(dateToString(ZonedDateTime.now(ZoneId.of("UTC"))));
		didDocument.setVersionId("1");

		
		for (DidKeyInfo didKeyInfo : didKeyInfos) {
			addVerifiCationMethod(didKeyInfo);
		}
		
		return didDocument;
	}

	/**
    * Load the DID Document file and set it to the DidDocument object inside the DidManager
    *
    * @param didDocPath Path to the JSON file containing the DID document, including the filename
    * @throws CoreException
    */
	public void load(String didDocPath) throws CoreException {

		ObjectMapper objectMapper = new ObjectMapper();
	    
	    File file = new File(didDocPath);
	    
	    if (!file.exists() || !file.isFile()) {
	        throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_DIDDOCUMENT_FILE_NOT_FOUND);
	    }

	    String didDocJson = null;
	    
	    try {
	        JsonNode rootNode = objectMapper.readTree(file);
	        didDocJson = rootNode.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    if(didDocJson != null) {
	    	didDocument = new DidDocument(didDocJson);
	    } else {
	        throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_READ_DIDDOCUMENT_FILE_FAIL);
	    }
	}

	/**
     * Set the JSON string to the DidDocument object inside the DidManager
     *
     * @param didJson JSON string representing the DID document
     */
	public void parse(String didJson) {
		didDocument = new DidDocument(didJson);
	}

	
	/**
     * Add a key to the verificationMethod section of the DID document, and include it in the purpose list if the key has a specific purposes
     *
     * @param didKeyInfo Information about the key to be added
     * @throws CoreException
     */
	public void addVerifiCationMethod(DidKeyInfo didKeyInfo) throws CoreException {

		tmpDidDocument = new DidDocument(didDocument.toJson());
		
		if (didKeyInfo == null) {
			throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_ADD_KEY_FAIL);
		}
		
		
		List<VerificationMethod> verificationMethodList = initializeList(tmpDidDocument.getVerificationMethod());
	    for(VerificationMethod verificationMethod : verificationMethodList) {
		    if(verificationMethod.getId().equals(didKeyInfo.getKeyId())) {
				tmpDidDocument = null;
				throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_DUPLICATED_KEY);
			}
	    }
		
		//verificationMethod set
	    VerificationMethod verificationMethod = new VerificationMethod();
		verificationMethod.setController(didKeyInfo.getController());
		verificationMethod.setId(didKeyInfo.getKeyId());
		verificationMethod.setPublicKeyMultibase(didKeyInfo.getPublicKey());
		verificationMethod.setType(didKeyInfo.getAlgoType());
		verificationMethod.setAuthType(didKeyInfo.getAuthType().getRawValue());
		
		verificationMethodList.add(verificationMethod);
			
		tmpDidDocument.setVerificationMethod(verificationMethodList);
		
		addKeyPurpose(didKeyInfo.getKeyId(), didKeyInfo.getKeyPurpose());
		
		this.didDocument = tmpDidDocument;
	}

	/**
     * Add key ID to the list of key purposes in the DID document.
     *
     * @param keyId The ID key to add the specific purposes
     * @param keyPurposes List of purpose to add
     * @throws CoreException
     */
	public void addKeyPurpose(String didKeyId, List<ProofPurpose> keyPurposes) throws CoreException {
		
		if (tmpDidDocument == null) {
	        tmpDidDocument = new DidDocument(this.didDocument.toJson());
		}	
		List<VerificationMethod> verificationMethodList = tmpDidDocument.getVerificationMethod();
	
	    boolean check = false;
	    for(VerificationMethod verificationMethod : verificationMethodList) {
		    if(verificationMethod.getId().equals(didKeyId)) {
		    	check = true;		
			}
	    }
	    if(!check) throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_UNREGISTERED_KEY);
	    
	    if(keyPurposes != null) {
			for(ProofPurpose keyPurpose : keyPurposes) {
				 switch (keyPurpose) {
			        case ASSERTION_METHOD:
			        	List<String> assertionMethodList = initializeList(tmpDidDocument.getAssertionMethod());
						checkKeyPurposeList(tmpDidDocument.getAssertionMethod(), didKeyId);
						assertionMethodList.add(didKeyId);
						tmpDidDocument.setAssertionMethod(assertionMethodList);
						break;
			        case AUTHENTICATION:
			        	List<String> authenticationList = initializeList(tmpDidDocument.getAuthentication());
			        	checkKeyPurposeList(tmpDidDocument.getAuthentication(), didKeyId);
			        	authenticationList.add(didKeyId);
			        	tmpDidDocument.setAuthentication(authenticationList);
			        	break;
			        case KEY_AGREEMENT:
			        	List<String> keyAgreementList = initializeList(tmpDidDocument.getKeyAgreement());
			        	checkKeyPurposeList(tmpDidDocument.getKeyAgreement(), didKeyId);
			        	keyAgreementList.add(didKeyId);
			        	tmpDidDocument.setKeyAgreement(keyAgreementList);
			        	break;
			        case CAPABILITY_INVOCATION:	
			        	List<String> capabilityInvocationList = initializeList(tmpDidDocument.getCapabilityInvocation());
			        	checkKeyPurposeList(tmpDidDocument.getCapabilityInvocation(), didKeyId);
			        	capabilityInvocationList.add(didKeyId);
			        	tmpDidDocument.setCapabilityInvocation(capabilityInvocationList);
			        	break;
			        case CAPABILITY_DELEGATION:
			        	List<String> capabilityDelegationList = initializeList(tmpDidDocument.getCapabilityDelegation());
			        	checkKeyPurposeList(tmpDidDocument.getCapabilityDelegation(), didKeyId);
			        	capabilityDelegationList.add(didKeyId);
			        	tmpDidDocument.setCapabilityDelegation(capabilityDelegationList);
			        	break;
				 }
			}
	    }

		this.didDocument = tmpDidDocument;

	}
	

	/**
	 * Remove the information of the key from the verification method section and remove the ID of the key saved in the key purpose.
	 *
	 * @param keyId The ID of the key to remove
	 * @throws CoreException
	 */
	public void removeVerificationMethod(String keyId) throws CoreException {

		tmpDidDocument = new DidDocument(this.didDocument.toJson());

		List<VerificationMethod> verificationMethod = tmpDidDocument.getVerificationMethod();

		List<VerificationMethod> newVerificationMethod = removeVerificationMethod(verificationMethod, keyId);
		tmpDidDocument.setVerificationMethod(newVerificationMethod);

		removeKeyPurpose(keyId);
		
		this.didDocument = tmpDidDocument;
	}

	/**
	 * Search for the VerificationMethod using the provided key ID from the DID document
	 *
	 * @param keyId The ID of the key to search in DID document
	 * @return The VerificationMethod object representing the key information, or null if not found
	 */
	public VerificationMethod getVerificationMethodByKeyId(String didKeyId) {
	    List<VerificationMethod> verificationMethodList = new ArrayList<>(didDocument.getVerificationMethod());
	    
	    for(VerificationMethod verificationMethod : verificationMethodList) {
	        if(didKeyId.equals(verificationMethod.getId())) {
	            return verificationMethod;
	        }
	    }
	    return null;
	}
	
	/**
	 * Search for a list of key IDs used for signatures in the DID document.
	 *
	 * @return List of key IDs used for signatures
	 * @throws CoreException
	 */
	public List<String> getAllSignKeyIdList() throws CoreException {
	    List<String> signKeyIds = new ArrayList<>();

	    if (didDocument.getVerificationMethod() == null) {
	        throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_UNSAVED_KEY);
	    }

	    for (VerificationMethod verificationMethod : didDocument.getVerificationMethod()) {
	    	if ((didDocument.getAssertionMethod() != null && didDocument.getAssertionMethod().contains(verificationMethod.getId())) || 
        			(didDocument.getAuthentication() != null && didDocument.getAuthentication().contains(verificationMethod.getId())) ||
        				(didDocument.getCapabilityInvocation() != null && didDocument.getCapabilityInvocation().contains(verificationMethod.getId())) ||
    						(didDocument.getCapabilityDelegation() != null && didDocument.getCapabilityDelegation().contains(verificationMethod.getId()))) {
	        	signKeyIds.add(verificationMethod.getId());
	        }
	    }

	    return signKeyIds.isEmpty() ? null : signKeyIds;
	}
	
	/**
	 * Extract the original data for signing each key in the DID document.
	 *
	 * @param keyIdList List of key IDs for signing
	 * @return List of SignatureParams containing the original data for signing
	 * @throws CoreException
	 */
	public List<SignatureParams> getOriginDataForSign(List<String> keyIds) throws CoreException {
		
		List<SignatureParams> signatureParams = new ArrayList<SignatureParams>();
		List<String> signableKeyIds = this.getAllSignKeyIdList();
		DidDocument tmpDidDocument = new DidDocument(this.didDocument.toJson());

		if (keyIds != null && !keyIds.isEmpty()) {
			for (String keyId : keyIds) {

				VerificationMethod verificationMethod = getVerificationMethodByKeyId(keyId);
				if (verificationMethod == null) {
					throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_UNSAVED_KEY);
				}

				if (!signableKeyIds.contains(keyId)) {
					throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_NOT_A_SIGNING_KEY);
				}

				 EnumSet<ProofPurpose> proofPurposes = getKeyPurpose(keyId);
				 for (ProofPurpose purpose : proofPurposes) {
					 if(purpose != ProofPurpose.KEY_AGREEMENT) {
						SignatureParams signatureParam = new SignatureParams();
						
						// Create proof for each purpose
						Proof proof = new Proof();
						proof.setCreated(dateToString(ZonedDateTime.now(ZoneId.of("UTC"))));
						proof.setProofPurpose(purpose.getRawValue());
						proof.setVerificationMethod(didDocument.getId() + "?" + "versionId=" + didDocument.getVersionId() + "#" + keyId);
						
						switch (verificationMethod.getType()) {
							case "RsaVerificationKey2018":
								proof.setType(ProofType.RSA_SIGNATURE_2018.getRawValue());
								break;
							case "Secp256k1VerificationKey2018":
								proof.setType(ProofType.SECP256K1_SIGNATURE_2018.getRawValue());
								break;
							case "Secp256r1VerificationKey2018":
								proof.setType(ProofType.SECP256R1_SIGNATURE_2018.getRawValue());
								break;
						}
						if(didDocument.getProofs() != null)
						{
						    tmpDidDocument.setProofs(null);
						}
						tmpDidDocument.setProof(proof);
						
						// Populate SignatureParams
						signatureParam.setKeyId(keyId);
						signatureParam.setAlgorithm(proof.getType());
						signatureParam.setOriginData(tmpDidDocument.toJson());
						signatureParam.setKeyPurpose(purpose.getRawValue());
						
						signatureParams.add(signatureParam);
					 }
				 }
			}
		}
		 
		return signatureParams.isEmpty() ? null : signatureParams;
	}

	/**
	 * Verifies the signature of the DID document.
	 * 
	 * @throws CoreException
	 */
    public void verifyDocumentSignature() throws CoreException {
        DidDocument tmpDidDocument = new DidDocument(this.didDocument.toJson());

        List<Proof> tmpProofs = tmpDidDocument.getProofs() != null ? new ArrayList<>(tmpDidDocument.getProofs()) : new ArrayList<>(Collections.singletonList(tmpDidDocument.getProof()));
            
        for (Proof proof : tmpProofs) {
            Proof tmpProof = new Proof();
            tmpProof.setCreated(proof.getCreated());
            tmpProof.setProofPurpose(proof.getProofPurpose());
            tmpProof.setVerificationMethod(proof.getVerificationMethod());
            tmpProof.setType(proof.getType());
            tmpDidDocument.setProofs(null);
            tmpDidDocument.setProof(tmpProof);
            
            SignatureParams sigParams = new SignatureParams();
            sigParams.setOriginData(tmpDidDocument.toJson());
            
            String keyId = getKeyIdByDidKeyUrl(tmpProof.getVerificationMethod());
            VerificationMethod verificationMethod = getVerificationMethodByKeyId(keyId);
            if(verificationMethod == null) {
                throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_NOT_EXIST_SIGNING_KEY);
            }
            
            sigParams.setPublicKey(verificationMethod.getPublicKeyMultibase());
            sigParams.setAlgorithm(proof.getType());
            sigParams.setSignatureValue(proof.getProofValue());
            sigParams.setKeyPurpose(proof.getProofPurpose());
            sigParams.setKeyId(getKeyIdByDidKeyUrl(proof.getVerificationMethod()));
            
            VerifyUtil.verifySignature(sigParams);
        }
    }

	/**
	 * Retrieves the proof purposes associated with a given key ID from the DID document.
	 *
	 * @param keyId The ID of the key for which to get the purpose of the proof
	 * @return EnumSet of ProofPurpose representing the purposes of the key
	 * @throws CoreException
	 */
    private EnumSet<ProofPurpose> getKeyPurpose(String keyId) throws CoreException {
		EnumSet<ProofPurpose> methodTypeEnumSet = EnumSet.noneOf(ProofPurpose.class);

		if (tmpDidDocument == null)
			tmpDidDocument = new DidDocument(this.didDocument.toJson());

		String publicKeyId = keyId;
		if (tmpDidDocument.getAssertionMethod() != null && tmpDidDocument.getAssertionMethod().contains(publicKeyId)) {
			methodTypeEnumSet.add(ProofPurpose.ASSERTION_METHOD);
		}

		if (tmpDidDocument.getAuthentication() != null && tmpDidDocument.getAuthentication().contains(publicKeyId)) {
			methodTypeEnumSet.add(ProofPurpose.AUTHENTICATION);
		}

		if (didDocument.getKeyAgreement() != null && tmpDidDocument.getKeyAgreement().contains(publicKeyId)) {
			methodTypeEnumSet.add(ProofPurpose.KEY_AGREEMENT);
		}

		if (tmpDidDocument.getCapabilityInvocation() != null
				&& tmpDidDocument.getCapabilityInvocation().contains(publicKeyId)) {
			methodTypeEnumSet.add(ProofPurpose.CAPABILITY_INVOCATION);
		}

		if (tmpDidDocument.getCapabilityDelegation() != null
				&& tmpDidDocument.getCapabilityDelegation().contains(publicKeyId)) {
			methodTypeEnumSet.add(ProofPurpose.CAPABILITY_DELEGATION);
		}

		if (methodTypeEnumSet.isEmpty()) {
			throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_EMPTY_KEYPURPOSE_LIST);
		}

		return methodTypeEnumSet;
	}
	
	/**
	 * Adds a service endpoint to the DID document.
	 *
	 * @param didServiceId The ID of the service to add
	 * @param didServiceType The type of the service to add
	 * @param url The URL of the service endpoint to add
	 * @throws CoreException
	 */
    public void addServiceEndPoint(String didServiceId, DidServiceType didServiceType, String url) throws CoreException {
    	tmpDidDocument = new DidDocument(this.didDocument.toJson());
    	List<Service> services = tmpDidDocument.getService();
        if (services == null) {
            services = new ArrayList<>();
        }

        Service existingService = null;
        for (Service service : services) {
            if (didServiceId.equals(service.getId())) {
                existingService = service;
                break;
            }
        }

        if (existingService != null) {
            
	        boolean urlExists = false;
	        if (existingService.getServiceEndpoint().contains(url)) {
	            urlExists = true;
	        }
	        
	        if (didServiceType != null && !didServiceType.getRawValue().equals(existingService.getType())) {
	            throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_EXISTED_SERVICE_ID);
	        }
            
            if (urlExists) {
            	throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_EXISTED_SERVICE_URL);
            }
            
            existingService.getServiceEndpoint().add(url);
        } else {
            if (didServiceType == null) {
                throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_SERVICE_TYPE_IS_NULL);
            }

            Service newService = new Service();
            newService.setId(didServiceId);
            newService.setType(didServiceType.getRawValue());
            List<String> urlList = new ArrayList<>();
            urlList.add(url);
            newService.setServiceEndpoint(urlList);
            services.add(newService);
        }

        tmpDidDocument.setService(services);

        tmpDidDocument.setUpdated(dateToString(ZonedDateTime.now(ZoneId.of("UTC"))));
        
	    this.didDocument = tmpDidDocument;
        
    }
	
    /**
     * Deletes a service endpoint from the DID document.
     *
     * @param didServiceId The ID of the service endpoint to delete
     * @param type The type of the service endpoint to delete
     * @param url The URL of the service endpoint to delete
     * @throws CoreException
     */
    public void deleteServiceEndPoint(String didServiceId, DidServiceType type, String url) throws CoreException {
        tmpDidDocument = new DidDocument(this.didDocument.toJson());

        List<Service> services = tmpDidDocument.getService();

        if (services == null || services.isEmpty()) {
            throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_SERVICE_NOT_FOUND);
        }

        boolean serviceMatched = false;

        for (Iterator<Service> serviceIterator = services.iterator(); serviceIterator.hasNext();) {
            Service service = serviceIterator.next();

            boolean idMatches = didServiceId == null || didServiceId.equals(service.getId());
            boolean typeMatches = type == null || type.getRawValue().equals(service.getType());

            if (idMatches && typeMatches) {
                serviceMatched = true;
                if (url != null) {
                    List<String> endpoints = service.getServiceEndpoint();
                    Iterator<String> endpointIterator = endpoints.iterator();

                    boolean endpointMatched = false;

                    while (endpointIterator.hasNext()) {
                        String endpoint = endpointIterator.next();
                        if (endpoint.equals(url)) {
                            endpointIterator.remove();
                            endpointMatched = true;
                        }
                    }

                    if (!endpointMatched) {
                        throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_NOT_EXISTED_SERVICE_URL);
                    }

                    if (endpoints.isEmpty()) {
                        serviceIterator.remove();
                    }
                } else {
                    serviceIterator.remove();
                }
            }
        }

        if (!serviceMatched) {
            throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_NOT_MATCHED_SERVICE);
        }

        if (services.isEmpty()) {
            tmpDidDocument.setService(null);
        } else {
            tmpDidDocument.setService(services);
        }

        tmpDidDocument.setUpdated(dateToString(ZonedDateTime.now(ZoneId.of("UTC"))));

        this.didDocument = tmpDidDocument;
    }
	
    /**
     * Adds proofs to the DID Document. If multiple signature parameters are provided,
     * it creates separate proofs for each and adds them to the document. If only one
     * signature parameter is provided, it creates a single proof.
     *
     * @param sigParamsList List of signature parameters to create proofs from.
     *                      Each SignatureParams object contains origin data and key ID.
     * @throws CoreException
     */
	public void addProof(List<SignatureParams> signatureParams) throws CoreException {
		if(signatureParams.size() > 1) {
			List<Proof> proofs = new ArrayList<Proof>();
			for (SignatureParams signatureParam : signatureParams) {
				tmpDidDocument = new DidDocument(signatureParam.getOriginData());
				Proof tmpProof = tmpDidDocument.getProof(); 
	
				String keyIdByProof = this.getKeyIdByDidKeyUrl(tmpProof.getVerificationMethod());
	
				if (!keyIdByProof.equals(signatureParam.getKeyId())) {
					throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_UNSAVED_KEY);
				}
	
				Proof proof = makeProof(signatureParam, tmpProof);
				proofs.add(proof);
			}
			this.didDocument.setProof(null);
			this.didDocument.setProofs(proofs);
		}else {
			Proof proof = new Proof();
			SignatureParams signatureParam = signatureParams.get(0);
			tmpDidDocument = new DidDocument(signatureParam.getOriginData());
			Proof tmpProof = tmpDidDocument.getProof(); 

			String keyIdByProof = this.getKeyIdByDidKeyUrl(tmpProof.getVerificationMethod());

			if (!keyIdByProof.equals(signatureParam.getKeyId())) {
				throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_UNSAVED_KEY);
			}

			proof = makeProof(signatureParam, tmpProof);

			this.didDocument.setProofs(null);
			this.didDocument.setProof(proof);
		}
	}
	
	/**
	 * Creates a proof object based on the given signature parameters and updates
	 * the proof value.
	 *
	 * @param sigParams Signature parameters containing the signature value.
	 * @param proof Current proof object to update.
	 * @return Updated proof object with the signature value set.
	 */
	private Proof makeProof(SignatureParams signatureParam, Proof proof) { 
		Proof tmpProof = proof;
		tmpProof.setProofValue(signatureParam.getSignatureValue());

		return tmpProof;
	}

	/**
	 * Removes the specified key ID from all key purpose lists (assertionMethod,
	 * authentication, keyAgreement, capabilityInvocation, capabilityDelegation) in
	 * the temporary DID Document.
	 *
	 * @param keyId Key ID to remove from key purpose lists.
	 * @throws CoreException If the key ID is not found in any of the key purpose
	 *                             lists or if there are issues with key management.
	 */
	public void removeKeyPurpose(String keyId) throws CoreException {
	    if (tmpDidDocument == null) {
	        tmpDidDocument = new DidDocument(didDocument.toJson());
	    }

	    if(tmpDidDocument.getAssertionMethod() != null && tmpDidDocument.getAssertionMethod().contains(keyId)) {
	    	tmpDidDocument.setAssertionMethod(removeKeyPurposeByKeyId(didDocument.getAssertionMethod(), keyId));
	    }
		if(tmpDidDocument.getAuthentication() != null && tmpDidDocument.getAuthentication().contains(keyId)) {
			tmpDidDocument.setAuthentication(removeKeyPurposeByKeyId(didDocument.getAuthentication(), keyId));
		}
		if(tmpDidDocument.getKeyAgreement() != null && tmpDidDocument.getKeyAgreement().contains(keyId)) {
			tmpDidDocument.setKeyAgreement(removeKeyPurposeByKeyId(didDocument.getKeyAgreement(), keyId));
		}
		if(tmpDidDocument.getCapabilityInvocation() != null && tmpDidDocument.getCapabilityInvocation().contains(keyId)) {
			tmpDidDocument.setCapabilityInvocation(removeKeyPurposeByKeyId(didDocument.getCapabilityInvocation(), keyId));
		}
		if(tmpDidDocument.getCapabilityDelegation() != null && tmpDidDocument.getCapabilityDelegation().contains(keyId)) {
			tmpDidDocument.setCapabilityDelegation(removeKeyPurposeByKeyId(didDocument.getCapabilityDelegation(), keyId));
		}

	    this.didDocument = tmpDidDocument;
	}

	/**
	 * Removes the specified key ID from the given key purpose list.
	 *
	 * @param keyPurposeList List of key purposes to remove the key ID from.
	 * @param keyId Key ID to remove from the key purpose list.
	 * @return Updated list of key purposes after removing the key ID. Returns null
	 *         if the list becomes empty after removal.
	 * @throws CoreException If the key purpose list is empty or if the key ID
	 *                             is not found in the list.
	 */
	private List<String> removeKeyPurposeByKeyId(List<String> keyPurposeList, String didKeyId) throws CoreException {
	    if (keyPurposeList == null) {
	        throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_EMPTY_KEYPURPOSE_LIST);
	    }

	    if (!keyPurposeList.remove(didKeyId)) {
	        throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_EMPTY_KEYPURPOSE_LIST);
	    }

	    return keyPurposeList.isEmpty() ? null : keyPurposeList;
	}

	
	/**
	 * Removes a verification method from the list if it exists.
	 *
	 * @param verificationMethodList The list of verification methods.
	 * @param didKeyId The ID of the verification method to remove.
	 * @return The updated list of verification methods after removal.
	 * @throws CoreException If the verification method with the given ID does not exist in the list.
	 */
	private List<VerificationMethod> removeVerificationMethod(List<VerificationMethod> verificationMethodList, String didKeyId)
			throws CoreException {

		boolean isverificationMethodList = isVerificationMethodList(verificationMethodList, didKeyId);

		if (!isverificationMethodList) {
			throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_UNSAVED_KEY);
		}

		Iterator<VerificationMethod> iterator = verificationMethodList.iterator();
		while (iterator.hasNext()) {
			VerificationMethod verificationMethod = iterator.next();
			if (verificationMethod.getId().equals(didKeyId)) {
				iterator.remove();
			}
		}
		return verificationMethodList;
	}

	/**
	 * Extracts the key ID from a DID key URL.
	 *
	 * @param didKeyUrl The DID key URL (format: "did#keyId").
	 * @return The extracted key ID.
	 */
	private String getKeyIdByDidKeyUrl(String didKeyUrl) {
		String keyId = didKeyUrl.split("#")[1];
		return keyId;
	}
 	
	/**
	 * Checks if a verification method with the given ID exists in the list.
	 *
	 * @param verificationMethodList The list of verification methods.
	 * @param didKeyId The ID of the verification method to check.
	 * @return true if the verification method exists in the list, false otherwise.
	 */
	private boolean isVerificationMethodList(List<VerificationMethod> verificationMethodList, String didKeyId) {
	    for (VerificationMethod verificationMethod : verificationMethodList) {
	        if (verificationMethod.getId().equals(didKeyId)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Adds the specified context to the DID Document's current contexts list if it
	 * is not already present.
	 *
	 * @param context Context to add to the DID Document.
	 * @throws CoreException If the context is already present in the DID Document's
	 *                             current contexts list.
	 */
	public void addContext(String context) throws CoreException {
	    List<String> currentContexts = didDocument.getContext();
	    if (!currentContexts.contains(context)) {
	        currentContexts.add(context);
	    }
	    
	    didDocument.setContext(currentContexts);
	}

	/**
	 * Initializes a new ArrayList from the provided list if it is not null,
	 * otherwise returns a new empty ArrayList.
	 *
	 * @param list The list to initialize.
	 * @param <T> Type of elements in the list.
	 * @return An ArrayList initialized with elements from the provided list,
	 *         or a new empty ArrayList if the provided list is null.
	 */
	private <T> List<T> initializeList(List<T> list) {
	    return list != null ? new ArrayList<>(list) : new ArrayList<>();
	}
	
	/**
	 * Converts the given ZonedDateTime object to a formatted string using
	 * the specified date format.
	 *
	 * @param date ZonedDateTime object to convert.
	 * @return Formatted string representation of the date.
	 */
	public static String dateToString(ZonedDateTime date) {
		return date.format(DATE_FORMAT);
	}
	
	/**
	 * Checks if the provided key ID exists in the list of key purposes.
	 *
	 * @param keyPurposeList List of key purposes to check.
	 * @param keyId Key ID to check for duplicates.
	 * @throws CoreException If the key ID is already present in the key purpose list.
	 */
	private void checkKeyPurposeList(List<String> keyPurposeList, String publicKeyId) throws CoreException {
		if (keyPurposeList != null && keyPurposeList.contains(publicKeyId)) {
			throw new CoreException(CoreErrorCode.ERR_CODE_DIDMANAGER_DUPLICATED_KEY);
		}
	}
}
