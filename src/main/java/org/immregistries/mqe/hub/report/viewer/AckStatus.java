package org.immregistries.mqe.hub.report.viewer;

import java.util.HashMap;
import java.util.Map;

public enum AckStatus {

  ACCEPT("AA", 1),
  REJECT("AR", 2),
  ERROR("AE", 3);


  private Integer ackStatusId;
  private String ackStatusCd;

  private static Map<String, AckStatus> refMap = new HashMap<String, AckStatus>();

  static {
    for (AckStatus vmm : AckStatus.values()) {
      String ref = vmm.ackStatusCd;
      refMap.put(ref, vmm);
    }
  }

  AckStatus(String cd, Integer id) {
    this.ackStatusCd = cd;
    this.ackStatusId = id;
  }

  public Integer getAckStatusId() {
    return this.ackStatusId;
  }

  public static AckStatus getByCd(String code) {
    return refMap.get(code);
  }

  public static Integer getIdForCode(String code) {
    AckStatus in = refMap.get(code);
    return in != null ? in.ackStatusId : -1;
  }

  public static String geCdForId(Integer id) {
    for (AckStatus st : AckStatus.values()) {
      if (st.ackStatusId == id) {
        return st.ackStatusCd;
      }
    }

    return "UNK";
  }
}
