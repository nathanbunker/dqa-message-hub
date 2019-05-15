package org.immregistries.mqe.hub.submission;

import java.util.Arrays;
import java.util.List;
import org.immregistries.mqe.hl7util.Reportable;
import org.immregistries.mqe.hl7util.ReportableSource;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hl7util.builder.AckData;
import org.immregistries.mqe.hl7util.builder.HL7Util;
import org.immregistries.mqe.hl7util.model.CodedWithExceptions;
import org.immregistries.mqe.hl7util.model.Hl7Location;
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
  @Value("${iisgateway.url}")
  private String IIS_GATEWAY_URL = "http://localhost/iis-kernel/pop";

  public String queryIIS(Hl7MessageSubmission messageSubmission) {

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
      return result.getBody();
    } else {
      String ackType = "AR";
      String severityLevel = "E";
      String message = "Unsupported message type";
      AckData ackData = new AckData();
      Reportable reportable = new Reportable() {
        @Override
        public ReportableSource getSource() {
          return ReportableSource.MQE;
        }

        @Override
        public SeverityLevel getSeverity() {
          return SeverityLevel.ERROR;
        }

        @Override
        public String getReportedMessage() {
          return "Queries are not supported";
        }

        @Override
        public List<Hl7Location> getHl7LocationList() {
          return null;
        }

        @Override
        public CodedWithExceptions getHl7ErrorCode() {
          CodedWithExceptions cwe = new CodedWithExceptions();
          cwe.setIdentifier("200");
          cwe.setText("Unsupported message type");
          cwe.setNameOfCodingSystem("HL70357");
          return cwe;
        }

        @Override
        public String getDiagnosticMessage() {
          return "";
        }

        @Override
        public CodedWithExceptions getApplicationErrorCode() {
          return null;
        }
      };
      return HL7Util.makeAckMessage(ackType, severityLevel, message, ackData, reportable);
    }
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
