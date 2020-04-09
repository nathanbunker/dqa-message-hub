package org.immregistries.mqe.hub.report.viewer;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to get JSON objects related to the messages in the system. 
 */

@RestController
@RequestMapping(value = "/api/provider")
public class ProviderHistoryController {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProviderHistoryController.class);

  @Autowired
  MessageHistoryJdbcRepository repo;

  @RequestMapping(method = RequestMethod.GET, value = "/{providerKey}/counts/year/{year}")
  MqeMessageHistory jsonProviderMessageHistoryForYear(HttpServletRequest request,
                                                      @PathVariable("providerKey") String providerKey, @PathVariable("year") int year,
                                                      String filters, AuthenticationToken token) {
    MqeMessageHistory history = new MqeMessageHistory();
    history.setYear(year);
    List<MessageCounts> counts = repo.getFacilityMessageHistoryByUsername(providerKey, year, token.getPrincipal().getUsername());
    history.setMessageHistory(counts);

    return history;
  }
}
