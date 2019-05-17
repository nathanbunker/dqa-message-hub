package org.immregistries.mqe.hub.settings;

import java.util.ArrayList;
import java.util.List;

public class DetectionsSettingsContainer {
    private List<DetectionsSettings> settings = new ArrayList<>();

    public List<DetectionsSettings> getSettings() {
        return settings;
    }

    public void setSettings(List<DetectionsSettings> settings) {
        this.settings = settings;
    }
}
