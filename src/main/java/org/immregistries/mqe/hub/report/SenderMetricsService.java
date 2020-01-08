package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.util.validation.MqeDetection;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.vxu.VxuField;
import org.immregistries.mqe.vxu.VxuObject;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.validator.report.codes.VaccineBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SenderMetricsService {

  private static final Logger logger = LoggerFactory.getLogger(SenderMetricsService.class);

  @Autowired
  SenderMetricsJpaRepository senderMetricsRepo;
  @Autowired
  CodeCountJpaRepository codeRepo;
  @Autowired
  VaccineCountJpaRepository vaccCountRepo;

  public void saveMetrics(SenderMetrics metrics) {
    logger.info("Metrics: " + metrics);
    senderMetricsRepo.save(metrics);
    //senderMetricsRepo.flush();
  }

  public MqeMessageMetrics getMetricsFor(String sender, Date day) {
    return getMetricsFor(sender, day, day);
  }

  public MqeMessageMetrics getMetricsFor(String sender, Date dayStart, Date dayEnd) {

    if (StringUtils.isBlank(sender)) {
      sender = "MQE";
    }

    SenderMetrics metrics = senderMetricsRepo
        .findBySenderNameAndMetricsDateGreaterThanEqualAndMetricsDateLessThanEqual(sender, dayStart,
            dayEnd);
    logger.info("Metrics found for " + sender + " dayStart: " + dayStart + " dayEnd: " + dayEnd);
    logger.info("Metrics: " + metrics);
    MqeMessageMetrics out = new MqeMessageMetrics();
    if (metrics == null) {
      return out;
    }

    out.setProvider(sender);
    out.getObjectCounts().put(VxuObject.PATIENT, metrics.getPatientCount());
    out.getObjectCounts().put(VxuObject.MESSAGE_HEADER, metrics.getPatientCount());
    out.getObjectCounts().put(VxuObject.VACCINATION, metrics.getVaccinationCount());
    Map<MqeDetection, Integer> attrCounts = out.getAttributeCounts();

    for (SenderDetectionMetrics sam : metrics.getDetectionMetrics()) {
      Detection ma = Detection.getByMqeErrorCodeString(sam.getMqeDetectionCode());
      attrCounts.put(ma, sam.getAttributeCount());
    }

    List<CodeCount> codesExisting = metrics.getCodes();
    logger.info("SM Codes: " + codesExisting);
    List<CollectionBucket> codes = new ArrayList<>();
    for (CodeCount sam : codesExisting) {
      CollectionBucket cc = new CollectionBucket(sam.getCodeType(), sam.getAttribute(),
          sam.getCodeValue(), sam.getCodeCount());
      codes.add(cc);
    }
    out.getCodes().setCodeCountList(codes);

    List<VaccineCount> vaccines = metrics.getVaccineCounts();
    logger.info("Vaccine counts: " + vaccines);
    List<VaccineBucket> vbList = new ArrayList<>();
    for (VaccineCount vc : vaccines) {
      VaccineBucket vb = new VaccineBucket(vc.getVaccineCvx(), vc.getAge(), vc.isAdministered(),
          vc.getCount());
      vbList.add(vb);
    }
    out.getVaccinations().addAll(vbList);

    return out;
  }

  @Autowired
  SenderJpaRepository senderRepo;

  public SenderMetrics addToSenderMetrics(String sender, Date day,
      MqeMessageMetrics incomingMetrics) {
    SenderMetrics metrics = senderMetricsRepo.findBySenderNameAndMetricsDate(sender, day);

    Sender s = senderRepo.findByName(sender);

    if (s==null) {
      s = new Sender();
      s.setName(sender);
      s.setCreatedDate(new Date());
      senderRepo.save(s);
    }


    if (metrics == null) {
      metrics = new SenderMetrics();
      metrics.setSender(s);
      metrics.setMetricsDate(day);
    }

    Map<VxuObject, Integer> objectCounts = incomingMetrics.getObjectCounts();
    Map<MqeDetection, Integer> detectionCounts = incomingMetrics.getAttributeCounts();
    Map<Integer, Integer> patientAgeCounts = incomingMetrics.getPatientAgeCounts();

    for (VxuObject io : objectCounts.keySet()) {
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

    for (MqeDetection detection : detectionCounts.keySet()) {
      if (detection == null) {
        continue;
      }
      //find the right metrics object...
      List<SenderDetectionMetrics> dms = metrics.getDetectionMetrics();
      Integer count = detectionCounts.get(detection);

      if (count != null) {//It exists!!!!
        SenderDetectionMetrics thisOne = null;
        for (SenderDetectionMetrics sdm : dms) {
          if (sdm != null &&
              detection.getMqeMqeCode().equals(
                  sdm.getMqeDetectionCode()
              )) {
            //it's the same attribute!!! use it!
            thisOne = sdm;
          }
        }
        //If you didn't find one, make a new one.
        if (thisOne == null) {
          thisOne = new SenderDetectionMetrics();
          thisOne.setMqeDetectionCode(detection.getMqeMqeCode());
          thisOne.setSenderMetrics(metrics);
          dms.add(thisOne);
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
      CollectionBucket cb = new CollectionBucket(cc.getCodeType(), cc.getAttribute(),
          cc.getCodeValue());
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
      VxuField f = VxuField.getByName(bucket.getTypeCode());
      cc.setCodeType(f.toString());
      cc.setOrigin(f.getHl7Locator());
      cc.setCodeValue(bucket.getValue());
      cc.setCodeCount(bucket.getCount());
      cc.setSenderMetrics(metrics);
      metrics.getCodes().add(cc);
    }

    this.aggregateVaccineCounts(metrics, incomingMetrics.getVaccinations().getCodeCountList());

    saveMetrics(metrics);
    logger.info("Metrics: " + metrics);
    senderMetricsRepo.save(metrics);
    //senderMetricsRepo.flush();

    return metrics;
  }

  private void aggregateVaccineCounts(SenderMetrics metrics, List<VaccineBucket> vaccinations) {
    //Add to the sender vaccine metrics.
    List<VaccineBucket> vRemaining = new ArrayList<>(vaccinations);
    List<VaccineCount> vaccineCounts = metrics.getVaccineCounts();

    //		for (CodeBucket cb : codes.getCodeCountList()) {
    //			//look and see if it already is represented in the set.  add to it if it is, add it if its not.
    for (VaccineCount vc : vaccineCounts) {
      VaccineBucket cb = new VaccineBucket(vc.getVaccineCvx(), vc.getAge(), vc.isAdministered());
      int idx = vaccinations.indexOf(cb);
      if (idx > -1) {
        //add the counts to the list.
        VaccineBucket cbIn = vaccinations.get(idx);
        vc.setCount(vc.getCount() + cbIn.getCount());
        vRemaining.remove(cbIn);
      }
    }

    for (VaccineBucket bucket : vRemaining) {
      //none of these are in the db yet.
      VaccineCount vc = new VaccineCount();
      vc.setVaccineCvx(bucket.getCode());
      vc.setAge(bucket.getAge());
      vc.setAdministered(bucket.isAdministered());
      vc.setSenderMetrics(metrics);
      vc.setCount(1);
      metrics.getVaccineCounts().add(vc);
    }
  }

}
