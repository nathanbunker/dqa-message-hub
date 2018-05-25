package org.immregistries.dqa.hub.report.vaccine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum VaccineCodeReportBuilder {
  INSTANCE;

  private VaccineCodesReportType preBuilt;

  private static final Logger logger = LoggerFactory
      .getLogger(VaccineCodeReportBuilder.class);

  public VaccineCodesReportType getVaccineCodesReportType(InputStream inputStream) {
    logger.trace("input stream: " + inputStream);
    if (inputStream == null) {
      throw new IllegalArgumentException(
          "No file provided for Vaccine Code Report Type:  Verify that you are building a CodeMap from a file that exists.");
    }

    JAXBContext jaxbContext;
    try {

      jaxbContext = JAXBContext.newInstance(VaccineCodesReportType.class);
      Unmarshaller jaxbUM = jaxbContext.createUnmarshaller();
      VaccineCodesReportType vcrt = (VaccineCodesReportType) jaxbUM.unmarshal(inputStream);
      this.preBuilt = vcrt;
      return vcrt;
    } catch (JAXBException e) {
      throw new RuntimeException("Could not marshall the vaccine code report type", e);
    }
  }

  public VaccineCodesReportType getDefaultVaccineCodesReportType() {
	  VaccineCodesReportType cm;
    String file = "vaccineCodes.xml";
    InputStream is;
    try {
      is = getVaccineCodesReportTypeFromSameDirAsJar(file);
      logger.warn("Using Compiled.xml from directory");
    } catch (FileNotFoundException e) {
      logger.warn("Compiled.xml not found in directory with jar.  checking classpath");
      is = getVaccineCodesReportTypeFromClasspathResource("/" + file);
      if (is != null) {
        logger.warn("Using Compiled.xml from classpath (resources folder in jar)");
      }
    }
    if (is != null) {
      cm = getVaccineCodesReportType(is);
    } else {
      throw new IllegalArgumentException(
          "You cannot build a CodeMap if the input stream is null.  Verify that you are building an input stream from a file that exists. ");
    }
    return cm;
  }

  public VaccineCodesReportType getVaccineCodesReportType() {
    if (preBuilt == null) {
      this.preBuilt = getDefaultVaccineCodesReportType();
    }
    return preBuilt;
  }

  public InputStream getVaccineCodesReportTypeFromClasspathResource(String resourcePath) {
    return Object.class.getResourceAsStream(resourcePath);
  }

  public InputStream getVaccineCodesReportTypeFromSameDirAsJar(String resourcePath) throws FileNotFoundException {
    logger.warn("Current dir: " + new File("").getAbsolutePath());
    File f = new File(resourcePath);
    logger.warn("Looking in: " + f.getAbsolutePath() + " for file");
    return new FileInputStream(f);
  }
}
