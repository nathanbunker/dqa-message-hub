package org.immregistries.mqe.hub.code;

import java.util.ArrayList;
import java.util.List;

public class MqeCodeContainer {
    List<MqeCodeType> codeTypeList = new ArrayList<>();

  public List<MqeCodeType> getCodeTypeList() {
    return codeTypeList;
  }

  public void setCodeTypeList(List<MqeCodeType> codeTypeList) {
    this.codeTypeList = codeTypeList;
  }
}
