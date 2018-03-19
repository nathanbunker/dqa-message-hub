package org.immregistries.dqa.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.immregistries.dqa.validator.issue.Detection;
import org.immregistries.dqa.validator.issue.IssueObject;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.immregistries.dqa.validator.report.codes.CodeCollection;
import org.immregistries.dqa.validator.report.codes.CollectionBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service public class SenderMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(SenderMetricsService.class);

    @Autowired SenderMetricsJpaRepository senderRepo;
    @Autowired CodeCountJpaRepository codeRepo;

    public DqaMessageMetrics getMetricsFor(String sender, Date day) {
    	return getMetricsFor(sender, day, day);
    }
    public DqaMessageMetrics getMetricsFor(String sender, Date dayStart, Date dayEnd) {
    	
        if (StringUtils.isEmpty(sender)) {
            sender = "DQA";
        }

        SenderMetrics metrics = senderRepo.findBySenderAndMetricsDateGreaterThanEqualAndMetricsDateLessThanEqual(sender, dayStart, dayEnd);
        logger.info("Metrics found for " + sender + " dayStart: " + dayStart + " dayEnd: " + dayEnd);
        logger.info("Metrics: " + metrics);
        DqaMessageMetrics out = new DqaMessageMetrics();
        if (metrics == null) {
            return out;
        }

        out.getObjectCounts().put(IssueObject.PATIENT, metrics.getPatientCount());
        out.getObjectCounts().put(IssueObject.MESSAGE_HEADER, metrics.getPatientCount());
        out.getObjectCounts().put(IssueObject.VACCINATION, metrics.getVaccinationCount());
        Map<Detection, Integer> attrCounts = out.getAttributeCounts();

        for (SenderDetectionMetrics sam : metrics.getAttributes()) {
            Detection ma = Detection.getByDqaErrorCodeString(sam.getDqaDetectionCode());
            attrCounts.put(ma, sam.getAttributeCount());
        }

        List<CodeCount> codesExisting = metrics.getCodes();
        logger.info("SM Codes: " + codesExisting);
        List<CollectionBucket> codes = new ArrayList<>();
        for (CodeCount sam : codesExisting) {
            CollectionBucket cc = new CollectionBucket(sam.getCodeType(), sam.getAttribute(), sam.getCodeValue(), sam.getCodeCount());
            codes.add(cc);
        }
        out.getCodes().setCodeCountList(codes);

        return out;
    }

    public SenderMetrics addToSenderMetrics(String sender, Date day, DqaMessageMetrics incomingMetrics) {
        SenderMetrics metrics = senderRepo.findBySenderAndMetricsDate(sender, day);

        if (metrics == null) {
            metrics = new SenderMetrics();
            metrics.setSender(sender);
            metrics.setMetricsDate(day);
        }

        Map<IssueObject, Integer> objectCounts = incomingMetrics.getObjectCounts();
        Map<Detection, Integer> detectionCounts = incomingMetrics.getAttributeCounts();

        for (IssueObject io : objectCounts.keySet()) {
            Integer count = objectCounts.get(io);
            if (count != null && count > 0) {
                switch (io) {
                case PATIENT:
                    int patientCount = metrics.getPatientCount() + count;
                    metrics.setPatientCount(patientCount);
                    break;
                case VACCINATION:
                    int vaccCount = metrics.getVaccinationCount() + count;
                    metrics.setVaccinationCount(vaccCount);
                    break;
                default:
                    break;
                }
            }
        }

        for (Detection attr : detectionCounts.keySet()) {
            //find the right metrics object...
            List<SenderDetectionMetrics> sams = metrics.getAttributes();
            Integer count = detectionCounts.get(attr);
            if (count != null && count > 0) {
                SenderDetectionMetrics thisOne = null;
                for (SenderDetectionMetrics sam : sams) {
                    if (attr.getDqaErrorCode().equals(sam.getDqaDetectionCode())) {
                        //it's the same attribute!!! use it!
                        thisOne = sam;
                    }
                }
                //If you didn't find one, make a new one.
                if (thisOne == null) {
                    thisOne = new SenderDetectionMetrics();
                    thisOne.setDqaDetectionCode(attr.getDqaErrorCode());
                    thisOne.setSenderMetrics(metrics);
                    sams.add(thisOne);
                }
                int addedCounts = count + thisOne.getAttributeCount();
                thisOne.setAttributeCount(addedCounts);
            }
        }

        List<CollectionBucket> codes = incomingMetrics.getCodes().getCodeCountList();
        //logger.warn("codes: " + codes);

        List<CollectionBucket> remainingToProcess = new ArrayList<>(codes);
        List<CodeCount> counts = metrics.getCodes();

        //		for (CodeBucket cb : codes.getCodeCountList()) {
        //			//look and see if it already is represented in the set.  add to it if it is, add it if its not.
        for (CodeCount cc : counts) {
            CollectionBucket cb = new CollectionBucket(cc.getCodeType(), cc.getAttribute(), cc.getCodeValue());
            int idx = codes.indexOf(cb);
            if (idx > -1) {
                //add the counts to the list.
                CollectionBucket cbIn = codes.get(idx);
                cc.setCodeCount(cc.getCodeCount() + cbIn.getCount());
                remainingToProcess.remove(cbIn);
            }
        }

        for (CollectionBucket bucket : remainingToProcess) {
            //none of these are in the db yet.
            CodeCount cc = new CodeCount();
            cc.setAttribute(bucket.getAttribute());
            cc.setCodeType(bucket.getType());
            cc.setCodeValue(bucket.getValue());
            cc.setCodeCount(bucket.getCount());
            cc.setSenderMetrics(metrics);
            metrics.getCodes().add(cc);
        }

        //logger.warn("codes: " + metrics.getCodes());
        logger.info("Metrics: " + metrics);
        senderRepo.save(metrics);
        senderRepo.flush();

        return metrics;
    }
}
