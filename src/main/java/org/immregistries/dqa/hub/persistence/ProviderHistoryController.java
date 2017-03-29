package org.immregistries.dqa.hub.persistence;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
@RequestMapping(value="/provider")
public class ProviderHistoryController {

	private static final Logger LOGGER = LoggerFactory
		.getLogger(ProviderHistoryController.class);
	
	@Autowired MessageHistoryJdbcRepository repo;
	
	@RequestMapping(method = RequestMethod.GET, value="/{providerKey}/counts/year/{year}")
	DqaMessageHistory jsonProviderMessageHistoryForYear(HttpServletRequest request, @PathVariable("providerKey") String providerKey,  @PathVariable("year") int year, String filters) {
		DqaMessageHistory history = new DqaMessageHistory();
		history.setYear(year);
		List<MessageCounts> counts = repo.getFacilityMessageHistory(providerKey, year);
		history.setMessageHistory(counts);
		
		return history;
	}
}
