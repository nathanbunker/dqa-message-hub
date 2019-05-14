package org.immregistries.mqe.hub.submission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.immregistries.mqe.core.util.DateUtility;
import org.immregistries.mqe.hl7util.Reportable;
import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hl7util.builder.AckBuilder;
import org.immregistries.mqe.hl7util.builder.AckData;
import org.immregistries.mqe.hl7util.model.Hl7Location;
import org.immregistries.mqe.hub.report.SenderMetricsService;
import org.immregistries.mqe.hub.report.viewer.MessageCode;
import org.immregistries.mqe.hub.report.viewer.MessageDetection;
import org.immregistries.mqe.hub.report.viewer.MessageMetadata;
import org.immregistries.mqe.hub.report.viewer.MessageMetadataJpaRepository;
import org.immregistries.mqe.hub.report.viewer.MessageVaccine;
import org.immregistries.mqe.hub.rest.model.Hl7MessageHubResponse;
import org.immregistries.mqe.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.mqe.validator.MqeMessageService;
import org.immregistries.mqe.validator.MqeMessageServiceResponse;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.ValidationReport;
import org.immregistries.mqe.validator.engine.ValidationRuleResult;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.ReportScorer;
import org.immregistries.mqe.validator.report.codes.CodeCollection;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.vxu.MqeMessageHeader;
import org.immregistries.mqe.vxu.MqeVaccination;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	public static final String IIS_GATEWAY_URL="http://florence.immregistries.org/iis-kernel/pop";

	public String queryIIS(Hl7MessageSubmission messageSubmission) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("MESSAGEDATA", messageSubmission.getMessage());
		map.add("FACILITYID", messageSubmission.getFacilityCode());
		map.add("PASSWORD", messageSubmission.getPassword());
		map.add("USERID", messageSubmission.getUser());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> result = restTemplate.exchange( IIS_GATEWAY_URL,
				HttpMethod.POST,
				request, String.class);
		return result.getBody();
	}

	public void sendVXU(Hl7MessageSubmission messageSubmission) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("MESSAGEDATA", messageSubmission.getMessage());
		map.add("FACILITYID", messageSubmission.getFacilityCode());
		map.add("PASSWORD", messageSubmission.getPassword());
		map.add("USERID", messageSubmission.getUser());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> result = restTemplate.exchange( IIS_GATEWAY_URL,
				HttpMethod.POST,
				request, String.class);
		return;
		
	}


  
}
