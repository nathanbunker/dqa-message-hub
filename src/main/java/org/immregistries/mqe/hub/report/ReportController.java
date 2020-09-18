package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.report.FacilitySummaryReport.PatientSummary;
import org.immregistries.mqe.hub.report.viewer.*;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.mqe.hub.settings.DetectionSeverityOverride;
import org.immregistries.mqe.hub.settings.DetectionSeverityJpaRepository;
import org.immregistries.mqe.hub.submission.Hl7MessageConsumer;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.ReportScorer;
import org.immregistries.mqe.validator.report.ScoreReportable;
import org.immregistries.mqe.validator.report.VxuScoredReport;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/report")
@RestController
public class ReportController {

  private static final Log logger = LogFactory.getLog(ReportController.class);

  @Autowired
  ProviderReportJdbcService providerReportJdbcService;

  @Autowired
  MessageHistoryJdbcRepository repo;

  @Autowired
  MessagesViewJpaRepository mvRepo;

  @Autowired
  private CodeCollectionService codeCollectionService;

  @Autowired
  MessageRetrieverService messageRetreiver;

  @Autowired
  private Hl7MessageConsumer msgr;

  @Autowired
  private FacilityMessageCountsService metricsSvc;
  
  @Autowired
  private DetectionSeverityJpaRepository detectionsSettingsRepo;

  private ReportScorer scorer = ReportScorer.INSTANCE;

  @RequestMapping(value = "/demo")
  public VxuScoredReport exampleReport() throws Exception {
    logger.info("ReportController exampleReport demo!");
    VxuScoredReport report = scorer.getDefaultReportForMessage(testMessage);
    return report;
  }

  @RequestMapping(value = "/message", method = RequestMethod.POST)
  public VxuScoredReport scoreMessage(@RequestBody Hl7MessageSubmission submission, AuthenticationToken token)
      throws Exception {
    logger.info("ReportController scoreMessage demo! sender:" + submission.getFacilityCode());
    String submitter = submission.getFacilityCode();

    if (StringUtils.isBlank(submitter)) {
      submission.setFacilityCode("MQE");
    }

    msgr.processMessageAndSaveMetrics(submission, token.getPrincipal().getUsername());

    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(submitter, new Date(), token.getPrincipal().getUsername());
    VxuScoredReport report = scorer.getDefaultReportForMetrics(allDaysMetrics);
    return report;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{providerKey}/date/{dateStart}/{dateEnd}")
  public VxuScoredReport getReportFor(@PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date date,  @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd, AuthenticationToken token) {
    logger.info("ReportController get report! sender:" + providerKey + " date: " + date);
    return this.getScoredReportAndOverrideDefaults(providerKey, date, dateEnd, token.getPrincipal().getUsername());
  }


  @RequestMapping(method = RequestMethod.GET, value = "/example/detection/{mqeCode}/{providerKey}/start/{dateStart}/end/{dateEnd}")
  public MqeExampleMessage getExampleMessageForDetection(@PathVariable("mqeCode") String mqeCode, @PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date date,  @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd, AuthenticationToken token) {
    logger.info("ReportController get report! sender:" + providerKey + " date: " + date);
    return this.providerReportJdbcService.getExampleMessageForDetection(mqeCode, providerKey, date, dateEnd, token.getPrincipal().getUsername());
  }


  @RequestMapping(method = RequestMethod.GET, value = "/example/code/{codeType}/{codeValue}/{providerKey}/start/{dateStart}/end/{dateEnd}")
  public MqeExampleMessage getMessageExampleForCode(@PathVariable("codeType") String codeType, @PathVariable("codeValue") String codeValue, @PathVariable("providerKey") String providerKey, @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart, @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd, AuthenticationToken token) {
    final String username = token.getPrincipal().getUsername();
    return providerReportJdbcService.getExampleMessageForCodeTypeAndValue(codeType, codeValue, providerKey, dateStart, dateEnd, username);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/complete/{providerKey}/start/{dateStart}/end/{dateEnd}")
  public ProviderReport getCompleteReportFor(@PathVariable("providerKey") String providerKey, @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart, @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd, AuthenticationToken token) {
    final String username = token.getPrincipal().getUsername();
    logger.info("ReportController get complete report! sender:" + providerKey + " date: " + dateStart + " user: " + username);
//    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, username);
    StopWatch sw = new StopWatch();
    sw.start();
    ProviderReport providerReport = providerReportJdbcService.getProviderReport(providerKey, dateStart, dateEnd, username);
    sw.stop();
    logger.warn("providerReportJdbcService.getProviderReport took ["+sw.getTime() + "]ms");
    FacilitySummaryReport fsr = providerReport.getCountSummary();
    fsr.getMessages().setTotal(providerReport.getNumberOfMessage());

    PatientSummary ps = providerReportJdbcService.getPatientAgesByProvider(providerKey, token.getPrincipal().getUsername(), dateStart, dateEnd);
    fsr.setPatients(ps);

    return  providerReport;
  }

  private List<ScoreReportable> getErrors(String providerKey, String username, Date date, Date dateEnd, VxuScoredReport report) {
    List<ScoreReportable> errors = new ArrayList<>();
    for(ScoreReportable detection: report.getDetectionCounts()) {
      if(detection.getSeverity().equals(SeverityLevel.ERROR)) {
        Page<MessageMetadata> md = this.mvRepo.findByDetectionId(username, providerKey, date, dateEnd, detection.getMqeCode(), new PageRequest(1,1));
        if(md != null && md.getNumberOfElements() > 0) {
          detection.setExampleMessage(md.getContent().get(0).getMessage());
        }
        errors.add(detection);
      }
    }
    return errors;
  }

  List<CollectionBucket> getCodeIssues(String providerKey, String username, Date date, Date dateEnd, List<CollectionBucket> codes) {
    List<CollectionBucket> codeIssues = new ArrayList<>();
    for(CollectionBucket codeCount: codes) {
      if(!codeCount.getStatus().equals("Valid")) {
        Page<MessageMetadata> md = this.mvRepo.findByCodeValue(username, providerKey, date, dateEnd, codeCount.getValue(), codeCount.getTypeCode(), new PageRequest(1,1));
        if(md != null && md.getNumberOfElements() > 0) {
          codeCount.setExampleMessage(md.getContent().get(0).getMessage());
        }
        codeIssues.add(codeCount);
      }
    }
    return codeIssues;
  }

  private VxuScoredReport getScoredReportAndOverrideDefaults(String providerKey, Date dateStart, Date dateEnd, String username) {
    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd, username);
    VxuScoredReport report = scorer.getDefaultReportForMetrics(allDaysMetrics);
    for (ScoreReportable score : report.getDetectionCounts()) {
      DetectionSeverityOverride detectionSetting = detectionsSettingsRepo.findByDetectionSeverityOverrideGroupNameAndMqeCode(providerKey, score.getMqeCode());
      if (detectionSetting != null) {
        SeverityLevel severity = SeverityLevel.findByLabel(detectionSetting.getSeverity());
        score.setSeverity(severity);
      }
    }
    return report;
  }


  private String testMessage =
      "MSH|^~\\&|||||20170301131228-0500||VXU^V04^VXU_V04|3WzJ-A.01.01.2aF|P|2.5.1|" +
          "\nPID|||3WzJ-A.01.01^^^AIRA-TEST^MR||McCracken^Vinvella^^^^^L|Butler^Pauline|20130225|F||2076-8^Native Hawaiian or Other Pacific Islander^HL70005|81 Page Pl^^GR^MI^49544^USA^P||^PRN^PH^^^616^9245843|||||||||2135-2^Hispanic or Latino^HL70005|"
          +
          "\nPD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20170301|20170301|" +
          "\nNK1|1|McCracken^Pauline^^^^^L|MTH^Mother^HL70063|81 Page Pl^^GR^MI^49544^USA^P|^PRN^PH^^^616^9245843|"
          +
          "\nORC|RE||V51L2.3^AIRA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L|"
          +
          "\nRXA|0|1|20170301||21^Varicella^CVX|0.5|mL^milliliters^UCUM||00^Administered^NIP001||||||Y5841RR||MSD^Merck and Co^MVX||||A|"
          +
          "\nRXR|SC^^HL70162|RA^^HL70163|" +
          "\nOBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V02^VFC eligible - Medicaid/Medicaid Managed Care^HL70064||||||F|||20170301|||VXC40^Eligibility captured at the immunization level^CDCPHINVS|"
          +
          "\nOBX|2|CE|30956-7^Vaccine Type^LN|2|21^Varicella^CVX||||||F|" +
          "\nOBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20080313||||||F|" +
          "\nOBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20170301||||||F|";


}
