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

  private boolean messageTextFilter;
  private boolean detectionIdFilter;

  public ViewerFilter(String rawAjaxFilterText) {
    if (StringUtils.isNotBlank(rawAjaxFilterText)) {
      setFiltersFromAjaxString(rawAjaxFilterText);
    }
  }

  public void setFiltersFromAjaxString(String ajaxString) {
    Map<String, String> filterMap = filterTextToMap(ajaxString);
    setMessageTextFilterFromString(filterMap.get("messageSearchText"));
    setDetectionIdFilterFromString(filterMap.get("detectionId"));
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

  public void setDetectionId(String detectionId) {
    this.detectionId = detectionId;
  }

  public boolean isDetectionIdFilter() {
    return detectionIdFilter;
  }

  public void setDetectionIdFilter(boolean detectionIdFilter) {
    this.detectionIdFilter = detectionIdFilter;
  }
}
