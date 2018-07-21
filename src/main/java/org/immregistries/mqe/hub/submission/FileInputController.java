package org.immregistries.mqe.hub.submission;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.hl7util.parser.HL7QuickParser;
import org.immregistries.mqe.hub.rest.FileUploadData;
import org.immregistries.mqe.hub.rest.MessageInputController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/file")
@RestController
public class FileInputController {

  private static final Logger logger = LoggerFactory.getLogger(FileInputController.class);

  @Autowired
  private MessageInputController messageController;
  private Map<String, FileUploadData> fileQueue = new LinkedHashMap<>();

  private HL7QuickParser quickParser = HL7QuickParser.INSTANCE;

  private static final String MSH_REGEX = "^\\s*MSH\\|\\^~\\\\&\\|.*";
  private static final String FHS_BHS_REGEX = "^\\s*(FHS|BHS)\\|.*";
  private static final String HL7_SEGMENT_REGEX = "^\\w\\w\\w\\|.*";

  @RequestMapping(value = "upload-messages", method = RequestMethod.POST)
  public FileUploadData urlEncodedHttpFormFilePost(@RequestParam("file") MultipartFile file,
      String facilityId) throws Exception {

    InputStream inputStream = file.getInputStream();

    String fileId = "file" + String.valueOf(new Date().getTime());
    FileUploadData fileUpload = new FileUploadData(facilityId, file.getOriginalFilename(), fileId);

    this.fileQueue.put(fileId, fileUpload);

    //These are to check if it's a zip file, and to use it yes:
    ZipInputStream zis = new ZipInputStream(inputStream);
    ZipEntry entry = zis.getNextEntry();

    if (entry == null) {
      //it's not a zip file.  process as text file.
      logger.info("Not a zip file!");
      fileUpload.getHl7Messages().addAll(this.getMessagesFromInputStream(file.getInputStream()));
    } else {
      //Process the first one.
      fileUpload.getHl7Messages().addAll(getMessagesFromInputStream(zis));
      //Then the rest.
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          fileUpload.getHl7Messages().addAll(getMessagesFromInputStream(zis));
        }
      }
    }

    logger.info(
        "\nFilename: " + fileUpload.getFileName() + "\n" + "Number of messages: " + fileUpload
            .getNumberOfMessages() + "\n" + "Reported under: " + fileUpload);

    return fileUpload;
  }

  private List<String> getMessagesFromInputStream(InputStream inputStream) throws IOException {

    List<String> messages = new ArrayList<>();
    StringBuilder oneMessage = new StringBuilder();
    String line;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    while ((line = bufferedReader.readLine()) != null) {
      logger.info("Line: " + line);

      if (!line.matches(FHS_BHS_REGEX)) {
        if (line.matches(MSH_REGEX)) {
          if (oneMessage.length() <= 0) {
            oneMessage.append(line);
          } else {
            messages.add(oneMessage.toString());
            oneMessage.setLength(0);
            oneMessage.append(line);
          }
        } else {
          if (line.matches(HL7_SEGMENT_REGEX)) {
            oneMessage.append("\r");
            oneMessage.append(line);
          }
        }
      }
    }

    if (oneMessage.length() > 0) {
      messages.add(oneMessage.toString());
    }

    return messages;
  }

  @RequestMapping(value = "report-file", method = RequestMethod.GET)
  public FileUploadData reportFile(@RequestParam("fileId") String fileId) {
    return this.fileQueue.get(fileId);
  }


  @RequestMapping(value = "report-acks", method = RequestMethod.GET)
  public List<String> reportAcks(@RequestParam("fileId") String fileId) {
    FileUploadData ud = this.fileQueue.get(fileId);

    if (ud != null) {
      return ud.getAckMessages();
    }

    return new ArrayList<>();
  }

  @RequestMapping(value = "remove-file", method = RequestMethod.GET)
  public void removeFile(@RequestParam("fileId") String fileId) {

    FileUploadData data = this.fileQueue.get(fileId);

    if (data != null) {
      data.setStatus("deleted");
    }

    this.fileQueue.remove(fileId);
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
  public FileProcessingQueue getQueues() {
    FileProcessingQueue queue = new FileProcessingQueue();
    queue.getUploads().addAll(this.fileQueue.values());
    return queue;
  }

  @RequestMapping(value = "stop-file", method = RequestMethod.GET)
  public void stopFile(@RequestParam("fileId") String fileId) {
    FileUploadData fud = this.fileQueue.get(fileId);
    if (fud != null) {
      fud.setStatus("Stop");
    }
  }

  @RequestMapping(value = "unpause-file", method = RequestMethod.GET)
  public void unpauseFile(@RequestParam("fileId") String fileId) {
    FileUploadData fud = this.fileQueue.get(fileId);
    if (fud != null && fud.getPercentage() < 100) {
      fud.setStatus("started");
    }
  }

  @RequestMapping(value = "process-file", method = RequestMethod.POST)
  public FileUploadData processFile(@RequestParam("fileId") String fileId) throws Exception {
    FileUploadData fileUpload = this.fileQueue.get(fileId);

    if ("started".equals(fileUpload.getStatus())) {
      return fileUpload;
    }

    fileUpload.setStatus("started");

    try {
      for (String message : fileUpload.getHl7Messages()) {
        while (!"started".equals(fileUpload.getStatus())) {
          Thread.sleep(1000);
          logger.warn(
              "File " + fileId + " Stopped. Waiting for restart. Remaining Messages to process: "
                  + fileUpload.getNumberUnProcessed());
          if ("deleted".equals(fileUpload.getStatus())) {
            return fileUpload;
          }
        }
        String sender = quickParser.getMsh4Sender(message);
        
        if (StringUtils.isBlank(sender)) {
          sender = "Unspecified";
        }
        
        String ackResult = messageController
            .urlEncodedHttpFormPost(message, null, null, sender);

        //If the ack ends with a line break, remove it.
        ackResult = ackResult.replaceAll("\\r$", "");
        try {
          logger.info("Finding date from message:" + message);
          String msgDate = quickParser.getMsh7MessageDate(message);
          fileUpload.addDateMsg(msgDate);
        } catch (Exception e) {
          logger.error("Error finding date from message: " + message, e);
        }
        fileUpload.addAckMessage(ackResult);
      }
      fileUpload.setStatus("finished");
    } catch (Exception e) {
      logger.error("Exception processing messages: " + e.getMessage());
      e.printStackTrace();
      fileUpload.setStatus("exception");
    }

    return fileUpload;
  }
}

