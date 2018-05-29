package org.immregistries.dqa.hub.report.viewer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.immregistries.dqa.hl7util.parser.HL7MessageMap;
import org.immregistries.dqa.hl7util.parser.MessageParser;
import org.immregistries.dqa.hl7util.parser.MessageParserHL7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MessageRetrieverService {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(MessageRetrieverService.class);

  @Autowired
  MessageMetadataJpaRepository mmRepo;
  private final MessageParser parser = new MessageParserHL7();

  public MessageListContainer getMessages(String providerKey, Date dateCreated,
      ViewerFilter filters, int pageNumber, int itemsCount) {
    LOGGER.info("getMessages");
    //Decide which method to call based on what gets sent in:
    List<MessageMetadata> messages = new ArrayList<>();
    Page<MessageMetadata> mvpage = null;

    Pageable pager = new PageRequest(pageNumber, itemsCount, Sort.Direction.ASC, "inputTime");

    LOGGER.info("getMessages NO FILTER");
    mvpage = mmRepo.findByProviderAndDatePaged(providerKey, dateCreated, pager);

    long totalElements = 0;
    int totalPages = 0;

    if (mvpage != null) {
      messages = mvpage.getContent();
      totalElements = mvpage.getTotalElements();
      totalPages = mvpage.getTotalPages();
    }

    LOGGER.info("Messages not null? " + (messages != null));
    LOGGER.info("Messages length: " + (messages != null ? messages.size() : 0));

    MessageListContainer container = new MessageListContainer();
    container.setTotalMessages(totalElements);
    container.setTotalPages(totalPages);
    container.setItemsPerPage(itemsCount);
    container.setPageNumber(pageNumber);

    List<MessageListItem> items = new ArrayList<MessageListItem>();

    for (MessageMetadata entry : messages) {
      items.add(getMessageListItemFor(entry));
    }
    container.getMessageList().addAll(items);

    return container;
  }

  MessageListItem getMessageListItemFor(MessageMetadata mv) {
    LOGGER.info("getMessageListItemFor");
    MessageListItem item = new MessageListItem();

    item.setId(mv.getId());
    item.setReceived(mv.getInputTime());
//		item.setMcirError("Y".equals(mv.getTransferErrorFl()));

    HL7MessageMap map = parser.getMessagePartMap(mv.getMessage());
    HL7MessageMap ackMap = parser.getMessagePartMap(mv.getResponse());

    String messageControlId = map.getValue("MSH-10");
    item.setMessageControlId(messageControlId);

//		item.setId(mq.getMessageQueueId());
    item.setAckStatus(ackMap.getValue("MSA-1"));
    item.setCvxList(generateCVXListString(map));
    item.setReceived(mv.getInputTime());
//		item.setReceivedDateDisplay(mq.getDateCreated().toString());

    String patientFirst = map.getValue("PID-5-2");
    String patientLast = map.getValue("PID-5-1");
    item.setPatientName(patientLast + ", " + patientFirst);

    return item;
  }

  String generateCVXListString(HL7MessageMap map) {
    LOGGER.info("generateCVXListString");
    StringBuilder list = new StringBuilder();

    List<String> segments = map.getMessageSegments();
    int cnt = 0;
    for (int i = 0; i < segments.size(); i++) {
      String segName = segments.get(i);
      switch (segName) {
        case "RXA":
//					LOGGER.info("Getting cvx info for segment : " + i);
          String cvx = map.getValue("RXA-5-2", i, 1);
          String cvxCd = map.getValue("RXA-5-1", i, 1);
          if (cnt >= 1) {
            list.append(", " + "\n");
          }
          list.append(cvxCd + " (" + cvx + ")");
          cnt++;
      }
    }

    return list.toString();
  }

}
