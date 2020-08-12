package org.immregistries.mqe.hub.submission;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.rest.FileUploadData;
import org.immregistries.mqe.hub.rest.MessageInputController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

public class HL7File {

  private static final Logger logger = LoggerFactory.getLogger(HL7File.class);
  private static final String MSH_REGEX = "^\\s*MSH\\|\\^~\\\\&\\|.*";
  private static final String FHS_BHS_REGEX = "^\\s*(FHS|BHS)\\|.*";
  private static final String HL7_SEGMENT_REGEX = "^\\w\\w\\w\\|.*";

  private final List<String> messageList = new ArrayList<>();
  public List<String> getMessageList() {
    return messageList;
  }

  public void addMessagesFromInput(InputStream inputStream) throws IOException {
    this.messageList.addAll(this.getMessagesFromInputStream(inputStream));
  }

  private List<String> getMessagesFromInputStream(InputStream inputStream) throws IOException {
    List<String> messages = new ArrayList<>();
    StringBuilder oneMessage = new StringBuilder();
    String line;
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

    while ((line = bufferedReader.readLine()) != null) {

      if (logger.isDebugEnabled()) {
        logger.debug("Line: " + line);
      }

      if (!line.matches(FHS_BHS_REGEX)) {
        if (line.matches(MSH_REGEX)) {
          /* replace any white space at the beginning of the line.
           * This will eliminate issues with unusual message separators.
           * We were seeing "VT" and "FS" - vertical tab and file separator
           */
          line = line.replaceAll("^\\s+MSH", "MSH");

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
}

