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

Core SDK Error
==

- Topic: CoreSDKError
- Author: Jongyun Baek
- Date: 2024-08-27
- Version: v1.0.0

| Version          | Date       | Changes                  |
| ---------------- | ---------- | ------------------------ |
| v1.0.0  | 2024-08-27 | Initial version          |

<div style="page-break-after: always;"></div>

# Table of Contents
- [Model](#model)
  - [Error Response](#error-response)
- [Error Code](#error-code)
  - [1.1. DidManager (01xxx)](#11-didmanager01xxx)
  - [1.2. VcManager (02xxx)](#12-vcmanager02xxx)
  - [1.3. VpManager (03xxx)](#13-vpmanager03xxx)

# Model
## Error Response

### Description
```
Error struct for Core SDK. It has code and message pair.
Code starts with SSDKCOR.
```

### Declaration
```java
public class ErrorResponse {
    private final String code;
    private final String description;
}
```

### Property

| Name               | Type       | Description                            | **M/O** | **Note**              |
|--------------------|------------|----------------------------------------|---------|-----------------------|
| code               | String     | Error code. It starts with SSDKCOR     |    M    |                       | 
| message            | String     | Error description                      |    M    |                       | 

<br>

# Error Code
## 1. Core SDK
### 1.1. DidManager(01xxx)

| Error Code   | Error Message                                                | Description      | Action Required                             |
|--------------|--------------------------------------------------------------|------------------|---------------------------------------------|
| SSDKCOR01000 | Failed to add key.                                           | -                | Check if the key information object to be added.    |
| SSDKCOR01001 | DuplicatedKey.                                               | -                | Check if the key is already registered.                    |
| SSDKCOR01002 | Key is not saved in the verification method.                 | -                | Check if the key is present in VerificationMethod    |
| SSDKCOR01003 | Keypurpose list is empty.                                    | -                | Check if the purpose of the key has been set.    |
| SSDKCOR01004 | It's not a signing key.                                      | -                | Check if the key is eligible for signing.     |
| SSDKCOR01005 | ID is null.                                                  | -                | Check if the DID is present.    |
| SSDKCOR01006 | Signkey does not exist in DIDs.                              | -                | Verify if it is a signing key present in the DID document.    |
| SSDKCOR01007 | Service not found.                                           | -                | Check if the service to be deleted is present in the DID document.    |
| SSDKCOR01008 | Service ID already exists with a different type.             | -                | Check if the service is already present.    |
| SSDKCOR01009 | Service type must be provided for a new service ID.          | -                | Check if the service ID is the same.    |
| SSDKCOR01010 | the Service URL already exists.                              | -                | Check if the service URL is already present.    |
| SSDKCOR01011 | Service URL does not exist.                                  | -                | Check if the service URL is present in the DID document.    |
| SSDKCOR01012 | There is not matched service.                                | -                | Check if the service to be deleted is present.    |
| SSDKCOR01013 | The Key is not registered on VerificationMethod.             | -                | Check if the key is in the VerificationMethod.    |
| SSDKCOR01014 | DidDocument file not found.                                  | -                | Check if the DID file is located at the specified path.    |
| SSDKCOR01015 | Failed to read DidDocument File.                             | -                | Check if the DID file is a valid file.    |


<br>

### 1.2. VcManager(02xxx)

| Error Code   | Error Message                                 | Description      | Action Required                        |
|--------------|-----------------------------------------------|------------------|----------------------------------------|
| SSDKCOR02000 | Public claim is not submited.                 | -                | Check if there is Claim information in the VC to be issued.        |
| SSDKCOR02001 | SignKey is not of type Assertion Method.      | -                | Check if the signing key is intended for the AssertionMethod purpose.        |
| SSDKCOR02002 | SignKey and key of proof are different.       | -                | Check if the signing key and the key in the Proof are the same.        |
| SSDKCOR02003 | Required information is missing in the schema.| -                | Check if the schema for issuing the VC contains sufficient information.        |
| SSDKCOR02004 | Multibase encoding failed.                    | -                | Check the data to be encoded and the encoding type.        | 
| SSDKCOR02005 | Expired Verifiable Credential.                | -                | Check the expiration date of the VC.       |
| SSDKCOR02006 | Privacy Data does not Exist.                  | -                | Check if there are Claim information within the VC.        |
| SSDKCOR02007 | Signkey does not exist in DIDs.               | -                | Verify if it is a signing key present in the DID document.        |
| SSDKCOR02008 | VcType is missing.                            | -                | Check if the vcType is present in the VC issuance parameters.        |



<br>

### 1.3. VpManager (03xxx)

| Error Code   | Error Message                                | Description      | Action Required                         |
|--------------|----------------------------------------------|------------------|-----------------------------------------|
| SSDKCOR03000 | Expired Verifiable Presentation.             | -                | Check the expiration date of the VP.      |
| SSDKCOR03001 | Privacy Data does not Exist.                 | -                | Check if there are Claim information within the VC. |
| SSDKCOR03002 | This Issuer is not allowed.                  | -                | Check if the VC Issuer meets the required conditions. |
| SSDKCOR03003 | Requried Claim is not Submited.              | -                | Check if the required claims have been submitted. |
| SSDKCOR03004 | Expired Verifiable Credential.               | -                | Check the expiration date of the VC. |
| SSDKCOR03005 | Schema Id, Type does not match Type.         | -                | Check if the schema meets the filter conditions. |
| SSDKCOR03006 | Verify Signature is failed.                  | -                | Verify the signature and the signature source data. |
| SSDKCOR03007 | Failed to generate VP,VC HashData.           | -                | Check the data to be hashed. |
| SSDKCOR03008 | Multibase decoding failed.                   | -                | Check the data to be decoded and the decoding type. |
| SSDKCOR03009 | Signkey does not exist in DIDs.              | -                | Verify if it is a signing key present in the DID document. |


<br>
