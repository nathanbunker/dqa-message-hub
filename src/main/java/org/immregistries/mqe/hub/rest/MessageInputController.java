package org.immregistries.mqe.hub.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.immregistries.mqe.hl7util.test.MessageGenerator;
import org.immregistries.mqe.hub.report.FileResponse;
import org.immregistries.mqe.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.mqe.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.mqe.hub.submission.Hl7MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/messages")
@RestController
public class MessageInputController {

  private static final Logger logger = LoggerFactory.getLogger(MessageInputController.class);

  @Autowired
  private Hl7MessageConsumer messageConsumer;

  @Autowired
  MessageMetadataJpaRepository metaRepo;

  @Autowired
  private Hl7MessageConsumer msgr;

  @RequestMapping(value = "in", method = RequestMethod.POST)
  public Hl7MessageHubResponse scoreMessageAndPersist(@RequestBody Hl7MessageSubmission submission)
      throws Exception {
    logger.info("ReportController scoreMessage demo!");
    logger.info("processing this message: [" + submission.getMessage() + "]");
    Hl7MessageHubResponse ack = msgr.processMessageAndSaveMetrics(submission);
    return ack;
  }

  @Transactional
  @RequestMapping(value = "form-standard", method = RequestMethod.POST)
  public String urlEncodedHttpFormPost(
      String MESSAGEDATA, String USERID, String PASSWORD, String FACILITYID) throws Exception {

    String response = "hl7 message interface endpoint! message: " + MESSAGEDATA + " user: " + USERID
        + " password: " + PASSWORD + " facilityId: " + FACILITYID;
    logger.info(response);

    Hl7MessageSubmission messageSubmission = new Hl7MessageSubmission();
    messageSubmission.setMessage(MESSAGEDATA);
    messageSubmission.setUser(USERID);
    messageSubmission.setPassword(PASSWORD);
    messageSubmission.setFacilityCode(FACILITYID);

    String ack = messageConsumer.processMessageAndSaveMetrics(messageSubmission).getAck();
    return ack;
  }

  @RequestMapping(value = "msg-result", method = RequestMethod.POST)
  public FileResponse messageResults(@RequestBody ArrayList<String> me) {

    FileResponse fr = new FileResponse();

    for (int i = 0; i < me.size(); i++) {
      fr.addAckMessage(me.get(i));
      if (me.get(i).contains("|AA|")) {
        fr.setAa_count(fr.getAa_count() + 1);
      } else if (me.get(i).contains("|AE|")) {
        fr.setAe_count(fr.getAe_count() + 1);
      }
    }

    fr.setResponseMessage(
        "There are " + (me.size() / 2) + " messages. # of AA: " + fr.getAa_count() + " # of AE: "
            + fr.getAe_count());

    return fr;
  }

  @RequestMapping(value = "msg-download", method = RequestMethod.POST)
  public void fileDownload(@RequestBody ArrayList<String> acks) {
    FileWriter fw;

    try {
      File file = new File("C:\\dev\\MQE\\mqe-message-hub\\download\\acks.txt");
      fw = new FileWriter(file);

      for (int i = 1; i < acks.size(); i += 2) {
        fw.write(acks.get(i));
      }
      fw.close();
      System.out.printf("File is located at %s%n", file.getAbsolutePath());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @RequestMapping(value = "json", method = RequestMethod.POST)
  public Hl7MessageHubResponse jsonFormPost(@RequestBody Hl7MessageSubmission submission) {
    logger.info("hl7 message interface endpoint! message: " + submission.getMessage() + " user: "
        + submission.getUser() + " password: " + submission.getPassword() + " facilityId: "
        + submission.getFacilityCode());

    String vxu = submission.getMessage();

    if (vxu != null) {
      Hl7MessageHubResponse response = messageConsumer.processMessageAndSaveMetrics(submission);
      vxu = vxu.replaceAll("[\\r]+", "\n");
      response.setVxu(vxu);

      //process the ack to display right on the web:
      String ack = response.getAck();
      if (ack != null) {
        ack = ack.replaceAll("[\\r]+", "\n");
      }
      response.setAck(ack);
      logger.info("ACK: \n" + ack);
      return response;
    } else {
      Hl7MessageHubResponse response = new Hl7MessageHubResponse();
      response.setSender(submission.getFacilityCode());
      response.setAck("INVALID REQUEST:  VXU is empty");
      return response;
    }
  }

  @RequestMapping(value = "json/notsaved", method = RequestMethod.POST)
  public Hl7MessageHubResponse jsonFormPostNotSaved(@RequestBody Hl7MessageSubmission submission) {
    logger.info("jsonFormPostNotSaved! message: " + submission.getMessage() + " user: "
        + submission.getUser() + " password: " + submission.getPassword() + " facilityId: "
        + submission.getFacilityCode());

    String vxu = submission.getMessage();

    if (vxu != null) {
      Hl7MessageHubResponse response = messageConsumer.processMessage(submission);
      vxu = vxu.replaceAll("[\\r]+", "\n");
      response.setVxu(vxu);

      //process the ack to display right on the web:
      String ack = response.getAck();
      if (ack != null) {
        ack = ack.replaceAll("[\\r]+", "\n");
      }
      response.setAck(ack);
      logger.info("ACK: \n" + ack);
      return response;
    } else {
      Hl7MessageHubResponse response = new Hl7MessageHubResponse();
      response.setSender(submission.getFacilityCode());
      response.setAck("INVALID REQUEST:  VXU is empty");
      return response;
    }
  }

  @RequestMapping(value = "json/example", method = RequestMethod.GET)
  public Hl7MessageSubmission getExampleJsonFormPost() {
    logger.info("Getting example!");
    Hl7MessageSubmission example = new Hl7MessageSubmission();
    example.setMessage(MessageGenerator.INSTANCE.getUniqueMessage());
    example.setUser("regularUser");
    example.setPassword("password123");
    return example;
  }

}
