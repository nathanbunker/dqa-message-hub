package org.immregistries.mqe.hub.report.vaccineReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import org.immregistries.mqe.hub.report.vaccineReport.generated.ObjectFactory;
import org.immregistries.mqe.hub.report.vaccineReport.generated.VaccinesAdministeredExpectation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum VaccineReportBuilder {
                                  INSTANCE;


  private static final Logger logger = LoggerFactory.getLogger(VaccineReportBuilder.class);

  private VaccineReportConfig vaccineReportConfig = null;

  public VaccineReportConfig getVaccineReportConfig(InputStream inputStream) {
    if (vaccineReportConfig == null) {
      logger.trace("input stream: " + inputStream);
      if (inputStream == null) {
        throw new IllegalArgumentException(
            "No file provided for Vaccine Report meta data:  Verify that you are building a Vaccine Report meta data from a file that exists.");
      }

      JAXBContext jaxbContext;
      try {

        jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller jaxbUM = jaxbContext.createUnmarshaller();
        VaccinesAdministeredExpectation vaet =
            (VaccinesAdministeredExpectation) JAXBIntrospector
                .getValue(jaxbUM.unmarshal(inputStream));
        vaccineReportConfig = new VaccineReportConfig(vaet);
      } catch (JAXBException e) {
        throw new RuntimeException("Could not marshall the vaccine report meta data", e);
      }
    }
    return vaccineReportConfig;
  }

  public VaccineReportConfig getDefaultVaccineReportConfig() {
    VaccineReportConfig cm;
    String file = "VaccineReport.xml";
    InputStream is;
    try {
      is = getVaccineReportConfigFromSameDirAsJar(file);
      logger.warn("Using VaccineReport.xml from directory");
    } catch (FileNotFoundException e) {
      logger.warn("VaccineReport.xml not found in directory with jar.  checking classpath");
      is = getVaccineReportConfigFromClasspathResource("/" + file);
      if (is != null) {
        logger.warn("Using VaccineReport.xml from classpath (resources folder in jar)");
      }
    }
    if (is != null) {
      cm = getVaccineReportConfig(is);
    } else {
      throw new IllegalArgumentException(
          "You cannot build a VaccineReportConfig if the input stream is null.  Verify that you are building an input stream from a file that exists. ");
    }
    return cm;
  }

  public InputStream getVaccineReportConfigFromClasspathResource(String resourcePath) {
    return Object.class.getResourceAsStream(resourcePath);
  }

  public InputStream getVaccineReportConfigFromSameDirAsJar(String resourcePath)
      throws FileNotFoundException {
    logger.warn("Current dir: " + new File("").getAbsolutePath());
    File f = new File(resourcePath);
    logger.warn("Looking in: " + f.getAbsolutePath() + " for file");
    return new FileInputStream(f);
  }
}
