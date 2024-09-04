/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.util;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.omnione.did.core.data.rest.SignatureParams;
import org.omnione.did.core.data.rest.SignatureVcParams;
import org.omnione.did.core.exception.CoreErrorCode;
import org.omnione.did.core.exception.CoreException;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.crypto.enums.DigestType;
import org.omnione.did.crypto.enums.EccCurveType;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.DigestUtils;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.crypto.util.SignatureUtils;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.VerificationMethod;
import org.omnione.did.data.model.enums.did.ProofType;
import org.omnione.did.data.model.vc.VcProof;
import org.omnione.did.data.model.vc.VerifiableCredential;

public class VerifyUtil {

    /**
     * Checks if the given date data is expired.
     *
     * @param validUntil The date string to check.
     * @return True if the date is expired, false otherwise.
     * @throws CoreException
     */
    public static boolean isExpired(String validUntil) throws CoreException {
        ZonedDateTime expirationZonedDateTime = VerifiableCredential.stringToUTC0Date(validUntil);
        ZonedDateTime nowZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        if (expirationZonedDateTime.isBefore(nowZonedDateTime)) {
            return true;
        }
        return false;
    }
    
    /**
     * Constructs and returns a SignatureVcParams object using the provided Verifiable Credential and issuer's DID Document.
     * 
     * @param tmpVerifiableCredential The Verifiable Credential used to extract signature-related parameters.
     * @param issuerDidDocument The DID Document of the issuer, used to retrieve the public key for verification.
     * @param isSingleClaim A boolean flag indicating whether the signature is for a single claim or the entire Verifiable Credential.
     * @param proofValue The cryptographic proof (signature) associated with the Verifiable Credential or claim.
     * @return A SignatureVcParams object populated with the necessary data for signature verification.
     * @throws CoreException
     */
    public static SignatureVcParams getSignatureVcParams(VerifiableCredential tmpVerifiableCredential, DidDocument issuerDidDocument
            , boolean isSingleClaim, String proofValue) throws CoreException {

        VcProof vcProof = tmpVerifiableCredential.getProof();
        VcProof tmpProof = new VcProof();
        tmpProof.setCreated(vcProof.getCreated());
        tmpProof.setProofPurpose(vcProof.getProofPurpose());
        tmpProof.setType(vcProof.getType());
        tmpProof.setVerificationMethod(vcProof.getVerificationMethod());

        tmpVerifiableCredential.setProof(tmpProof);
        
        SignatureVcParams sigVcParams = new SignatureVcParams();
        sigVcParams.setIsSingleClaim(isSingleClaim);
        sigVcParams.setOriginData(tmpVerifiableCredential.toJson());
        sigVcParams.setSignatureValue(proofValue);
        DidManager didManager = new DidManager();
        didManager.parse(issuerDidDocument.toJson());

        VerificationMethod publicKey =  didManager.getVerificationMethodByKeyId(tmpProof.getVerificationMethod().split("\\#")[1]);

        if (publicKey == null) {
            throw new CoreException(CoreErrorCode.ERR_CODE_VCMANAGER_NOT_EXIST_SIGNING_KEY);
        }
        sigVcParams.setPublicKey(publicKey.getPublicKeyMultibase());
        sigVcParams.setAlgorithm(tmpProof.getType());
        
        return sigVcParams;
    }
    
    /**
     * Hashes the provided data using SHA-256.
     *
     * @param signOrignData The data to hash.
     * @return The hashed data.
     * @throws CoreException 
     */
    private static byte[] hashData(String signOrignData) throws CoreException{
        try {
            return DigestUtils.getDigest(signOrignData.getBytes(StandardCharsets.UTF_8), DigestType.SHA256);
        } catch (CryptoException e) {
            throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_GEN_HASH_FAIL ,e);
        }
    }

    /**
     * Gets the EccCurveType based on the Sign algorithm.
     *
     * @param sigAlgorithm The signature algorithm.
     * @return The corresponding elliptic curve type.
     */
    private static EccCurveType getCurveTypeByAlgorithm(String sigAlgorithm) {
        ProofType proofType = ProofType.fromString(sigAlgorithm);

        // TODO To modify RSA signature verification, also modify the syntax below.
        EccCurveType vcEccCurveType = proofType == ProofType.SECP256K1_SIGNATURE_2018 ? EccCurveType.Secp256k1 : EccCurveType.Secp256r1;
        return vcEccCurveType;
    }

    /**
     * Verifies the signature with the provided public key, hashed data, and signature.
     *
     * @param compressPublicKeyBytes The compressed public key bytes.
     * @param hashedSignOrignData The hashed data.
     * @param signatureByte The signature bytes.
     * @param eccCurveType The ECC curve type.
     * @throws CoreException
     */
    public static void verifySignature(SignatureParams sigParams) throws CoreException {
        byte[] hashedSignOrignData = hashData(sigParams.getOriginData());

        byte[] signatureByte;
        byte[] compressPublicKeyBytes;
        try {
            signatureByte = MultiBaseUtils.decode(sigParams.getSignatureValue());
            compressPublicKeyBytes = MultiBaseUtils.decode(sigParams.getPublicKey());
        } catch (CryptoException e) {
            throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_MULTIBASE_DECODING_FAIL ,e);
        }
        
        EccCurveType eccCurveType = getCurveTypeByAlgorithm(sigParams.getAlgorithm());
        
        try {
            SignatureUtils.verifyCompactSignWithCompressedKey(compressPublicKeyBytes, hashedSignOrignData, signatureByte, eccCurveType);
        } catch (CryptoException e) {
            throw new CoreException(CoreErrorCode.ERR_CODE_VPMANAGER_VERIFY_SIGNATURE_FAIL, e.getMessage());
        }
    }
}
