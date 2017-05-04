
package org.immregistries.dqa.hub.report;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.immregistries.dqa.validator.report.ReportScorer;
import org.immregistries.dqa.validator.report.VxuScoredReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/report")
@RestController
public class ReportController {
    private static final Log logger = LogFactory.getLog(ReportController.class);

    @Autowired
    private Hl7MessageConsumer msgr;  
 
    @Autowired
    private SenderMetricsService metricsSvc;
    
    private ReportScorer scorer = ReportScorer.INSTANCE;
    
    @RequestMapping(value = "/demo")
    public VxuScoredReport exampleReport() throws Exception {
    	logger.info("ReportController exampleReport demo!");
    	VxuScoredReport report = scorer.getDefaultReportForMessage(testMessage); 
    	return report;
    }
    
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public VxuScoredReport scoreMessage(@RequestBody Hl7MessageSubmission submission) throws Exception {
    	logger.info("ReportController scoreMessage demo! sender:" + submission.getFacilityCode());
    	String submitter = submission.getFacilityCode();
    	
    	if (StringUtils.isEmpty(submitter)) {
    		submission.setFacilityCode("DQA");
    	}
    	
    	msgr.processMessageAndMakeAck(submission);
    	
    	DqaMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(submitter, new Date());
    	VxuScoredReport report = scorer.getDefaultReportForMetrics(allDaysMetrics); 
    	return report;
    }
    
	private String testMessage = 
			  "MSH|^~\\&|||||20170301131228-0500||VXU^V04^VXU_V04|3WzJ-A.01.01.2aF|P|2.5.1|"+
			"\nPID|||3WzJ-A.01.01^^^AIRA-TEST^MR||McCracken^Vinvella^^^^^L|Butler^Pauline|20130225|F||2076-8^Native Hawaiian or Other Pacific Islander^HL70005|81 Page Pl^^GR^MI^49544^USA^P||^PRN^PH^^^616^9245843|||||||||2135-2^Hispanic or Latino^HL70005|"+
			"\nPD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20170301|20170301|"+
			"\nNK1|1|McCracken^Pauline^^^^^L|MTH^Mother^HL70063|81 Page Pl^^GR^MI^49544^USA^P|^PRN^PH^^^616^9245843|"+
			"\nORC|RE||V51L2.3^AIRA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L|"+
			"\nRXA|0|1|20170301||21^Varicella^CVX|0.5|mL^milliliters^UCUM||00^Administered^NIP001||||||Y5841RR||MSD^Merck and Co^MVX||||A|"+
			"\nRXR|SC^^HL70162|RA^^HL70163|"+
			"\nOBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V02^VFC eligible - Medicaid/Medicaid Managed Care^HL70064||||||F|||20170301|||VXC40^Eligibility captured at the immunization level^CDCPHINVS|"+
			"\nOBX|2|CE|30956-7^Vaccine Type^LN|2|21^Varicella^CVX||||||F|"+
			"\nOBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20080313||||||F|"+
			"\nOBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20170301||||||F|";

    
}
