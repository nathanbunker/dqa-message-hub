package org.immregistries.mqe.hub.code;

import org.immregistries.codebase.client.reference.CodesetType;

public class MqeCodeType {
    private String typeCode;
    private String name;
    private String description;

    public MqeCodeType(){

    }

    public MqeCodeType(CodesetType ct) {
      this.description = ct.getDescription();
      this.name = ct.name();
      this.typeCode = ct.name();
    }
  public String getTypeCode() {
    return typeCode;
  }

  public void setTypeCode(String typeCode) {
    this.typeCode = typeCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
