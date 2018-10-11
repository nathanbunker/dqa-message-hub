package org.immregistries.mqe.hub.report.viewer;

public class CodeDetail {
  private String codeType;
  private String codeValue;
  private String codeStatus;
  private int codeCount;

  public String getCodeType() {
    return codeType;
  }

  public void setCodeType(String codeType) {
    this.codeType = codeType;
  }

  public String getCodeValue() {
    return codeValue;
  }

  public void setCodeValue(String codeValue) {
    this.codeValue = codeValue;
  }

  public String getCodeStatus() {
    return codeStatus;
  }

  public void setCodeStatus(String codeStatus) {
    this.codeStatus = codeStatus;
  }

  public int getCodeCount() {
    return codeCount;
  }

  public void setCodeCount(int codeCount) {
    this.codeCount = codeCount;
  }
}
