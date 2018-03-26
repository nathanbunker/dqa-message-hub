package org.immregistries.dqa.hub.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.codebase.client.generated.Code;
import org.immregistries.dqa.codebase.client.reference.CodesetType;
import org.immregistries.dqa.validator.engine.codes.CodeRepository;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.immregistries.dqa.validator.report.codes.CodeCollection;
import org.immregistries.dqa.validator.report.codes.CollectionBucket;
import org.immregistries.dqa.validator.report.codes.VaccineBucket;
import org.immregistries.dqa.validator.report.codes.VaccineCollection;
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
    DqaMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd);
    CodeCollection senderCodes = allDaysMetrics.getCodes();
    Map<String, List<CollectionBucket>> map = new TreeMap<>();
    for (CollectionBucket cb : senderCodes.getCodeCountList()) {
      String s = cb.getType();
      CodesetType t = CodesetType.getByTypeCode(s);
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
        list = new ArrayList<CollectionBucket>();
        list.add(cb);
        map.put(cb.getType(), list);
      } else {
        //we want to aggregate, and ignore the attributes, so we have to add them up, since they're separate in the database.
        boolean found = false;
        for (CollectionBucket bucket : list) {
          if (bucket.getType().equals(cb.getType()) && bucket.getValue().equals(cb.getValue())) {
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


  @RequestMapping(method = RequestMethod.GET, value = "/vaccinations/{providerKey}/{dateStart}/{dateEnd}")
  public VaccinationCollectionMap getVaccinationsFor(
      @PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart,
      @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd) {
    logger.info(
        "DatabaseController getVaccinationsFor sender:" + providerKey + " dateStart: " + dateStart
            + " dateEnd: " + dateEnd);
    DqaMessageMetrics allDaysMetrics = metricsSvc.getMetricsFor(providerKey, dateStart, dateEnd);
    VaccineCollection senderVaccines = allDaysMetrics.getVaccinations();
    senderVaccines = senderVaccines.reduce();
    //map them to age groups.
    VaccinationCollectionMap vcm = new VaccinationCollectionMap();
    for (VaccineBucket vb : senderVaccines.getCodeCountList()) {
      AgeCategory ac = AgeCategory.getCategoryForAge(vb.getAge());
      List<VaccineBucket> list = vcm.getMap().get(ac);
      if (list == null) {
        list = new ArrayList<>();
        vcm.getMap().put(ac, list);
      }
      list.add(vb);
    }
    return vcm;
  }

}
