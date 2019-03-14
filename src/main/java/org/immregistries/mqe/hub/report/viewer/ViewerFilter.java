package org.immregistries.mqe.hub.report.viewer;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * This will hold the possible filters built into the system. 
 * @author Josh
 *
 */
public class ViewerFilter {

  private String messageText;
  private String detectionId;
  private String codeValue;
  private String codeType;
  private String vaccineGroup;
  private String vaccineGroupAge;

  private boolean messageTextFilter;
  private boolean detectionIdFilter;
  private boolean codeValueFilter;
  private boolean codeTypeFilter;
  private boolean vaccineGroupFilter;
  private boolean vaccineGroupAgeFilter;

  public ViewerFilter(String rawAjaxFilterText) {
    if (StringUtils.isNotBlank(rawAjaxFilterText)) {
      setFiltersFromAjaxString(rawAjaxFilterText);
    }
  }

  public void setFiltersFromAjaxString(String ajaxString) {
    Map<String, String> filterMap = filterTextToMap(ajaxString);
    setMessageTextFilterFromString(filterMap.get("messageSearchText"));
    setDetectionIdFilterFromString(filterMap.get("detectionId"));
    setCodeValueFilterFromString(filterMap.get("codeValue"));
    setCodeTypeFilterFromString(filterMap.get("codeType"));
    setVaccineGroupFilterFromString(filterMap.get("vaccineGroup"));
    setVaccineGroupAgeFilterFromString(filterMap.get("vaccineGroupAge"));
    
  }

  private void setVaccineGroupAgeFilterFromString(String age) {
	    this.vaccineGroupAge = age;
	    this.vaccineGroupAgeFilter = StringUtils.isNotBlank(age);
}

private void setVaccineGroupFilterFromString(String value) {
	    this.vaccineGroup = value;
	    this.vaccineGroupFilter = StringUtils.isNotBlank(value);
}

void setCodeValueFilterFromString(String codeValue) {
    this.codeValue = codeValue;
    this.codeValueFilter = StringUtils.isNotBlank(codeValue);
  }

  void setCodeTypeFilterFromString(String codeType) {
      this.codeType = codeType;
    this.codeTypeFilter = StringUtils.isNotBlank(codeType);
  }

  void setMessageTextFilterFromString(String messageTextSearch) {
    this.messageText = messageTextSearch;
    this.messageTextFilter = StringUtils.isNotBlank(messageText);
  }

  void setDetectionIdFilterFromString(String detectionIdStr) {
    this.detectionId = detectionIdStr;
    this.detectionIdFilter = StringUtils.isNotBlank(detectionIdStr);
  }

  public Map<String, String> filterTextToMap(String filterText) {

    Map<String, String> filterMap = new HashMap<String, String>();

    if (StringUtils.isNotBlank(filterText)) {
      if (filterText.split("\\|").length >= 1) {
        for (String filter : filterText.split("\\|")) {
          String property = StringUtils.substringBefore(filter, "::");
          String value = StringUtils.substringAfter(filter, "::");
          filterMap.put(property, value);
        }
      }
    }

    return filterMap;
  }

  /**
   * @return the messageText
   */
  public String getMessageText() {
    return messageText;
  }

  /**
   * @return the hasMessageTextFilter
   */
  public boolean isMessageTextFilter() {
    return messageTextFilter;
  }

  public String getDetectionId() {
    return detectionId;
  }

  public boolean isDetectionIdFilter() {
    return detectionIdFilter;
  }

  public String getCodeValue() {
    return codeValue;
  }

  public String getCodeType() {
    return codeType;
  }

  public boolean isCodeValueFilter() {
    return codeValueFilter;
  }

  public boolean isCodeTypeFilter() {
    return codeTypeFilter;
  }

public String getVaccineGroup() {
	return vaccineGroup;
}

public boolean isVaccineGroupFilter() {
	return vaccineGroupFilter;
}

public String getVaccineGroupAge() {
	return vaccineGroupAge;
}

public boolean isVaccineGroupAgeFilter() {
	return vaccineGroupAgeFilter;
}

}
