package org.immregistries.dqa.hub.rest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.hl7util.builder.AckBuilder;
import org.immregistries.dqa.hl7util.builder.AckData;
import org.immregistries.dqa.validator.DqaMessageService;
import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.model.DqaMessageHeader;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/tester")
@RestController
public class DqaHl7TestingController {
    private static final Log logger = LogFactory.getLog(DqaHl7TestingController.class);


    


    @RequestMapping(value = "hl7", method = RequestMethod.POST)
    public String hl7MessageInterface(
            @RequestBody String message) throws Exception {
        logger.info("hl7MessageInterface demo!");
        //send through the Validator in the DQA Validator project. 
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
        data.setProcessingControlId("generatedControlId" + new DateTime().getMillis());
        data.setReceivingApplication("DQA Message Hub App");
        data.setReceivingFacility("DQA Facility");
        data.setSendingFacility(header.getSendingFacility());
        data.setSendingApplication(header.getSendingApplication());
        data.setResponseType("?");
        data.setReportables(reportables);
        
        String ack = ackBuilder.buildAckFrom(data);
        
        return ack;
    }
    

    @RequestMapping(value = "hl7/validationList", method = RequestMethod.POST)
    public DqaMessageServiceResponse hl7ValidationList(
            @RequestBody String message) throws Exception {
        logger.info("hl7ValidationList demo!");
        //send through the Validator in the DQA Validator project. 
        DqaMessageService validator = DqaMessageService.INSTANCE;
        return validator.processMessage(message);
        //Use the results to build an ACK using the DQA util project.
    }

    

   
}
