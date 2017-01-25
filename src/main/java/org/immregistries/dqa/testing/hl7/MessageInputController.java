package org.immregistries.dqa.testing.hl7;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/messages")
@RestController
public class MessageInputController {
    private static final Log logger = LogFactory.getLog(MessageInputController.class);

    @RequestMapping(value = "in", method = RequestMethod.POST)
    public String hl7MessageEndpoint(
            @RequestBody String message) throws Exception {
        logger.info("hl7 message interface endpoint!");
        //This will take the message, parse, validate, and save the results so that it will show up 
        //as part of the reporting UIX. 
        return "message received";
    }
    
}
