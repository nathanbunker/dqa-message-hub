package org.immregistries.mqe.hub.report.vaccineReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.immregistries.mqe.hub.report.vaccineReport.generated.AgeCategoryStatusType;
import org.immregistries.mqe.hub.report.vaccineReport.generated.AgeCategoryType;
import org.immregistries.mqe.hub.report.vaccineReport.generated.VaccineReportGroupType;
import org.immregistries.mqe.hub.report.vaccineReport.generated.VaccinesAdministeredExpectationType;

public class VaccineReportConfig {
  private VaccinesAdministeredExpectationType vaet;
  private List<AgeCategory> ageCategoryList = new ArrayList<>();
  private AgeCategory otherAgeCategory = new AgeCategory("Other", 0, 0);
  private Map<String, List<VaccineReportGroup>> map = new HashMap<>();
  private VaccineReportGroup unknownReportGroup = new VaccineReportGroup("Unknown");
  private Map<String, AgeCategory> ageCategoryMap = new HashMap<>();

  public VaccineReportConfig(VaccinesAdministeredExpectationType vaet) {
    this.vaet = vaet;
    if (vaet.getAgeCategories() != null) {
      int order = 0;
      for (AgeCategoryType act : vaet.getAgeCategories().getAgeCategory()) {
        AgeCategory ac = new AgeCategory(act.getLabel(), act.getAgeMin(), act.getAgeMax());
        order++;
        ac.setOrder(order);
        ageCategoryList.add(ac);
        ageCategoryMap.put(ac.getLabel(), ac);
      }
    }
    if (vaet.getVaccineReportGroup() != null) {
      for (VaccineReportGroupType vrgt : vaet.getVaccineReportGroup()) {
        List<String> cvxList = new ArrayList<>();
        for (Short s : vrgt.getCvx()) {
          String cvx = s.toString();
          if (cvx.length() == 1) {
            cvx = "0" + cvx;
          }
          if (cvx.length() > 0) {
            cvxList.add(cvx);
          }
        }
        String[] cvxs = new String[cvxList.size()];
        {
          int i = 0;
          for (String cvx : cvxList) {
            cvxs[i] = cvx;
            i++;
          }
        }
        if (cvxs.length > 0) {
          VaccineReportGroup vrg = new VaccineReportGroup(vrgt.getLabel(), cvxs);
          if (vrg.getLabel().equals("Unknown")) {
            unknownReportGroup = vrg;
          }
          for (String cvx : cvxList) {
            List<VaccineReportGroup> vaccineReportGroupList = map.get(cvx);
            if (vaccineReportGroupList == null) {
              vaccineReportGroupList = new ArrayList<>();
              map.put(cvx, vaccineReportGroupList);
            }
            vaccineReportGroupList.add(vrg);
          }
          for (AgeCategoryStatusType acst : vrgt.getAgeCategoryStatus()) {
            AgeCategory ageCategory = ageCategoryMap.get(acst.getLabel());
            VaccineReportStatus vaccineReportStatus =
                VaccineReportStatus.getVaccineReportStatus(acst.getStatus());
            if (ageCategory != null && vaccineReportStatus != null) {
              vrg.getVaccineReportStatusMap().put(ageCategory, vaccineReportStatus);
            }
          }

        }
      }
    }
  }

  public AgeCategory getCategoryForAge(int age) {
    for (AgeCategory ac : ageCategoryList) {
      if (ac.isInCategory(age)) {
        return ac;
      }
    }
    return otherAgeCategory;
  }

  public List<VaccineReportGroup> getVacineReportGroupList(String cvx) {
    List<VaccineReportGroup> v = map.get(cvx);
    if (v == null) {
      v = new ArrayList<>();
      v.add(unknownReportGroup);
    }
    return v;
  }
}
