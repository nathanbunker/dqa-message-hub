package org.immregistries.dqa.hub.submission;

import org.immregistries.dqa.hub.rest.FileUploadData;
import org.immregistries.dqa.hub.rest.MessageInputController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestMapping(value = "/file")
@RestController public class FileInputController {

    private static final Logger logger = LoggerFactory.getLogger(FileInputController.class);

    @Autowired private MessageInputController messageController;

    private Map<String, FileUploadData> fileQueue = new HashMap<>();

    private static final String MSH_REGEX = "^MSH\\|.*";

    @RequestMapping(value = "upload-messages", method = RequestMethod.POST)
    public FileUploadData urlEncodedHttpFormFilePost(@RequestParam("file") MultipartFile file,
        String facilityId) throws Exception {

        InputStream inputStream = file.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String fileId = "file" + String.valueOf(new Date().getTime());
        FileUploadData fileUpload = new FileUploadData(facilityId, file.getOriginalFilename(), fileId);
        this.fileQueue.put(fileId, fileUpload);

        String line;
        String oneMessage = "";

        while ((line = bufferedReader.readLine()) != null) {
            if (line.matches(MSH_REGEX)) {
                if (oneMessage.equals("")) {
                    oneMessage = line;
                } else {
                    fileUpload.addVXUMessage(oneMessage);
                    oneMessage = line;
                }
            } else {
                oneMessage = oneMessage.concat("\r\n");
                oneMessage = oneMessage.concat(line);
            }
            logger.info(line);
        }
        fileUpload.addVXUMessage(oneMessage);
        logger.info("Filename: " + fileUpload.getFileName() + "\n" + "Number of messages: " + fileUpload.getNumberOfMessages() + "\n" + "Reported under: "
            + fileUpload);

        return fileUpload;
    }

    @RequestMapping(value = "report-file", method = RequestMethod.GET)
    public FileUploadData reportFile(@RequestParam("fileId") String fileId) {
        return this.fileQueue.get(fileId);
    }

    @RequestMapping(value = "stop-file", method = RequestMethod.POST) public void stopFile(@RequestParam("fileId") String fileId) {
        this.fileQueue.get(fileId).setStatus("Stop");
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

                if (!"started".equals(fileUpload.getStatus())) {
                    return fileUpload;
                } else {
                    logger.info("File " + fileId + " Stopped. Remaining Messages to process: " + fileUpload.getNumberUnProcessed());
                }

                String ackResult = messageController.urlEncodedHttpFormPost(message, null, null, fileUpload.getFacilityId());
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
