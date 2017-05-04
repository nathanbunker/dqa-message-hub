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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Hl7MessageConsumer {

	private DqaMessageService validator = DqaMessageService.INSTANCE;
    private ReportScorer scorer = ReportScorer.INSTANCE;
    @Autowired
    private SenderMetricsService metricsSvc;
	@Autowired
	private MessageMetadataJpaRepository metaRepo;
    
	public Hl7MessageHubResponse processMessageAndMakeAck(Hl7MessageSubmission messageSubmission)
	{
		String message = messageSubmission.getMessage();
		
		String sender = messageSubmission.getFacilityCode();
		if (sender == null) {
			sender = "DQA Unspecified";
		}
		
        DqaMessageServiceResponse validationResults = validator.processMessage(message);
        
        this.saveMetricsFromValidationResults(sender, validationResults);
        
        String ack = makeAckFromValidationResults(validationResults);
        
        this.saveMessageForSender(message, ack, sender);
        
        Hl7MessageHubResponse response = new Hl7MessageHubResponse();
        
        response.setAck(ack);
        response.setDqaResponse(validationResults);
        response.setSender(sender);
        
        return response;
	}
	
	public void saveMessageForSender(String message, String ack, String sender) {
		MessageMetadata mm = new MessageMetadata();
        mm.setInputTime(new Date());
        mm.setMessage(message);
        mm.setResponse(ack);
        mm.setProvider(sender);
        metaRepo.save(mm);
	}
	
	public void saveMetricsFromValidationResults(String sender, DqaMessageServiceResponse validationResults) {
		 	DqaMessageMetrics metrics = scorer.getDqaMetricsFor(validationResults);
	        metricsSvc.addToSenderMetrics(sender,  new Date(), metrics);
	}
	
	public String makeAckFromValidationResults(DqaMessageServiceResponse validationResults) {

        List<ValidationRuleResult> resultList = validationResults.getValidationResults();
        List<Reportable> reportables = new ArrayList<Reportable>();
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
