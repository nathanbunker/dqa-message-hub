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
import org.immregistries.mqe.validator.engine.codes.CodeRepository;
import org.immregistries.mqe.validator.report.MqeMessageMetrics;
import org.immregistries.mqe.validator.report.codes.CodeCollection;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.validator.report.codes.VaccineBucket;
import org.immregistries.mqe.validator.report.codes.VaccineCollection;
import org.immregistries.mqe.vxu.VxuField;
import org.immregistries.mqe.vxu.VxuObject;
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
      String s = cb.getType();
      VxuField f = VxuField.getByName(s);
      CodesetType t = f.getCodesetType();
      if (t==null) {
        throw new RuntimeException("well...  this is embarrasing. there's a field with no type: " + f);
      }
      cb.setSource(f.getHl7Locator());
      cb.setType(t.getDescription());
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

      List<CollectionBucket> list = map.get(cb.getType());

      if (list == null) {
        list = new ArrayList<>();
        list.add(cb);
        map.put(cb.getType(), list);
      } else {
        //we want to aggregate, and ignore the attributes, so we have to add them up, since they're separate in the database.
        boolean found = false;
        for (CollectionBucket bucket : list) {
          if (bucket.getType().equals(cb.getType())
              && bucket.getValue().equals(cb.getValue())
              && bucket.getSource().equals(cb.getSource())
              ) {
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
    //map them to age groups.

    VaccinationCollectionMap vcm = new VaccinationCollectionMap();
    for (VaccineBucket vb : senderVaccines.getCodeCountList()) {
      if (vb.isAdministered()) {
        List<VaccineReportGroup> vrgList = VaccineReportGroup.get(vb.getCode());
        AgeCategory ac = AgeCategory.getCategoryForAge(vb.getAge());
        Map<VaccineReportGroup, VaccineAdministered> map = vcm.getMap().get(ac);
        if (map == null) {
          map = new HashMap<>();
          vcm.getMap().put(ac, map);
        }
        for (VaccineReportGroup vrg : vrgList) {
          VaccineAdministered va = map.get(vrg);

          if (va == null) {
            va = new VaccineAdministered();
            va.setAge(AgeCategory.getCategoryForAge(vb.getAge()));
            va.setVaccine(vrg);
            va.setCount(vb.getCount());
            // Placeholder for status
            va.setStatus("Placeholder");
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

}
