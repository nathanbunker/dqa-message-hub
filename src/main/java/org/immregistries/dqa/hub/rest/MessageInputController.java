package org.immregistries.dqa.hub.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/messages")
@RestController
public class MessageInputController {
    private static final Log logger = LogFactory.getLog(MessageInputController.class);

    @Autowired 
    private Hl7MessageConsumer messageConsumer;

    @RequestMapping(value = "in", method = RequestMethod.POST)
    public String hl7MessageEndpoint(
            @RequestBody String message) throws Exception {
        logger.info("hl7 message interface endpoint!");
        //This will take the message, parse, validate, and save the results so that it will show up 
        //as part of the reporting UIX. 
        return "message received";
    }
    

    @RequestMapping(value = "form-standard", method = RequestMethod.POST)
    public String urlEncodedHttpFormPost(
            String MESSAGEDATA, String USERID, String PASSWORD, String FACILITYID) throws Exception {
    	
    	String response = "hl7 message interface endpoint! message: " + MESSAGEDATA + " user: " + USERID + " password: " + PASSWORD + " facilityId: " + FACILITYID;
        logger.info(response);
        
        Hl7MessageSubmission messageSubmission = new Hl7MessageSubmission();
        messageSubmission.setMessage(MESSAGEDATA);
        messageSubmission.setUser(USERID);
        messageSubmission.setPassword(PASSWORD);
        messageSubmission.setFacilityCode(FACILITYID);
        
        return messageConsumer.makeAck(messageSubmission);
    }
    
    @RequestMapping(value = "json", method = RequestMethod.POST)
    public String jsonFormPost(@RequestBody Hl7MessageSubmission submission) {
    	String response = "hl7 message interface endpoint! message: " + submission.getMessage() + " user: " + submission.getUser() + " password: " + submission.getPassword() + " facilityId: " + submission.getFacilityCode();
        logger.info(response);
        return response;
    }
    @RequestMapping(value = "json/example", method = RequestMethod.GET)
    public Hl7MessageSubmission getExampleJsonFormPost() {
    	Hl7MessageSubmission example = new Hl7MessageSubmission();
    	example.setMessage("This is a cool message");
    	example.setUser("regularUser");
    	example.setPassword("password123");
    	example.setFacilityCode("DQATestFacility");
        return example;
    }
    
}
