package org.immregistries.mqe.hub.settings;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;

public class MqeProperties extends Properties {
    
	private static final long serialVersionUID = 4112578685029874840L;
	private final HashSet<Object> keys = new LinkedHashSet<Object>();
	private final HashSet<DetectionsSettings> detectionKeys = new LinkedHashSet<DetectionsSettings>();

    public MqeProperties() {
    }

    public Iterable<Object> orderedKeys() {
        return Collections.list(keys());
    }

    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
}
