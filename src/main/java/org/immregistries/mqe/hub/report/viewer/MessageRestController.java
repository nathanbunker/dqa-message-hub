package org.immregistries.mqe.hub.report.viewer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.immregistries.mqe.hl7util.SeverityLevel;
import org.immregistries.mqe.hl7util.parser.MessageParser;
import org.immregistries.mqe.hl7util.parser.MessageParserHL7;
import org.immregistries.mqe.hl7util.parser.model.HL7MessagePart;
import org.immregistries.mqe.hl7util.parser.profile.generator.MessageProfileReader;
import org.immregistries.mqe.hl7util.parser.profile.generator.MessageProfileReaderNIST;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.settings.DetectionsSettings;
import org.immregistries.mqe.hub.settings.DetectionsSettingsJpaRepository;
import org.immregistries.mqe.validator.detection.Detection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to get JSON objects related to the messages in the system.
 *
 * @author Josh
 */
@RestController
@RequestMapping(value = "/api/messages")
//@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public class MessageRestController {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(MessageRestController.class);


  MessageParser parser = new MessageParserHL7();

  MessageProfileReader profileReader = new MessageProfileReaderNIST();

  @Autowired
  MessageMetadataJpaRepository messageRepo;

  @Autowired
  MessageRetrieverService messageRetreiver;
  
  @Autowired
  private DetectionsSettingsJpaRepository detectionsSettingsRepo;

  @RequestMapping(method = RequestMethod.GET, value = "/{providerKey}/date/{dateStart}/{dateEnd}/messages/{messages}/page/{page}")
  public MessageListContainer jsonMessagesGetter(HttpServletRequest request,
      @PathVariable("providerKey") String providerKey,
      @PathVariable("dateStart") @DateTimeFormat(pattern = "yyyyMMdd") Date dateStart,
      @PathVariable("dateEnd") @DateTimeFormat(pattern = "yyyyMMdd") Date dateEnd,
      @PathVariable("page") int pageNumber, @PathVariable("messages") int itemsCount,
      AuthenticationToken token,
      String filters) {

    LOGGER.info("jsonMessagesGetter - calling for messages.  ");

    ViewerFilter vf = new ViewerFilter(filters);

    MessageListContainer container = messageRetreiver.getMessages(providerKey, token.getPrincipal().getUsername(), dateStart, dateEnd, vf, pageNumber, itemsCount);

    LOGGER.info(
        "jsonMessagesGetter - Messages: " + container.getTotalMessages() + " pages: " + container
            .getTotalPages() + " current page: " + container.getPageNumber());
    return container;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  public MessageDetailItem jsonMessage(HttpServletRequest request,
      @PathVariable("id") long messageQueueId) {

    LOGGER.info("id coming in from the call: " + messageQueueId);
    MessageMetadata mq = messageRepo.getOne(messageQueueId);
    List<HL7MessagePart> vxuParts = parser.getMessagePartList(mq.getMessage());

    List<HL7LocationValue> vxuLocs = prepareHL7LocationList(vxuParts);

    MessageListItem mli = messageRetreiver.getMessageListItemFor(mq);

    MessageDetailItem mdi = new MessageDetailItem();
    mdi.setVxuParts(vxuLocs);
    mdi.setMessageMetaData(mli);
    mdi.setProviderKey(mq.getSenderMetrics().getSender().getName());
    for (MessageCode mc : mq.getCodes()) {
      CodeDetail cd = new CodeDetail();
      cd.setCodeCount(mc.getCodeCount());
      cd.setCodeStatus(mc.getCodeStatus());
      cd.setCodeType(mc.getCodeType());
      cd.setCodeValue(mc.getCodeValue());
      mdi.getCodes().add(cd);
    }

    List<MessageDetection> mdList = mq.getDetections();
    for (MessageDetection mdt : mdList) {
      Detection d = Detection.getByMqeErrorCodeString(mdt.getDetectionId());
      String l = mdt.getLocationTxt();
      DetectionDetail dd = new DetectionDetail();
      dd.setDetectionId(mdt.getDetectionId());
      if (d != null) {
        dd.setDescription(d.getDisplayText());
        dd.setName(d.toString());
        dd.setSeverity(d.getSeverity().getCode());
        SeverityLevel severityLevelOverride = getSeverityOverride(mq.getSenderMetrics().getSender().getName(), mdt.getDetectionId());
        if (severityLevelOverride != null) {
        	dd.setSeverity(severityLevelOverride.getCode());
        }
        dd.setLocation(l);
      }
        mdi.getDetections().add(dd);

    }

    String received = mq.getMessage().replaceAll("[\\r]+", "\n");
    mdi.setMessageReceived(received);
    mdi.setMessageResponse(mq.getResponse().replaceAll("[\\r]+", "\n"));

    return mdi;
  }
  
  private SeverityLevel getSeverityOverride(String provider, String mqeCode) {
	  SeverityLevel sl = null;
	DetectionsSettings setting = detectionsSettingsRepo.findByDetectionGroupNameAndMqeCode(provider, mqeCode);
		if (setting != null) {
			sl = SeverityLevel.findByLabel(setting.getSeverity());
		}
		return sl;
  }

  List<HL7LocationValue> prepareHL7LocationList(List<HL7MessagePart> list) {
    List<HL7LocationValue> newList = new ArrayList<HL7LocationValue>();
    int x = 0;
    for (HL7MessagePart part : list) {
      HL7LocationValue value = partToLocation(part, x++);
      newList.add(value);
    }
    return newList;
  }

  HL7LocationValue partToLocation(HL7MessagePart part, int idx) {
    HL7LocationValue val = new HL7LocationValue();

    val.setValue(part.getValue());
    val.setValueIndex(idx);

    val.setFieldRepetition(part.getFieldRepetition());
    val.setSegmentIndex(part.getSegmentIndex());

    val.setLocation(part.getLocationCd());

    String locationDesc = profileReader.getFieldDescription(part.getLocationCd());
    val.setLocationDescription(locationDesc);

    return val;
  }
}
