---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---

Core SDK Server API
==

- Subject: Core SDK Server API
- Author: Baek Jong-Yun
- Date: 2024-08-30
- Version: v1.0.0

| Version | Date       | Changes                   |
| ------- | ---------- | -------------------------- |
| v1.0.0  | 2024-08-30 | Initial version            |

<div style="page-break-after: always;"></div>

# Table of Contents
- [1. APIs](#1-apis)
  - [1.1. DID](#11-did)
    - [1.1.1. createDocument](#111-createdocument)
    - [1.1.2. load](#112-load)
    - [1.1.3. parse](#113-parse)
    - [1.1.4. addVerifiCationMethod](#114-addverificationmethod)
    - [1.1.5. addKeyPurpose](#115-addkeypurpose)
    - [1.1.6. removeVerificationMethod](#116-removeverificationmethod)
    - [1.1.7. removeKeyPurpose](#117-removekeypurpose)
    - [1.1.8. getVerificationMethodByKeyId](#118-getverificationmethodbykeyid)
    - [1.1.9. getAllSignKeyIdList](#119-getallsignkeyidlist)
    - [1.1.10. getOriginDataForSign](#1110-getorigindataforsign)
    - [1.1.11. verifyDocumentSignature](#1111-verifydocumentsignature)
    - [1.1.12. addServiceEndPoint](#1112-addserviceendpoint)
    - [1.1.13. deleteServiceEndPoint](#1113-deleteserviceendpoint)
    - [1.1.14. addProof](#1114-addproof)
  - [1.2. VerifiableCredential](#12-verifiablecredential)
    - [1.2.1. issueCredential](#121-issuecredential)
    - [1.2.2. generateVcMetaData](#122-generatevcmetadata)
    - [1.2.3. getOriginDataForSign](#123-getorigindataforsign)
    - [1.2.4. addProof](#124-addproof)
    - [1.2.5. verifyCredential](#125-verifyCredential)
  - [1.3. VerifiablePresentation](#13-verifiablepresentation)
    - [1.3.1. verify](#131-verify)
    - [1.3.2. getClaims](#132-getclaims)
- [2. Reference Classes](#2-reference-classes)
  - [2.1. DidKeyInfo](#21-didkeyinfo)
  - [2.2. IssueVcParam](#22-issuevcparam)
  - [2.3. ClaimInfo](#23-claiminfo)
  - [2.4. VpVerifyParam](#24-vpverifyparam)
  - [2.5. SignatureParams](#25-signatureparams)
  - [2.6. SignatureVcParams](#26-signaturevcparams)
- [3. Sample](#3-sample)
  - [3.1. DidDocument Json](#31-did-document-json)
  - [3.2. VerifiableCredential Json](#32-verifiable-credential-json)

# 1. APIs
## 1.1. DID

### 1.1.1. createDocument

#### Class Name
`DidManager`

#### Function Name
`createDocument`

#### Function Description
`Initial creation of the DID document`

#### Input Parameters
| Parameter | Type   | Description                | **M/O** | **Notes** |
|-----------|--------|----------------------------|---------|-----------|
| did        | String | Owner's DID                 | M       |           |
| controller| String | DID of the controller       | M       |           |
| didKeyInfos | List&lt;DidKeyInfo&gt; | Key information to be added to the DID document | M | Refer to the data model DidKeyInfo |

* controller: The entity that can change the state of the DID Document

#### Output Parameters
| Type         | Description            | **M/O** | **Notes** |
|--------------|------------------------|---------|-----------|
| DidDocument  | DidDocument object     | M       |           |

#### Function Declaration

```java
DidDocument createDocument(String did, String controller, List<DidKeyInfo> didKeyInfos) -> throws CoreException
```

### Function Usage
```java
// Example of creating a Provider DID Document
didManager = new DidManager();
String did = "did:omn:issuer";
String controller = "did:omn:tas";

List<DidKeyInfo> didKeyInfos = new ArrayList<>();

DidKeyInfo keyInfo = new DidKeyInfo();
keyInfo.setKeyId("assert");
keyInfo.setAlgoType(DidKeyType.SECP256R1_VERIFICATION_KEY_2018.getRawValue());
keyInfo.setPublicKey("zvXsXFNahfw9Cz4KQEdLjBtoUEUiVHoMxWs23j6axNuTP");
keyInfo.setController(did);
keyInfo.setAuthType(AuthType.Free);
didKeyInfos.add(keyInfo);

didManager.createDocument(did, controller, didKeyInfos);
```

<br>

## 1.1.2 load

### Class Name
`DidManager`

### Function Name
`load`

### Function Description
`Loads the DID Document file and sets it in the DidManager.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **Notes** |
|-----------|--------|----------------------------|---------|-----------|
| didDocPath    | String    | Path of the DID Document file including the filename | M | |

### Output Parameters
n/a

### Function Declaration

```java
void load(String didDocPath) -> throws CoreException
```

### Function Usage
```java
String didDocPath = "src/test/test.did"; // DID path including the file name
DidManager didManager = new DidManager();
didManager.load(didDocPath);
```

<br>

## 1.1.3. parse

### Class Name
`DidManager`

### Function Name
`parse`

### Function Description
`Parses the DID Document string into an object and sets it in DidManager.`

### Input Parameters
| Parameter | Type   | Description                | **M/O** | **Remarks** |
|-----------|--------|----------------------------|---------|-------------|
| didDocJson    | String    | DID Document string | M       |             |


### Output Parameters
n/a

### Function Declaration

```java
void parse(String didJson);
```

### Function Usage
```java
DidManager didManager = new DidManager();
String didDocumentJson; // didDocument string
didManager.parse(didDocumentJson);
```

<br>

## 1.1.4. addVerifiCationMethod

### Class Name
`DidManager`

### Function Name
`addVerifiCationMethod`

### Function Description
`Adds a key to the `verificationMethod` of the DID Document`

### Input Parameters

| Parameter    | Type        | Description                          | **M/O** | **Notes**                  |
|--------------|-------------|--------------------------------------|---------|----------------------------|
| didKeyInfo    | DidKeyInfo   | Key information to be added to the `verificationMethod` | M       | Refer to the data model DidKeyInfo |

* When setting the `keyPurpose` variable within the `didKeyInfo` object, the `addKeyPurpose()` method is executed internally.

### Output Parameters
void

### Function Declaration

```java
void addVerifiCationMethod(DidKeyInfo didKeyInfo) -> throws CoreException
```

### Function Usage
```java
DidKeyInfo keyInfo = new DidKeyInfo();
keyInfo.setKeyId("auth");
keyInfo.setAlgoType(DidKeyType.SECP256R1_VERIFICATION_KEY_2018.getRawValue());
keyInfo.setPublicKey("z26c1jGCoQa2gpuUxGifkkpwgmndSGUH5Ma6fCsxcg9XiX");
keyInfo.setController("did:omn:issuer");
keyInfo.setAuthType(AuthType.Free);

didManager.addVerifiCationMethod(keyInfo);
```

<br>

## 1.1.5. addKeyPurpose

### Class Name
`DidManager`

### Function Name
`addKeyPurpose`

### Function Description
`Defines the purpose of a key added to the `verificationMethod` of a DID Document.`

### Input Parameters

| Parameter   | Type                        | Description                  | **M/O** | **Notes**                |
|-------------|-----------------------------|------------------------------|---------|--------------------------|
| didKeyId     | String                      | Key ID to which purpose is added | M       |                          |
| keyPurposes  | List&lt;ProofPurpose&gt;    | List of key purpose enums    | M       | Refer to ProofPurpose data |


### Output Parameters
void


### Function Declaration

```java
void addKeyPurpose(String didKeyId, List<ProofPurpose> keyPurposes) -> throws CoreException
```

### Function Usage
```java
List<ProofPurpose> proofPurposes = new ArrayList<>();
proofPurposes.add(ProofPurpose.AUTHENTICATION);

didManager.addKeyPurpose("auth", proofPurposes);
```

<br>

## 1.1.6. removeVerificationMethod

### Class Name
`DidManager`

### Function Name
`removeVerificationMethod`

### Function Description
`Deletes the public key stored in the `verificationMethod` of the DID Document.`  
`If the purpose of the key to be deleted is defined, the `removeKeyPurpose()` method is executed as well.`

### Input Parameters

| Parameter | Type   | Description       | **M/O** | **Notes** |
|-----------|--------|-------------------|---------|-----------|
| keyId     | String | Key ID            | M       |           |


### Output Parameters
void


### Function Declaration

```java
void removeVerificationMethod(String keyId) -> throws CoreException
```

### Function Usage
```java
didManager.removeVerificationMethod("assert");
```

<br>

## 1.1.7. removeKeyPurpose

### Class Name
`DidManager`

### Function Name
`removeKeyPurpose`

### Function Description
`Query the Key Usage List and delete the entered Key ID.`

### Input Parameters

| Parameter | Type   | Description                     | **M/O** | **Notes** |
|-----------|--------|---------------------------------|---------|-----------|
| keyId     | String | The Key ID to delete the usage for  | M       |           |


### Output Parameters
void


### Function Declaration

```java
void removeKeyPurpose(String keyId) -> throws DidMa
```

### Function Usage
```java
didManager.removeKeyPurpose("assert");
```

<br>

## 1.1.8. getVerificationMethodByKeyId

### Class Name
`DidManager`

### Function Name
`getVerificationMethodByKeyId`

### Function Description
`Retrieves the key information stored in the DID Document by key ID.`

### Input Parameters

| Parameter | Type   | Description         | **M/O** | **Notes** |
|-----------|--------|---------------------|---------|-----------|
| didKeyId     | String | Did Key ID              | M       |           |


### Output Parameters

| Type       | Description      | **M/O** | **Notes**                      |
|------------|------------------|---------|--------------------------------|
| VerificationMethod  | VerificationMethod object | M       | Refer to the VerificationMethod data model |


### Function Declaration

```java
VerificationMethod getVerificationMethodByKeyId(String didKeyId);
```

### Function Usage
```java
PublicKey pubKey = didManager.getVerificationMethodByKeyId("assert");

System.out.println("multibasePubKey : " +pubKey.getPublicKeyMultibase());
System.out.println("keyAlgo : " + pubKey.getType()); 
```

<br>

## 1.1.9. getAllSignKeyIdList

### Class Name
`DidManager`

### Function Name
`getAllSignKeyIdList`

### Function Description
`Retrieves all key IDs stored in the DID Document, excluding keys for key agreement purposes (non-signable).`

### Input Parameters
n/a


### Output Parameters

| Type                | Description                           | **M/O** | **Notes** |
|---------------------|---------------------------------------|---------|-------------|
| List&lt;String&gt;  | All signable key IDs                  | M       |             |
* Signable key: Keys excluding keyAgreement usage

### Function Declaration

```java
List<String> getAllSignKeyIdList() -> throws CoreException
```

### Function Usage
```java
List<String> keyIds = new ArrayList<String>();
keyIds = didManager.getAllSignKeyIdList();
```

<br>

## 1.1.10. getOriginDataForSign

### Class Name
`DidManager`

### Function Name
`getOriginDataForSign`

### Function Description
`Extracts the original data for signing the DID Document.`

### Input Parameters

| Parameter | Type                  | Description                | **M/O** | **Notes** |
|-----------|-----------------------|----------------------------|---------|-------------|
| keyIds    | List&lt;String&gt;    | List of signing key IDs    | M       |             |


### Output Parameters

| Type                          | Description                | **M/O** | **Notes**              |
|-------------------------------|----------------------------|---------|--------------------------|
| List&lt;SignatureParams&gt;  | Data objects for signing   | M       | [Link](#25-signatureparams) |


### Function Declaration

```java
List<SignatureParams> getOriginDataForSign(List<String> keyIds) throws CoreException
```

# Function Usage
```java
// 1. Create a list of keyIDs to request signatures
List<String> keyIds = new ArrayList<String>();
keyIds.add("assert");
keyIds.add("auth");

// 2. Create objects to request signatures
List<SignatureParams> signatureParams = didManager.getOriginDataForSign(keyIds);

for(SignatureParams signatureParam : signatureParams) {
    System.out.println("originData: "+signatureParam.getOriginData());
}
```

<br>

## 1.1.11. verifyDocumentSignature

## Class Name
`DidManager`

## Function Name
`verifyDocumentSignature`

## Function Description
`Verifies the signature of a DID document.`

## Input Parameters
n/a


## Output Parameters
n/a


## Function Declaration

```java
void verifyDocumentSignature() -> throws CoreException
```

### Function Usage
```java
// Used when the DID Document with a signature is set in DidManager
didManager.verifyDocumentSignature();
```

<br>

## 1.1.12. addServiceEndPoint

### Class Name
`DidManager`

### Function Name
`addServiceEndPoint`

### Function Description
`Stores new service information or adds a service URL to the `Provider`'s DID Document.`

### Input Parameters

| Parameter      | Type               | Description       | **M/O** | **Notes**              |
|----------------|--------------------|-------------------|---------|------------------------|
| didServiceId   | String             | Service ID        | M       |                        |
| didServiceType | DidServiceType     | Service Type Enum | O       | Refer to the data model DidServiceType |
| url            | String             | Service URL       | M       |                        |


### Output Parameters
void


### Function Declaration

```java
void addServiceEndPoint(String didServiceId, DidServiceType didServiceType, String url) -> throws CoreException
```

### Function Usage
```java
// Set the DID Document to be changed in the DidManager and use it
String serviceId = "serviceID-1";
String serviceUrl = "https://did.omnione.net";
didManager.addServiceEndPoint(serviceId, DidServiceType.LINKED_DOMAINS, serviceUrl);
```

<br>

## 1.1.13. deleteServiceEndPoint

### Class Name
`DidManager`

### Function Name
`deleteServiceEndPoint`

### Function Description
`Deletes a service stored in the DID Document or removes the URL of the service.`

### Input Parameters

| Parameter        | Type              | Description          | **M/O** | **Notes**                           |
|------------------|-------------------|----------------------|---------|------------------------------------|
| didServiceId     | String            | Service ID           | O       |                                    |
| didServiceType   | DidServiceType    | Service Type Enum    | O       | Refer to the data model DidServiceType |
| url              | String            | Service URL          | O       |                                    |


### Output Parameters
void


### Function Declaration

```java
void deleteServiceEndPoint(String didServiceId, DidServiceType type, String url) -> throws CoreException
```

### Function Usage
```java
// After setting the DID Document to DidManager, use it to modify the service
String didServiceId = "serviceID-1";
String serviceUrl = "https://did.omnione.net";
didManager.deleteServiceEndPoint(didServiceId, DidServiceType.LINKED_DOMAINS, serviceUrl);
```

<br>

## 1.1.14. addProof

### Class Name
`DidManager`

### Function Name
`addProof`

### Function Description
`Adds a signature value to the DID Document of the TAS/Provider.`

### Input Parameters

| Parameter       | Type                              | Description                    | **M/O** | **Notes**                      |
|-----------------|-----------------------------------|--------------------------------|---------|---------------------------------|
| signatureParams | List&lt;SignatureParams&gt;       | Object containing signature-related data | M       | [Link](#25-signatureparams) |


### Output Parameters
void


### Function Declaration

```java
void addProof(List<SignatureParams> signatureParams) -> throws CoreException
```

### Function Usage
```java
// Set the DID Document to DidManager and use it after adding the signature

// Object without signature value from getOriginDataForSign()
List<SignatureParams> signatureParams = didManager.getOriginDataForSign(keyIds);

// Set signature values for each key ID in the SignatureParams object from the server
Map<String, String> signList = new HashMap<>(); 
signList.put("assert", "z3alkfjsldfj……WEdyvC");
signList.put("auth", "z3jmVrd4MqXP……EfCqKP");

List<SignatureParams> tmpSignatureParams = new ArrayList<SignatureParams>();
for(SignatureParams singleParam : signatureParams) {
    if(signList.containsKey(singleParam.getKeyId())) {
        singleParam.setSignatureValue(signList.get(singleParam.getKeyId()));
    }
    tmpSignatureParams.add(singleParam);
}

didManager.addProof(tmpSignatureParams);
```

<br>

## 1.2. VerifiableCredential

## 1.2.1. issueCredential

### Class Name
`VcManager`

### Function Name
`issueCredential`

### Function Description
`Issues a VerifiableCredential.`

### Input Parameters

| Parameter | Type          | Description                        | **M/O** | **Notes** |
|-----------|---------------|------------------------------------|---------|-------------|
| vcParam    | IssueVcParam  | Data object for VC issuance         | M       | [Link](#22-issuevcparam) |
| subjectDid        | String        | Owner's DID                         | M       |             |
* For information on the ClaimInfo object to be set within IssueVcParam, refer to [Link](#23-claiminfo).

### Output Parameters

| Type                   | Description                | **M/O** | **Notes**               |
|------------------------|----------------------------|---------|---------------------------|
| VerifiableCredential  | VC object                  | M       | Refer to the data model VerifiableCredential |

### Function Declaration

```java
VerifiableCredential issueCredential(IssueVcParam vcParam, String subjectDid) -> throws CoreException
```

### Function Usage
```java
// Create a sample using Provider VC
//1. Set up issueVcParam information
IssueVcParam issueVcParam = new IssueVcParam();

//1-1. Set up VcSchema information retrieved from the blockchain
VcSchema schema = new VcSchema();
issueVcParam.setSchema(schema);

//1-2. Set up Provider (VC Issuer) information retrieved from the blockchain
ProviderDetail providerDetail = new ProviderDetail();
issueVcParam.setProviderDetail(providerDetail);

//1-3. Set up Issuer DIDDocument information retrieved from the blockchain
DidDocument issuerDidDoc= new DidDocument();
issueVcParam.setDidDocument(issuerDidDoc);

//1-4. Set up user's personal information for claims stored in VcSchema
HashMap<String,ClaimInfo> privacy = new HashMap<>();  
ClaimInfo claimInfo = new ClaimInfo();
claimInfo.setCode("testId.userName");
claimInfo.setValue("Raon".getBytes());
ClaimInfo claimInfo2 = new ClaimInfo();
claimInfo2.setCode("testId.address");
claimInfo2.setValue("seoul".getBytes());
privacy.put(claimInfo.getCode(), claimInfo);
privacy.put(claimInfo2.getCode(), claimInfo2);
issueVcParam.setIssuerAddClaims(privacy);

//1-5. Set VC Type
List<VcType> vctypes = new ArrayList<>();
vctypes.add(VcType.CERTIFICATE_VC);
issueVcParam.setVcType(vctypes);

//1-6. Set up Evidence information
DocumentVerificationEvidence evidence = new DocumentVerificationEvidence(); 
evidence.setEvidenceDocument("BusinessLicense");
evidence.setSubjectPresence("Physical");
evidence.setDocumentPresence("Physical");
evidence.setId("Evidence Test URL");
evidence.setType("DocumentVerification");
evidence.setVerifier("did:omn:tas");
List<Evidence> evidenceList = new ArrayList<>();
evidenceList.add(evidence);
issueVcParam.setEvidences(evidenceList);

//2. Set Holder DID
String subjectDid = "did:omn:raon";

//3. Call issueVc()
VerifiableCredential vc = new VerifiableCredential();
vc = vcManager.issueCredential(issueVcParam, subjectDid);
```

<br>

## 1.2.2. generateVcMetaData

### Class Name
`VcManager`

### Function Name
`generateVcMetaData`

### Function Description
`Extracts VerifiableCredential information (Meta Data) to be stored on the blockchain.`

### Input Parameters

| Parameter               | Type                   | Description         | **M/O** | **Notes**                          |
|-------------------------|------------------------|---------------------|---------|--------------------------------------|
| verifiableCredential    | VerifiableCredential   | Issued VC object    | M       | Refer to the data model VerifiableCredential |
| certVcRefStr    | String   | Provider subscription certificate VC URL    | O       | Refer to the data model Provider |



### Output Parameters
| Type    | Description           | **M/O** | **Notes**             |
|---------|-----------------------|---------|-------------------------|
| VcMeta  | VC meta-information object | M       | Refer to the data model VcMeta |


### Function Declaration

```java
VcMeta generateVcMetaData(VerifiableCredential verifiableCredential, String certVcRefStr);
```

### Function Usage
```java
VcManager vcManager = new VcManager();
String certVcRefStr = "Provider subscription certificate VC URL";
VcMeta vcMeta = vcManager.generateVcMetaData(verifiableCredential, certVcRefStr);
```

<br>

## 1.2.3. getOriginDataForSign

### Class Name
`VcManager`

### Function Name
`getOriginDataForSign`

### Function Description
`Extracts the full signature source text and signature source text per claim for a Verifiable Credential.`

### Input Parameters

| Parameter               | Type                  | Description                              | **M/O** | **Notes**                      |
|-------------------------|-----------------------|------------------------------------------|---------|--------------------------------|
| signKeyId                   | String                | Issuer key ID used for signing           | M       |                                |
| issuerDidDoc                  | DidDocument           | Issuer's DID Document                    | M       | Refer to the DidDocument data model |
| verifiableCredential    | VerifiableCredential | The VC to be signed                      | M       | Refer to the VerifiableCredential data model |


### Output Parameters
| Type                       | Description                | **M/O** | **Notes**                         |
|----------------------------|----------------------------|---------|-----------------------------------|
| List&lt;SignatureVcParams&gt; | Signature-related data object | M       | [Link](#26-signaturevcparams) |


### Function Declaration

```java
List<SignatureVcParams> getOriginDataForSign(String signKeyId, DidDocument issuerDidDoc, 
                                              VerifiableCredential verifiableCredential);
```

### Function Usage
```java
VcManager vcManager = new VcManager();

String signKeyId;
DidDocument issuerDidDoc;
VerifiableCredential verifiableCredential;

List<SignatureVcParams> signatureParamslist= new ArrayList<SignatureVcParams>();
SignatureParamslist = vcManager.getOriginDataForSign(signKeyId, issuerDidDoc, verifiableCredential);


for(SignatureVcParams signatureParams : signatureParamslist) {
    System.out.println("originData : " + signatureParams.getOriginData());
}
```

<br>

## 1.2.4. addProof

### Class Name
`VcManager`

### Function Name
`addProof`

### Function Description
`Adds the Issuer's signature to the Verifiable Credential.`

### Input Parameters
| Parameter         | Type                      | Description                              | **M/O** | **Notes** |
|-------------------|---------------------------|------------------------------------------|---------|-----------|
| vc                | VerifiableCredential      | Verifiable Credential to which the proof will be added | M       | Refer to the data model VerifiableCredential |
| sigVcParamsList   | List&lt;SignatureVcParams&gt; | Data object with the signature value set | M       | [Link](#26-signaturevcparams) |

* Refer to the data model Proof


### Output Parameters
| Type                | Description              | **M/O** | **Notes** |
|---------------------|--------------------------|---------|-----------|
| VerifiableCredential | VC with signature included | M       | Refer to the data model VerifiableCredential |


### Function Declaration

```java
VerifiableCredential addProof(VerifiableCredential vc, List<SignatureVcParams> sigVcParamsList) -> throws CoreException 
```

### Function Usage
```java
VcManager vcManager = new VcManager();

VerifiableCredential vc; // VC to which the signature will be added

// Set the signature value in the SignatureParams object on the server
List<SignatureParams> signatureParams;

VerifiableCredential finalVc = vcManager.addProof(vc, signatureParams);
```

<br>

## 1.2.5. verifyCredential

### Class Name
`VcManager`

### Function Name
`verifyCredential`

### Function Description
`Verify Issuer's signature to the Verifiable Credential.`

### Input Parameters
| Parameter         | Type                      | Description                              | **M/O** | **Notes** |
|-------------------|---------------------------|------------------------------------------|---------|-----------|
| vc                | VerifiableCredential      | Verifiable Credential to verify | M       | Refer to the data model VerifiableCredential |
| issuerDidDoc                  | DidDocument           | Issuer's DID Document                    | M       | Refer to the DidDocument data model |
| isCheckVcExpirationDate  | boolean             | Option to check VC expiration date       | O       |                                    |



### Output Parameters
n/a


### Function Declaration

```java
void verifyCredential(VerifiableCredential verifiableCredential, DidDocument issuerDidDocument, boolean isCheckVcExpirationDate) -> throws CoreException 
```

### Function Usage
```java
VcManager vcManager = new VcManager();

// VC to verify
VerifiableCredential vc;

// Diddocument of issuer that issued VC
DidDocument issuerDidDocument;

// Whether to check VC expiration date
boolean isCheckVcExpirationDate;

vcManager.verifyCredential(vc, issuerDidDocument, isCheckVcExpirationDate);
```

<br>

## 1.3. Verifiable Presentation

### 1.3.1. verify

#### Class Name
`VpManager`

#### Function Name
`verify`

#### Function Description
`Verifies the signatures of the Holder and issuers for each Claim in the Verifiable Presentation,`
`and checks if the VC meets the filter conditions.`

#### Input Parameters
| Parameter | Type                       | Description                           | **M/O** | **Notes**                          |
|-----------|----------------------------|---------------------------------------|---------|------------------------------------|
| verifiablePresentation | VerifiablePresentation | The VP to be verified                | M       | Refer to the data model VerifiablePresentation |
| verifyParam            | VpVerifyParam           | Data object related to VP verification | M       | [Link](#24-vpverifyparam)          |
* Refer to the data model for the Filter object within VpVerifyParam

#### Output Parameters
n/a


#### Function Declaration

```java
void verify(VerifiablePresentation verifiablePresentation, VpVerifyParam verifyParam) -> throws CoreException
```

### Function Usage
```java
String holderDidsJson;
String issuerDidsJson;
String vp;

holderDidManager = new DidManager();
holderDidManager.parse(holderDidsJson);

issuerDidManager = new DidManager();
issuerDidManager.parse(issuerDidsJson);

VerifiablePresentation verifiablePresentation = new VerifiablePresentation();
verifiablePresentation.fromJson(vp);


VpVerifyParam verifyParam = new VpVerifyParam(holderDidManager.getDidDocument(), issuerDidManager.getDidDocument());

Filter filter = new Filter();
// Set Filter information
verifyParam.setFilter(filter);

vpManager.verify(verifiablePresentation, verifyParam);
```

<br>

## 1.3.2. getClaims

### Class Name
`VpManager`

### Function Name
`getClaims`

### Function Description
`Extracts claims stored inside a verified Verifiable Presentation.`

### Input Parameters
n/a


### Output Parameters
| Type                  | Description                          |**M/O** | **Remarks**                   |
|-----------------------|--------------------------------------|---------|-------------------------------|
| List&lt;Claim&gt;    | Extracts claim information from VP   | M       | See data model Claim          |


### Function Declaration

```java
List<Claim> getClaims();
```

### Function Usage
```java
// After using the verify() function, use the Verifiable Presentation set in VpManager
List<Claim> claimList = new ArrayList<Claim>(); 
claimList = vpManager.getClaims();

for(Claim claim : claimList) {
    System.out.print("code : "+claim.getCode() + ", ");
    System.out.println("value : "+ claim.getValue());
}
```

<br>

## 2. Reference Classes

### 2.1. DidKeyInfo
| Parameter     | Type                | Description                          | **M/O** | **Note**                       |
|---------------|---------------------|--------------------------------------|---------|--------------------------------|
| controller    | String              | The entity that can change the key's state | M       |                                |
| keyId         | String              | Key ID stored in the Wallet           | M       |                                |
| publicKey     | String              | Base58-encoded public key             | M       |                                |
| algoType      | String              | Key algorithm                         | M       |                                |
| authType      | AuthType            | Key authentication type               | M       |                                |
| keyPurpose    | List&lt;ProofPurpose&gt; | Purpose of the key usage              | O       | Refer to the data model ProofPurpose |


### 2.2. IssueVcParam
| Parameter             | Type                | Description                            | **M/O** | **Note**                          |
|-----------------------|---------------------|----------------------------------------|---------|-----------------------------------|
| schema                | VcSchema            | VC schema information retrieved from the blockchain | M       | Refer to the data model VcSchema |
| providerDetail        | ProviderDetail      | Issuer information retrieved from the blockchain | M       | Refer to the data model ProviderDetail |
| issuanceDate          | ZonedDateTime       | Issuance date                          | O       | java.time.ZonedDateTime            |
| privacy        | Map&lt;String,ClaimInfo&gt; | User's personal information           | M       | [Link](#23-claiminfo)              |
| context               | List&lt;String&gt; | VC Context                             | O       |                                   |
| validFrom             | ZonedDateTime       | VC validity period (start date)        | O       | java.time.ZonedDateTime            |
| validUntil            | ZonedDateTime       | VC validity period (end date)          | O       | java.time.ZonedDateTime            |
| vcType                | List&lt;VcType&gt; | VC Type                                | M       | Refer to the data model VcType     |
| evidences             | List&lt;Evidence&gt; | Methods to verify certificate authenticity | M       | Refer to the data model Evidence   |
| schemaType             | String | Schema Type | O       | |


### 2.3. ClaimInfo
| Parameter    | Type                | Description                                | **M/O** | **Note**                              |
|--------------|---------------------|--------------------------------------------|---------|---------------------------------------|
| code         | String              | Combination of "namespaceID"+"."+"claimID" | M       | Utilizes VcSchema blockchain query info |
| value        | byte[]              | User's personal information                | M       |                                       |
| encodeType   | String              | Encoding type                              | O       |                                       |
| digestSRI    | String              | Hash value for the claim                   | O       |                                       |
| i18n         | Map&lt;String,I18N&gt; | Multilingual options                       | O       | Refer to the data model I18N           |


### 2.4. VpVerifyParam
| Parameter              | Type                | Description                              | **M/O** | **Note**                           |
|------------------------|---------------------|------------------------------------------|---------|------------------------------------|
| checkVcExpirationDate  | boolean             | Option to check VC expiration date       | O       |                                    |
| filter                 | Filter              | VC verification conditions               | O       | Refer to the data model Filter     |
| holderDidDocument      | DidDocument         | Holder DID Document                      | M       | Refer to the data model DidDocument |
| issuerDidDocument      | DidDocument         | Issuer DID Document                      | M       | Refer to the data model DidDocument |


### 2.5. SignatureParams
| Parameter       | Type                | Description                        | **M/O** | **Note**                                     |
|-----------------|---------------------|------------------------------------|---------|----------------------------------------------|
| keyId           | String              | Signature key ID                   | M       |                                              |
| keyPurpose      | String              | Key purpose                        | M       | rawValue of variable declared in ProofPurpose class |
| hashedData      | String              | Hashed signature plaintext         | O       |                                              |
| publicKey       | String              | Encoded public key                 | O       |                                              |
| originData      | String              | Signature plaintext                | M       |                                              |
| algorithm       | String              | Key algorithm                       | M       |                                              |
| signatureValue  | String              | Signature value                    | O       |                                              |

### 2.6. SignatureVcParams
| Parameter       | Type                | Description                        | **M/O** | **Note**                                     |
|-----------------|---------------------|------------------------------------|---------|----------------------------------------------|
| keyId           | String              | Signature key ID                   | M       |                                              |
| keyPurpose      | String              | Key purpose                        | M       | rawValue of variable declared in ProofPurpose class |
| hashedData      | String              | Hashed signature plaintext         | O       |                                              |
| publicKey       | String              | Encoded public key                 | O       |                                              |
| isSingleClaim       | Boolean              | A boolean value used to distinguish between overall claim and individual claims. | O       |      |
| claimCode       | String              | Claim code for individual claim verification | O       | Used in VerifiableCredential signature      |
| originData      | String              | Signature plaintext                | M       |                                              |
| algorithm       | String              | Key algorithm                       | M       |                                              |
| signatureValue  | String              | Signature value                    | O       |                                              |

# 3. Sample
### 3.1 DID Document Json
```json
//issuer DID Document
{
    "@context": [
        "https://www.w3.org/ns/did/v1"
    ],
    "assertionMethod": [
        "assert"
    ],
    "authentication": [
        "auth"
    ],
    "controller": "did:omn:tas",
    "created": "2024-07-11T08:35:35Z",
    "deactivated": false,
    "id": "did:omn:issuer",
    "keyAgreement": [
        "keyagree"
    ],
    "proofs": [
        {
            "created": "2024-07-11T08:35:36Z",
            "proofPurpose": "assertionMethod",
            "proofValue": "z3rqyRHRMoANLo6zKGkmHiZ8QYpNwfqddaYNywREWrQCvADoU2inZyJC3bJN9iBTATG7dGUoTj8FeZCWwqdkx1oU7x",
            "type": "Secp256r1Signature2018",
            "verificationMethod": "did:omn:issuer?versionId=1#assert"
        },
        {
            "created": "2024-07-11T08:35:36Z",
            "proofPurpose": "authentication",
            "proofValue": "z3sRmBRiMyXejZaaL6ba5tNUJuJdKc3ew3cr8a9839SYfWRGXzCLrwPiNHNJmBjRQ4sWhutcn7Xq17YhW7xzJJgZmf",
            "type": "Secp256r1Signature2018",
            "verificationMethod": "did:omn:issuer?versionId=1#auth"
        }
    ],
    "service": [
        {
            "id": "hompage",
            "serviceEndpoint": [
                "https://did.omnione.net"
            ],
            "type": "LinkedDomains"
        }
    ],
    "updated": "2024-07-11T08:35:36Z",
    "verificationMethod": [
        {
            "authType": 1,
            "controller": "did:omn:issuer",
            "id": "assert",
            "publicKeyMultibase": "zvXsXFNahfw9Cz4KQEdLjBtoUEUiVHoMxWs23j6axNuTP",
            "type": "Secp256r1VerificationKey2018"
        },
        {
            "authType": 1,
            "controller": "did:omn:issuer",
            "id": "auth",
            "publicKeyMultibase": "z21Fy2h5uqmhw8xSVxBXNtBbVVjPTuKMam8ebz2FR7CD62",
            "type": "Secp256r1VerificationKey2018"
        },
        {
            "authType": 1,
            "controller": "did:omn:issuer",
            "id": "keyagree",
            "publicKeyMultibase": "znByKCSPznGAKc48CF7i7BhWuhEnz2U7sU4m5TxTrJVEf",
            "type": "Secp256r1VerificationKey2018"
        }
    ],
    "versionId": "1"
}
```

## 3.2 Verifiable Credential JSON
```json
{
    "@context": [
        "https://www.w3.org/ns/credentials/v2"
    ],
    "credentialSchema": {
        "id": "https://raonsecure.com/schema/issuer_id_v1.json",
        "type": "OsdSchemaCredential"
    },
    "credentialSubject": {
        "claims": [
            {
                "caption": "Name",
                "code": "org.opendid.v1.name",
                "format": "plain",
                "location": "inline",
                "type": "text",
                "value": "Issuer Server 1"
            },
            {
                "caption": "Address",
                "code": "org.opendid.v1.url",
                "format": "html",
                "type": "text",
                "value": "z2rJefXWWUHwciS9PSveUh"
            },
            {
                "caption": "Role",
                "code": "org.opendid.v1.role",
                "format": "plain",
                "location": "inline",
                "type": "text",
                "value": "issuer"
            }
        ],
        "id": "did:omn:issuer1"
    },
    "encoding": "UTF-8",
    "evidence": [
        {
            "documentPresence": "Physical",
            "evidenceDocument": "BusinessLicense",
            "id": "https://raonsecure.com/evidence/directory",
            "subjectPresence": "Physical",
            "type": "DocumentVerification",
            "verifier": "did:omn:tas"
        }
    ],
    "formatVersion": "1.0",
    "id": "7a656c59-d21f-4771-a0f9-6e12c275a906",
    "issuanceDate": "2024-07-11T04:51:04Z",
    "issuer": {
        "id": "did:omn:tas",
        "name": "TAS Name"
    },
    "language": "ko",
    "proof": {
        "created": "2024-07-11T04:51:04Z",
        "proofPurpose": "assertionMethod",
        "proofValue": "z3rqyRHRMoANLo6zKGkmHiZ8QYpNwfqddaYNywREWrQCvADoU2inZyJC3bJN9iBTATG7dGUoTj8FeZCWwqdkx1oU7x",
        "proofValueList": [
            "z3sRmBRiMyXejZaaL6ba5tNUJuJdKc3ew3cr8a9839SYfWRGXzCLrwPiNHNJmBjRQ4sWhutcn7Xq17YhW7xzJJgZmf",
            "z3q2ttoxpNzYA2Mp3tzZxW4ZXoQCPmyE3eZEfPHWmuDWPqBY6aZLjyVxMCfXDFu9L5PWkyoBTcvqUfHcTUH5LFmqVk",
            "z3jtxzLfa8esfQnjKvWCMk8y3QtJncf1Py7M2BDFcMVfudqnPTAY6XfqrjqyKvSYXeqPc2RXYVHCCD6pKcjZ53gBcS"
        ],
        "type": "Secp256r1Signature2018",
        "verificationMethod": "did:omn:tas?version=1.0#assert"
    },
    "type": [
        "CertificateVC"
    ],
    "validFrom": "2024-07-11T04:51:04Z",
    "validUntil": "9999-12-31T23:59:59Z"
}
```