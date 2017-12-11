package org.immregistries.dqa.hub.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.immregistries.dqa.codebase.client.reference.CodesetType;
import org.immregistries.dqa.validator.engine.codes.CodeReceived;
import org.immregistries.dqa.validator.issue.Detection;
import org.immregistries.dqa.validator.issue.IssueObject;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SenderMetricsService {
	
	private static final Logger logger = LoggerFactory.getLogger(SenderMetricsService.class);

	@Autowired
	SenderMetricsJpaRepository senderRepo;
	
	public DqaMessageMetrics getMetricsFor(String sender, Date day) {
		
		if (StringUtils.isEmpty(sender)) {
			sender = "DQA";
		}
		
		SenderMetrics metrics = senderRepo.findBySenderAndMetricsDate(sender,  day);
		
		logger.info("Metrics found for " + sender + " day: " + day);
		logger.info("Metrics: " + metrics);
		
		DqaMessageMetrics out = new DqaMessageMetrics();
		out.getObjectCounts().put(IssueObject.PATIENT,  metrics.getPatientCount());
		out.getObjectCounts().put(IssueObject.MESSAGE_HEADER,  metrics.getPatientCount());
		out.getObjectCounts().put(IssueObject.VACCINATION,  metrics.getVaccinationCount());
		Map<Detection, Integer> attrCounts = out.getAttributeCounts();
		Map<CodeReceived, Integer> codeCounts = out.getCodeCounts();
		
		for (SenderAttributeMetrics sam : metrics.getAttributes()) {
			Detection ma = Detection.getByDqaErrorCodeString(sam.getDqaAttributeCode());
			attrCounts.put(ma, sam.getAttributeCount());
		}
		
		for (SenderCodeMetrics sam : metrics.getCodes()) {
			CodeReceived cr = new CodeReceived();
			cr.setCodeset(CodesetType.getByTypeCode(sam.getCodeSet()));
			cr.setValue(sam.getValue());
			codeCounts.put(cr, sam.getCodeCount());
		}
		
		return out;
	}
	
	public SenderMetrics addToSenderMetrics(String sender, Date day, DqaMessageMetrics incomingMetrics) {
		SenderMetrics metrics = senderRepo.findBySenderAndMetricsDate(sender,  day);
		
		if (metrics == null) {
			metrics = new SenderMetrics();
			metrics.setSender(sender);
			metrics.setMetricsDate(day);
		}
		
		Map<IssueObject, Integer> objectCounts = incomingMetrics.getObjectCounts();
		Map<Detection, Integer> attributeCounts = incomingMetrics.getAttributeCounts();
		Map<CodeReceived, Integer> codeCounts = incomingMetrics.getCodeCounts();
		
		for (IssueObject io : objectCounts.keySet()) {
			Integer count = objectCounts.get(io);
			if (count != null && count > 0) {
				switch (io) {
					case PATIENT : 
						int patientCount = metrics.getPatientCount() + count;
						metrics.setPatientCount(patientCount);
						break;
					case VACCINATION : 
						int vaccCount = metrics.getVaccinationCount() + count;
						metrics.setVaccinationCount(vaccCount);
						break;
				default:
					break;
				}
			}
		}
		
		
		for (Detection attr : attributeCounts.keySet()) {
			//find the right metrics object...
			List<SenderAttributeMetrics> sams = metrics.getAttributes();
			Integer count = attributeCounts.get(attr);
			if (count != null && count > 0) {
				SenderAttributeMetrics thisOne = null;
				for (SenderAttributeMetrics sam : sams) {
					if (attr.getDqaErrorCode().equals(sam.getDqaAttributeCode())) {
						//it's the same attribute!!! use it!
						thisOne = sam;
					}
				}
				//If you didn't find one, make a new one. 
				if (thisOne == null) {
					thisOne = new SenderAttributeMetrics();
					thisOne.setDqaAttributeCode(attr.getDqaErrorCode()); 
					thisOne.setSenderMetrics(metrics);
					sams.add(thisOne);
				}
				int addedCounts = count + thisOne.getAttributeCount();
				thisOne.setAttributeCount(addedCounts);
			}
		}
		
		for (CodeReceived code : codeCounts.keySet()) {
			//find the right metrics object...
			List<SenderCodeMetrics> scms = metrics.getCodes();
			Integer count = attributeCounts.get(code);
			if (count != null && count > 0) {
				SenderCodeMetrics thisOne = null;
				for (SenderCodeMetrics scm : scms) {
					if (code.getCodeset().getType().equals(scm.getCodeSet())) {
						if (code.getValue().equals(scm.getValue())) {
							//it's the same one!!! use it!
							thisOne = scm;
						}
					}
				}
				//If you didn't find one, make a new one. 
				if (thisOne == null) {
					thisOne = new SenderCodeMetrics();
					thisOne.setCodeSet(code.getCodeset().getType());
					thisOne.setValue(code.getValue());
					thisOne.setSenderMetrics(metrics);
					scms.add(thisOne);
				}
				
				int addedCounts = count + thisOne.getCodeCount();
				thisOne.setCodeCount(addedCounts);
			}
		}

		senderRepo.save(metrics);
		senderRepo.flush();
		
		return metrics;
	}
}
