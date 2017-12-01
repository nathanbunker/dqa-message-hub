package org.immregistries.dqa.hub.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.immregistries.dqa.hl7util.test.MessageGenerator;
import org.immregistries.dqa.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.dqa.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
// -------------

@RequestMapping(value = "/messages")
@RestController
public class MessageInputController {
    private static final Logger logger = LoggerFactory.getLogger(MessageInputController.class);

    @Autowired 
    private Hl7MessageConsumer messageConsumer;
    
    @Autowired 
    MessageMetadataJpaRepository metaRepo;
    
    @Autowired
    private Hl7MessageConsumer msgr;  
    
    @RequestMapping(value = "in", method = RequestMethod.POST)
    public Hl7MessageHubResponse scoreMessageAndPersist(@RequestBody Hl7MessageSubmission submission) throws Exception {
    	logger.info("ReportController scoreMessage demo!");
    	logger.info("processing this message: [" + submission.getMessage() + "]");
    	Hl7MessageHubResponse ack = msgr.processMessageAndMakeAck(submission);
    	return ack;
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
        
        String ack = messageConsumer.processMessageAndMakeAck(messageSubmission).getAck();
        return  ack;
    }
    
    @RequestMapping(value = "form-file", method = RequestMethod.POST)
    public String urlEncodedHttpFormFilePost(@RequestParam("file")
    		MultipartFile file) throws Exception {
    	
//        String REGEX = "^MSH\\|.*";
        String line;
//        String MESSAGEDATA = "";
//        String USERID = "user";
//        String PASSWORD = "pass";
//        String FACILITYID = "SF-000001";
//        String fileName = file.getOriginalFilename();
//        String messageResult;
//        String ackResult;
//        int count = 0;

        InputStream inputStream = file.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        
        DqaMessageInfo dqaInfo = new DqaMessageInfo(0);
        			
        while ((line = bufferedReader.readLine()) != null) {
//        	if (line.matches(REGEX)) {
//        		if (MESSAGEDATA.equals("")) {
//        			MESSAGEDATA = line;
//        			count++;
//        		}
//        		else {
//        	        ackResult = urlEncodedHttpFormPost(MESSAGEDATA, USERID, PASSWORD, FACILITYID);
//        	        dqaInfo.addHl7Messages(MESSAGEDATA);
//        	        dqaInfo.addHl7Messages(ackResult);
//        			MESSAGEDATA = line;
//        			count++;
//        		}
//        	}
//        	else {
//        		MESSAGEDATA = MESSAGEDATA.concat("\r\n");
//        		MESSAGEDATA = MESSAGEDATA.concat(line);
//        	}
        	logger.info(line);
        }
        
//        messageResult = "Filename: " + fileName + "\n" + "Number of messages: " + count + "\n" + "Reported under: " + FACILITYID;
//        
//        ackResult = urlEncodedHttpFormPost(MESSAGEDATA, USERID, PASSWORD, FACILITYID);
//        dqaInfo.addHl7Messages(MESSAGEDATA);
//        dqaInfo.addHl7Messages(ackResult);
//        
//        dqaInfo.printHL7Array();
        return "you did it!";
    }
    
    
    @RequestMapping(value = "json", method = RequestMethod.POST)
    public Hl7MessageHubResponse jsonFormPost(@RequestBody Hl7MessageSubmission submission) {
    	logger.info("hl7 message interface endpoint! message: " + submission.getMessage() + " user: " + submission.getUser() + " password: " + submission.getPassword() + " facilityId: " + submission.getFacilityCode());
    	
    	
    	String vxu = submission.getMessage();
    	
    	if (vxu != null) {
    		Hl7MessageHubResponse response = messageConsumer.processMessageAndMakeAck(submission);
    		vxu = vxu.replaceAll("[\\r]+", "\n");
    		response.setVxu(vxu);
    		
    		//process the ack to display right on the web: 
    		String ack = response.getAck();
    		if (ack != null) {
    			ack = ack.replaceAll("[\\r]+", "\n");
    		}
    		response.setAck(ack);
    		logger.info("ACK: \n"+ack);
    		return response;
    	} else {
    		Hl7MessageHubResponse response = new Hl7MessageHubResponse();
    		response.setSender(submission.getFacilityCode());
    		response.setAck("INVALID REQUEST:  VXU is empty");
    		return response;
    	}
    }

    MessageGenerator mg = new MessageGenerator();

    @RequestMapping(value = "json/example", method = RequestMethod.GET)
    public Hl7MessageSubmission getExampleJsonFormPost() {
    	logger.info("Getting example!");
    	Hl7MessageSubmission example = new Hl7MessageSubmission();
    	example.setMessage(mg.getUniqueMessage());
    	example.setUser("regularUser");
    	example.setPassword("password123");
    	example.setFacilityCode("DQATestFacility");
        return example;
    }
    
}
