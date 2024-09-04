/* 
 * Copyright 2024 Raonsecure
 */

package org.omnione.did.core.data.rest;

import java.util.Map;

import jakarta.validation.Valid;

import org.omnione.did.crypto.enums.MultiBaseType;
import org.omnione.did.data.model.DataObject;
import org.omnione.did.data.model.util.json.GsonWrapper;
import org.omnione.did.data.model.vc.I18N;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimInfo extends DataObject {

  /**
   * The claim code.
   * format = Claim Namespace + Claim Id
   */
  @SerializedName("code")
  @Expose
  private String code;
  
  /**
   * The value of the claim, stored as a byte array.
   */
  @SerializedName("value")
  @Expose
  private byte[] value;
  
  /**
   * The encoding type used for the claim, defaulting to base58btc.
   */
  @SerializedName("encodeType")
  @Expose
  private String encodeType = MultiBaseType.base58btc.getCharacter();
  
  /**
   * Required unless location is "inline"
   * W3C subresource integrity
   * format : {hashAlgorithm} - {hashBase64} 
   */
  @SerializedName("digestSRI")
  @Expose
  private String digestSRI;
  
  /**
   * Multilingual information
   */
  @SerializedName("i18n")
  @Expose 
  private Map<String, @Valid I18N> i18n;



  @Override
  public void fromJson(String val) {

    GsonWrapper gson = new GsonWrapper();
    ClaimInfo obj = gson.fromJson(val, ClaimInfo.class);

    code = obj.getCode();
    value = obj.getValue();
    digestSRI = obj.getDigestSRI(); 
  }

}
