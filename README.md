# Core Server SDK

Welcome to the Core Server SDK Repository. <br> This repository provides an SDK for developing DID, VC, and VP.

## Folder Structure
```
did-core-sdk-server
├── CHANGELOG.md
├── CLA.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE
├── LICENSE-dependencies.md
├── MAINTAINERS.md
├── README.md
├── README_ko.md
├── RELEASE-PROCESS.md
├── SECURITY.md
├── docs
│   └── api
│       ├── CORE_SDK_SERVER_API.md
│       ├── CORE_SDK_SERVER_API_ko.md
│       └── CoreSDKError.md
└── source
    ├── did-core-sdk-server
    │   ├── README.md
    │   ├── README_ko.md
    │   ├── build.gradle
    │   ├── gradle
    │   │   └── wrapper
    │   ├── .gitignore
    │   ├── build
    │   ├── libs
    │   │   ├── did-crypto-sdk-server-1.0.0.jar        
    │   │   └── did-datamodel-sdk-server-1.0.0.jar
    │   ├── gradlew        
    │   ├── gradlew.bat
    │   ├── settings.gradle
    │   └── src
    └── release
        └── did-core-sdk-server-1.0.0.jar
```

| Name                    | Description                                     |
| ----------------------- | ----------------------------------------------- |
| source                  | SDK source code project                         |
| docs                    | Documentation                                   |
| ┖ api                   | API guide documentation                         |
| README.md               | Overview and description of the project         |
| CLA.md                  | Contributor License Agreement                   |
| CHANGELOG.md            | Version-specific changes in the project         |
| CODE_OF_CONDUCT.md      | Code of conduct for contributors                |
| CONTRIBUTING.md         | Contribution guidelines and procedures          |
| LICENSE                 | Apache 2.0                                      |
| LICENSE-dependencies.md | Licenses for the project’s dependency libraries |
| MAINTAINERS.md          | General guidelines for maintaining              |
| RELEASE-PROCESS.md      | Release process                                 |
| SECURITY.md             | Security policies and vulnerability reporting   |

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

## Libraries

Libraries can be found in the [Releases](https://github.com/OmniOneID/did-core-sdk-server/releases).

## Core SDK
1. Copy the did-core-sdk-server-1.0.0.jar file to the libs of the server project.
2. Add the following dependencies to the server project's build.gradle.

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
* The Crypto-Sdk-Server and Data-Model-Server SDKs are required to use this SDK.

3. Sync `Gradle` to ensure the dependencies are properly added.

## API Reference

API Reference can be found [here](docs/CORE_SDK_SERVER_API.md)


## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for details on our code of conduct, and the process for submitting pull requests to us.


## License
[Apache 2.0](LICENSE)
