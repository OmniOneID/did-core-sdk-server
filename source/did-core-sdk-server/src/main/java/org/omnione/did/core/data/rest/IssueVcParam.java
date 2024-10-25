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

package org.omnione.did.core.data.rest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.omnione.did.data.model.enums.vc.CredentialSchemaType;
import org.omnione.did.data.model.enums.vc.VcType;
import org.omnione.did.data.model.provider.ProviderDetail;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.Evidence;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
@Setter
public class IssueVcParam {

    public static final String DEFAULT_VALID_UNTIL = "9999-12-31T23:59:59Z";
    
    /**
     * The schema of the Verifiable Credential.
     */
    private VcSchema VcSchema;

    /**
     * The issuer information retrieved from blockchain
     */
    private ProviderDetail providerDetail;
    
    /**
     * The issuance date of the Verifiable Credential
     */
    @Setter(AccessLevel.NONE)
    private ZonedDateTime issuanceDate = ZonedDateTime.now(ZoneOffset.UTC);

    /**
     * The personal information about claims
     */
    private Map<String,ClaimInfo> privacy;
    

    /**
     * The context settings.
     * (e.g., "https://www.w3.org/ns/credentials/v2")
     */
    private List<String> context; 
    
    /**
     * The validity start date of the certificate.
     */
    private ZonedDateTime validFrom;
    
    /**
     * The validity end date of the certificate.
     */
    private ZonedDateTime validUntil; 
    
    /**
     * The Verifiable Credential type settings.
     * enum VcType Value.
     */
    private List<VcType> vcType; 
    
    /**
     * The evidence of documents submitted to receive the certificate.
     */
    private List<Evidence> evidences; 
    
    /**
     * The schema type.
     * enum CredentialSchemaType Value.
     */
    private String schemaType; 
    
    public String getSchemaType() {
        if(schemaType == null || schemaType.isEmpty())
            schemaType = CredentialSchemaType.OSD_SCHEMA_CREDENTIAL.getRawValue();
        return schemaType;
    }
}