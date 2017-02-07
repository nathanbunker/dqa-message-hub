package org.immregistries.dqa.hub.rest;

// Added imports
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
// -------------

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    
    @RequestMapping(value = "form-file", method = RequestMethod.POST)
    public String urlEncodedHttpFormFilePost(
    		MultipartFile file) throws Exception {
    	
        logger.info(file);
        
        String REGEX = "^MSH\\|.*";
        String line;
        String MESSAGEDATA = "";
        String USERID = "user";
        String PASSWORD = "pass";
        String FACILITYID = "SF-000001";
        String fileName = file.getOriginalFilename();
        String messageResult;
        String ackResult;
        int count = 0;

        InputStream inputStream = file.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        
        DqaMessageInfo dqaInfo = new DqaMessageInfo(0);
        			
        while ((line = bufferedReader.readLine()) != null) {
        	if (line.matches(REGEX)) {
        		if (MESSAGEDATA.equals("")) {
        			MESSAGEDATA = line;
        			count++;
        		}
        		else {
        	        ackResult = urlEncodedHttpFormPost(MESSAGEDATA, USERID, PASSWORD, FACILITYID);
        	        dqaInfo.addHl7Messages(MESSAGEDATA);
        	        dqaInfo.addHl7Messages(ackResult);
        			MESSAGEDATA = line;
        			count++;
        		}
        	}
        	else {
        		MESSAGEDATA = MESSAGEDATA.concat("\r\n");
        		MESSAGEDATA = MESSAGEDATA.concat(line);
        	}
        }
        
        messageResult = "Filename: " + fileName + "\n" + "Number of messages: " + count + "\n" + "Reported under: " + FACILITYID;
        
        ackResult = urlEncodedHttpFormPost(MESSAGEDATA, USERID, PASSWORD, FACILITYID);
        dqaInfo.addHl7Messages(MESSAGEDATA);
        dqaInfo.addHl7Messages(ackResult);
        
        dqaInfo.printHL7Array();
        
        return messageResult;
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
