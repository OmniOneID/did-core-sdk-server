/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.core.exception;

public enum CoreErrorCode implements CoreErrorCodeInterface{
	
    ERR_CODE_CORE_SDK_BASE("SSDKCOR", ""),

	ERR_CODE_DIDMANAGER_BASE(ERR_CODE_CORE_SDK_BASE, "01", ""),
	ERR_CODE_DIDMANAGER_ADD_KEY_FAIL(ERR_CODE_DIDMANAGER_BASE, 	"000",	"Failed to add key"), 
	ERR_CODE_DIDMANAGER_DUPLICATED_KEY(ERR_CODE_DIDMANAGER_BASE, 	"001",	"duplicatedKey"),  
	ERR_CODE_DIDMANAGER_UNSAVED_KEY(ERR_CODE_DIDMANAGER_BASE, 	"002",	"Key is not saved in the verification method"), 
	ERR_CODE_DIDMANAGER_EMPTY_KEYPURPOSE_LIST(ERR_CODE_DIDMANAGER_BASE, "003", "Keypurpose list is empty" ),
	ERR_CODE_DIDMANAGER_NOT_A_SIGNING_KEY(ERR_CODE_DIDMANAGER_BASE, "004", "It's not a signing key" ),
	ERR_CODE_DIDMANAGER_ID_IS_NULL(ERR_CODE_DIDMANAGER_BASE, "005", "ID is null" ),
	ERR_CODE_DIDMANAGER_NOT_EXIST_SIGNING_KEY(ERR_CODE_DIDMANAGER_BASE, "006", "Signkey does not exist in DIDs"),
	ERR_CODE_DIDMANAGER_SERVICE_NOT_FOUND(ERR_CODE_DIDMANAGER_BASE, "007", "Service not found"), 
	ERR_CODE_DIDMANAGER_EXISTED_SERVICE_ID(ERR_CODE_DIDMANAGER_BASE, "008", "Service ID already exists with a different type"),
	ERR_CODE_DIDMANAGER_SERVICE_TYPE_IS_NULL(ERR_CODE_DIDMANAGER_BASE, "009", "Service type must be provided for a new service ID"),
	ERR_CODE_DIDMANAGER_EXISTED_SERVICE_URL(ERR_CODE_DIDMANAGER_BASE, "010", "the Service URL already exists"),
	ERR_CODE_DIDMANAGER_NOT_EXISTED_SERVICE_URL(ERR_CODE_DIDMANAGER_BASE, "011", "Service URL does not exist"),
	ERR_CODE_DIDMANAGER_NOT_MATCHED_SERVICE(ERR_CODE_DIDMANAGER_BASE, "012", "There is not matched service"), 
	ERR_CODE_DIDMANAGER_UNREGISTERED_KEY(ERR_CODE_DIDMANAGER_BASE, "013","The Key is not registered on VerificationMethod"),
	ERR_CODE_DIDMANAGER_DIDDOCUMENT_FILE_NOT_FOUND(ERR_CODE_DIDMANAGER_BASE,  "014", "DidDocument file not found."),
	ERR_CODE_DIDMANAGER_READ_DIDDOCUMENT_FILE_FAIL(ERR_CODE_DIDMANAGER_BASE,  "015", "Failed to read DidDocument File"),
	
	ERR_CODE_VCMANAGER_BASE(ERR_CODE_CORE_SDK_BASE, "02", ""),
	ERR_CODE_VCMANAGER_NOT_SUBMITED_PUBLIC_CLAIM(ERR_CODE_VCMANAGER_BASE, 	"000",	"Public claim is not submited"),
	ERR_CODE_VCMANAGER_NOT_ASSERTION_METHOD_TYPE(ERR_CODE_VCMANAGER_BASE, 	"001",	"SignKey is not of type Assertion Method"),
	ERR_CODE_VCMANAGER_NOT_MACTH_SIGN_KEY_AND_PROOF_KEY(ERR_CODE_VCMANAGER_BASE, 	"002",	"SignKey and key of proof are different"),
	ERR_CODE_VCMANAGER_SCHEMA_MISSING_DETAIL(ERR_CODE_VCMANAGER_BASE,     "003", "Required information is missing in the schema"),
	ERR_CODE_VCMANAGER_MULTIBASE_ENCODING_FAIL(ERR_CODE_VCMANAGER_BASE,     "004", "Multibase encoding failed"),
	ERR_CODE_VCMANAGER_EXPIRED_VC(ERR_CODE_VCMANAGER_BASE,     "005",  "Expired Verifiable Credential"),
	ERR_CODE_VCMANAGER_PRIVACY_NOT_EXIST(ERR_CODE_VCMANAGER_BASE,  "006",  "Privacy Data does not Exist"), 
	ERR_CODE_VCMANAGER_NOT_EXIST_SIGNING_KEY(ERR_CODE_VCMANAGER_BASE, "007", "Signkey does not exist in DIDs"),
	ERR_CODE_VCMANAGER_MISSING_VC_TYPE(ERR_CODE_VCMANAGER_BASE, "008", "VcType is missing"),
	
	ERR_CODE_VPMANAGER_BASE(ERR_CODE_CORE_SDK_BASE, "03", ""),
	ERR_CODE_VPMANAGER_EXPIRED_VP(ERR_CODE_VPMANAGER_BASE, 	"000",	"Expired Verifiable Presentation"), 
	ERR_CODE_VPMANAGER_PRIVACY_NOT_EXIST(ERR_CODE_VPMANAGER_BASE, 	"001",	"Privacy Data does not Exist"), 
	ERR_CODE_VPMANAGER_NOT_ALLOW_ISSUER(ERR_CODE_VPMANAGER_BASE, 	"002",	"This Issuer is not allowed"), 
	ERR_CODE_VPMANAGER_NOT_CONTAIN_CLAIM(ERR_CODE_VPMANAGER_BASE, 	"003",	"Requried Claim is not Submited"), 
	ERR_CODE_VPMANAGER_EXPIRED_VC(ERR_CODE_VPMANAGER_BASE, 	"004",	"Expired Verifiable Credential"), 
	ERR_CODE_VPMANAGER_NOT_MATCHED_SCHEMA(ERR_CODE_VPMANAGER_BASE,  "005", "Schema Id, Type does not match Type"),
	ERR_CODE_VPMANAGER_VERIFY_SIGNATURE_FAIL(ERR_CODE_VPMANAGER_BASE,  "006", "Verify Signature is failed"),
	ERR_CODE_VPMANAGER_GEN_HASH_FAIL(ERR_CODE_VPMANAGER_BASE,  "007", "Failed to generate VP,VC HashData."),
	ERR_CODE_VPMANAGER_MULTIBASE_DECODING_FAIL(ERR_CODE_VPMANAGER_BASE,     "008", "Multibase decoding failed"),
	ERR_CODE_VPMANAGER_NOT_EXIST_SIGNING_KEY(ERR_CODE_VPMANAGER_BASE, "009", "Signkey does not exist in DIDs"),
	ERR_CODE_VPMANAGER_NOT_EXIST_PROOFVALUE(ERR_CODE_VPMANAGER_BASE, "010", "ProofValue(Total claim signature value) does not exist in VC Proof"),
	;
	private String code;
	private String msg;

	private CoreErrorCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	private CoreErrorCode(CoreErrorCode errCodeKeymanagerKeyBase, String subCode, String msg) {
		this.code = errCodeKeymanagerKeyBase.getCode() + subCode;
		this.msg = msg;
	}

	@Override
	public String getCode() {
		return code;
	}
	
	@Override
	public String getMsg() {
		return msg;
	}
	
	public static CoreErrorCodeInterface getEnumByCode(int code) {
		
		CoreErrorCode agentTypes[] = CoreErrorCode.values();
		for (CoreErrorCode iwCode : agentTypes) {
			if(iwCode.getCode().equals(code)){
				return iwCode;
			}
		}
		
		throw new AssertionError("Unknown Enum Code");

	}

}
