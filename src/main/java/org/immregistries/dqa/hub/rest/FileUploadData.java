package org.immregistries.dqa.hub.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FileUploadData {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadData.class);

    private String status = "waiting";

    private String facilityId;
    private String fileName;
    private String fileId;

    private List<String> hl7Messages = new ArrayList<>();
    private List<String> ackMessages = new ArrayList<>();

    public FileUploadData() {
    }

    public FileUploadData(String facilityId, String fileName, String fileId) {
        this.facilityId = facilityId;
        this.fileName = fileName;
        this.fileId = fileId;
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
        return this.ackMessages;
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

    public void addVXUMessage(String message) {
        this.hl7Messages.add(message);
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
        return (int) (getNumberProcessed() * 100)  / getNumberOfMessages();
    }

}
