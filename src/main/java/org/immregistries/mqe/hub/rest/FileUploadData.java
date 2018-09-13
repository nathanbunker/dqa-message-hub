package org.immregistries.mqe.hub.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadData {

  private static final Logger logger = LoggerFactory.getLogger(FileUploadData.class);

  private String status = "waiting";

  private String facilityId;
  private String fileName;
  private String fileId;
  private long startTimeMs = 1;
  private long endTimeMs = 0;

  @JsonIgnore
  private Map<String, Integer> messageDates = new HashMap<>();

  @JsonIgnore
  private ByteArrayOutputStream data;

  @JsonIgnore
  private ZipInputStream zipData;

  @JsonIgnore
  private List<String> hl7Messages = new ArrayList<>();

  @JsonIgnore
  private List<String> ackMessages = new ArrayList<>();

  public ByteArrayOutputStream getData() {
    return data;
  }

  public void setData(ByteArrayOutputStream input) {
    this.data = input;
  }

  public FileUploadData() {
  }

  public FileUploadData(String facilityId, String fileName, String fileId) {
    this.facilityId = facilityId;
    this.fileName = fileName;
    this.fileId = fileId;
  }

  public Map<String, Integer> getMessageDates() {
    return messageDates;
  }

  public void addDateMsg(String date) {
    Integer i = this.messageDates.get(date);
    if (i == null) {
      i = 0;
    }
    this.messageDates.put(date, ++i);
  }


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<String> getHl7Messages() {
    return this.hl7Messages;
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

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public void addAckMessage(String message) {
    this.ackMessages.add(message);
  }

  public int getNumberOfMessages() {
    return this.hl7Messages.size();
  }

  public int getNumberUnProcessed() {
    return this.hl7Messages.size() - this.ackMessages.size();
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
  
  public long getStartTimeMs() {
		return startTimeMs;
	}

	public void setStartTimeMs(long startTimeMs) {
		this.startTimeMs = startTimeMs;
	}

  public ZipInputStream getZipData() {
    return zipData;
  }

  public void setZipData(ZipInputStream zipData) {
    this.zipData = zipData;
  }

  public long getEndTimeMs() {
    return endTimeMs;
  }

  public void setEndTimeMs(long endTimeMs) {
    this.endTimeMs = endTimeMs;
  }
}
