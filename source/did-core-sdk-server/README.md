# Core-Server SDK Guide
This document is a guide for using the OpenDID Core Server SDK, providing functionality to generate DID Document, Verifiable Credential (VC) information, and verify Verifiable Presentation (VP) required by Open DID.


## S/W Specifications
| Category | Details                |
|------|----------------------------|
| Language  | Java 17|
| Build System  | Gradle 8.2 |

<br>

## Build Instructions
: Since this SDK is a Gradle project, Gradle must be installed
1. Open the `build.gradle` file of the project and add the following content:
```groovy
plugins {
    id 'java-library'
}

repositories {
    jcenter()
}

group = 'org.omnione.did'

jar {
    archiveBaseName.set('did-core-sdk-server') 
    archiveVersion.set('1.0.0')
    archiveClassifier.set('') 
}

java {
    sourceCompatibility = '17'
    targetCompatibility = '17'
}

dependencies {
    implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.google.guava:guava:33.2.1-jre'
    implementation 'org.hibernate:hibernate-validator:7.0.0.Final'
    implementation 'com.google.code.gson:gson:2.8.9'
    implementation 'org.projectlombok:lombok:1.18.24'
    implementation files('libs/did-crypto-sdk-server-1.0.0.jar')
    implementation files('libs/did-datamodel-sdk-server-1.0.0.jar')
	
    annotationProcessor 'org.projectlombok:lombok:1.18.24'
}
```
* The Crypto-Sdk-Server and Datamodel-Sdk-Server SDKs are required to build this SDK.
2. In the IDE, open the `Gradle task` window and execute the `build > build` task for the project.
3. Once the execution is complete, the `did-core-sdk-server-1.0.0.jar` file will be generated in the `%Core repository%/build/libs/` folder.

<br>

## SDK Integration Instructions
1. Copy the `did-core-sdk-server-1.0.0.jar`, `did-crypto-sdk-server-1.0.0.jar`, and `did-datamodel-sdk-server-1.0.0.jar` files to the `libs` directory of your app project.
2. Add the following dependencies to the build.gradle file of your app project:

```groovy
    implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.google.guava:guava:33.2.1-jre'
    implementation 'org.hibernate:hibernate-validator:7.0.0.Final'
    implementation 'com.google.code.gson:gson:2.8.9'
    implementation 'org.projectlombok:lombok:1.18.24'
    implementation files('libs/did-core-sdk-server-1.0.0.jar')
    implementation files('libs/did-crypto-sdk-server-1.0.0.jar')
    implementation files('libs/did-datamodel-sdk-server-1.0.0.jar')
	
    annotationProcessor 'org.projectlombok:lombok:1.18.24'
```
* The `Crypto-Sdk-Server` and `Datamodel-Sdk-Server` SDKs are required to use this SDK.
3. Synchronize `Gradle` to ensure that the dependencies are added correctly.

<br>

## API Specification
| Category | API Documentation Link |
|------|----------------------------|
| DidManager  | [Core SDK Server- DidManager API](../../docs/CORE_SDK_SERVER_API.md) |
| VcManager  | [Core SDK Server - VcManager API](../../docs/CORE_SDK_SERVER_API.md) |
| VpManager  | [Core SDK Server - VpManager API](../../docs/CORE_SDK_SERVER_API.md)  |

### DidManager
The DidManager provides functionalities for creating and managing DID Documents.<br>
Key features include:

* <b>Create DID Document</b>: Creates a DID Document.
* <b>Manage DID Document</b>: Updates the DID Document, and deletes keys, services, etc., within the document.
* <b>Generate and verify signature for DID Document</b>: Generates the original data for DID Document signature and verifies the signature value.
  
### VcManager
The VcManager provides functionalities for issuing, verifying, and creating VcMeta Data for Verifiable Credentials (VC).<br>
Key features include:

* <b>Issue VC</b>: Issues a VC based on the request data.
* <b>Create VC Meta</b>: Creates VC Meta Data based on the issued VC.
* <b>Support and add VC signature value</b>: Generates the original data for VC signature and adds the signature value to the VC.
* <b>Verify VC</b>: Verifies the VC.

### VpManager
The VpManager provides functionalities for verifying Verifiable Presentations (VP) and returning verified VcClaims within the VP.<br>
Key features include:

* <b>Verify VP</b>: Verifies the signature values of the Holder and Issuer within the VP.
* <b>Return VcClaims</b>: Returns the verified Vc Claims within the VP.

<br/>

## Data Class
These are the Data classes used in the OpenDID Core-Server SDK, and their main functions are as follows:
* <b>ClaimInfo</b>: Defines the Claim information within the VC to be issued.
* <b>DidKeyInfo</b>: Defines the key to be added to the DidDocument.
* <b>IssueVcParam</b>: Defines the data for issuing a VC.
* <b>SignatureParam</b>: Defines the data for generating/verifying the signature value.
* <b>SignatureVcParam</b>: Defines the data for generating/verifying the VC signature value.
* <b>VpVerifyParam</b>: Defines the data for verifying a VP.