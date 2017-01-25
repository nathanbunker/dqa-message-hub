package org.immregistries.dqa.testing.hl7;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hl7util.Reportable;
import org.immregistries.dqa.hl7util.builder.AckBuilder;
import org.immregistries.dqa.hl7util.builder.AckData;
import org.immregistries.dqa.testing.data.County;
import org.immregistries.dqa.testing.data.CountyJpaRepository;
import org.immregistries.dqa.validator.DqaMessageService;
import org.immregistries.dqa.validator.DqaMessageServiceResponse;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.model.DqaMessageHeader;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/tester")
@RestController
public class DqaHl7TestingController {
    private static final Log logger = LogFactory.getLog(DqaHl7TestingController.class);


    @Autowired
    private CountyJpaRepository countyRepo;
    


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

    

    //this is an example of how you would form a post.  
    //this one takes the string, and makes a county with it. 
    //and then returns the list of all the counties. 
    //which is weird, and doesn't conform to any proper pattern. 
    @RequestMapping(value = "demo", method = RequestMethod.POST)
    public List<County> demo(
            @RequestBody String message) throws Exception {

    	addCountyWithName(message);
        logger.info("demo!");
        countyRepo.findAll();
        return countyRepo.findAll();
    }
    
    
    
    protected void addCountyWithName(String name) {
    	County c = new County();
    	c.setClassCode("HA!");
    	c.setCountyCode("111");
    	long id = Double.valueOf(Math.floor(Math.random() * 1000000)).longValue();
    	c.setFipsNationalCountyId(id);
    	c.setName(name + " " + id);
    	c.setStateAbbreviation("MI");
    	c.setStateCode("256");
    	countyRepo.saveAndFlush(c);
    }
}
