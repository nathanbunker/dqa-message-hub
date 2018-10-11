package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;
import org.immregistries.mqe.hub.report.vaccineReport.AgeCategory;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportBuilder;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportConfig;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportGroup;
import org.immregistries.mqe.hub.report.vaccineReport.VaccineReportStatus;
import org.immregistries.mqe.validator.engine.codes.CodeRepository;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.codes.CodeCollection;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.validator.report.codes.VaccineBucket;
import org.immregistries.mqe.validator.report.codes.VaccineCollection;
import org.immregistries.mqe.vxu.VxuField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/codes")
@RestController
public class DatabaseController {

  private static final Log logger = LogFactory.getLog(DatabaseController.class);

  @Autowired
  private SenderMetricsService metricsSvc;
  @Autowired
  private SenderMetricsJpaRepository repo;

  private final CodeRepository codeRepo = CodeRepository.INSTANCE;

  @RequestMapping(value = "/senderMetrics")
  public List<SenderMetrics> getAllSM() throws Exception {
    logger.info("DatabaseController exampleReport demo!");
    return repo.findAll();
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{providerKey}/{dateStart}/{dateEnd}")
  public CodeCollectionMap getCodesFor(@PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart,
      @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd) {
    logger.info("DatabaseController getCodesFor sender:" + providerKey + " dateStart: " + dateStart
        + " dateEnd: " + dateEnd);
    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd);
    CodeCollection senderCodes = allDaysMetrics.getCodes();
    Map<String, List<CollectionBucket>> map = new TreeMap<>();
    for (CollectionBucket cb : senderCodes.getCodeCountList()) {
      String s = cb.getTypeCode();
      VxuField f = VxuField.getByName(s);
      CodesetType t = f.getCodesetType();
      if (t == null) {
        throw new RuntimeException(
            "well...  this is embarrasing. there's a field with no type: " + f);
      }
      cb.setSource(f.getHl7Locator());
      cb.setTypeName(t.getDescription());
      Code c = codeRepo.getCodeFromValue(cb.getValue(), t);
      if (c != null) {
        if (c.getCodeStatus() != null && StringUtils.isNotBlank(c.getCodeStatus().getStatus())) {
          String status = c.getCodeStatus().getStatus();
          cb.setStatus(status);
        } else {
          cb.setStatus("Unrecognized");
        }
        String description = c.getLabel();
        cb.setLabel(description);
      } else {
        cb.setStatus("Unrecognized");
      }

      List<CollectionBucket> list = map.get(cb.getTypeName());

      if (list == null) {
        list = new ArrayList<>();
        list.add(cb);
        map.put(cb.getTypeName(), list);
      } else {
        // we want to aggregate, and ignore the attributes, so we have to add them up,
        // since they're separate in the database.
        boolean found = false;
        for (CollectionBucket bucket : list) {
          if (bucket.getTypeName().equals(cb.getTypeName()) && bucket.getValue().equals(cb.getValue())
              && bucket.getSource().equals(cb.getSource())) {
            bucket.setCount(bucket.getCount() + cb.getCount());
            found = true;
          }
        }

        if (!found) {
          list.add(cb);

        }
      }

    }

    CodeCollectionMap cm = new CodeCollectionMap();
    cm.setMap(map);
    return cm;
  }

  @RequestMapping(method = RequestMethod.GET,
      value = "/vaccinations/{providerKey}/{dateStart}/{dateEnd}")
  public VaccinationCollectionMap getVaccinationsFor(
      @PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart,
      @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd) {
    logger.info("DatabaseController getVaccinationsFor sender:" + providerKey + " dateStart: "
        + dateStart + " dateEnd: " + dateEnd);
    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd);
    VaccineCollection senderVaccines = allDaysMetrics.getVaccinations();
    senderVaccines = senderVaccines.reduce();
    // map them to age groups.

    VaccineReportConfig vaccineReportConfig =
        VaccineReportBuilder.INSTANCE.getDefaultVaccineReportConfig();
    VaccinationCollectionMap vcm = new VaccinationCollectionMap();
    for (VaccineBucket vb : senderVaccines.getCodeCountList()) {
      if (vb.isAdministered()) {
        List<VaccineReportGroup> vrgList =
            vaccineReportConfig.getVacineReportGroupList(vb.getCode());
        AgeCategory ac = vaccineReportConfig.getCategoryForAge(vb.getAge());
        Map<VaccineReportGroup, VaccineAdministered> map = vcm.getMap().get(ac);
        if (map == null) {
          map = new HashMap<>();
          vcm.getMap().put(ac, map);
        }
        for (VaccineReportGroup vrg : vrgList) {
          VaccineAdministered va = map.get(vrg);

          if (va == null) {
            va = new VaccineAdministered();
            AgeCategory age = vaccineReportConfig.getCategoryForAge(vb.getAge());
            va.setAge(age);
            va.setVaccine(vrg);
            va.setCount(vb.getCount());
            VaccineReportStatus vaccineReportStatus = vrg.getVaccineReportStatusMap().get(age);
            if (vaccineReportStatus == null) {
              va.setStatus("");
            } else {
              va.setStatus(vaccineReportStatus.getLabel());
            }
            map.put(vrg, va);
          } else {
            va.setCount(va.getCount() + vb.getCount());
          }
        }
      }
    }
    for (Map<VaccineReportGroup, VaccineAdministered> map : vcm.getMap().values()) {
      for (VaccineAdministered va : map.values()) {
        va.setPercent(0);
      }
    }
    return vcm;
  }

  @RequestMapping(method = RequestMethod.GET,
      value = "/vaccinationsExpected/{providerKey}/{dateStart}/{dateEnd}")
  public VaccinationExpectedCollectionMap getVaccinationsExpectedFor(
      @PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart,
      @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd) {
    logger.info("DatabaseController getVaccinationsExpectedFor sender:" + providerKey
        + " dateStart: " + dateStart + " dateEnd: " + dateEnd);
    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd);
    VaccineCollection senderVaccines = allDaysMetrics.getVaccinations();
    senderVaccines = senderVaccines.reduce();
    // map them to age groups.

    VaccineReportConfig vaccineReportConfig =
        VaccineReportBuilder.INSTANCE.getDefaultVaccineReportConfig();
    VaccinationExpectedCollectionMap vcm =
        getVaccinationCollectionMap(senderVaccines, vaccineReportConfig);
    List<VaccineReportGroup> vaccineReportGroupList =
        new ArrayList<VaccineReportGroup>(vcm.getMap().keySet());
    for (VaccineReportGroup vrg : vaccineReportGroupList) {
      boolean remove = true;
      for (AgeCategory ac : vaccineReportConfig.getAgeCategoryList()) {
        Map<AgeCategory, VaccineAdministered> map = vcm.getMap().get(vrg);
        VaccineAdministered va = map.get(ac);
        switch (va.getVaccineReportStatus()) {
          case EXPECTED:
          case POSSIBLE:
            remove = false;
            break;
          case NOT_EXPECTED:
          case NOT_POSSIBLE:
            break;
        }
      }
      if (remove) {
        vcm.getMap().remove(vrg);
      }

    }
    return vcm;
  }


  @RequestMapping(method = RequestMethod.GET,
      value = "/vaccinationsNotExpected/{providerKey}/{dateStart}/{dateEnd}")
  public VaccinationExpectedCollectionMap getVaccinationsNoExpectedFor(
      @PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart,
      @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd) {
    logger.info("DatabaseController getVaccinationsNotExpectedFor sender:" + providerKey
        + " dateStart: " + dateStart + " dateEnd: " + dateEnd);
    MqeMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd);
    VaccineCollection senderVaccines = allDaysMetrics.getVaccinations();
    senderVaccines = senderVaccines.reduce();
    // map them to age groups.

    VaccineReportConfig vaccineReportConfig =
        VaccineReportBuilder.INSTANCE.getDefaultVaccineReportConfig();
    VaccinationExpectedCollectionMap vcm =
        getVaccinationCollectionMap(senderVaccines, vaccineReportConfig);
    List<VaccineReportGroup> vaccineReportGroupList =
        new ArrayList<VaccineReportGroup>(vcm.getMap().keySet());
    for (VaccineReportGroup vrg : vaccineReportGroupList) {
      boolean remove = false;
      for (AgeCategory ac : vaccineReportConfig.getAgeCategoryList()) {
        Map<AgeCategory, VaccineAdministered> map = vcm.getMap().get(vrg);
        VaccineAdministered va = map.get(ac);
        switch (va.getVaccineReportStatus()) {
          case EXPECTED:
          case POSSIBLE:
            remove = true;
            break;
          case NOT_EXPECTED:
          case NOT_POSSIBLE:
            break;
        }
      }
      if (remove) {
        vcm.getMap().remove(vrg);
      }

    }
    return vcm;
  }


  private VaccinationExpectedCollectionMap getVaccinationCollectionMap(
      VaccineCollection senderVaccines, VaccineReportConfig vaccineReportConfig) {
    VaccinationExpectedCollectionMap vcm = new VaccinationExpectedCollectionMap();
    for (VaccineReportGroup vaccineReportGroup : vaccineReportConfig.getVaccineReportGroupList()) {
      if (vaccineReportGroup.getDisplayPriority() > 0) {
        Map<AgeCategory, VaccineAdministered> map = new HashMap<>();
        vcm.getMap().put(vaccineReportGroup, map);
      }
    }
    for (VaccineBucket vb : senderVaccines.getCodeCountList()) {
      if (vb.isAdministered()) {
        List<VaccineReportGroup> vrgList =
            vaccineReportConfig.getVacineReportGroupList(vb.getCode());
        AgeCategory ac = vaccineReportConfig.getCategoryForAge(vb.getAge());
        for (VaccineReportGroup vrg : vrgList) {
          Map<AgeCategory, VaccineAdministered> map = vcm.getMap().get(vrg);
          if (map == null) {
            map = new HashMap<>();
            vcm.getMap().put(vrg, map);
          }
          VaccineAdministered va = map.get(ac);

          if (va == null) {
            va = new VaccineAdministered();
            va.setPercent(0);
            AgeCategory age = vaccineReportConfig.getCategoryForAge(vb.getAge());
            va.setAge(age);
            va.setVaccine(vrg);
            va.setCount(vb.getCount());
            VaccineReportStatus vaccineReportStatus = vrg.getVaccineReportStatusMap().get(age);
            if (vaccineReportStatus == null) {
              va.setStatus("");
            } else {
              va.setStatus(vaccineReportStatus.getLabel());
            }
            map.put(ac, va);
          } else {
            va.setCount(va.getCount() + vb.getCount());
          }
        }
      }
    }
    for (VaccineReportGroup vrg : vcm.getMap().keySet()) {
      for (AgeCategory ac : vaccineReportConfig.getAgeCategoryList()) {
        Map<AgeCategory, VaccineAdministered> map = vcm.getMap().get(vrg);
        VaccineAdministered va = map.get(ac);
        if (va == null) {
          va = new VaccineAdministered();
          va.setPercent(0);
          va.setAge(ac);
          va.setVaccine(vrg);
          va.setCount(0);
          VaccineReportStatus vaccineReportStatus = vrg.getVaccineReportStatusMap().get(ac);
          va.setVaccineReportStatus(vaccineReportStatus);
          if (vaccineReportStatus == null) {
            va.setStatus("");
          } else {
            va.setStatus(vaccineReportStatus.getLabel());
          }
          map.put(ac, va);
        }
        if (va.getVaccineReportStatus() == null) {
          va.setVaccineReportStatus(VaccineReportStatus.NOT_EXPECTED);
        }
        String reportStyleClass = VaccineAdministered.REPORT_STYLE_CLASS_NOT_PRESENT_POSSIBLE;
        switch (va.getVaccineReportStatus()) {
          case EXPECTED:
            reportStyleClass =
                va.getCount() > 0 ? VaccineAdministered.REPORT_STYLE_CLASS_PRESENT_EXPECTED
                    : VaccineAdministered.REPORT_STYLE_CLASS_NOT_PRESENT_EXPECTED;
            break;
          case NOT_EXPECTED:
            reportStyleClass =
            va.getCount() > 0 ? VaccineAdministered.REPORT_STYLE_CLASS_PRESENT_NOT_EXPECTED
                : VaccineAdministered.REPORT_STYLE_CLASS_NOT_PRESENT_NOT_EXPECTED;
            break;
          case NOT_POSSIBLE:
            reportStyleClass =
            va.getCount() > 0 ? VaccineAdministered.REPORT_STYLE_CLASS_PRESENT_NOT_POSSIBLE
                : VaccineAdministered.REPORT_STYLE_CLASS_NOT_PRESENT_NOT_POSSIBLE;
            break;
          case POSSIBLE:
            reportStyleClass =
            va.getCount() > 0 ? VaccineAdministered.REPORT_STYLE_CLASS_PRESENT_POSSIBLE
                : VaccineAdministered.REPORT_STYLE_CLASS_NOT_PRESENT_POSSIBLE;
            break;
          default:

        }
        va.setReportStyleClass(reportStyleClass);
      }
    }
    return vcm;
  }

}
