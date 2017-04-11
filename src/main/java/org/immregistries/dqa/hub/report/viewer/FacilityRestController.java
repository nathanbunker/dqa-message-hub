package org.immregistries.dqa.hub.report.viewer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to get JSON objects related to the messages in the system. 
 * @author Josh
 *
 */
@RestController
@RequestMapping(value="/facilities")
public class FacilityRestController {

	private static final Logger LOGGER = LoggerFactory
		.getLogger(FacilityRestController.class);

	@Autowired
	ProviderJdbcRepository providers;
	
	@RequestMapping(method = RequestMethod.GET, value="")
	List<String> jsonFacilities(HttpServletRequest request) {
		List<String> tiList = new ArrayList<String>();
		tiList.addAll(providers.getActiveAuthorizedFacilities("DEFAULT"));
		return tiList;
    }
}
