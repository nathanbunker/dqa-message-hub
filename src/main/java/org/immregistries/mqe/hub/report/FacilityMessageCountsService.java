package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
public class FacilityMessageCountsService {

  private static final Logger logger = LoggerFactory.getLogger(FacilityMessageCountsService.class);

  @Autowired
  FacilityMessageCountsJpaRepository facilityMessageCountsRepo;
  @Autowired
  CodeCountJpaRepository codeRepo;
  @Autowired
  VaccineCountJpaRepository vaccCountRepo;

  public void saveMetrics(FacilityMessageCounts metrics) {
    logger.info("Metrics: " + metrics);
    facilityMessageCountsRepo.save(metrics);
    //facilityMessageCountsRepo.flush();
  }

  public MqeMessageMetrics getMetricsFor(String facility, Date day, String username) {
    return getMetricsFor(facility, day, day, username);
  }

  public MqeMessageMetrics getMetricsFor(String facility, Date dayStart, Date dayEnd, String username) {

    if (StringUtils.isBlank(facility)) {
      facility = "MQE";
    }

    List<FacilityMessageCounts> metricsList = facilityMessageCountsRepo.findByUsernameEqualsAndFacilityNameAndUploadDateGreaterThanEqualAndUploadDateLessThanEqual(username, facility, dayStart, dayEnd);
    logger.info("Metrics found for " + facility + " dayStart: " + dayStart + " dayEnd: " + dayEnd);
    logger.info("Metrics: " + metricsList);
    MqeMessageMetrics out = new MqeMessageMetrics();

    if (metricsList == null) {
      return out;
    }

    int patientCount = 0;
    int messageHeaderCount = 0;
    int vaccinationCount = 0;

    Map<MqeDetection, Integer> detectionCounts = out.getAttributeCounts();
    Map<CollectionBucket, Integer> codes = new HashMap<>();
    Map<VaccineBucket, Integer> vbList = new HashMap<>();

    for (FacilityMessageCounts fmc : metricsList) {
      patientCount += fmc.getPatientCount();
      messageHeaderCount += fmc.getPatientCount();
      vaccinationCount += fmc.getVaccinationCount();

      for (FacilityDetections sam : fmc.getDetectionMetrics()) {
        Detection ma = Detection.getByMqeErrorCodeString(sam.getMqeDetectionCode());
        //need to do set math, adding to the hash entry for each
        Integer dc = detectionCounts.get(ma);
        if (dc == null) {
          dc = 0;
        }
        dc += sam.getAttributeCount();
        detectionCounts.put(ma, dc);
      }

      List<FacilityVaccineCounts> vaccines = fmc.getFacilityVaccineCounts();
      logger.info("Vaccine counts: " + vaccines);
      for (FacilityVaccineCounts vc : vaccines) {
        VaccineBucket vb = new VaccineBucket(vc.getVaccineCvx(), vc.getAge(), vc.isAdministered(), vc.getCount());
        Integer vbc = vbList.get(vb);
        if (vbc != null) {
          vb.setCount(vb.getCount() + vbc);
        }
        vbList.put(vb, vb.getCount());
      }

      List<FacilityCodeCount> codesExisting = fmc.getCodes();
//      logger.info("SM Codes: " + codesExisting);
      for (FacilityCodeCount sam : codesExisting) {
        CollectionBucket cc = new CollectionBucket(sam.getCodeType(), sam.getAttribute(), sam.getCodeValue(), sam.getCodeCount());
        Integer ccc = codes.get(cc);
        if (ccc != null) {
          cc.setCount(cc.getCount()+ccc);
        }
        codes.put(cc, cc.getCount());
      }

    }

    out.setProvider(facility);
    out.getObjectCounts().put(VxuObject.PATIENT, patientCount);
    out.getObjectCounts().put(VxuObject.MESSAGE_HEADER, messageHeaderCount);
    out.getObjectCounts().put(VxuObject.VACCINATION, vaccinationCount);
    out.getCodes().setCodeCountList(new ArrayList<>(codes.keySet()));
    out.getVaccinations().addAll(new ArrayList<>(vbList.keySet()));

    return out;
  }

  @Autowired
  FacilityJpaRepository facilityRepo;

  public FacilityMessageCounts addToFacilityMessageCounts(String facility, Date day, String username, MqeMessageMetrics incomingMetrics) {

    FacilityMessageCounts metrics = facilityMessageCountsRepo.findByFacilityNameAndUploadDateAndUsername(facility, day, username);

    Facility s = facilityRepo.findByName(facility);

    if (s==null) {
      s = new Facility();
      s.setName(facility);
      s.setCreatedDate(new Date());
      facilityRepo.save(s);
    }


    if (metrics == null) {
      metrics = new FacilityMessageCounts();
      metrics.setFacility(s);
      metrics.setUploadDate(day);
      metrics.setUsername(username);
    }

    Map<VxuObject, Integer> objectCounts = incomingMetrics.getObjectCounts();
    Map<MqeDetection, Integer> detectionCounts = incomingMetrics.getAttributeCounts();
//    Map<Integer, Integer> patientAgeCounts = incomingMetrics.getPatientAgeCounts();

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
      List<FacilityDetections> dms = metrics.getDetectionMetrics();
      Integer count = detectionCounts.get(detection);

      if (count != null) {//It exists!!!!
        FacilityDetections thisOne = null;
        for (FacilityDetections sdm : dms) {
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
          thisOne = new FacilityDetections();
          thisOne.setMqeDetectionCode(detection.getMqeMqeCode());
          thisOne.setFacilityMessageCounts(metrics);
          dms.add(thisOne);
        }
        int addedCounts = count + thisOne.getAttributeCount();
        thisOne.setAttributeCount(addedCounts);
      }
    }

    //This is... from the incoming message.
    List<CollectionBucket> codes = incomingMetrics.getCodes().getCodeCountList();

    //This is from the incoming message, added to a list to keep track of.
    List<CollectionBucket> remainingToProcess = new ArrayList<>(codes);

    //This is the set of already existing codes in the database.
    List<FacilityCodeCount> counts = metrics.getCodes();

    //		loop over the existing codes that we know about.
    for (FacilityCodeCount cc : counts) {
      //Convert it from the DB object to a "collection bucket"
      CollectionBucket cb = new CollectionBucket(cc.getCodeType(), cc.getAttribute(), cc.getCodeValue());
      cb.setSource(cc.getOrigin());
      //Find the collection bucket in the existing code list... b/c this is how we tell if it already exists???
      int idx = codes.indexOf(cb);

      //If it's an entry in the set already, just aggregate it, and remove it from the "to be processed" list.
      if (idx > -1) {
        //add the counts to the list.
        CollectionBucket cbIn = codes.get(idx);
        cc.setCodeCount(cc.getCodeCount() + cbIn.getCount());
        remainingToProcess.remove(cbIn);
      }
    }

    for (CollectionBucket bucket : remainingToProcess) {
      //none of these are in the db yet.
      FacilityCodeCount cc = new FacilityCodeCount();
      cc.setAttribute(bucket.getAttribute());
      VxuField f = VxuField.getByName(bucket.getTypeCode());
      cc.setCodeType(f.toString());
      cc.setOrigin(f.getHl7Locator());
      cc.setCodeValue(bucket.getValue());
      cc.setCodeStatus(bucket.getStatus());
      cc.setCodeCount(bucket.getCount());
      cc.setFacilityMessageCounts(metrics);
      metrics.getCodes().add(cc);
    }

    this.aggregateVaccineCounts(metrics, incomingMetrics.getVaccinations().getCodeCountList());

    saveMetrics(metrics);
    logger.info("Metrics: " + metrics);
    facilityMessageCountsRepo.save(metrics);
//    facilityMessageCountsRepo.flush();

    return metrics;
  }

  private void aggregateVaccineCounts(FacilityMessageCounts metrics, List<VaccineBucket> vaccinations) {
    //Add to the facility vaccine metrics.
    List<VaccineBucket> vRemaining = new ArrayList<>(vaccinations);
    List<FacilityVaccineCounts> facilityVaccineCounts = metrics.getFacilityVaccineCounts();

    //		for (CodeBucket cb : codes.getCodeCountList()) {
    //			//look and see if it already is represented in the set.  add to it if it is, add it if its not.
    for (FacilityVaccineCounts vc : facilityVaccineCounts) {
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
      FacilityVaccineCounts vc = new FacilityVaccineCounts();
      vc.setVaccineCvx(bucket.getCode());
      vc.setAge(bucket.getAge());
      vc.setAdministered(bucket.isAdministered());
      vc.setFacilityMessageCounts(metrics);
      vc.setCount(1);
      metrics.getFacilityVaccineCounts().add(vc);
    }
  }

}
