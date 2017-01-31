package gov.mi.agcts.address.util;

import static org.junit.Assert.assertNotNull;

import org.immregistries.dqa.hub.util.ObjectLoggingUtil;
import org.junit.Test;

public class ObjectLoggingUtilTest {

	ObjectLoggingUtil util = new ObjectLoggingUtil();
    
	@Test
    public void ResultObjectTest() {
        String output = util.jsonify("Super String");
        System.out.println(output);
        
        assertNotNull("output should be something", output);
    }
}
