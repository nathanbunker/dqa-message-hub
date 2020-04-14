package org.immregistries.mqe.hub.report.viewer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
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
public class MessageHistoryJdbcRepository {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(MessageHistoryJdbcRepository.class);

  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    JdbcTemplate template = new JdbcTemplate(dataSource);
    template.setFetchSize(500);
    this.jdbcTemplate = new NamedParameterJdbcTemplate(template);
  }

  /*
   * Beginning of the parts:
   */

  private static final String baseFromWhereStatement =
            " from MESSAGE_METADATA mm "
          + " join FACILITY_MESSAGE_COUNTS fmc on fmc.FACILITY_MESSAGE_COUNTS_ID = mm.FACILITY_MESSAGE_COUNTS_ID "
          + " join FACILITY f on f.facility_id = fmc.facility_id "
          + " where f.name = :providerIdentifier "
          + " and fmc.username = :username "
          + " and trunc(mm.input_time) >= :rangeStart "
          + " and trunc(mm.input_time) <= :rangeEnd ";

  private static final String getFacilityMessageCount =
          "   SELECT COUNT (*) AS MSG_COUNT "
          + baseFromWhereStatement;

  private static final String getFacilityMessageHistory =
       " SELECT COUNT (*) AS MSG_COUNT, TRUNC (mm.INPUT_TIME) AS day "
      + baseFromWhereStatement
      + " group by trunc(mm.input_time) "
      + " order by trunc(mm.input_time) asc ";

  public List<MessageCounts> getFacilityMessageHistoryByUsername(final String providerIdentifier, int year, String username) {

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("rangeEnd", rangeEnd)
            .addValue("username", username)
            .addValue("rangeStart", rangeStart)
            .addValue("providerIdentifier", providerIdentifier);

    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    ;

    String query = getFacilityMessageHistory;
    LOGGER.debug("JDBC Query: " + query);

    try {
      fmcList.addAll(jdbcTemplate.query(query, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id[" + providerIdentifier + "]");
    }

    return fmcList;
  }

  @Cacheable("facilityMessageCountByUsername")
  public int getFacilityMessageCountByUsername(final String providerIdentifier, Date rangeStart, Date rangeEnd, String username) {

    SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("rangeEnd", rangeEnd)
            .addValue("username", username)
            .addValue("rangeStart", rangeStart)
            .addValue("providerIdentifier", providerIdentifier);

    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();

    String query = getFacilityMessageCount;
    LOGGER.debug("JDBC Query: " + query);

    try {
      return jdbcTemplate.queryForObject(query, namedParameters, new RowMapper<Integer>() {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
          return rs.getInt("MSG_COUNT");
        }
      });
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id[" + providerIdentifier + "]");
      return 0;
    }
  }

  private Date getRangeStartForYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, 0, 1);
    return cal.getTime();
  }

  private Date getRangeEndForYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, 11, 30);
    return cal.getTime();
  }

  private RowMapper<MessageCounts> getHistoryRowMapper() {
    return new RowMapper<MessageCounts>() {
      public MessageCounts mapRow(ResultSet rs, int rowNum) throws SQLException {
        MessageCounts fmc = new MessageCounts();
        fmc.setCount(rs.getInt("MSG_COUNT"));
        fmc.setDay(rs.getDate("DAY"));
        return fmc;
      }
    };
  }
}
