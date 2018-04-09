package org.immregistries.dqa.hub.report.viewer;

import java.util.ArrayList;
import java.util.List;

public class MessageListContainer {

  private int pageNumber;
  private int itemsPerPage;
  private long totalMessages;
  private int totalPages;

  private List<MessageListItem> messageList = new ArrayList<>();

  /**
   * @return the pageNumber
   */
  public int getPageNumber() {
    return pageNumber;
  }

  /**
   * @param pageNumber the pageNumber to set
   */
  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  /**
   * @return the itemsPerPage
   */
  public int getItemsPerPage() {
    return itemsPerPage;
  }

  /**
   * @param itemsPerPage the itemsPerPage to set
   */
  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  /**
   * @return the messageList
   */
  public List<MessageListItem> getMessageList() {
    return messageList;
  }

  /**
   * @param messageList the messageList to set
   */
  public void setMessageList(List<MessageListItem> messageList) {
    this.messageList = messageList;
  }

  /**
   * @return the totalPages
   */
  public int getTotalPages() {
    return totalPages;
  }

  /**
   * @param totalPages the totalPages to set
   */
  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  /**
   * @return the totalMessages
   */
  public long getTotalMessages() {
    return totalMessages;
  }

  /**
   * @param totalMessages the totalMessages to set
   */
  public void setTotalMessages(long totalMessages) {
    this.totalMessages = totalMessages;
  }

}
