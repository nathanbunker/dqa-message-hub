package org.immregistries.mqe.hub.report;

import org.immregistries.mqe.validator.report.ScoreReportable;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;


@Repository
public class ProviderReportJdbcService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProviderReportJdbcService.class);

    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String baseFromStatement =
            " from MESSAGE_METADATA mm "
            + " join FACILITY_MESSAGE_COUNTS fmc on fmc.FACILITY_MESSAGE_COUNTS_ID = mm.FACILITY_MESSAGE_COUNTS_ID "
            + " join FACILITY f on f.facility_id = fmc.facility_id ";

    private static final String baseWhereStatement =
            " where f.name = :providerIdentifier "
            + " and fmc.username = :username "
            + " and trunc(mm.input_time) >= :rangeStart "
            + " and trunc(mm.input_time) <= :rangeEnd ";



    @Autowired
    public void setDataSource(DataSource dataSource) {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      template.setFetchSize(500);
      this.jdbcTemplate = new NamedParameterJdbcTemplate(template);
    }

    ProviderReport getProvideReport(String providerKey, Date dateStart, Date dateEnd, String username) {
      ProviderReport report = new ProviderReport();
      report.setErrors(this.getDetectionsReport(providerKey, dateStart, dateEnd, username));
      report.setCodeIssues(this.getCodeIssuesReport());

      return report;
    }

    List<ScoreReportable> getDetectionsReport(String providerKey, Date dateStart, Date dateEnd, String username) {
        String querySelect = "SELECT SUM(ATTRIBUTE_COUNT) as count, MQE_DETECTION_CODE, coalesce(o1.SEVERITY , ds.SEVERITY) ";
        String joinFacilityMessageDetection =
                "JOIN FACILITY_DETECTION_COUNTS fdc on fdc.FACILITY_MESSAGE_COUNTS_ID = mm.FACILITY_MESSAGE_COUNTS_ID " +
                "JOIN DETECTION_SEVERITY_OVERRIDE o1 on o1.MQE_CODE = fdc.MQE_DETECTION_CODE " +
                "left JOIN DETECTION_SEVERITY_OVERRIDE_GROUP o1g on o1.DETECTION_SEVERITY_OVERRIDE_GROUP_ID = o1g.DETECTION_SEVERITY_OVERRIDE_GROUP_ID " +
                "AND o1g.NAME = :providerIdentifier " +
                "JOIN DETECTION_SEVERITY ds on ds.MQE_CODE = fdc.MQE_DETECTION_CODE ";
        String queryEnd = "GROUP BY MQE_DETECTION_CODE " +
                "ORDER BY count DESC " +
                "LIMIT 10";

        String query = querySelect + baseFromStatement + joinFacilityMessageDetection + baseWhereStatement + queryEnd;

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", dateEnd)
                .addValue("username", username)
                .addValue("rangeStart", dateStart)
                .addValue("providerIdentifier", providerKey);

        System.out.println(query);
        jdbcTemplate.query(query, namedParameters, new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
                return null;
            }
        });
        return null;
    }

    List<CollectionBucket> getCodeIssuesReport() {
        return null;
    }

}