/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.manager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.omnione.did.core.data.rest.ClaimInfo;
import org.omnione.did.core.data.rest.IssueVcParam;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.core.exception.CoreErrorCode;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.util.VerifyUtil;
import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.enums.did.DidKeyType;
import org.omnione.did.data.model.enums.did.ProofPurpose;
import org.omnione.did.data.model.enums.did.ProofType;
import org.omnione.did.data.model.enums.vc.ClaimFormat;
import org.omnione.did.data.model.enums.vc.ClaimType;
import org.omnione.did.data.model.enums.vc.Location;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.data.model.provider.Provider;
import org.omnione.did.data.model.provider.ProviderDetail;
import org.omnione.did.data.model.schema.ClaimDef;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.Claim;
import org.omnione.did.data.model.vc.CredentialSchema;
import org.omnione.did.data.model.vc.CredentialSubject;
import org.omnione.did.data.model.vc.I18N;
import org.omnione.did.data.model.vc.Issuer;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.data.model.vc.VcProof;
import org.omnione.did.data.model.vc.VerifiableCredential;

public class VcManager {

  /**
   * Issue VerifiableCredential
   * 
   * @param vcParam The parameters required to issue the verifiable credential, including types, providerDetails, schema etc...
   * @param subjectDid The DID (Decentralized Identifier) of the subject to whom the verifiable credential is issued.
   * @return The issued verifiable credential with all required attributes set according to the provided parameters. 
   * @throws CoreException 
   */
  public VerifiableCredential issueCredential(IssueVcParam vcParam, String subjectDid) throws CoreException {

    VerifiableCredential verifiableCredential = new VerifiableCredential();

    if (vcParam.getContext() != null && !vcParam.getContext().isEmpty()) {
      verifiableCredential.setContext(vcParam.getContext());
    } else {
      verifiableCredential.setContext();
    }

    verifiableCredential.setId(UUID.randomUUID().toString());
    
    if(vcParam.getVcType() != null && !vcParam.getVcType().isEmpty()) {
    	List<String> vcTypeList = vcParam.getVcType().stream()
    			.map(VcType::getRawValue)
    			.collect(Collectors.toList());
    	verifiableCredential.setType(vcTypeList);
    } else {
    	throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_MISSING_VC_TYPE);
    }


    verifiableCredential.setIssuer(createIssuer(vcParam.getProviderDetail()));
    verifiableCredential.setIssuanceDate(vcParam.getIssuanceDate());

    verifiableCredential.setValidFrom(vcParam.getValidFrom() != null ? 
        vcParam.getValidFrom() : verifiableCredential.getIssuanceDateObject());
    verifiableCredential.setValidUntil(vcParam.getValidUntil() != null ? 
        VerifiableCredential.dateToUTC0String(vcParam.getValidUntil()) : IssueVcParam.DEFAULT_VALID_UNTIL);

    if(vcParam.getVcSchema().getMetadata().getFormatVersion() != null || 
        !vcParam.getVcSchema().getMetadata().getFormatVersion().isEmpty())
      verifiableCredential.setFormatVersion(vcParam.getVcSchema().getMetadata().getFormatVersion());
    else 
        throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_SCHEMA_MISSING_DETAIL);

    if(vcParam.getVcSchema().getMetadata().getLanguage() != null || 
        !vcParam.getVcSchema().getMetadata().getLanguage().isEmpty())
      verifiableCredential.setLanguage(vcParam.getVcSchema().getMetadata().getLanguage());
    else 
        throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_SCHEMA_MISSING_DETAIL);

    verifiableCredential.setEvidence(vcParam.getEvidences());

    verifiableCredential.setCredentialSchema(createSchema(vcParam));

    checkClaims(vcParam.getPrivacy().keySet(), vcParam.getVcSchema().getCredentialSubject().getClaims());
    
    verifiableCredential.setCredentialSubject(createSubject(vcParam, verifiableCredential.getId(), subjectDid));

    return verifiableCredential;
  }

  /**
   * Generate VerifiableCredential Meta Data
   * 
   * @param verifiableCredential The VerifiableCredential for which to generate metadata.
   * @param certVcRefStr Provider subscription certificate VC URL
   * @return The Meta Data about the verifiable credential stored in the blockchain.
   */
  public VcMeta generateVcMetaData(VerifiableCredential verifiableCredential, String certVcRefStr) {
    VcMeta vcMeta = new VcMeta();
    vcMeta.setId(verifiableCredential.getId());
    vcMeta.setIssuer(getProviderByIssuer(verifiableCredential.getIssuer(), certVcRefStr));
    vcMeta.setSubject(verifiableCredential.getCredentialSubject().getId());
    vcMeta.setCredentialSchema(verifiableCredential.getCredentialSchema());
    vcMeta.setStatus(VcStatus.ACTIVE.getRawValue());
    vcMeta.setIssuanceDate(verifiableCredential.getIssuanceDate());
    vcMeta.setValidFrom(verifiableCredential.getValidFrom());
    vcMeta.setValidUntil(verifiableCredential.getValidUntil());
    vcMeta.setFormatVersion(verifiableCredential.getFormatVersion());
    vcMeta.setLanguage(verifiableCredential.getLanguage());
    return vcMeta;
  }
  
  /**
   * Get Signature Origin Data
   * 
   * @param signKeyId The ID of the signing key.
   * @param issuerDidDoc The DID document of the issuer.
   * @param verifiableCredential The verifiable credential for which to get the signature origin data.
   * @return The list of signature parameters containing the original signature data.
   * @throws CoreException 
   */
  public List<SignatureVcParams> getOriginDataForSign(String signKeyId, DidDocument issuerDidDoc, 
      VerifiableCredential verifiableCredential) throws CoreException {

    String publicKeyType = getPublicKeyTypeByDidDoc(signKeyId, issuerDidDoc);
    String proofType = getProofTypeByKeyType(publicKeyType);

    if(proofType == null || proofType.isEmpty()) {
      throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_NOT_ASSERTION_METHOD_TYPE);
    }

    List<SignatureVcParams> sigVcParamsList = new ArrayList<>();

    VerifiableCredential tmpVerifiableCredential = new VerifiableCredential();
    tmpVerifiableCredential.fromJson(verifiableCredential.toJson());

    tmpVerifiableCredential.setProof(getProofWithOutSign(proofType, tmpVerifiableCredential.getIssuanceDate(),
            signKeyId, ProofPurpose.ASSERTION_METHOD, issuerDidDoc));

    SignatureVcParams sigVcParams = new SignatureVcParams();
    sigVcParams.setIsSingleClaim(false);
    sigVcParams.setKeyId(signKeyId);
    sigVcParams.setOriginData(tmpVerifiableCredential.toJson());
    sigVcParams.setAlgorithm(proofType);
    sigVcParams.setKeyPurpose(ProofPurpose.ASSERTION_METHOD.getRawValue());
    
    sigVcParamsList.add(sigVcParams);

    return getClaimsSigParams(sigVcParamsList, tmpVerifiableCredential, signKeyId, proofType);
  }

  /**
   * Add Proof Data
   * @version 1.0.0
   * @param vc The verifiable credential to add the proof for.
   * @param sigVcParamsList The list of signature parameters to use for adding the proof.
   * @return The verifiable credential with the proof add.
   * @throws CoreException 
   */
  public VerifiableCredential addProof(VerifiableCredential vc, List<SignatureVcParams> sigVcParamsList) throws CoreException {
    VerifiableCredential tempVc = new VerifiableCredential();
    tempVc.fromJson(sigVcParamsList.get(0).getOriginData());
    VcProof proof = tempVc.getProof();
    
    for(SignatureVcParams sigVcParams : sigVcParamsList)
        proof = updateProofWithSignatureParams(proof, sigVcParams);
    
    vc.setProof(proof);
    
    return vc;
  }

  /**
   * Verifies the provided Verifiable Credential against the issuer's DID Document.
   * 
   * @param verifiableCredential The Verifiable Credential to be verified.
   * @param issuerDidDocument The DID Document of the issuer used for verification.
   * @param isCheckVcExpirationDate A boolean flag indicating whether the credential's expiration date should be checked.
   * @throws CoreException
   */
  public void verifyCredential(VerifiableCredential verifiableCredential, DidDocument issuerDidDocument, boolean isCheckVcExpirationDate) throws CoreException {
      
      if(isCheckVcExpirationDate) {
          boolean isExpired = VerifyUtil.isExpired(verifiableCredential.getValidUntil());
          if(isExpired) {
              throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_EXPIRED_VC, verifiableCredential.getId());
          }
      }
      
      if (verifiableCredential.getCredentialSubject() != null) {
          List<Claim> claimList = verifiableCredential.getCredentialSubject().getClaims();

          if (claimList == null || claimList.size() == 0) {
              throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_PRIVACY_NOT_EXIST, verifiableCredential.getId());
          }

          VerifiableCredential tmpVerifiableCredential = new VerifiableCredential();
          tmpVerifiableCredential.fromJson(verifiableCredential.toJson());

          SignatureVcParams sigVcParams = new SignatureVcParams();
          sigVcParams = VerifyUtil.getSignatureVcParams(tmpVerifiableCredential, issuerDidDocument, false
                  , verifiableCredential.getProof().getProofValue());
          
          // ProofValue Verify
          VerifyUtil.verifySignature(sigVcParams);
          
          for (int i = 0; i < claimList.size(); i++) {
              tmpVerifiableCredential.fromJson(verifiableCredential.toJson());
              
              List<Claim> tmpClaimList = new ArrayList<Claim>();
              tmpClaimList.add(claimList.get(i));
              
              tmpVerifiableCredential.getCredentialSubject().setClaims(tmpClaimList);
              
              SignatureVcParams sigVcParamsByClaims = new SignatureVcParams();
              sigVcParamsByClaims = VerifyUtil.getSignatureVcParams(tmpVerifiableCredential, issuerDidDocument, true
                      , verifiableCredential.getProof().getProofValueList().get(i));
              sigVcParamsByClaims.setClaimCode(claimList.get(i).getCode());
              
              // ProofsValue Verify
              VerifyUtil.verifySignature(sigVcParamsByClaims);
          }
      }
  }

/**
   * Create Issuer
   *
   * @param providerDetail The provider details used to create the issuer.
   * @return The created issuer.
   */
  private Issuer createIssuer(ProviderDetail providerDetail) {
      Issuer issuer = new Issuer();
      issuer.setId(providerDetail.getDid());
      issuer.setName(providerDetail.getName());
      return issuer;
  }  

  /**
   * Create Schema
   *
   * @param vcParam The parameters required to create the schema.
   * @return The created credential schema to be set in the verifiable credential.
   */
  private CredentialSchema createSchema(IssueVcParam vcParam) {

    CredentialSchema credSchema = new CredentialSchema();
    credSchema.setId(vcParam.getVcSchema().getId());
    credSchema.setType(vcParam.getSchemaType());
    return credSchema;
  }

  /**
   * Create Subject
   *
   * @param vcParam The parameters required to create the credential subject.
   * @param vcId The ID of the verifiable credential.
   * @param subjectDid The DID of the subject.
   * @return The created credential subject to be set in the verifiable credential.
   * @throws CoreException 
   */
  private CredentialSubject createSubject(IssueVcParam vcParam, String vcId, String subjectDid) throws CoreException {
    CredentialSubject credentialSubject = new CredentialSubject();  
    credentialSubject.setId(subjectDid);
    
    VcSchema vcSchema = vcParam.getVcSchema();

    List<Claim> vcInnerClaimList = new ArrayList<Claim>();

    List<SchemaClaims> schemaClaimsList = vcSchema.getCredentialSubject().getClaims();
    for(SchemaClaims schemaClaim : schemaClaimsList) {

      for (ClaimDef claimDef : schemaClaim.getItems()) {
          ClaimInfo claimInfo = vcParam.getPrivacy().get(schemaClaim.getNamespace().getId()+"."+claimDef.getId());
          if(claimInfo != null) {        
            Claim claim = createClaims(claimDef, claimInfo, vcId);
              if(claim !=null) {
                  vcInnerClaimList.add(claim);
              }   
          }
      }

      if (!vcInnerClaimList.isEmpty()) {
        credentialSubject.setClaims(vcInnerClaimList);
      }
    }
    return credentialSubject;
  }

  /**
   * Create Claims
   *
   * @param claimDef The claim definition.
   * @param claimInfo The claim containing actual personal information.
   * @param vcId The ID of the verifiable credential.
   * @return The created claim.
   * @throws CoreException 
   */
  private Claim createClaims(ClaimDef claimDef, ClaimInfo claimInfo, String vcId) throws CoreException {
    Claim claim =  new Claim();   
    claim.setCaption(claimDef.getCaption());
    claim.setType(claimDef.getType());
    claim.setFormat(claimDef.getFormat());
    claim.setHideValue(claimDef.isHideValue());
    if(claimDef.getLocation() != null)
        claim.setLocation(claimDef.getLocation());
    claim.setCode(claimInfo.getCode());
    if(claimDef.getI18n() != null)
        claim.setI18n(createI18n(claimDef.getI18n(), claimInfo.getI18n()));

    // TODO Value multiBase Policy
    String value = null;
    if(claimDef.getLocation() == null) {
        value = new String(claimInfo.getValue(), StandardCharsets.UTF_8);
    }
    else if(claimDef.getType().equals(ClaimType.TEXT.getRawValue())) {
        try {
            value = (claimDef.getFormat().equals(ClaimFormat.PLAIN.getRawValue()) && claimDef.getLocation().equals(Location.INLINE.getRawValue()))
                    ?  new String(claimInfo.getValue(), StandardCharsets.UTF_8)
                            :  MultiBaseUtils.encode(claimInfo.getValue(), MultiBaseType.getByCharacter(claimInfo.getEncodeType()));
        } catch (CryptoException e) {
            throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_MULTIBASE_ENCODING_FAIL, e);
        }
    } else {
        if(claimDef.getLocation().equals(Location.ATTACH.getRawValue())) {
            value = claimInfo.getValue() !=null ? new String(claimInfo.getValue(), StandardCharsets.UTF_8) : vcId + "#" + claimInfo.getCode();
        } else if(claimDef.getLocation().equals(Location.REMOTE.getRawValue())) {
            try {
                value = MultiBaseUtils.encode(claimInfo.getValue(), MultiBaseType.getByCharacter(claimInfo.getEncodeType()));
            } catch (CryptoException e) {
                throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_MULTIBASE_ENCODING_FAIL, e);
            }
        }
    }
    claim.setValue(value);
    if (claimDef.getLocation() != Location.INLINE.getRawValue()) {                 
      claim.setDigestSRI(claimInfo.getDigestSRI());
    }

    return claim;
  }

  /**
   * Creates a map of I18N objects by combining caption information from `claimDefI18n` with existing I18N objects from `claimInfoI18n`.
   * 
   * @param claimDefI18n A map where the key is the language code and the value is the caption text.
   * @param claimInfoI18n A map where the key is the language code and the value is the corresponding I18N object.
   * @return A map containing the updated I18N objects, where the key is the language code.
   */
  private Map<String, I18N> createI18n(Map<String, String> claimDefI18n, Map<String, I18N> claimInfoI18n) {
      Map<String, I18N> claimI18n = new HashMap<>();
      
      for(String language : claimDefI18n.keySet()) {
          I18N i18n = claimInfoI18n.get(language);
          i18n.setCaption(claimDefI18n.get(language));
          claimI18n.put(language, i18n);
      }
      return claimI18n;
  }

  /**
   * Check Claims
   *
   * @param claimCodes The set of claim codes submitted with the issuer's actual personal information.
   * @param schemaClaims The list of required schema claims that must be present in the verifiable credential.
   * @throws CoreException
   */
  private void checkClaims(Set<String> claimCodes, List<@Valid SchemaClaims> schemaClaims) throws CoreException {
    List<String> schemaClaimCodes = new ArrayList<>();

    for(SchemaClaims schemaClaim : schemaClaims) {
      for (ClaimDef claimDef : schemaClaim.getItems()) {
        if(claimDef.getRequired() == null || claimDef.getRequired()) {        
          String schemaClaimCode = schemaClaim.getNamespace().getId()+"."+claimDef.getId();
          schemaClaimCodes.add(schemaClaimCode);
        }
      }
    }
    for(String schemaClaimCode : schemaClaimCodes) {
      boolean check = claimCodes.stream().anyMatch(claimCode -> schemaClaimCode.equals(claimCode));

      if (!check) {
        throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_NOT_SUBMITED_PUBLIC_CLAIM);
      }
    }
  }

  /**
   * Get Provider Detail by Issuer
   *
   * @param issuer The issuer for which to get the provider details.
   * @param certVcRefStr Provider subscription certificate VC URL
   * @return The provider details associated with the given issuer.
   */
  private Provider getProviderByIssuer(Issuer issuer, String certVcRefStr) {
      Provider provider = new Provider();
      provider.setDid(issuer.getId());
      provider.setCertVcRef(certVcRefStr);
      return provider;
  }  

  /**
   * Update Proof with Signature Params
   *
   * @param proof The proof to update.
   * @param sigVcParams The signature parameters to use for updating the proof.
   * @return The updated proof.
   * @throws CoreException
   */
  private VcProof updateProofWithSignatureParams(VcProof proof, SignatureVcParams sigVcParams) throws CoreException {
    
    validateKeyId(sigVcParams.getKeyId(), proof);
    
    if (!sigVcParams.getIsSingleClaim()) {
        proof.setProofValue(sigVcParams.getSignatureValue());
    } else {
        if(proof.getProofValueList() == null) {
            proof.setProofValueList(new ArrayList<String>());
        }       
        proof.getProofValueList().add(sigVcParams.getSignatureValue());
    }
    
    return proof;
  }

  /**
   * Validate Key ID
   *
   * @param keyId The key ID to validate.
   * @param proof The proof object containing the key information and signature.
   * @throws CoreException
   */
  private void validateKeyId(String keyId, VcProof proof) throws CoreException {
    if (!keyId.equals(proof.getVerificationMethod().split("#")[1])) {
      throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_NOT_MACTH_SIGN_KEY_AND_PROOF_KEY);
    }
  }

  /**
   * Get Claims Signature Params
   *
   * @param sigVcParamsList The list of signature parameters.
   * @param tmpVerifiableCredential The temporary verifiable credential.
   * @param signKeyId The ID of the signing key.
   * @param proofType The type of proof.
   * @return The updated list of signature parameters including claims signature parameters.
   */
  private List<SignatureVcParams> getClaimsSigParams(List<SignatureVcParams> sigVcParamsList, VerifiableCredential tmpVerifiableCredential, String signKeyId, String proofType) {

    List<Claim> claimList = tmpVerifiableCredential.getCredentialSubject().getClaims();

    for (Claim claim : claimList) {
      List<Claim> tmpClaimList = new ArrayList<Claim>();
      tmpClaimList.add(claim);
      tmpVerifiableCredential.getCredentialSubject().setClaims(tmpClaimList);

      SignatureVcParams sigVcParam = new SignatureVcParams();
      sigVcParam.setKeyId(signKeyId);
      sigVcParam.setOriginData(tmpVerifiableCredential.toJson());
      sigVcParam.setAlgorithm(proofType);
      sigVcParam.setKeyPurpose(ProofPurpose.ASSERTION_METHOD.getRawValue());
      sigVcParam.setIsSingleClaim(true);
      sigVcParam.setClaimCode(claim.getCode());

      sigVcParamsList.add(sigVcParam);
    }
    return sigVcParamsList;
  }

  /**
   * Get Public Key Type
   *
   * @param signKeyId The ID of the signing key.
   * @param issuerDidDoc The DID document of the issuer.
   * @return The type of the public key.
   * @throws CoreException
   */
  private String getPublicKeyTypeByDidDoc(String signKeyId, DidDocument issuerDidDoc) throws CoreException {    
    boolean isContainKey = false;

    List<String> didDocPubKeyIdList = issuerDidDoc.getAssertionMethod();
    for(String pubKeyId : didDocPubKeyIdList) {
      if(pubKeyId.equals(signKeyId)) {
        isContainKey = true;
        break;
      }      
    }

    if(!isContainKey) {
      throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_NOT_ASSERTION_METHOD_TYPE);
    }

    List<VerificationMethod> verificationMethodList = issuerDidDoc.getVerificationMethod();
    for(VerificationMethod verificationMethod : verificationMethodList) {
      if(verificationMethod.getId().equals(signKeyId)) {
        return verificationMethod.getType();
      }
    }
    return null;
  }

  /**
   * Get Proof Type by Key Type
   *
   * @param publicKeyType The type of the public key.
   * @return The type of proof.
   */
  private String getProofTypeByKeyType(String publicKeyType) {
    
    ProofType proofType = null;
    DidKeyType didKeyType = DidKeyType.fromString(publicKeyType);
    
    switch(didKeyType) {
      case RSA_VERIFICATION_KEY_2018: 
        proofType = ProofType.RSA_SIGNATURE_2018;
        return proofType.getRawValue();
      case SECP256K1_VERIFICATION_KEY_2018:
        proofType = ProofType.SECP256K1_SIGNATURE_2018;
        return proofType.getRawValue();
      case SECP256R1_VERIFICATION_KEY_2018:
        proofType = ProofType.SECP256R1_SIGNATURE_2018;
        return proofType.getRawValue();
      default :
          return null;
    }
  }

  /**
   * Get Proof Without Sign
   *
   * @param proofType The type of proof.
   * @param issuanceDate The issuance date of the verifiable credential.
   * @param signKeyId The ID of the signing key.
   * @param proofPurpose The purpose of the proof.
   * @param issuerDidDoc The DID document of the issuer.
   * @return The proof without the signature.
   */
  private VcProof getProofWithOutSign(String proofType, String issuanceDate, String signKeyId, ProofPurpose proofPurpose, DidDocument issuerDidDoc) {
    VcProof vcProof = new VcProof();
    vcProof.setType(proofType);
    vcProof.setCreated(issuanceDate);
    vcProof.setVerificationMethod(issuerDidDoc.getId()+"?versionId="+issuerDidDoc.getVersionId()+"#"+signKeyId);
    vcProof.setProofPurpose(proofPurpose.getRawValue());
    return vcProof;
  }
}
