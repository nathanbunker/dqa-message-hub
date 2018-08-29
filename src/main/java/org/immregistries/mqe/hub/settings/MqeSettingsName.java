package org.immregistries.mqe.hub.settings;

public enum MqeSettingsName {
  NIST_URL("nistURL"),
  NIST_ACTIVATION("nistActivation"),
  SS_API_KEY("smartyStreetsAPIKey"),
  SS_AUTH_ID("smartyStreetsAuthID"),
  SS_ACTIVATION("smartyStreetsActivation");

  public final String name;

  MqeSettingsName(String name) {
    this.name = name;
  }
}
