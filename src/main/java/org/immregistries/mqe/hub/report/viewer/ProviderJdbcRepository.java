package org.immregistries.mqe.hub.report.viewer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderJdbcRepository {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProviderJdbcRepository.class);

  NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    JdbcTemplate template = new JdbcTemplate(dataSource);
    template.setFetchSize(4000);
    this.jdbcTemplate = new NamedParameterJdbcTemplate(template);
  }

  /**
   * Get message errors for this message.
   */
  private static final String getProviders =
      " select distinct name from SENDER";

  @Cacheable("facilitiesList")
  public List<String> getActiveFacilities() {

    List<String> facilityList = new ArrayList<String>();
    LOGGER.debug("JDBC Query: " + getProviders);
    try {
      List<String> rows = jdbcTemplate.query(getProviders, getFacilityRowMapper());
      facilityList.addAll(rows);
      LOGGER.info("facilities found: " + facilityList.size());
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("facilities not found for query: " + getProviders);
    }

    return facilityList;
  }

  /**
   * Get message errors for this message.
   */
  private static final String getAuthorizedTransferInterfaces =
      " select distinct name from sender";

  private static final String getAuthorizedTransferInterfacesForUser =
          " select distinct s.name " +
                  "from SENDER_METRICS sm " +
                  "join SENDER s on s.sender_id = sm.sender_sender_id where sm.username = :ssUserId";

  @Cacheable("facilitiesList")
  public List<String> getActiveAuthorizedFacilities(String ssUserId) {
    SqlParameterSource namedParameters = new MapSqlParameterSource()
        .addValue("ssUserId", ssUserId);

    List<String> facilityList = new ArrayList<String>();
    LOGGER.debug("JDBC Query: " + getAuthorizedTransferInterfaces);
    try {
      List<String> rows = jdbcTemplate
          .query(getAuthorizedTransferInterfaces, namedParameters, getFacilityRowMapper());
      facilityList.addAll(rows);
      LOGGER.info("facilities found: " + facilityList.size());
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("facilities not found for query: " + getProviders);
    }

    return facilityList;
  }

  public List<String> getActiveAuthorizedFacilitiesForUser(String ssUserId) {
    SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("ssUserId", ssUserId);

    List<String> facilityList = new ArrayList<String>();
    LOGGER.debug("JDBC Query: " + getAuthorizedTransferInterfacesForUser);
    try {
      List<String> rows = jdbcTemplate
              .query(getAuthorizedTransferInterfacesForUser, namedParameters, getFacilityRowMapper());
      facilityList.addAll(rows);
      LOGGER.info("facilities found: " + facilityList.size());
    } catch (EmptyResultDataAccessException er) {
      LOGGER.warn("facilities not found for query: " + getProviders);
    }

    return facilityList;
  }

  private RowMapper<String> getFacilityRowMapper() {
    return new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("name");
        return name;
      }
    };
  }
}
