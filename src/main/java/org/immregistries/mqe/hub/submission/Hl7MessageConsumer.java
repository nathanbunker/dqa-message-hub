package org.immregistries.mqe.hub.submission;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immregistries.mqe.core.util.DateUtility;
import org.immregistries.mqe.hl7util.Reportable;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hl7util.builder.AckBuilder;
import org.immregistries.mqe.hl7util.builder.AckData;
import org.immregistries.mqe.hl7util.model.Hl7Location;
import org.immregistries.mqe.hub.report.SenderMetricsService;
import org.immregistries.mqe.hub.report.viewer.MessageCode;
import org.immregistries.mqe.hub.report.viewer.MessageDetection;
import org.immregistries.mqe.hub.report.viewer.MessageMetadata;
import org.immregistries.mqe.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.mqe.hub.report.viewer.MessageVaccine;
import org.immregistries.mqe.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.mqe.validator.MqeMessageService;
import org.immregistries.mqe.validator.MqeMessageServiceResponse;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.ValidationReport;
import org.immregistries.mqe.validator.engine.ValidationRuleResult;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.ReportScorer;
import org.immregistries.mqe.validator.report.codes.CodeCollection;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.vxu.MqeMessageHeader;
import org.immregistries.mqe.vxu.MqeVaccination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import org.immregistries.mqe.nist.validator.connector.Assertion;
//import org.immregistries.mqe.nist.validator.connector.NISTValidator;

@Service
public class Hl7MessageConsumer {

  @Autowired
  private NistValidatorService nistValidatorService;
  private MqeMessageService validator = MqeMessageService.INSTANCE;
  private ReportScorer scorer = ReportScorer.INSTANCE;
  @Autowired
  private SenderMetricsService metricsSvc;
  @Autowired
  private MessageMetadataJpaRepository metaRepo;
  @Autowired
  NistValidatorHandler nistValidatorHandler;
  @Autowired
  private IISGateway iisGatewayService;


  public Hl7MessageHubResponse processMessage(Hl7MessageSubmission messageSubmission) {
    String message = messageSubmission.getMessage();

    String sender = messageSubmission.getFacilityCode();
    if (sender == null) {
      sender = "MQE";
    }

    List<Reportable> nistReportableList = nistValidatorHandler.validate(message);

    //force serial processing...
    MqeMessageServiceResponse validationResults = validator.processMessage(message);

    String ack = makeAckFromValidationResults(validationResults, nistReportableList);

    Hl7MessageHubResponse response = new Hl7MessageHubResponse();
    response.setAck(ack);
    response.setMqeResponse(validationResults);
    response.setSender(sender);

    return response;
  }

  public Hl7MessageHubResponse processMessageAndSaveMetrics(
      Hl7MessageSubmission messageSubmission) {
    Hl7MessageHubResponse response = this.processMessage(messageSubmission);
	submitMessageToIIS(messageSubmission);
    MqeMessageServiceResponse dqr = response.getMqeResponse();
    Date sentDate = dqr.getMessageObjects().getMessageHeader().getMessageDate();
    this.saveMetricsFromValidationResults(response.getSender(), dqr, sentDate);
    MessageMetadata mm = this
        .saveMessageForSender(messageSubmission.getMessage(), response.getAck(),
            response.getSender(), sentDate, response);

    return response;
  }
  
  private void submitMessageToIIS(Hl7MessageSubmission messageSubmission) {
	iisGatewayService.sendVXU(messageSubmission);	
  }

//  int daysSpread = 60;
//  Date getRandomDate() {
//    DateTime dt = new DateTime();
//    int randomDays = (int) Math.floor(Math.random() * daysSpread);
//    DateTime thisDate = dt.minusDays(randomDays);
//    if (thisDate.getDayOfWeek() >= 6) {
//      int randomWeekDay = (int) Math.floor(Math.random() * 5) + 1;
//      int day = thisDate.getDayOfWeek();
//      if (day == 7) {
//        randomWeekDay = randomWeekDay - 1;
//      }
//      thisDate = thisDate.minusDays(randomWeekDay);
//    }
//
//    Date sentDate = thisDate.toDate();
//    return sentDate;
//  }

  private MessageMetadata saveMessageForSender(String message, String ack, String sender,
      Date sentDate, Hl7MessageHubResponse response) {
    MessageMetadata mm = new MessageMetadata();
    //for demo day, let's make a random date in the last month.
    mm.setInputTime(sentDate);
    message = message.replaceAll("\\n\\r", "\\r");
    mm.setMessage(message);
    mm.setResponse(ack);
    mm.setProvider(sender);

    for (ValidationRuleResult rr : response.getMqeResponse().getValidationResults()) {
      for (ValidationReport vr : rr.getValidationDetections()) {
        Detection d = vr.getDetection();
        if (SeverityLevel.ERROR == d.getSeverity() ||
            SeverityLevel.WARN == d.getSeverity()) {
          StringBuilder locBldr = new StringBuilder();
          for (Hl7Location loc : vr.getHl7LocationList()) {
            locBldr.append(loc.toString());
          }
          String loc = locBldr.toString();
          MessageDetection md = new MessageDetection();
          md.setDetectionId(d.getMqeMqeCode());
          md.setLocationTxt(loc);
          md.setMessageMetadata(mm);
          mm.getDetections().add(md);
        }
      }
    }

    MqeMessageMetrics metrics = scorer.getMqeMetricsFor(response.getMqeResponse());
    CodeCollection c = metrics.getCodes();
    for (CollectionBucket cb : c.getCodeCountList()) {
      MessageCode mc = new MessageCode();
      mc.setCodeCount(cb.getCount());
      mc.setCodeType(cb.getTypeCode());
      mc.setCodeValue(cb.getValue());
      mc.setCodeStatus(cb.getStatus());
      mc.setMessageMetadata(mm);
      mm.getCodes().add(mc);
    }
    
    Map<String, MessageVaccine> map = new HashMap<>();
    for (MqeVaccination mv : response.getMqeResponse().getMessageObjects().getVaccinations()) {
    	if (!mv.isAdministered()){
    		continue;
    	}
    	Date date = mv.getAdminDate();
    	Date birthdate = response.getMqeResponse().getMessageObjects().getPatient().getBirthDate();
    	int age = DateUtility.INSTANCE.getYearsBetween(birthdate, date);
    	String cvx = mv.getCvxDerived();
    	String key = cvx+":"+age;
    	MessageVaccine v = map.get(key);
    	if (v == null) {
    		v = new MessageVaccine();
    		v.setCount(0);
    		v.setMessageMetadata(mm);
    		v.setVaccineCvx(cvx);
    		v.setAge(age);
    		mm.getVaccines().add(v);
    		map.put(key, v);
    	}
    	v.setCount(v.getCount() + 1);
    }
    metaRepo.save(mm);

    return mm;
  }

  private MqeMessageMetrics saveMetricsFromValidationResults(String sender,
      MqeMessageServiceResponse validationResults, Date metricsDate) {
    MqeMessageMetrics metrics = scorer.getMqeMetricsFor(validationResults);
    metricsSvc.addToSenderMetrics(sender, metricsDate, metrics);
    return metrics;
  }

  private String makeAckFromValidationResults(MqeMessageServiceResponse validationResults,
      List<Reportable> nistReportablesList) {

    List<ValidationRuleResult> resultList = validationResults.getValidationResults();
    List<Reportable> reportables = new ArrayList<>(nistReportablesList);
    /* This code needs to get put somewhere better. */
    for (ValidationRuleResult result : resultList) {
      reportables.addAll(result.getValidationDetections());
    }

    AckBuilder ackBuilder = AckBuilder.INSTANCE;

    AckData data = new AckData();
    MqeMessageHeader header = validationResults.getMessageObjects().getMessageHeader();
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

    return ackBuilder.buildAckFrom(data);
  }
}
