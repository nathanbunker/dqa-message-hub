package org.immregistries.dqa.hub.report.viewer;

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
  private Integer statisticId;
  private String ackStatusCd1;
  private String ackStatusCd2;

  private boolean messageTextFilter;
  private boolean statisticFilter;
  private boolean ackStatusFilter;

  public ViewerFilter(String rawAjaxFilterText) {
    if (StringUtils.isNotBlank(rawAjaxFilterText)) {
      setFiltersFromAjaxString(rawAjaxFilterText);
    }
  }

  public void setFiltersFromAjaxString(String ajaxString) {
    Map<String, String> filterMap = filterTextToMap(ajaxString);
    setMessageTextFilterFromString(filterMap.get("messageSearchText"));
    setStatisticFilterFromString(filterMap.get("statisticId"));
    setAckStatusesFromString(filterMap.get("ackStatus"));
  }

  void setMessageTextFilterFromString(String messageTextSearch) {
    this.messageText = messageTextSearch;
    this.messageTextFilter = StringUtils.isNotBlank(messageText);
  }

  void setStatisticFilterFromString(String statisticIdStr) {
    if (StringUtils.isNumeric(statisticIdStr)) {
      this.statisticId = Integer.parseInt(statisticIdStr);
    }
    this.statisticFilter = statisticId != null && statisticId > -1;
  }

  void setAckStatusesFromString(String ackStatusCsv) {
    if (ackStatusCsv != null) {
      String[] list = ackStatusCsv.split(",");
      this.ackStatusCd1 = list[0];
      if (list.length > 1) {
        this.ackStatusCd2 = list[1];
      }
    }
    this.ackStatusFilter =
        StringUtils.isNotBlank(ackStatusCd2) || StringUtils.isNotBlank(ackStatusCd1);
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
   * @return the statisticId
   */
  public Integer getStatisticId() {
    return statisticId;
  }

  /**
   * @return the ackStatusCd1
   */
  public String getAckStatusCd1() {
    return ackStatusCd1;
  }

  /**
   * @return the ackStatusCd2
   */
  public String getAckStatusCd2() {
    return ackStatusCd2;
  }

  /**
   * @return the hasMessageTextFilter
   */
  public boolean isMessageTextFilter() {
    return messageTextFilter;
  }

  /**
   * @return the hasStatisticFilter
   */
  public boolean isStatisticFilter() {
    return statisticFilter;
  }

  /**
   * @return the hasAckStatusFilter
   */
  public boolean isAckStatusFilter() {
    return ackStatusFilter;
  }
}
