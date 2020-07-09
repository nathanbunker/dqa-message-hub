package org.immregistries.mqe.hub.report.viewer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.immregistries.mqe.hub.report.FacilitySummaryReport;
import org.immregistries.mqe.hub.report.FacilitySummaryReport.PatientSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ReportJdbcRepository {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ReportJdbcRepository.class);

  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    JdbcTemplate template = new JdbcTemplate(dataSource);
    template.setFetchSize(500);
    this.jdbcTemplate = new NamedParameterJdbcTemplate(template);
  }

  private static final String getAdultChildCounts =
            "     select "
          + " round(sum(case when minor_adult = 'ADULT' then counted else 0 end)) as ADULT_COUNT, "
          + " round(sum(case when minor_adult = 'CHILD' then counted else 0 end)) as CHILD_COUNT "
          + " from ( "
          + "     select count(*) as counted, case when PATIENT_AGE > 18 then 'ADULT' else 'CHILD' end as minor_adult "
          + " from MESSAGE_METADATA mm "
          + " join FACILITY_MESSAGE_COUNTS fmc on fmc.FACILITY_MESSAGE_COUNTS_ID = mm.FACILITY_MESSAGE_COUNTS_ID "
          + " join FACILITY f on f.facility_id = fmc.facility_id "
          + " where f.name = :providerIdentifier "
          + " and fmc.username = :username "
          + " and trunc(mm.input_time) >= :rangeStart "
          + " and trunc(mm.input_time) <= :rangeEnd "
          + " group by case when PATIENT_AGE > 18 then 'ADULT' else 'CHILD' end "
          + "    ) ";

  public PatientSummary getPatientAgesByProvider(final String providerIdentifier, final String username, final  Date rangeStart, final  Date rangeEnd ) {

    SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("rangeEnd", rangeEnd)
            .addValue("rangeStart", rangeStart)
            .addValue("username", username)
            .addValue("providerIdentifier", providerIdentifier);

    String query = getAdultChildCounts;
    LOGGER.debug("JDBC Query: " + query);
    try {
      return jdbcTemplate.queryForObject(query, namedParameters, new RowMapper<PatientSummary>() {
        public PatientSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
          int child = rs.getInt("adult_count");
          int adult = rs.getInt("CHILD_count");
          FacilitySummaryReport fsr = new FacilitySummaryReport();
          PatientSummary ps = fsr.getPatients();
          ps.setAdults(adult);
          ps.setChildren(child);
          ps.setTotal(adult+child);
          return ps;
        }
      });
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("Adult/Child count not found for [" + providerIdentifier + "]");
      return new FacilitySummaryReport().getPatients();
    }
  }
}
