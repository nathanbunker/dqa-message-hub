package org.immregistries.mqe.hub.submission;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.core.util.DateUtility;
import org.immregistries.mqe.hl7util.Reportable;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hl7util.builder.AckBuilder;
import org.immregistries.mqe.hl7util.builder.AckData;
import org.immregistries.mqe.hl7util.model.Hl7Location;
import org.immregistries.mqe.hl7util.parser.HL7Reader;
import org.immregistries.mqe.hub.report.Sender;
import org.immregistries.mqe.hub.report.SenderJpaRepository;
import org.immregistries.mqe.hub.report.SenderMetricsService;
import org.immregistries.mqe.hub.report.viewer.MessageCode;
import org.immregistries.mqe.hub.report.viewer.MessageDetection;
import org.immregistries.mqe.hub.report.viewer.MessageMetadata;
import org.immregistries.mqe.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.mqe.hub.report.viewer.MessageVaccine;
import org.immregistries.mqe.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.mqe.hub.settings.DetectionsSettings;
import org.immregistries.mqe.hub.settings.DetectionsSettingsJpaRepository;
import org.immregistries.mqe.util.validation.MqeDetection;
import org.immregistries.mqe.validator.MqeMessageService;
import org.immregistries.mqe.validator.MqeMessageServiceResponse;
import org.immregistries.mqe.validator.detection.ValidationReport;
import org.immregistries.mqe.validator.engine.ValidationRuleResult;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.ReportScorer;
import org.immregistries.mqe.validator.report.codes.CodeCollection;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.vxu.MqeMessageHeader;
import org.immregistries.mqe.vxu.MqeVaccination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

//import org.immregistries.mqe.nist.validator.connector.Assertion;
//import org.immregistries.mqe.nist.validator.connector.NISTValidator;

@Service
public class Hl7MessageConsumer {

  private static final Logger logger = LoggerFactory.getLogger(Hl7MessageConsumer.class);

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
  private DetectionsSettingsJpaRepository detectionsSettingsRepo;
  @Autowired
  private IISGateway iisGatewayService;

  private static final HashMap<String, HashMap<String, SeverityLevel>> severityOverrides =
      new HashMap<>();

  public Hl7MessageHubResponse processMessage(Hl7MessageSubmission messageSubmission) {
    String message = messageSubmission.getMessage();

    //    StopWatch stopWatch = new StopWatch();
    //    stopWatch.start();
    String sender = validator.getSendingFacility(message);
    if (StringUtils.isBlank(sender)) {
      sender = "Unspecified";
    }
    //    stopWatch = new StopWatch();
    //    stopWatch.start();
    List<Reportable> nistReportableList = nistValidatorHandler.validate(message);
    //    stopWatch.stop();
    //    logger.warn("nistValidatorHandler.validate: " + stopWatch.getTotalTimeMillis());

    //    stopWatch = new StopWatch();
    //    stopWatch.start();
    HashMap<String, SeverityLevel> so = severityOverrides.get(sender);
    if (so == null) {
      so = retrieveDetectionOverrides(sender);
      severityOverrides.put(sender, so);
    }
    //    new HashMap<String, String>();
    //    stopWatch.stop();
    //    logger.warn("retrieveDetectionOverrides: " + stopWatch.getTotalTimeMillis());


    //    stopWatch = new StopWatch();
    //    stopWatch.start();
    //force serial processing...
    MqeMessageServiceResponse validationResults = validator.processMessage(message);
    //    stopWatch.stop();
    //    logger.warn("validator.processMessage: " + stopWatch.getTotalTimeMillis());

    //    stopWatch = new StopWatch();
    //    stopWatch.start();
    // apply sender level detection overrides
    applySenderDetectionOverrides(so, validationResults);
    //    stopWatch.stop();
    //    logger.warn("applySenderDetectionOverrides: " + stopWatch.getTotalTimeMillis());

    //    stopWatch = new StopWatch();
    //    stopWatch.start();
    String ack = makeAckFromValidationResults(validationResults, nistReportableList);
    //    stopWatch.stop();
    //    logger.warn("makeAckFromValidationResults: " + stopWatch.getTotalTimeMillis());

    Hl7MessageHubResponse response = new Hl7MessageHubResponse();
    response.setAck(ack);
    response.setMqeResponse(validationResults);
    response.setSender(sender);

    return response;
  }

  private void applySenderDetectionOverrides(HashMap<String, SeverityLevel> overrides,
      MqeMessageServiceResponse validationResults) {
    List<ValidationRuleResult> results = validationResults.getValidationResults();
    for (ValidationRuleResult res : results) {
      for (ValidationReport report : res.getValidationDetections()) {
        SeverityLevel slOverride = overrides.get(report.getDetection().getMqeMqeCode());
        if (slOverride != null) {
          report.setSeverityLevel(slOverride);
        }
      }
    }
  }

  // Possibly use sender facility to gather sender's detection config
  @Cacheable("detectionOverrides")
  public HashMap<String, SeverityLevel> retrieveDetectionOverrides(String sender) {
    HashMap<String, SeverityLevel> detectionsOverride = new HashMap<>();

    if (sender != null) {
      List<DetectionsSettings> settings = detectionsSettingsRepo.findByDetectionGroupName("sender");
      for (DetectionsSettings ds : settings) {
        String code = ds.getMqeCode();
        String severity = ds.getSeverity();
        SeverityLevel sl = SeverityLevel.valueOf(severity);
        detectionsOverride.put(code, sl);
      }
    }
    return detectionsOverride;
  }


  private SeverityLevel getDefaultSeverityByCode(String detectionProp, String mqeCode) {
    SeverityLevel severityLevel = null;

    DetectionsSettings ds =
        detectionsSettingsRepo.findByDetectionGroupNameAndMqeCode(detectionProp, mqeCode);
    if (ds != null) {
      severityLevel = SeverityLevel.findByLabel(ds.getSeverity());
    }
    return severityLevel;
  }


  public Hl7MessageHubResponse processMessageAndSaveMetrics(
      Hl7MessageSubmission messageSubmission) {
    //  StopWatch stopWatch = new StopWatch();
    //  stopWatch.start();
    Hl7MessageHubResponse response = this.processMessage(messageSubmission);
    //  stopWatch.stop();
    //  logger.warn("processMessage: " + stopWatch.getTotalTimeMillis());


    sendToIisGatewayIfEnabled(messageSubmission, response);

    if (!iisGatewayService.isIisgatewayEnable()
        || iisGatewayService.isIisgatewayPersistDataEnable()) {
      MqeMessageServiceResponse dqr = response.getMqeResponse();
      Date sentDate = dqr.getMessageObjects().getMessageHeader().getMessageDate();

      //  stopWatch = new StopWatch();
      //  stopWatch.start();
      this.saveMetricsFromValidationResults(response.getSender(), dqr, sentDate);
      //  stopWatch.stop();
      //  logger.warn("saveMetricsFromValidationResults: " + stopWatch.getTotalTimeMillis());
      //  stopWatch = new StopWatch();
      //  stopWatch.start();
      MessageMetadata mm = this.saveMessageForSender(messageSubmission.getMessage(),
          response.getAck(), response.getSender(), sentDate, response);
      //  stopWatch.stop();
      //  logger.warn("saveMessageForSender: " + stopWatch.getTotalTimeMillis());
    }

    return response;
  }

  public void sendToIisGatewayIfEnabled(Hl7MessageSubmission messageSubmission,
      Hl7MessageHubResponse response) {
    if (iisGatewayService.isIisgatewayEnable()) {
      if (messageSubmission != null && messageSubmission.getUser() != null
          && !messageSubmission.getUser().equals("")) {
        if (!iisGatewayService.isIisgatewayFilterErrorsEnable()
            || hasNoErrors(response.getMqeResponse().getValidationResults())) {
          //  stopWatch = new StopWatch();
          //  stopWatch.start();
          String responseMessageFromIIS = submitMessageToIIS(messageSubmission);
          if (responseMessageFromIIS != null) {
            if (iisGatewayService.isIisgatewayReturnAckIisEnable()) {
              if (!iisGatewayService.isIisgatewayReturnAckMqeEnable()) {
                response.setAck(responseMessageFromIIS);
              } else {
                String additionalERRsegments = "";
                HL7Reader readerIIS = new HL7Reader(responseMessageFromIIS);
                String ackCodeIis = "AA";
                if (readerIIS.advanceToSegment("MSA")) {
                  ackCodeIis = readerIIS.getValue(1);
                }
                while (readerIIS.advanceToSegment("ERR")) {
                  String errorCode = readerIIS.getValue(4);
                  if (ackCodeIis.equals("AA") && (errorCode.equals("E") || errorCode.equals("W"))) {
                    ackCodeIis = "AE";
                  }
                  additionalERRsegments += readerIIS.getOriginalSegment() + "\r";
                }

                String ack = new String();
                HL7Reader readerMQE = new HL7Reader(response.getAck());
                while (readerMQE.advance()) {
                  if (readerMQE.getSegmentName().equals("MSA")) {
                    String ackCodeMqe = readerMQE.getValue(1);
                    if (ackCodeMqe.equals("AA") && !ackCodeIis.equals("AA")) {
                      ackCodeMqe = ackCodeIis;
                    }
                    ack += "MSA|" + ackCodeMqe + "|" + readerMQE.getValue(2) + "|\r";
                  } else {
                    ack += readerMQE.getOriginalSegment() + "\r";
                  }
                }
                if (ackCodeIis.equals("AA")) {
                  ack +=
                      "ERR|||0^Message Accepted^HL70357|I||||Message was forwarded to IIS and was accepted|\r";
                } else if (ackCodeIis.equals("AA")) {
                  ack +=
                      "ERR|||207^Application Internal Error^HL70357|I||||Message was forwarded to IIS and but it was not accepted|\r";
                }
                ack += additionalERRsegments;
                response.setAck(ack);
              }
            }
          }
          //  stopWatch.stop();
          //  logger.warn("submitMessageToIIS: " + stopWatch.getTotalTimeMillis());
        }
      }
    }
  }

  private static boolean hasNoErrors(List<ValidationRuleResult> validationRuleResultList) {
    for (ValidationRuleResult validationRuleResult : validationRuleResultList) {
      for (Reportable reportable : validationRuleResult.getValidationDetections()) {
        if (reportable.getSeverity() == SeverityLevel.ERROR) {
          return false;
        }
      }
    }
    return true;
  }


  private String submitMessageToIIS(Hl7MessageSubmission messageSubmission) {
    return iisGatewayService.sendVXU(messageSubmission);
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

  @Autowired
  SenderJpaRepository senderRepo;

  private MessageMetadata saveMessageForSender(String message, String ack, String sender,
      Date sentDate, Hl7MessageHubResponse response) {
    MessageMetadata mm = new MessageMetadata();

    Sender s = senderRepo.findByName(sender);

    if (s == null) {
      s = new Sender();
      s.setName(sender);
      s.setCreatedDate(new Date());
      senderRepo.save(s);
    }

    //for demo day, let's make a random date in the last month.
    mm.setInputTime(sentDate);
    message = message.replaceAll("\\n\\r", "\\r");
    mm.setMessage(message);
    mm.setResponse(ack);
    mm.setSender(s);

    for (ValidationRuleResult rr : response.getMqeResponse().getValidationResults()) {
      for (ValidationReport vr : rr.getValidationDetections()) {
        MqeDetection d = vr.getDetection();
        if (SeverityLevel.ERROR == d.getSeverity() || SeverityLevel.WARN == d.getSeverity()) {
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
      if (!mv.isAdministered()) {
        continue;
      }
      Date date = mv.getAdminDate();
      Date birthdate = response.getMqeResponse().getMessageObjects().getPatient().getBirthDate();
      int age = DateUtility.INSTANCE.getYearsBetween(birthdate, date);
      mm.setPatientAge(age);
      String cvx = mv.getCvxDerived();
      String key = cvx + ":" + age;
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
