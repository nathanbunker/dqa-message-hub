package org.immregistries.dqa.hub.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
// -------------
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hub.report.MessageEvaluation;
import org.immregistries.dqa.hub.report.viewer.MessageMetadata;
import org.immregistries.dqa.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "/messages")
@RestController
public class MessageInputController {
    private static final Log logger = LogFactory.getLog(MessageInputController.class);

    @Autowired 
    private Hl7MessageConsumer messageConsumer;
    
    @Autowired 
    MessageMetadataJpaRepository metaRepo;
    
    @Autowired
    private Hl7MessageConsumer msgr;  
    
    @RequestMapping(value = "in", method = RequestMethod.POST)
    public String scoreMessageAndPersist(@RequestBody Hl7MessageSubmission submission) throws Exception {
    	logger.info("ReportController scoreMessage demo!");
    	logger.info("processing this message: [" + submission.getMessage() + "]");
    	String ack = msgr.processMessageAndMakeAck(submission);
    	return ack;
    }

    @RequestMapping(value = "in/msg-only", method = RequestMethod.POST)
    public String hl7MessageEndpoint(@RequestBody String message) throws Exception {
        logger.info("hl7 message interface endpoint!");
        
        String provider = "DQA Default";
        
        Hl7MessageSubmission submission = new Hl7MessageSubmission();
        submission.setFacilityCode(provider);
        submission.setMessage(message);
        submission.setPassword("none");
        submission.setUser("none");
        
        String ack = messageConsumer.processMessageAndMakeAck(submission);

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
        
        String ack = messageConsumer.processMessageAndMakeAck(messageSubmission);
        return  ack;
    }
    
    @RequestMapping(value = "form-file", method = RequestMethod.POST)
    public String urlEncodedHttpFormFilePost(@RequestParam("file")
    		MultipartFile file) throws Exception {
    	
        logger.info(file);
        
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
    public MessageEvaluation jsonFormPost(@RequestBody Hl7MessageSubmission submission) {
    	logger.info("hl7 message interface endpoint! message: " + submission.getMessage() + " user: " + submission.getUser() + " password: " + submission.getPassword() + " facilityId: " + submission.getFacilityCode());
    	String vxu = submission.getMessage();
    	String ack = "";
    	if (vxu != null) {
    		ack = messageConsumer.processMessageAndMakeAck(submission);
    		vxu = vxu.replaceAll("[\\r]+", "\n");
    		if (ack != null) {
    			ack = ack.replaceAll("[\\r]+", "\n");
    		}
    	}
    	
    	MessageEvaluation me = new MessageEvaluation();
    	me.setMessageAck(ack);
    	me.setMessageVxu(vxu);
    	logger.info("ACK: \n"+ack);
        return me;
    }
    
    String exampleMessageText = 
     "MSH|^~\\&|||||20170203184141-0700||VXU^V04^VXU_V04|25VK-K.01.03.pr|P|2.5.1|\n"
    +"PID|||25VK-K.01.03^^^AIRA-TEST^MR|||Brooks^Butterfly|20130206|M||2106-3^White^HL70005|233 Cherokee Ln^^Flint^MI^48501^USA^P||^PRN^PH^^^810^9573567|||||||||2186-5^not Hispanic or Latino^HL70005|\n"
    +"PD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20170203|20170203|\n"
    +"NK1|1|Krog^Butterfly^^^^^L|MTH^Mother^HL70063|233 Cherokee Ln^^Flint^MI^48501^USA^P|^PRN^PH^^^810^9573567|\n"
    +"ORC|RE||H57E4302.3^AIRA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L|\n"
    +"RXA|0|1|20170203||94^MMRV^CVX|0.5|mL^milliliters^UCUM||00^Administered^NIP001||||||V7737HT||MSD^Merck and Co^MVX||||A|\n"
    +"RXR|SC^^HL70162|RA^^HL70163|\n"
    +"OBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V02^VFC eligible - Medicaid/Medicaid Managed Care^HL70064||||||F|||20170203|||VXC40^Eligibility captured at the immunization level^CDCPHINVS|\n"
    +"OBX|2|CE|30956-7^Vaccine Type^LN|2|94^MMRV^CVX||||||F|\n"
    +"OBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20100521||||||F|\n"
    +"OBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20170203||||||F|";

    @RequestMapping(value = "json/example", method = RequestMethod.GET)
    public Hl7MessageSubmission getExampleJsonFormPost() {
    	logger.info("Getting example!");
    	Hl7MessageSubmission example = new Hl7MessageSubmission();
    	example.setMessage(exampleMessageText);
    	example.setUser("regularUser");
    	example.setPassword("password123");
    	example.setFacilityCode("DQATestFacility");
        return example;
    }
    
}
