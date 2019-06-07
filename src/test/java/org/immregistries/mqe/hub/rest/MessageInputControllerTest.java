package org.immregistries.mqe.hub.rest;

import static org.junit.Assert.*;
import org.junit.Test;

public class MessageInputControllerTest {


  private static final String RSP_MESSAGE =
      "MSH|^~\\&|||||20140515001020-0500||QBP^Q11^QBP_Q11|793543|P|2.5.1|||ER|AL|||||Z44^CDCPHINVS\r"
          + "QPD|Z44^Request Evaluated History and Forecast^CDCPHINVS|37374859|123456^^^MYEHR^MR|Child^Bobbie^Q^^^^L|Que^Suzy^^^^^M|20050512|M|10 East Main St^^Myfaircity^GA^^^L\r"
          + "RCP|I|1^RD&Records&HL70126\r";

  private static final String VXU_MESSAGE =
      "MSH|^~\\&|Test EHR Application|X68||NIST Test Iz Reg|20120701082240-0500||VXU^V04^VXU_V04|NIST-IZ-001.00|P|2.5.1|||ER|AL|||||Z22^CDCPHINVS\r"
          + "PID|1||D26376273^^^NIST MPI^MR||Snow^Madelynn^Ainsley^^^^L|Lam^Morgan^^^^^M|20070706|F||2076-8^Native Hawaiian or Other Pacific Islander^CDCREC|32 Prescott Street Ave^^Warwick^MA^02452^USA^L||^PRN^PH^^^657^5558563|||||||||2186-5^non Hispanic or Latino^CDCREC\r"
          + "PD1|||||||||||02^Reminder/Recall - any method^HL70215|||||A|20120701|20120701\r"
          + "NK1|1|Lam^Morgan^^^^^L|MTH^Mother^HL70063|32 Prescott Street Ave^^Warwick^MA^02452^USA^L|^PRN^PH^^^657^5558563\r"
          + "ORC|RE||IZ-783274^NDA|||||||I-23432^Burden^Donna^A^^^^^NIST-AA-1^^^^PRN||57422^RADON^NICHOLAS^^^^^^NIST-AA-1^L^^^MD\r"
          + "RXA|0|1|20120814||33332-0010-01^Influenza, seasonal, injectable, preservative free^NDC|0.5|mL^MilliLiter [SI Volume Units]^UCUM||00^New immunization record^NIP001|7832-1^Lemon^Mike^A^^^^^NIST-AA-1^^^^PRN|^^^X68||||Z0860BB|20121104|CSL^CSL Behring^MVX|||CP|A\r"
          + "RXR|C28161^Intramuscular^NCIT|LD^Left Arm^HL70163\r"
          + "OBX|1|CE|64994-7^Vaccine funding program eligibility category^LN|1|V05^VFC eligible - Federally Qualified Health Center Patient (under-insured)^HL70064||||||F|||20120701|||VXC40^Eligibility captured at the immunization level^CDCPHINVS\r"
          + "OBX|2|CE|30956-7^vaccine type^LN|2|88^Influenza, unspecified formulation^CVX||||||F\r"
          + "OBX|3|TS|29768-9^Date vaccine information statement published^LN|2|20120702||||||F\r"
          + "OBX|4|TS|29769-7^Date vaccine information statement presented^LN|2|20120814||||||F\r";
  
  private static final String HEADER = "FHS|^~\\&|Test EHR Application|X68||NIST Test Iz Reg|20120701082240-0500||VXU^V04^VXU_V04|NIST-IZ-001.00|P|2.5.1|||ER|AL|||||Z22^CDCPHINVS\r";

  @Test
  public void test() {
    assertFalse(MessageInputController.isQBP(""));
    assertFalse(MessageInputController.isQBP(VXU_MESSAGE));
    assertFalse(MessageInputController.isQBP(HEADER + VXU_MESSAGE));
    assertFalse(MessageInputController.isQBP(RSP_MESSAGE.substring(0, 1)));
    assertFalse(MessageInputController.isQBP(RSP_MESSAGE.substring(0, 3)));
    assertFalse(MessageInputController.isQBP(RSP_MESSAGE.substring(0, 5)));
    assertFalse(MessageInputController.isQBP(RSP_MESSAGE.substring(0, 10)));
    assertTrue(MessageInputController.isQBP(RSP_MESSAGE));
    assertTrue(MessageInputController.isQBP(HEADER + RSP_MESSAGE));
  }

}
