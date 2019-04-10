package org.immregistries.mqe.hub.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.validator.detection.Detection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DetectionProperties {
  INSTANCE;
  private final Logger LOGGER = LoggerFactory.getLogger(DetectionProperties.class);
  Properties prop = new Properties();


  DetectionProperties() {
    loadProps();
    updateProps();
  }

  private void updateProps() {
	for (Detection detection : Detection.values()) {
		String mqeCode = detection.getMqeMqeCode();
		if (prop.getProperty(mqeCode) != null) {
			String severityLabel = prop.getProperty(mqeCode);
			SeverityLevel severityLevel = SeverityLevel.findByLabel(severityLabel);
			detection.setSeverity(severityLevel);
		}
	}
  }

private void loadProps() {
    try {
      prop.load(getInputStreamForProperties());
    } catch (IOException e) {
      LOGGER.error("Error reading detection properties, using defaults");
    }
  }

  public InputStream getInputStreamForProperties() {
    InputStream is;
    String fileName = "detections.properties";
    try {
      is = getFileFromRootDirectory(fileName);
      LOGGER.warn("Using " + fileName + " from directory");
    } catch (FileNotFoundException | NullPointerException e) {
      LOGGER.warn(fileName + " not found in directory with jar.  checking classpath");
      is = getFileFromClasspath(fileName);
      if (is != null) {
        LOGGER.warn("Using " + fileName + " from classpath (resources folder in jar)");
      }
    }
    if (is != null) {
      return is;
    } else {
      throw new IllegalArgumentException(
          "You cannot reference " + fileName + " if the input stream is null.  Verify that you are building an input stream from a file that exists. ");
    }
  }

  public InputStream getFileFromClasspath(String resourcePath) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    return loader.getResourceAsStream(resourcePath);
  }

  public InputStream getFileFromRootDirectory(String resourcePath) throws FileNotFoundException {
    final String dir = System.getProperty("user.dir");
    LOGGER.warn("Current dir: " + dir);
    LOGGER.warn("Looking in: " + dir + "/" + resourcePath + " for file");
    return new FileInputStream(resourcePath);
  }
  


}
