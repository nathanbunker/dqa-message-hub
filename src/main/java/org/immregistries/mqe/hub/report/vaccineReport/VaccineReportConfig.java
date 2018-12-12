package org.immregistries.mqe.hub.report.vaccineReport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.immregistries.mqe.hub.report.vaccineReport.generated.AgeCategoryStatus;
import org.immregistries.mqe.hub.report.vaccineReport.generated.VaccinesAdministeredExpectation;

public class VaccineReportConfig {
  private VaccinesAdministeredExpectation vaet;
  private List<AgeCategory> ageCategoryList = new ArrayList<>();
  private AgeCategory otherAgeCategory = new AgeCategory("Other", 0, 0);
  private Map<String, List<VaccineReportGroup>> map = new HashMap<>();
  private VaccineReportGroup unknownReportGroup = new VaccineReportGroup("Unknown");
  private Map<String, AgeCategory> ageCategoryMap = new HashMap<>();
  private List<VaccineReportGroup> vaccineReportGroupList;

  public List<VaccineReportGroup> getVaccineReportGroupList() {
    return vaccineReportGroupList;
  }

  public List<VaccineReportGroup> getVaccineReportGroup() {
    return new ArrayList<VaccineReportGroup>();
  }

  public List<AgeCategory> getAgeCategoryList() {
    return ageCategoryList;
  }

  public VaccineReportConfig(VaccinesAdministeredExpectation vaet) {
    this.vaet = vaet;
    vaccineReportGroupList = new ArrayList<>();
    if (vaet.getAgeCategories() != null) {
      int order = 0;
      for (org.immregistries.mqe.hub.report.vaccineReport.generated.AgeCategory act : vaet
          .getAgeCategories().getAgeCategory()) {
        AgeCategory ac = new AgeCategory(act.getLabel(), (int) act.getAgeMin().longValue(),
            (int) act.getAgeMax().longValue());
        order++;
        ac.setOrder(order);
        ageCategoryList.add(ac);
        ageCategoryMap.put(ac.getLabel(), ac);
      }
    }
    if (vaet.getVaccineReportGroup() != null) {
      for (org.immregistries.mqe.hub.report.vaccineReport.generated.VaccineReportGroup vrgt : vaet
          .getVaccineReportGroup()) {
        List<String> cvxList = new ArrayList<>();
        for (BigInteger s : vrgt.getCvx()) {
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
          this.vaccineReportGroupList.add(vrg);
          if (vrgt.getDisplayPriority() == null) {
            vrg.setDisplayPriority(0);
          } else {
            vrg.setDisplayPriority((int) vrgt.getDisplayPriority().longValue());
          }
          if (vrg.getLabel().equals("Unknown")) {
            unknownReportGroup = vrg;
          }
          for (String cvx : cvxList) {
            List<VaccineReportGroup> mappedVrgList = this.map.get(cvx);
            if (mappedVrgList == null) {
              mappedVrgList = new ArrayList<>();
              this.map.put(cvx, mappedVrgList);
            }
            mappedVrgList.add(vrg);
          }
          for (AgeCategoryStatus acst : vrgt.getAgeCategoryStatus()) {
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
  
  public List<String> getCvxListForVaccineReportGroup(String label) {
	  for (VaccineReportGroup vrg : this.vaccineReportGroupList) {
		  if (vrg.getLabel().equals(label)) {
			  return Arrays.asList(vrg.getCvxList());
		  }
	  }
	  return new ArrayList<>();
  }
  
  public AgeCategory getAgeCategoryForLabel(String label) {
	    for (AgeCategory ac : ageCategoryList) {
	        if (ac.getLabel().equals(label)) {
	          return ac;
	        }
	      }
	      return otherAgeCategory;
  }
}
