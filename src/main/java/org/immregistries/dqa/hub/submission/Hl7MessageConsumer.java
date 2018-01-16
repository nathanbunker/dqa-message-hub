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
import org.immregistries.dqa.nist.validator.connector.Assertion;
import org.immregistries.dqa.nist.validator.connector.NISTValidator;
import org.immregistries.dqa.validator.DqaMessageService;
import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.immregistries.dqa.validator.report.ReportScorer;
import org.immregistries.dqa.validator.report.codes.CodeCollection;
import org.immregistries.dqa.vxu.DqaMessageHeader;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Hl7MessageConsumer {

	private DqaMessageService validator = DqaMessageService.INSTANCE;
	private NISTValidator nistValidator = null;
    private ReportScorer scorer = ReportScorer.INSTANCE;
    @Autowired
    private SenderMetricsService metricsSvc;
	@Autowired
	private MessageMetadataJpaRepository metaRepo;
	
	private NISTValidator getNISTValidator()
	{
	  if (nistValidator == null)
	  {
	    nistValidator = new NISTValidator();
	  }
	  return nistValidator;
	}

    public Hl7MessageHubResponse processMessageAndMakeAck(Hl7MessageSubmission messageSubmission) {
        String message = messageSubmission.getMessage();

        String sender = messageSubmission.getFacilityCode();
        if (sender == null) {
            sender = "DQA Unspecified";
        }

        NISTValidator nistValidator = getNISTValidator();
        List<Reportable> nistReportableList = nistValidator.validateAndReport(message);

        //force serial processing...
        DqaMessageServiceResponse validationResults = validator.processMessage(message);
        Date randomSentDate = getRandomDate();
        this.saveMetricsFromValidationResults(sender, validationResults, randomSentDate);
        String ack = makeAckFromValidationResults(validationResults, nistReportableList);
        this.saveMessageForSender(message, ack, sender, randomSentDate);

        Hl7MessageHubResponse response = new Hl7MessageHubResponse();
        response.setAck(ack);
        response.setDqaResponse(validationResults);
        response.setSender(sender);

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
        mm.setMessage(message);
        mm.setResponse(ack);
        mm.setProvider(sender);
        metaRepo.save(mm);
	}
	
	public void saveMetricsFromValidationResults(String sender, DqaMessageServiceResponse validationResults, Date metricsDate) {
		 	DqaMessageMetrics metrics = scorer.getDqaMetricsFor(validationResults);
	        metricsSvc.addToSenderMetrics(sender,  metricsDate, metrics);
	}
	
	public String makeAckFromValidationResults(DqaMessageServiceResponse validationResults, List<Reportable> nistReportablesList) {

        List<ValidationRuleResult> resultList = validationResults.getValidationResults();
        List<Reportable> reportables = new ArrayList<Reportable>(nistReportablesList);
        //This code needs to get put somewhere better. 
        for (ValidationRuleResult result : resultList) {
        	reportables.addAll(result.getIssues());
        }
        
        AckBuilder ackBuilder = AckBuilder.INSTANCE;
        
        AckData data = new AckData();
        DqaMessageHeader header = validationResults.getMessageObjects().getMessageHeader();
        data.setMessageControlId(header.getMessageControl());
        data.setMessageDate(header.getMessageDate());
        data.setMessageProfileId(header.getMessageProfile());
        data.setMessageVersionId(header.getMessageVersion());
        data.setProcessingControlId(header.getProcessingStatus());
        data.setReceivingApplication("DQA Message Hub App");
        data.setReceivingFacility("DQA Facility");
        data.setSendingFacility(header.getSendingFacility());
        data.setSendingApplication(header.getSendingApplication());
        data.setResponseType("?");
        data.setReportables(reportables);
        
        String ack = ackBuilder.buildAckFrom(data);
        return ack;
	}
}
