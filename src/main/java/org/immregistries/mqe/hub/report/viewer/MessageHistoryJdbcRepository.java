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

  NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    JdbcTemplate template = new JdbcTemplate(dataSource);
    template.setFetchSize(500);
    this.jdbcTemplate = new NamedParameterJdbcTemplate(template);
  }

  /*
   * Beginning of the parts:
   */
  private static final String getFacilityMessageHistoryBEGIN =
      " SELECT COUNT (*) AS MSG_COUNT, TRUNC (mv.input_time) AS day "
          + " from MESSAGE_METADATA mv "
          + " join sender s on s.sender_id = mv.sender_sender_id "
          + " where s.name = :providerIdentifier "
          + " and trunc(mv.input_time) >= :rangeStart "
          + " and trunc(mv.input_time) <= :rangeEnd ";

  private static final String getFacilityMessageCount =
          " SELECT COUNT (*) AS MSG_COUNT from MESSAGE_METADATA mv "
                  + " join SENDER s on s.sender_id = mv.sender_sender_id "
                  + " where s.name = :providerIdentifier "
                  + " and trunc(mv.input_time) >= :rangeStart "
                  + " and trunc(mv.input_time) <= :rangeEnd ";

  private static final String getFacilityMessageHistoryMessageTextFilter =
      " and INSTR(UPPER(mv.message), UPPER(:messageSearchString)) > 1 ";

//	private static final String getErrorFLFilter = 
//			" and mv.TRANSFER_ERROR_FL = :errorFL ";

  private static final String getStatsFilter = ""
//    		" AND EXISTS (SELECT 1 FROM transfer_job_statistics ts JOIN TRANSFER_JOB tj ON ts.transfer_job_id = tj.TRANSFER_JOB_ID WHERE tj.message_batch_id = mv.message_batch_id and ts.statistic_value_tx > '0' and ts.statistic_id = :statId )"
      ;
  private static final String getAckStatusesFilters = "";
//			" AND mv.ack_status_id in (:status1, :status2) ";

  private static final String getFacilityMessageHistoryFINISH =
      " group by trunc(mv.input_time) "
          + " order by trunc(mv.input_time) asc ";


  protected Date getRangeStartForYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, 0, 1);
    return cal.getTime();
  }

  protected Date getRangeEndForYear(int year) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, 11, 30);
    return cal.getTime();
  }


  /**
   * gets a list of days and counts of messages.
   */
  private static final String getFacilityMessageHistoryAckStats =
      getFacilityMessageHistoryBEGIN
          + getStatsFilter
          + getAckStatusesFilters
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistoryAckStats(final String providerIdentifier,
      int year, Integer statId, String ackStatus1, String ackStatus2) {
    //Convert ack status codes to IDs.
    Integer ackSt1 = AckStatus.getIdForCode(ackStatus1);
    Integer ackSt2 = AckStatus.getIdForCode(ackStatus2);

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("status1", ackSt1)
        .addValue("status2", ackSt2)
        .addValue("statId", statId);
    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    LOGGER.debug("JDBC Query: " + getFacilityMessageHistoryAckStats);
    try {
      fmcList.addAll(jdbcTemplate
          .query(getFacilityMessageHistoryAckStats, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id["
          + providerIdentifier + "] and stat id[" + statId + "] ack1[" + ackStatus1 + "] ack2["
          + ackStatus2 + "]");
    }
    return fmcList;
  }

  /**
   * gets a list of days and counts of messages.
   */
  private static final String getFacilityMessageHistoryAckStatsText =
      getFacilityMessageHistoryBEGIN
          + getFacilityMessageHistoryMessageTextFilter
          + getStatsFilter
          + getAckStatusesFilters
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistoryAckStatsText(final String providerIdentifier,
      int year, String searchText, Integer statId, String ackStatus1, String ackStatus2) {
    //Convert ack status codes to IDs.
    Integer ackSt1 = AckStatus.getIdForCode(ackStatus1);
    Integer ackSt2 = AckStatus.getIdForCode(ackStatus2);

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("status1", ackSt1)
        .addValue("status2", ackSt2)
        .addValue("messageSearchString", searchText)
        .addValue("statId", statId);
    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    LOGGER.debug("JDBC Query: " + getFacilityMessageHistoryAckStatsText);
    try {
      fmcList.addAll(jdbcTemplate
          .query(getFacilityMessageHistoryAckStatsText, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id["
          + providerIdentifier + "] and stat id[" + statId + "] ack1[" + ackStatus1 + "] ack2["
          + ackStatus2 + "] srchText[" + searchText + "]");
    }
    return fmcList;
  }

  /**
   * gets a list of days and counts of messages.
   */
  private static final String getFacilityMessageHistoryStats =
      getFacilityMessageHistoryBEGIN
          + getStatsFilter
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistoryStats(final String providerIdentifier,
      int year, Integer statId) {

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("statId", statId);
    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    LOGGER.debug("JDBC Query: " + getFacilityMessageHistoryStats);
    try {
      fmcList.addAll(jdbcTemplate
          .query(getFacilityMessageHistoryStats, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id["
          + providerIdentifier + "] and stat id[" + statId + "]");
    }
    return fmcList;
  }


  /**
   * gets a list of days and counts of messages.
   */
  private static final String getFacilityMessageHistoryStatsAndMessageSearchText =
      getFacilityMessageHistoryBEGIN
          + getFacilityMessageHistoryMessageTextFilter
          + getStatsFilter
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistoryStatsAndMessageSearchText(
      final String providerIdentifier, int year, Integer statId, String searchText) {

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("statId", statId)
        .addValue("messageSearchString", searchText);
    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    LOGGER.debug("JDBC Query: " + getFacilityMessageHistoryStatsAndMessageSearchText);
    try {
      fmcList.addAll(jdbcTemplate
          .query(getFacilityMessageHistoryStatsAndMessageSearchText, namedParameters,
              getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn(
          "message history not found for interface id[" + providerIdentifier + "] and stat id["
              + statId + "] searchTxt[" + searchText + "]");
    }
    return fmcList;
  }


  /**
   * gets a list of days and counts of messages.
   */
  private static final String getFacilityMessageHistorySearchText =
      getFacilityMessageHistoryBEGIN
          + getFacilityMessageHistoryMessageTextFilter
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistorySearchText(final String providerIdentifier,
      int year, String searchText) {

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("messageSearchString", searchText);

    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    ;

    LOGGER.debug("JDBC Query: " + getFacilityMessageHistorySearchText);
    try {
      fmcList.addAll(jdbcTemplate
          .query(getFacilityMessageHistorySearchText, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn(
          "message history not found for interface id[" + providerIdentifier + "] searchTxt["
              + searchText + "]");
    }

    return fmcList;
  }


  /**
   * gets a list of days and counts of messages.
   */
  private static final String getFacilityMessageHistory =
      getFacilityMessageHistoryBEGIN
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistory(final String providerIdentifier, int year) {

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
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

  @Cacheable("facilityMessageCount")
  public int getFacilityMessageCount(final String providerIdentifier, Date rangeStart, Date rangeEnd) {

    SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("rangeEnd", rangeEnd)
            .addValue("rangeStart", rangeStart)
            .addValue("providerIdentifier", providerIdentifier);

    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();
    ;

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

  /**
   * gets a list of days and counts of messages.
   */
  private static final String getAckStatusAndMessageSearchQuery =
      getFacilityMessageHistoryBEGIN
          + getFacilityMessageHistoryMessageTextFilter
          + getAckStatusesFilters
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistoryAckStatusAndMessageSearchText(
      final String providerIdentifier, int year, String messageSearchString, String ackStatus1,
      String ackStatus2) {

    //Convert ack status codes to IDs.
    Integer ackSt1 = AckStatus.getIdForCode(ackStatus1);
    Integer ackSt2 = AckStatus.getIdForCode(ackStatus2);

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("messageSearchString", messageSearchString)
        .addValue("status1", ackSt1)
        .addValue("status2", ackSt2);

    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();

    String query = getAckStatusAndMessageSearchQuery;

    LOGGER.debug("JDBC Query: " + query);
    try {
      fmcList.addAll(jdbcTemplate.query(query, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id["
          + providerIdentifier + "] and message text[" + messageSearchString + "]");
    }

    return fmcList;
  }

  /**
   * gets a list of days and counts of messages.
   */
  private static final String getAckStatusQuery =
      getFacilityMessageHistoryBEGIN
          + getAckStatusesFilters
          + getFacilityMessageHistoryFINISH;

  @Cacheable("facilityHistory")
  public List<MessageCounts> getFacilityMessageHistoryAckStatus(final String providerIdentifier,
      int year, String ackStatus1, String ackStatus2) {

    //Convert ack status codes to IDs.
    Integer ackSt1 = AckStatus.getIdForCode(ackStatus1);
    Integer ackSt2 = AckStatus.getIdForCode(ackStatus2);

    Date rangeStart = getRangeStartForYear(year);
    Date rangeEnd = getRangeEndForYear(year);

    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("rangeEnd", rangeEnd)
        .addValue("rangeStart", rangeStart)
        .addValue("providerIdentifier", providerIdentifier)
        .addValue("status1", ackSt1)
        .addValue("status2", ackSt2);

    List<MessageCounts> fmcList = new ArrayList<MessageCounts>();

    String query = getAckStatusQuery;

    LOGGER.debug("JDBC Query: " + query);
    try {
      fmcList.addAll(jdbcTemplate.query(query, namedParameters, getHistoryRowMapper()));
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("message history not found for interface id["
          + providerIdentifier + "] and ackStatus1[" + ackStatus1 + "] ackStatus2[" + ackStatus2
          + "]");
    }

    return fmcList;
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
