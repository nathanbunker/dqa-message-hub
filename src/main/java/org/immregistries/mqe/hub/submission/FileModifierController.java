package org.immregistries.mqe.hub.submission;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.rest.FileUploadData;
import org.immregistries.mqe.hub.rest.FileUploadData.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/api/modify")
@RestController
public class FileModifierController {
  private static final Logger logger = LoggerFactory.getLogger(FileModifierController.class);
  private Map<String, FileUploadData> fileQueue = new LinkedHashMap<>();

  @RequestMapping(value = "upload-messages", method = RequestMethod.POST)
  public FileUploadData urlEncodedHttpFormFilePost(@RequestParam("file") MultipartFile file,
      String facilityId, AuthenticationToken token) throws Exception {
    String fileId = "file" + new Date().getTime();
    FileUploadData fileUpload = new FileUploadData(facilityId, file.getOriginalFilename(), fileId, token.getPrincipal().getUsername(), file.getInputStream());
    this.fileQueue.put(fileId, fileUpload);
    return fileUpload;
  }

  @RequestMapping(value = "report-file", method = RequestMethod.GET)
  public FileUploadData reportFile(@RequestParam("fileId") String fileId, AuthenticationToken token) {
    FileUploadData data = this.fileQueue.get(fileId);
    if(data != null && data.getUsername().equals(token.getPrincipal().getUsername())) {
      return data;
    }
    return null;
  }


  @RequestMapping(value = "report-acks", method = RequestMethod.GET)
  public List<String> reportAcks(@RequestParam("fileId") String fileId, AuthenticationToken token) {
    FileUploadData ud = this.fileQueue.get(fileId);

    if (ud != null && ud.getUsername().equals(token.getPrincipal().getUsername())) {
      return ud.getAckMessages();
    }

    return new ArrayList<>();
  }

  @RequestMapping(value = "remove-file", method = RequestMethod.GET)
  public void removeFile(@RequestParam("fileId") String fileId, AuthenticationToken token) {

    FileUploadData data = this.fileQueue.get(fileId);

    if (data != null && data.getUsername().equals(token.getPrincipal().getUsername())) {
      data.setStatus("deleted");
      this.fileQueue.remove(fileId);
    }


  }

  private class FileProcessingQueue {

    List<FileUploadData> uploads = new ArrayList<>();

    public List<FileUploadData> getUploads() {
      return uploads;
    }

    public void setUploads(List<FileUploadData> uploads) {
      this.uploads = uploads;
    }
  }

  @RequestMapping(value = "get-queues", method = RequestMethod.GET)
  public FileProcessingQueue getQueues(AuthenticationToken token) {
    FileProcessingQueue queue = new FileProcessingQueue();
    for(FileUploadData data : this.fileQueue.values()) {
      if(data.getUsername().equals(token.getPrincipal().getUsername())) {
        queue.getUploads().add(data);
      }
    }
    return queue;
  }

  @RequestMapping(value = "stop-file", method = RequestMethod.GET)
  public void stopFile(@RequestParam("fileId") String fileId, AuthenticationToken token) {
    FileUploadData fud = this.fileQueue.get(fileId);
    if (fud != null && fud.getUsername().equals(token.getPrincipal().getUsername())) {
      fud.setStatus("Stop");
      if (fud.getNumberUnProcessed() <= 0) {
        fud.setStatus("finished");
      }
    }
  }

  @RequestMapping(value = "unpause-file", method = RequestMethod.GET)
  public void unpauseFile(@RequestParam("fileId") String fileId, AuthenticationToken token) {
    FileUploadData fud = this.fileQueue.get(fileId);
    if (fud != null && fud.getUsername().equals(token.getPrincipal().getUsername()) && fud.getPercentage() < 100) {
      fud.setStatus("started");
    }
  }


  private final MessageAction messageModifier = new FileUploadData.MessageAction () {
    @Override
    public String doStuffWithThisMessage(String message, String transformText, AuthenticationToken token) throws Exception {
      //NATHAN... Put your file modifier stuff here.
      return "MSH|... {this would be an anonymized message}";
    }
  };

  @RequestMapping(value = "process-file", method = RequestMethod.POST)
  public FileUploadData processFile(@RequestParam("fileId") String fileId, AuthenticationToken token) throws Exception {
    FileUploadData fileUpload = this.fileQueue.get(fileId);
    if(fileUpload == null || !fileUpload.getUsername().equals(token.getPrincipal().getUsername())) {
      return null;
    }
    if ("started".equals(fileUpload.getStatus())) {
      return fileUpload;
    }
    fileUpload.processHL7File(token, messageModifier);
    return fileUpload;
  }
}
