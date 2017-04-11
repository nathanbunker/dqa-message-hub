
package org.immregistries.dqa.hub.report;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.immregistries.dqa.hub.rest.model.Hl7MessageSubmission;
import org.immregistries.dqa.hub.submission.Hl7MessageConsumer;
import org.immregistries.dqa.validator.report.DqaMessageMetrics;
import org.immregistries.dqa.validator.report.ReportScorer;
import org.immregistries.dqa.validator.report.VxuScoredReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/dbb")
@RestController
public class DatabaseController {
    private static final Log logger = LogFactory.getLog(DatabaseController.class);

    @Autowired
    private SenderMetricsService metricsSvc;
    @Autowired
    private SenderMetricsJpaRepository repo;
    
    @RequestMapping(value = "/senderMetrics")
    public List<SenderMetrics> getAllSM() throws Exception {
    	logger.info("ReportController exampleReport demo!");
    	return repo.findAll();
    }
    
}
