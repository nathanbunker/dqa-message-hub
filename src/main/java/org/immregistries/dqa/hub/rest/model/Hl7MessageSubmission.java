package org.immregistries.dqa.hub.rest.model;


public class Hl7MessageSubmission {

  private String message;
  private String user;
  private String password;
  private String sendingOrganization;


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFacilityCode() {
    return sendingOrganization;
  }

  public void setFacilityCode(String facilityCode) {
    this.sendingOrganization = facilityCode;
  }

}
