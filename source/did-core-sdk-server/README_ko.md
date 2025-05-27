# Core-Server SDK Guide
본 문서는 OpenDID Core Server SDK 사용을 위한 가이드로, 
Open DID에 필요한 DID Document(DID 문서), Verifiable Credential(이하 VC) 정보를 생성 및 Verifiable Presentation(이하 VP)을 검증하는 기능을 제공한다.


## S/W 사양
| 구분 | 내용                |
|------|----------------------------|
| Language  | Java 21|
| Build System  | Gradle 8.8 |

<br>

## 빌드 방법
: 본 SDK 그래들 프로젝트이므로 그래들이 설치 되어 있어야 한다.
1. 프로젝트의 `build.gradle` 파일을 열고, 아래와 같이 내용 추가한다.
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
    archiveVersion.set('2.0.0')
    archiveClassifier.set('') 
}

java {
    sourceCompatibility = '21'
    targetCompatibility = '21'
}

dependencies {
    implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.google.guava:guava:33.2.1-jre'
    implementation 'org.hibernate:hibernate-validator:7.0.0.Final'
    implementation 'com.google.code.gson:gson:2.8.9'
    implementation 'org.projectlombok:lombok:1.18.34'
    implementation files('libs/did-crypto-sdk-server-2.0.0.jar')
    implementation files('libs/did-datamodel-sdk-server-2.0.0.jar')
	
    annotationProcessor 'org.projectlombok:lombok:1.18.34'
}
```
* 해당 SDK를 빌드하기 위해선 Crypto-Sdk-Server, Datamodel-Sdk-Server SDK가 필요
2. 사용하는 IDE에서 `Gradle task` 창을 열고, 프로젝트의 `build > build > 태스크를 실행한다.
3. 실행이 완료되면 `%Core repository%/build/libs/` 폴더에 `did-core-sdk-server-2.0.0.jar` 파일을 생성된다.

<br>

## SDK 적용 방법
1. 앱 프로젝트의 libs에 did-core-sdk-server-2.0.0.jar, did-crypto-sdk-server-2.0.0.jar, did-datamodel-sdk-server-2.0.0.jar 파일을 복사한다.
2. 앱 프로젝트의 build gradle에 아래 의존성을 추가한다.

```groovy
    implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.google.guava:guava:33.2.1-jre'
    implementation 'org.hibernate:hibernate-validator:7.0.0.Final'
    implementation 'com.google.code.gson:gson:2.8.9'
    implementation 'org.projectlombok:lombok:1.18.34'
    implementation files('libs/did-core-sdk-server-2.0.0.jar')
    implementation files('libs/did-crypto-sdk-server-2.0.0.jar')
    implementation files('libs/did-datamodel-sdk-server-2.0.0.jar')
	
    annotationProcessor 'org.projectlombok:lombok:1.18.34'
```
* 해당 SDK를 사용하기 위해선 Crypto-Sdk-Server, Datamodel-Sdk-Server SDK가 필요
3. `Gradle`을 동기화하여 의존성이 제대로 추가되었는지 확인한다.

<br>

## API 규격서
| 구분 | API 문서 Link |
|------|----------------------------|
| DidManager  | [Core SDK Server- DidManager API](../../docs/CORE_SDK_SERVER_API_ko.md) |
| VcManager  | [Core SDK Server - VcManager API](../../docs/CORE_SDK_SERVER_API_ko.md) |
| VpManager  | [Core SDK Server - VpManager API](../../docs/CORE_SDK_SERVER_API_ko.md)  |

### DidManager
DidManager는 DID Document를 생성하고 관리하는 기능을 제공한다.<br>
주요 기능은 다음과 같다:

* <b>DID Document 생성</b>: DID Document를 생성한다.
* <b>DID Document 관리</b>: DID Document를 업데이트하며, 문서 안 키, 서비스 등을 삭제한다.
* <b>DID Document 서명 값 생성 지원, 서명 검증</b>: DID Document 서명을 위한 원문 데이터 생성 및 서명값을 검증한다.
  
### VcManager
VcManager는 Verifiable Credential(VC)을 발급하고 검증 및 VcMeta Data를 생성하는 기능을 제공한다.<br>
주요 기능은 다음과 같다:

* <b>VC 발급</b>: 발급 요청 데이터를 토대로 VC를 발급한다.
* <b>VCMeta 생성</b>: 발급된 VC를 토대로 VC Meta Data를 생성한다.
* <b>VC 서명 값 생성 지원 및 추가</b>: VC 서명을 위한 원문 데이터 생성 및 서명값을 VC에 추가한다.
* <b>VC 검증</b>: VC를 검증 한다.

### VpManager
VpManager는 Verifiable Presentation(VP)을 검증하고 검증된 VP 안 VcClaims을 반환하는 기능을 제공한다.<br>
주요 기능은 다음과 같다:

* <b>VP 검증</b>: VP안 Holder와 Issuer의 서명값을 검증한다.
* <b>VcClaims 반환</b>: 검증된 Vp 안 Vc Claims를 반환한다.

<br/>

## Data Class
OpenDID Core-Server SDK 에서 사용하는 Data 클래스 이며,<br>
주요 기능은 다음과 같다:
* <b>ClaimInfo</b>: 발급할 VC 안 Claim 정보 정의.
* <b>DidKeyInfo</b>: DidDocument에 추가할 Key 정의
* <b>IssueVcParam</b>: VC를 발급하기 위한 데이터 정의.
* <b>SignatureParam</b>: 서명값 생성/검증 위한 데이터 정의.
* <b>SignatureVcParam</b>: VC 서명값 생성/검증 위한 데이터 정의.
* <b>VpVerifyParam</b>: VP를 검증하기 위한 데이터 정의.