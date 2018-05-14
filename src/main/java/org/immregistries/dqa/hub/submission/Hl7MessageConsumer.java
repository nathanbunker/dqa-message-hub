package org.immregistries.dqa.hub.submission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.hl7util.builder.AckBuilder;
import org.immregistries.dqa.hl7util.builder.AckData;
import org.immregistries.dqa.hub.report.SenderMetricsService;
import org.immregistries.dqa.hub.report.viewer.MessageMetadata;
import org.immregistries.dqa.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.dqa.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.validator.DqaMessageService;
import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.immregistries.dqa.validator.report.ReportScorer;
import org.immregistries.dqa.vxu.DqaMessageHeader;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import org.immregistries.dqa.nist.validator.connector.Assertion;
//import org.immregistries.dqa.nist.validator.connector.NISTValidator;

@Service
public class Hl7MessageConsumer {

  private NistValidatorService nistValidatorService = NistValidatorService.INSTANCE;
  private DqaMessageService validator = DqaMessageService.INSTANCE;
  private ReportScorer scorer = ReportScorer.INSTANCE;
  @Autowired
  private SenderMetricsService metricsSvc;
  @Autowired
  private MessageMetadataJpaRepository metaRepo;


  public Hl7MessageHubResponse processMessage(Hl7MessageSubmission messageSubmission) {
    String message = messageSubmission.getMessage();

    String sender = messageSubmission.getFacilityCode();
    if (sender == null) {
      sender = "MQE";
    }
    
    List<Reportable> nistReportableList = nistValidatorService.validate(message);

    //force serial processing...
    DqaMessageServiceResponse validationResults = validator.processMessage(message);

    String ack = makeAckFromValidationResults(validationResults, nistReportableList);

    Hl7MessageHubResponse response = new Hl7MessageHubResponse();
    response.setAck(ack);
    response.setDqaResponse(validationResults);
    response.setSender(sender);

    return response;
  }

  public Hl7MessageHubResponse processMessageAndSaveMetrics(
      Hl7MessageSubmission messageSubmission) {
    Hl7MessageHubResponse response = this.processMessage(messageSubmission);
    DqaMessageServiceResponse dqr = response.getDqaResponse();
    Date sentDate = dqr.getMessageObjects().getMessageHeader().getMessageDate();
    this.saveMetricsFromValidationResults(response.getSender(), dqr, sentDate);
    this.saveMessageForSender(messageSubmission.getMessage(), response.getAck(),
        response.getSender(), sentDate);
    return response;
  }

  int daysSpread = 60;

  Date getRandomDate() {
    DateTime dt = new DateTime();
    int randomDays = (int) Math.floor(Math.random() * daysSpread);
    DateTime thisDate = dt.minusDays(randomDays);
    if (thisDate.getDayOfWeek() >= 6) {
      int randomWeekDay = (int) Math.floor(Math.random() * 5) + 1;
      int day = thisDate.getDayOfWeek();
      if (day == 7) {
        randomWeekDay = randomWeekDay - 1;
      }
      thisDate = thisDate.minusDays(randomWeekDay);
    }

    Date sentDate = thisDate.toDate();
    return sentDate;
  }

  public void saveMessageForSender(String message, String ack, String sender, Date sentDate) {
    MessageMetadata mm = new MessageMetadata();
    //for demo day, let's make a random date in the last month.
    mm.setInputTime(sentDate);
    message = message.replaceAll("\\n\\r", "\\r");
    mm.setMessage(message);
    mm.setResponse(ack);
    mm.setProvider(sender);
    metaRepo.save(mm);
  }

  public void saveMetricsFromValidationResults(String sender,
      DqaMessageServiceResponse validationResults, Date metricsDate) {
    DqaMessageMetrics metrics = scorer.getDqaMetricsFor(validationResults);
    metricsSvc.addToSenderMetrics(sender, metricsDate, metrics);
  }

  public String makeAckFromValidationResults(DqaMessageServiceResponse validationResults,
      List<Reportable> nistReportablesList) {

    List<ValidationRuleResult> resultList = validationResults.getValidationResults();
    List<Reportable> reportables = new ArrayList<Reportable>(nistReportablesList);
    //This code needs to get put somewhere better.
    for (ValidationRuleResult result : resultList) {
      reportables.addAll(result.getValidationDetections());
    }

    AckBuilder ackBuilder = AckBuilder.INSTANCE;

    AckData data = new AckData();
    DqaMessageHeader header = validationResults.getMessageObjects().getMessageHeader();
    data.setMessageControlId(header.getMessageControl());
    data.setMessageDate(header.getMessageDate());
    data.setMessageProfileId(header.getMessageProfile());
    data.setMessageVersionId(header.getMessageVersion());
    data.setProcessingControlId(header.getProcessingStatus());
    data.setReceivingApplication("MQE Message Hub App");
    data.setReceivingFacility("MQE Facility");
    data.setSendingFacility(header.getSendingFacility());
    data.setSendingApplication(header.getSendingApplication());
    data.setResponseType("?");
    data.setReportables(reportables);

    String ack = ackBuilder.buildAckFrom(data);
    return ack;
  }
}
