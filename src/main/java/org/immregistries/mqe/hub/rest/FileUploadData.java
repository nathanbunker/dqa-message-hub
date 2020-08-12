package org.immregistries.mqe.hub.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.hl7util.parser.HL7QuickParser;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.submission.HL7File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

public class FileUploadData {

  private static final Logger logger = LoggerFactory.getLogger(FileUploadData.class);

  private String status = "waiting";

  private String facilityId;
  private String fileName;
  private String fileId;
  private String username;
  private long startTimeMs = 1;
  private long endTimeMs = 0;

  private HL7QuickParser quickParser = HL7QuickParser.INSTANCE;

  @JsonIgnore
  private ByteArrayOutputStream data;

  @JsonIgnore
  private ZipInputStream zipData;

  @JsonIgnore
  private HL7File hl7File = new HL7File();

  @JsonIgnore
  private List<String> ackMessages = new ArrayList<>();

  public ByteArrayOutputStream getData() {
    return data;
  }

  public FileUploadData() {
  }

  public FileUploadData(String facilityId, String fileName, String fileId, String username, InputStream is)
      throws IOException {
    this.facilityId = facilityId;
    this.fileName = fileName;
    this.fileId = fileId;
    this.username = username;
    this.readWebInput(is);
  }

  public void readInputIntoStrings() throws IOException {
    this.setStatus("reading");
    byte[] bytes = this.getData().toByteArray();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
    //These are to check if it's a zip file, and to use it yes:
    ZipInputStream zis = new ZipInputStream(inputStream);

    ZipEntry entry = zis.getNextEntry();

    if (entry == null) {
      //it's not a zip file.  process as text file.
      logger.info("Not a zip file!");
      this.hl7File.addMessagesFromInput(new ByteArrayInputStream(bytes));
    } else {
      //Process the first one.
      this.hl7File.addMessagesFromInput(zis);
      //Then the rest.
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          this.hl7File.addMessagesFromInput(zis);
        }
      }
    }

    zis = null;
    entry= null;

    this.data = null;
  }

  public void readWebInput(InputStream inputStream) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    int length;
    byte[] buffer = new byte[1024];

    while ((length = inputStream.read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }

    this.data =result;
  }

  public interface MessageAction {
    String doStuffWithThisMessage(String message, String optionalText, AuthenticationToken token)
        throws Exception ;
  }

  public void processHL7File(AuthenticationToken token, MessageAction takeAction) throws IOException {
    this.readInputIntoStrings();
    logger.info("\nFilename: " + this.getFileName() + "\n" + "Number of messages: " + this.getNumberOfMessages());
    this.startTimeMs = new Date().getTime();
    this.setStatus("started");

    //Is it possible to eliminate messages from the input set as they're processed?

    try {
      for (int idx = 0; idx < this.hl7File.getMessageList().size(); idx++) {
        String message = this.hl7File.getMessageList().get(idx);
        while (!"started".equals(this.getStatus())) {
          Thread.sleep(1000);
          logger.warn(
              "File " + fileId + " Stopped. Waiting for restart. Remaining Messages to process: "
                  + this.getNumberUnProcessed());
          if ("deleted".equals(this.getStatus())) {
            return;
          }
        }
        String sendingFacility = quickParser.getMsh4Sender(message);

        if (StringUtils.isBlank(sendingFacility)) {
          if (StringUtils.isBlank(token.getPrincipal().getFacilityId())) {
            sendingFacility = "Unspecified";
          } else {
            sendingFacility = token.getPrincipal().getFacilityId();
          }
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String result = takeAction.doStuffWithThisMessage(message, sendingFacility, token);
        stopWatch.stop();
        logger.warn("Message Processing Time: " + stopWatch.getTotalTimeMillis());
        this.hl7File.getMessageList().set(idx, null);
        //If the ack ends with a line break, remove it.
        if (result != null) {
          result = result.replaceAll("\\r$", "");
          this.ackMessages.add(result);
        }
      }
      this.setStatus("finished");
      this.endTimeMs = new Date().getTime();
    } catch (Exception e) {
      logger.error("Exception processing messages: " + e.getMessage());
      e.printStackTrace();
      this.setStatus("exception");
    }
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<String> getAckMessages() {
    return new ArrayList<>(this.ackMessages);
  }

  public String getFacilityId() {
    return facilityId;
  }

  public void setFacilityId(String facilityId) {
    this.facilityId = facilityId;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileId() {
    return fileId;
  }

  public int getNumberOfMessages() {
    return this.hl7File.getMessageList().size();
  }

  public int getNumberUnProcessed() {
    return this.hl7File.getMessageList().size() - this.ackMessages.size();
  }

  public int getNumberProcessed() {
    return this.ackMessages.size();
  }

  public int getPercentage() {
    if (getNumberOfMessages() == 0) {
      return 100;
    }
    return (int) (getNumberProcessed() * 100) / this.getNumberOfMessages();
  }
  
  public int getAverageElapsed() {
    if (getNumberProcessed() == 0) {
        return 0;
      }
      return (int) (getElapsedTimeMs() / getNumberProcessed());
  }

  public long getElapsedTimeMs() {
    return (endTimeMs == 0 ? new Date().getTime()
        : endTimeMs)
        - startTimeMs;
}
  
}
