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

- 주제: Core SDK Server API
- 작성: 백종윤
- 일자: 2024-08-30
- 버전: v1.0.0

| 버전   | 일자       | 변경 내용                 |
| ------ | ---------- | -------------------------|
| v1.0.0 | 2024-08-30 | 초기 작성                 |


<div style="page-break-after: always;"></div>

# 목차
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

## 1.1.1. createDocument

### Class Name
`DidManager`

### Function Name
`createDocument`

### Function Introduction
`DID document 초기 생성`

### Input Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| id    | String    | 소유자의 DID |M| |
| controller    | String    |  controller의 DID |M| |
| didKeyInfos    | List&lt;DidKeyInfo&gt; | DID문서에 추가할 키 정보 |M| 데이터모델 DidKeyInfo 참고|

* controller : DID Document의 상태를 변경할 수 있는 주체

### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DidDocument  | DidDocument 객체 |M| |

### Function Declaration

```cpp
DidDocument createDocument(String did, String controller, List<DidKeyInfo> didKeyInfos) -> throws DidManagerException
```

### Function Usage
```cpp
//Proivder DID Document 생성 예시
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

## 1.1.2. load

### Class Name
`DidManager`

### Function Name
`load`

### Function Introduction
`DID Document 파일을 로드해서 DidManager에 세팅한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didDocPath    | String    | 파일명이 포함된 DID Document 파일 경로 |M||

### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DidDocument  | DidDocument 객체 |M| |

### Function Declaration

```cpp
DidDocument load(String didDocPath) -> throws DidManagerException
```

### Function Usage
```cpp
String didDocPath = "src/test/test.did"; // 파일명을 포함한 DID경로
DidManager didManager = new DidManager();
didManager.load(didDocPath);
```

<br>

## 1.1.3. parse

### Class Name
`DidManager`

### Function Name
`parse`

### Function Introduction
`DID Document 문자열을 객체로 파싱하여 DidManager에 세팅한다.`

### Input Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didDocJson    | String    | DID Document 문자열 |M||


### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| DidDocument  | DidDocument 객체 |M| |

### Function Declaration

```cpp
DidDocument parse(String didJson);
```

### Function Usage
```cpp
DidManager didManager = new DidManager();
String didDocumentJson; // didDocument 문자열
didManager.parse(didDocumentJson);
```

<br>

## 1.1.4. addVerifiCationMethod

### Class Name
`DidManager`

### Function Name
`addVerifiCationMethod`

### Function Introduction
`DID Document 의 verificationMethod에 키를 추가한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didKeyInfo    | DidKeyInfo    | verificationMethod에 추가할 키 정보 |M| 데이터모델 DidKeyInfo 참고|

* didKeyInfo 객체 내부 keyPurpose 변수 세팅시, 내부에서 addKeyPurpose() 메소드가 함께 실행된다.

### Output Parameters
void

### Function Declaration

```cpp
void addVerifiCationMethod(DidKeyInfo didKeyInfo) -> throws DidManagerException;
```

### Function Usage
```cpp
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

### Function Introduction
`DID Document의 verificationMethod에 추가된 키의 용도를 정의한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didKeyId    | String    | 용도를 추가할 키 ID |M||
| keyPurposes    | List&lt;ProofPurpose&gt; | 키 용도 Enum 리스트 |M| 데이터델 ProofPurpose 참고|


### Output Parameters
void


### Function Declaration

```cpp
void addKeyPurpose(String didKeyId, List<ProofPurpose> keyPurposes) -> throws DidManagerException;
```

### Function Usage
```cpp
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

### Function Introduction
`DID Document의 verificationMethod에 저장된 공개키를 삭제한다.`
`삭제하려는 키의 용도가 정의된 경우, removeKeyPurpose()메소드가 함께 실행된다`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyId    | String | 키 아이디 |M| |


### Output Parameters
void


### Function Declaration

```cpp
void removeVerificationMethod(String keyId) -> throws DidManagerException;
```

### Function Usage
```cpp
didManager.removeVerificationMethod("assert");
```

<br>

## 1.1.7. removeKeyPurpose

### Class Name
`DidManager`

### Function Name
`removeKeyPurpose`

### Function Introduction
`키 용도 List를 조회하여 입력된 Key ID를 삭제한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyId    | String | 용도를 삭제할 키 아이디 |M| |


### Output Parameters
void


### Function Declaration

```cpp
void removeKeyPurpose(String keyId) -> throws DidManagerException;
```

### Function Usage
```cpp
didManager.removeKeyPurpose("assert");
```

<br>

## 1.1.8. getVerificationMethodByKeyId

### Class Name
`DidManager`

### Function Name
`getVerificationMethodByKeyId`

### Function Introduction
`키 ID로 DID Document에 저장된 키 정보를 조회한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyId    | String    | 키 아이디 |M||


### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| PublicKey  | PublicKey 객체 |M| 데이터모델 PublicKey 참고|


### Function Declaration

```cpp
PublicKey getVerificationMethodByKeyId(String didKeyId);
```

### Function Usage
```cpp
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

### Function Introduction
`DID Document에 저장된 keyagreement 용도의 키(서명 불가)를 제외한 모든 키 ID를 조회한다.`

### Input Parameters
n/a


### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| List&lt;String&gt;  | 서명가능한 모든 키 ID |M| |
* 서명 가능한 키 : keyagreement 용도를 제외한 키

### Function Declaration

```cpp
List<String> getAllSignKeyIdList() -> throws DidManagerException
```

### Function Usage
```cpp
List<String> keyIds = new ArrayList<String>();
keyIds = didManager.getAllSignKeyIdList();
```

<br>

## 1.1.10. getOriginDataForSign

### Class Name
`DidManager`

### Function Name
`getOriginDataForSign`

### Function Introduction
`DID Document 서명을 위한 서명원문을 추출한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyIds    | List&lt;String&gt;    | 서명 키 아이디 List |M||


### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| List&lt;SignatureParams&gt;  | 서명을 위한 데이터 객체 |M| [Link](#25-signatureparams)|


### Function Declaration

```cpp
List<SignatureParams> getOriginDataForSign(List<String> keyIds) -> throws DidManagerException
```

### Function Usage
```cpp
// 1. 서명 요청할 keyID 리스트 생성
List<String> keyIds = new ArrayList<String>();
keyIds.add("assert");
keyIds.add("auth");

// 2. 서명 요청할 객체 생성
List<SignatureParams> signatureParams = didManager.getOriginDataForSign(keyIds);

for(SignatureParams signatureParam : signatureParams) {
    System.out.println("originData: "+signatureParam.getOriginData());
}
```

<br>

## 1.1.11. verifyDocumentSignature

### Class Name
`DidManager`

### Function Name
`verifyDocumentSignature`

### Function Introduction
`DID Document의 서명값을 검증 한다.`

### Input Parameters
n/a


### Output Parameters

| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| List&lt;SignatureParams&gt;  | 서명 검증을 위한 데이터 객체 |M| [Link](#25-signatureparams)|


### Function Declaration

```cpp
List<SignatureParams> verifyDocumentSignature() -> throws DidManagerException
```

### Function Usage
```cpp
//DidManager에 서명이 포함된 DID Document 가 세팅되어있을 때 사용
List<SignatureParams> signatureParams = didManager.verifyDocumentSignature();

for(SignatureParams signatureParam : signatureParams) {
    System.out.println("signature: "+signatureParam.getSignatureValue());
}
```

<br>

## 1.1.12. addServiceEndPoint

### Class Name
`DidManager`

### Function Name
`addServiceEndPoint`

### Function Introduction
`Provider의 DID Document에 신규 서비스 정보를 저장하거나 서비스 URL을 추가한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didServiceId    | String    | 서비스 아이디 |M||
| didServiceType    | DidServiceType    | 서비스 타입 Enum |O|데이터모델 DidServiceType 참고|
| url    | String    | 서비스 URL |M||


### Output Parameters
void


### Function Declaration

```cpp
void addServiceEndPoint(String didServiceId, DidServiceType didServiceType, String url) -> throws DidManagerException
```

### Function Usage
```cpp
//서비스를 변경할 DID Document를 DidManager에 세팅 한 후 사용
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

### Function Introduction
`DID Document에 저장된 서비스를 삭제하거나 서비스의 url을 제거 한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| didServiceId    | String    | 서비스 아이디 |O||
| didServiceType    | DidServiceType    | 서비스 타입 Enum |O|데이터모델 DidServiceType 참고|
| url    | String    | 서비스 URL |O||


### Output Parameters
void


### Function Declaration

```cpp
void deleteServiceEndPoint(String id, DidServiceType type, String url) -> throws DidManagerException
```

### Function Usage
```cpp
//서비스를 변경할 DID Document를 DidManager에 세팅 한 후 사용
String serviceId = "serviceID-1";
String serviceUrl = "https://did.omnione.net";
didManager.deleteServiceEndPoint(serviceId, DidServiceType.LINKED_DOMAINS, serviceUrl);
```

<br>

## 1.1.14. addProof

### Class Name
`DidManager`

### Function Name
`addProof`

### Function Introduction
`TAS/Provider의 DID Document에 서명값을 추가한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| signatureParams    | List&lt;SignatureParams&gt;    | 서명 관련 데이터 객체 |M|[Link](#25-signatureparams)|


### Output Parameters
void


### Function Declaration

```cpp
void addProof(List<SignatureParams> signatureParams) -> throws DidManagerException
```

### Function Usage
```cpp
//서명을 추가할 DID Document를 DidManager에 세팅한 후 사용

// getOriginDataForSign()서명값이 추가되지 않은 객체
List<SignatureParams> signatureParams = didManager.getOriginDataForSign(keyIds);

//키 ID 별 서명값을 SignatureParams 객체에 서버에서 세팅
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

### Function Introduction
`VerifiableCredential을 발급한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| vcParam    | IssueVcParam    | VC 발급을 위한 데이터 객체 |M|[Link](#22-issuevcparam)|
| did    | String    | 소유자의 DID |M||
* IssueVcParam 내부에 세팅할 ClaimInfo 객체에 대한 정보 참고 [Link](#23-claiminfo)

### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| VerifiableCredential  | VC 객체 |M| 데이터모델 VerifiableCredential 참고|


### Function Declaration

```cpp
VerifiableCredential issueCredential(IssueVcParam vcParam, String did) -> throws DidManagerException;
```

### Function Usage
```cpp
//Provider VC로 샘플 작성
//1. issueVcParam 정보 세팅
IssueVcParam issueVcParam = new IssueVcParam();

//1-1. 블록체인에서 조회한 VcSchema 정보 세팅
VcSchema schema = new VcSchema();
issueVcParam.setSchema(schema);

//1-2. 블록체인에서 조회한 Provider(VC Issuer) 정보 세팅
ProviderDetail providerDetail = new ProviderDetail();
issueVcParam.setProviderDetail(providerDetail);

//1-3. 블록체인에서 조회한 Issuer DIDDocument 정보 세팅
DidDocument issuerDidDoc= new DidDocument();
issueVcParam.setDidDocument(issuerDidDoc);

//1-4. VcSchema에 저장된 클레임에 대한 사용자의 개인정보 세팅
HashMap<String,ClaimInfo> issuerAddClaims = new HashMap<>();  
ClaimInfo claimInfo = new ClaimInfo();
claimInfo.setCode("testId.userName");
claimInfo.setValue("Raon".getBytes());
ClaimInfo claimInfo2 = new ClaimInfo();
claimInfo2.setCode("testId.address");
claimInfo2.setValue("seoul".getBytes());
issuerAddClaims.put(claimInfo.getCode(), claimInfo);
issuerAddClaims.put(claimInfo2.getCode(), claimInfo2);
issueVcParam.setIssuerAddClaims(issuerAddClaims);

//1-5. VC Type 세팅
List<VcType> vctypes = new ArrayList<>();
vctypes.add(VcType.CERTIFICATE_VC);
issueVcParam.setVcType(vctypes);

//1-6. Evidence 정보 세팅
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

//2. Holder DID 세팅
String did = "did:omn:raon";

//3. issueVc() 호출
VerifiableCredential vc = new VerifiableCredential();
vc = vcManager.issueCredential(issueVcParam, did);
```

<br>

## 1.2.2. generateVcMetaData

### Class Name
`VcManager`

### Function Name
`generateVcMetaData`

### Function Introduction
`블록체인에 저장할 VerifiableCredential 정보(Meta Data)를 추출한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| verifiableCredential    | VerifiableCredential    | 발급된 VC 객체 |M|데이터모델 VerifiableCredential 참고|


### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| VcMeta  | VC 메타정보 객체 |M|데이터모델 VcMeta 참고 |


### Function Declaration

```cpp
VcMeta generateVcMetaData(VerifiableCredential verifiableCredential);
```

### Function Usage
```cpp
VcManager vcManager = new VcManager();
VcMeta vcMeta = vcManager.generateVcMetaData(verifiableCredential);
```

<br>

## 1.2.3. getOriginDataForSign

### Class Name
`VcManager`

### Function Name
`getOriginDataForSign`

### Function Introduction
`VerifiableCredential의 전체 서명원문 및 클레임별 서명원문을 추출한다.`

### Input Parameters

| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyId    | String    | 서명에 사용할 Issuer 키 ID |M||
| didDoc    | DidDocument    | Issuer의 DID Document |M| 데이터모델 DidDocument 참고 |
| verifiableCredential    | VerifiableCredential    | 서명할 VC |M| 데이터모델 VerifiableCredential 참고|


### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| List&lt;SignatureParams&gt;  | 서명 관련 데이터 객체 |M| [Link](#25-signatureparams)|


### Function Declaration

```cpp
List<SignatureParams> getOriginDataForSign(String keyId, DidDocument didDoc, 
                                              VerifiableCredential verifiableCredential);
```

### Function Usage
```cpp
VcManager vcManager = new VcManager();

String keyId;
DidDocument didDoc;
VerifiableCredential verifiableCredential;

List<SignatureParams> signatureParamslist= new ArrayList<SignatureParams>();
SignatureParamslist = vcManager.getOriginDataForSign(keyId, didDoc, verifiableCredential);

for(SignatureParams signatureParams : signatureParamslist) {
    System.out.println("originData : " + signatureParams.getOriginData());
}
```

<br>

## 1.2.4. addProof

### Class Name
`VcManager`

### Function Name
`addProof`

### Function Introduction
`Verifiable Credential에 Issuer의 서명을 추가한다.`

### Input Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| vc    | VerifiableCredential    | Proof를 추가할 VerifiableCredential  |M|데이터모델 VerifiableCredential 참고|
| signatureParams    | List&lt;SignatureParams&gt;    | 서명값이 세팅된 데이터 객체 |M|[Link](#25-signatureparams)|

* 데이터 모델 Proof 참고


### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| VerifiableCredential  | 서명이 포함된 VC |M| 데이터모델 VerifiableCredential 참고 |


### Function Declaration

```cpp
VerifiableCredential addProof(VerifiableCredential vc, List<SignatureParams> signatureParams)
```

### Function Usage
```cpp
VcManager vcManager = new VcManager();

VerifiableCredential vc; // 서명을 추가할 VC

//서버에서 SignatureParams 객체에 서명값 세팅
List<SignatureParams> signatureParams;

VerifiableCredential fianlVc = vcManager.addProof(vc, signatureParams);
```

<br>

## 1.2.5. verifyCredential

### Class Name
`VcManager`

### Function Name
`verifyCredential`

### Function Description
`Verifiable Credential의 Issuer 서명값을 검증한다.`

### Input Parameters
| Parameter         | Type                      | Description                              | **M/O** | **비고** |
|-------------------|---------------------------|------------------------------------------|---------|-----------|
| vc                | VerifiableCredential      | 검증할 Verifiable Credential| M       | Refer to the data model VerifiableCredential |
| issuerDidDoc                  | DidDocument           | Issuer's DID Document                    | M       | Refer to the DidDocument data model |
| isCheckVcExpirationDate  | boolean             |   Verifiable Credential 만료일 체크 옵션     | O       |                                    |



### Output Parameters
n/a


### Function Declaration

```java
void verifyCredential(VerifiableCredential verifiableCredential, DidDocument issuerDidDocument, boolean isCheckVcExpirationDate) -> throws CoreException 
```

### Function Usage
```java
VcManager vcManager = new VcManager();

// 검증 할 VC
VerifiableCredential vc;

// VC를 발급한 Issuer의 Did 문서
DidDocument issuerDidDocument;

// VC 만료일 체크 옵션
boolean isCheckVcExpirationDate;

vcManager.verifyCredential(vc, issuerDidDocument, isCheckVcExpirationDate);
```
<br>

## 1.3. VerifiablePresentation

## 1.3.1. verify

### Class Name
`VpManager`

### Function Name
`verify`

### Function Introduction
`VerifiablePresentation의 Holder 서명과 Claim별 issuer 서명을 검증하고,`
`Filter 조건에 맞는 VC가 포함되었는지 확인한다.`

### Input Parameters
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| verifiablePresentation    | VerifiablePresentation    | 검증할 VP  |M|데이터모델 VerifiablePresentation 참고|
| verifyParam    | VpVerifyParam    | VP 검증관련 데이터 객체 |M|[Link](#24-vpverifyparam)|
* VpVerifyParam 내부 Filter 객체는 데이터모델 참고

### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| boolean  | 검증 결과 |M| |


### Function Declaration

```cpp
boolean verify(VerifiablePresentation verifiablePresentation, VpVerifyParam verifyParam) -> throws DidManagerException, CryptoException;
```

### Function Usage
```cpp
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
//Filter 정보 세팅
verifyParam.setFilter(filter);

vpManager.verify(verifiablePresentation, verifyParam);
```

<br>

## 1.3.2. getClaims

### Class Name
`VpManager`

### Function Name
`getClaims`

### Function Introduction
`검증이 완료된 VerifiablePresentation 내부에 저장된 클레임을 추출한다.`

### Input Parameters
n/a


### Output Parameters
| Type | Description                |**M/O** | **비고** |
|------|----------------------------|---------|---------|
| List&lt;Claim&gt;  | VP내부 클레임 정보 추출 |M|데이터모델 Claim 참고 |


### Function Declaration

```cpp
List<Claim> getClaims();
```

### Function Usage
```cpp
//verify() 함수 사용 후, VpManager에 세팅된 Verifiable Presentation 사용
List<Claim> claimList = new ArrayList<Claim>(); 
claimList = vpManager.getClaims();

for(Claim claim : claimList) {
    System.out.print("code : "+claim.getCode() + ", ");
    System.out.println("value : "+ claim.getValue());
}
```

<br>

## 2. 참조 클래스

## 2.1. DidKeyInfo
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| controller    | String    | 키의 상태를 변경할수있는 주체  |M||
| keyId    | String    | Wallet에 저장된 keyId |M||
| publicKey    | String    | Base58인코딩된 공개키 |M||
| algoType    | String    | 키 알고리즘 |M||
| authType    | AuthType    | 키 인증수단 |M||
| keyPurpose    | List&lt;ProofPurpose&gt; | 키 사용 용도 |O|데이터모델 ProofPurpose 참고|


## 2.2. IssueVcParam
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| schema    | VcSchema    | 블록체인에서 조회한 VC 스키마 정보 |M| 데이터모델 VcSchema 참고|
| providerDetail    | ProviderDetail    | 블록체인에서 조회한 Issuer 정보 |M| 데이터모델 ProviderDetail 참고|
| didDocument    | DidDocument    | Issuer의 DID Document |M|데이터모델 DidDocument 참고|
| issuanceDate    | ZonedDateTime    | 발급일자 |O|java.time.ZonedDateTime|
| issuerAddClaims    | Map&lt;String,ClaimInfo&gt; | 사용자의 개인정보 |M|[Link](#23-claiminfo)|
| context    | List&lt;String&gt;    | VC Context |O||
| validFrom    | ZonedDateTime    | VC 유효기간(시작일) |O|java.time.ZonedDateTime|
| validUntil    | ZonedDateTime    | VC 유효기간(종료일) |O|java.time.ZonedDateTime|
| vcType    | List&lt;VcType&gt;    | VC Type |M|데이터모델 VcType 참고|
| evidences    | List&lt;Evidence&gt;    | 증명서 진위여부 확인 방법 |M|데이터모델 Evidence 참고|


## 2.3. ClaimInfo
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| code    | String    | Claim의 "namespaceID"+"."+"claimID" 조합 문자열|M|VcSchema 블록체인 조회 정보 활용|
| value    | byte[]    | 사용자 개인정보 |M||
| encodeType    | String    | encoding type |O||
| digestSRI    | String | claim에 대한 hash 값 |O||
| i18n    | Map&lt;String,I18N&gt; | 다국어 옵션 |O|데이터모델 I18N 참고|


## 2.4. VpVerifyParam
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| checkVcExpirationDate    | boolean    | VC 만료일자 체크 옵션 |O||
| filter    | Filter    | VC 검증 조건 설정 |O|데이터모델 Filter 참고|
| holderDidDocument    | DidDocument    | Holder DID Document |M|데이터모델 DidDocument 참고|
| issuerDidDocument    | DidDocument | Issuer DID Document |M|데이터모델 DidDocument 참고|


## 2.5. SignatureParams
| Parameter | Type   | Description                | **M/O** | **비고** |
|-----------|--------|----------------------------|---------|---------|
| keyId    | String    | 서명 키 ID |M||
| keyPurpose    | String    | 키 용도 |M|ProofPurpose 클래스에 선언된 변수의 rawValue|
| hashedData    | String    | Hashed 서명 원문 |O||
| publicKey    | String | 인코딩된 공개키 |O||
| claimCode    | String    | 개별 클레임 확인을 위한 클레임 코드 |O|VerifiableCredential 서명에 사용|
| originData    | String    | 서명 원문 |M||
| algorithm    | String    | 키 알고리즘 |M||
| signatureValue    | String | 서명값 |O||

### 2.6. SignatureVcParams
| Parameter       | Type                | Description                        | **M/O** | **비고**                                     |
|-----------------|---------------------|------------------------------------|---------|----------------------------------------------|
| keyId           | String              | 서명 키 ID                   | M       |                                              |
| keyPurpose      | String              | 키 용도 |M|ProofPurpose 클래스에 선언된 변수의 rawValue|
| hashedData      | String              | Hashed 서명 원문         | O       |                                              |
| publicKey       | String              | 인코딩된 공개키                 | O       |                                              |
| isSingleClaim       | Boolean              | 전체 클레임, 개별클레임 Param 구분 옵션 | O       |      |
| claimCode       | String              | 개별 클레임 확인을 위한 클레임 코드 | O       | VerifiableCredential 서명에 사용      |
| originData      | String              | 서명 원문                | M       |                                              |
| algorithm       | String              | 키 알고리즘                       | M       |                                              |
| signatureValue  | String              | 서명값                    | O       |   

# 3. Sample
## 3.1 DID Document Json
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

## 3.2 Verifiable Credential Json
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
                "caption": "이름",
                "code": "org.opendid.v1.name",
                "format": "plain",
                "location": "inline",
                "type": "text",
                "value": "1번발급서버"
            },
            {
                "caption": "주소",
                "code": "org.opendid.v1.url",
                "format": "html",
                "type": "text",
                "value": "z2rJefXWWUHwciS9PSveUh"
            },
            {
                "caption": "역할",
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