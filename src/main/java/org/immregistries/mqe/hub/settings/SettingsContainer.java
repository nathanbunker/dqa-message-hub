package org.immregistries.mqe.hub.settings;

import java.util.ArrayList;
import java.util.List;

public class SettingsContainer {
    private List<MqeSettings> settings = new ArrayList<>();

    public List<MqeSettings> getSettings() {
        return settings;
    }

    public void setSettings(List<MqeSettings> settings) {
        this.settings = settings;
    }
}
