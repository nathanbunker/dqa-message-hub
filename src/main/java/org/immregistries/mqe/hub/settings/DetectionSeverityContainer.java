package org.immregistries.mqe.hub.settings;

import java.util.ArrayList;
import java.util.List;

public class DetectionSeverityContainer {
    private List<DetectionSeverityOverride> settings = new ArrayList<>();

    public List<DetectionSeverityOverride> getSettings() {
        return settings;
    }

    public void setSettings(List<DetectionSeverityOverride> settings) {
        this.settings = settings;
    }
}
