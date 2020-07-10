package org.immregistries.mqe.hub.settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.hub.report.SettingsGroupJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetectionProperties {
  private final Logger LOGGER = LoggerFactory.getLogger(DetectionProperties.class);

  @Autowired
  SettingsGroupJpaRepository sgRepo;

  @Autowired
  DefaultDetectionGuidanceRepository dgRepo;

  private final String propertyFileName = "detections.properties";
  private final String guidancePropertyFileName = "detectionGuidance.properties";
  public final String GROUP_PROPERTY = "GROUP_ID";
  public final String DEFAULT_GROUP = "DEFAULT";
  MqeProperties prop = new MqeProperties();
  private HashSet<DetectionSeverityOverride> allPropertySettings =
      new HashSet<DetectionSeverityOverride>();

  @PostConstruct
  private void init() {
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
          String[] strToken = currentLine.split("=");
          String propName = strToken[0];
          String propVal = strToken[1];
          if (GROUP_PROPERTY.contentEquals(propName)) {
            currentGroup = propVal;
          } else {
            DetectionSeverityOverrideGroup sg = sgRepo.findByName(currentGroup);
            if (sg == null) {
              sg = new DetectionSeverityOverrideGroup();
              sg.setName(currentGroup);
              sg.setCreatedDate(new Date());
              sgRepo.save(sg);
            }
            DetectionSeverityOverride ds = new DetectionSeverityOverride(sg, propName, propVal);
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

  private void loadGuidancePropertiesFromFile() throws IOException {
    InputStream inputStream = getGuidanceInputStreamForProperties();
    if (inputStream == null) {
      return;
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    Map<String, DetectionGuidance> detectionGuidanceMap = new HashMap<>();
    // get from database

    {
      List<DetectionGuidance> detectionGuidanceList = dgRepo.findAll();
      for (DetectionGuidance detectionGuidance : detectionGuidanceList) {
        detectionGuidanceMap.put(detectionGuidance.getMqeCode(), detectionGuidance);
      }
    }

    try {
      String currentLine = reader.readLine();
      while (currentLine != null) {
        if (!currentLine.isEmpty()) {
          String[] strToken = currentLine.split("=");
          if (strToken.length == 2) {
            String propName = strToken[0];
            String propVal = strToken[1];
            String[] propToken = propName.split("\\.");
            if (propName.startsWith("MQE") && propToken.length == 2) {
              String mqeCode = propToken[0];
              String concept = propToken[1];
              DetectionGuidance detectionGuidance = detectionGuidanceMap.get(mqeCode);
              if (detectionGuidance == null) {
                detectionGuidance = new DetectionGuidance();
                detectionGuidance.setMqeCode(mqeCode);
                detectionGuidanceMap.put(mqeCode, detectionGuidance);
              }
              if (concept.equals("howToFix")) {
                if (StringUtils.isEmpty(detectionGuidance.getHowToFix())) {
                  detectionGuidance.setHowToFix(propVal);
                }
              } else if (concept.equals("whyToFix")) {
                if (StringUtils.isEmpty(detectionGuidance.getWhyToFix())) {
                  detectionGuidance.setWhyToFix(propVal);
                }
              }
            }
          }
        }
        currentLine = reader.readLine();
      }
    } catch (IOException e) {
      LOGGER.error("Error reading guidance detection properties, using defaults");
    } finally {
      reader.close();
    }

    dgRepo.save(detectionGuidanceMap.values());
  }



  private void loadProps() {
    try {
      loadPropertiesFromFile();
    } catch (IOException e) {
      LOGGER.error("Error reading detection properties, using defaults");
    }
    try {
      loadGuidancePropertiesFromFile();
    } catch (IOException e) {
      LOGGER.error("Error reading guidance detection properties, using defaults");
    }
  }

  public HashSet<DetectionSeverityOverride> getAllPropertySettings() {
    return allPropertySettings;
  }

  public void setAllPropertySettings(HashSet<DetectionSeverityOverride> allPropertySettings) {
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

  public InputStream getGuidanceInputStreamForProperties() {
    InputStream is;
    try {
      is = getFileFromRootDirectory(guidancePropertyFileName);
      LOGGER.warn("Using " + guidancePropertyFileName + " from directory");
    } catch (FileNotFoundException | NullPointerException e) {
      LOGGER
          .warn(guidancePropertyFileName + " not found in directory with jar.  checking classpath");
      is = getFileFromClasspath(guidancePropertyFileName);
      if (is != null) {
        LOGGER.warn(
            "Using " + guidancePropertyFileName + " from classpath (resources folder in jar)");
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
