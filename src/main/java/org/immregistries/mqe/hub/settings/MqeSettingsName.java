package org.immregistries.mqe.hub.settings;

public enum MqeSettingsName {
                             NIST_URL("nistURL"),
                             NIST_ACTIVATION("nistActivation");
  public final String name;

  MqeSettingsName(String name) {
    this.name = name;
  }
}
