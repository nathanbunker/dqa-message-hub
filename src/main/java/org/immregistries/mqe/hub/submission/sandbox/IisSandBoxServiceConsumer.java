package org.immregistries.mqe.hub.submission.sandbox;


import org.immregistries.mqe.hub.cfg.MqeMessageHubApplicationProperties;
import org.immregistries.mqe.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

/**
 * 
 * Handle submitting VXU and QBP to the
 * IIS Sandbox as simple http posts.
 * 
 * @author jamesabrannan
 *
 */
@Service
public class IisSandBoxServiceConsumer {

  @Autowired
  private MqeMessageHubApplicationProperties props;


  public static String USERID_IIS = "Mercy";
  public static String PASSWORD_IIS = "password1234";
  public static String FACILITY_IIS = "Mercy Hospital";

  /**
   * Submit to IIS Sandbox pop method and get results for a qbp query
   * message
   * 
   * @param submission
   * @return
   * @throws Exception
   */
  public Hl7MessageHubResponse processQuery(Hl7MessageSubmission submission) throws Exception {
    return this.doPost(submission, true);
  }

  /**
   * Submit to IIS Sandbox pop method and get results for a vxu message
   * 
   * @param submission
   * @return
   * @throws Exception 
   */
  public Hl7MessageHubResponse saveVxu(Hl7MessageSubmission submission) throws Exception {
    return this.doPost(submission, false);
  }

  /**
   * Send a post request to the IIS SandBox
   * 
   * @param submission
   * @param isQuery
   * @return
   * @throws Exception
   */
  private Hl7MessageHubResponse doPost(Hl7MessageSubmission submission, boolean isQuery)
      throws Exception {
    Hl7MessageHubResponse msgResponse = null;
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("USERID", IisSandBoxServiceConsumer.USERID_IIS);
    map.add("PASSWORD", IisSandBoxServiceConsumer.PASSWORD_IIS);
    map.add("FACILITYID", IisSandBoxServiceConsumer.FACILITY_IIS);
    map.add("MESSAGEDATA", submission.getMessage());

    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(map, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response =
        restTemplate.postForEntity(props.getIisSandboxPopUrl(), request, String.class);
    if (response.getStatusCode().value() == 200) {
      msgResponse = new Hl7MessageHubResponse();
      msgResponse.setVxu(submission.getMessage());
      msgResponse.setSender(submission.getFacilityCode());
      msgResponse.setAck(response.getBody());
    } else {
      if (!isQuery) {
        throw new Exception("Unable to save vxu to IIS Sandbox. Server Response Code: "
            + response.getStatusCode().value());
      } else {
        throw new Exception("Unable to query IIS Sandbox. Server Response Code: "
            + response.getStatusCode().value());
      }
    }

    return msgResponse;
  }

}
