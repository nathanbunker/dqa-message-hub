package org.immregistries.dqa.hub.submission;

import java.util.ArrayList;
import java.util.List;

import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.hl7util.SeverityLevel;
import org.immregistries.dqa.hl7util.builder.AckBuilder;
import org.immregistries.dqa.hl7util.builder.AckData;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.validator.DqaMessageService;
import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.engine.issues.ValidationIssue;
import org.immregistries.dqa.validator.model.DqaMessageHeader;
import org.springframework.stereotype.Service;

@Service
public class Hl7MessageConsumer {

	
	public String makeAck(Hl7MessageSubmission messageSubmission)
	{
		String message = messageSubmission.getMessage();
        DqaMessageService validator = DqaMessageService.INSTANCE;
        DqaMessageServiceResponse validationResults = validator.processMessage(message);
        
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
