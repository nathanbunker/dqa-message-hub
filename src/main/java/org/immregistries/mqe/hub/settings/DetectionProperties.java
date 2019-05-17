package org.immregistries.mqe.hub.settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DetectionProperties {
  INSTANCE;
  private final Logger LOGGER = LoggerFactory.getLogger(DetectionProperties.class);
  
  private final String propertyFileName = "detections.properties";
  public final String GROUP_PROPERTY = "GROUP_ID";
  public final String DEFAULT_GROUP = "DEFAULT";
  MqeProperties prop = new MqeProperties();
  private HashSet<DetectionsSettings> allPropertySettings = new HashSet<DetectionsSettings>();
  
  DetectionProperties() {
    loadProps();
  }
  
  private void loadPropertiesFromFile() throws IOException {
	  InputStream inputStream = getInputStreamForProperties();
	  if (inputStream == null) {
		  return;
	  }
	  
	  BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	  
	  try {
		  String currentLine = reader.readLine();
		  String currentGroup = "";
		  while (currentLine != null) {
			  if (!currentLine.isEmpty()) {
				  String [] strToken = currentLine.split("=");
				  System.out.println(strToken[0] + " " + strToken[1]);
				  String propName = strToken[0];
				  String propVal = strToken[1];
				  if (GROUP_PROPERTY.contentEquals(propName)) {
					  currentGroup = propVal;
				  } else {
					  DetectionsSettings ds = new DetectionsSettings(currentGroup, propName, propVal);
					  allPropertySettings.add(ds);
				  }
			  }
			  currentLine = reader.readLine();
		  }
		} catch (IOException e) {
			LOGGER.error("Error reading detection properties, using defaults");
		} finally {
			reader.close();
		}
  }
  


private void loadProps() {
    try {
  	  loadPropertiesFromFile();
    } catch (IOException e) {
      LOGGER.error("Error reading detection properties, using defaults");
    }
  }

  public HashSet<DetectionsSettings> getAllPropertySettings() {
	return allPropertySettings;
}

public void setAllPropertySettings(HashSet<DetectionsSettings> allPropertySettings) {
	this.allPropertySettings = allPropertySettings;
}

public InputStream getInputStreamForProperties() {
    InputStream is;
    try {
      is = getFileFromRootDirectory(propertyFileName);
      LOGGER.warn("Using " + propertyFileName + " from directory");
    } catch (FileNotFoundException | NullPointerException e) {
      LOGGER.warn(propertyFileName + " not found in directory with jar.  checking classpath");
      is = getFileFromClasspath(propertyFileName);
      if (is != null) {
        LOGGER.warn("Using " + propertyFileName + " from classpath (resources folder in jar)");
      }
    }
    if (is != null) {
      return is;
    } else {
      return null;
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
