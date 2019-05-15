package org.immregistries.mqe.hub.submission;

import java.util.Arrays;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class IISGateway {

  private static final boolean ENABLED = false;

//  public static final String IIS_GATEWAY_URL = "http://florence.immregistries.org/iis-kernel/pop";
  public static final String IIS_GATEWAY_URL = "http://localhost/iis-kernel/pop";

  public String queryIIS(Hl7MessageSubmission messageSubmission) {

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));

    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    map.add("MESSAGEDATA", messageSubmission.getMessage());
    map.add("FACILITYID", messageSubmission.getFacilityCode());
    map.add("PASSWORD", messageSubmission.getPassword());
    map.add("USERID", messageSubmission.getUser());

    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<MultiValueMap<String, String>>(map, headers);
    ResponseEntity<String> result =
        restTemplate.exchange(IIS_GATEWAY_URL, HttpMethod.POST, request, String.class);
    return result.getBody();
  }

  public void sendVXU(Hl7MessageSubmission messageSubmission) {
    if (ENABLED) {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));

      MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
      map.add("MESSAGEDATA", messageSubmission.getMessage());
      map.add("FACILITYID", messageSubmission.getFacilityCode());
      map.add("PASSWORD", messageSubmission.getPassword());
      map.add("USERID", messageSubmission.getUser());

      HttpEntity<MultiValueMap<String, String>> request =
          new HttpEntity<MultiValueMap<String, String>>(map, headers);
      ResponseEntity<String> result =
          restTemplate.exchange(IIS_GATEWAY_URL, HttpMethod.POST, request, String.class);
      return;
    }
  }



}
