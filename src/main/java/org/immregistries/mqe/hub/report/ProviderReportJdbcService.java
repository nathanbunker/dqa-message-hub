package org.immregistries.mqe.hub.report;

import org.apache.commons.lang3.StringUtils;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;
import org.immregistries.mqe.hub.report.viewer.MessageHistoryJdbcRepository;
import org.immregistries.mqe.validator.engine.codes.CodeRepository;
import org.immregistries.mqe.validator.report.ScoreReportable;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
import org.immregistries.mqe.vxu.VxuField;
import org.immregistries.mqe.vxu.VxuObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final CodeRepository codeRepo = CodeRepository.INSTANCE;

    @Autowired
    MessageHistoryJdbcRepository messageHistoryJdbcRepository;

    @Autowired
    public void setDataSource(DataSource dataSource) {
      JdbcTemplate template = new JdbcTemplate(dataSource);
      template.setFetchSize(500);
      this.jdbcTemplate = new NamedParameterJdbcTemplate(template);
    }

    ProviderReport getProvideReport(String providerKey, Date dateStart, Date dateEnd, String username) {
        ProviderReport report = new ProviderReport();
        report.setProvider(providerKey);
        report.setStartDate(dateStart);
        report.setEndDate(dateStart);
        report.setNumberOfMessage(messageHistoryJdbcRepository.getFacilityMessageCountByUsername(providerKey, dateStart, dateEnd, username));
        report.setNumberOfErrors(this.getErrorCount(providerKey, dateStart, dateEnd, username));
        report.setErrors(this.getDetectionsReport(providerKey, dateStart, dateEnd, username));
        report.setCodeIssues(this.getCodeIssuesReport(providerKey, dateStart, dateEnd, username));
        report.setVaccinationCodes(this.getVaccineCodesBuckets(providerKey, dateStart, dateEnd, username));
        /* new data */
        return report;
    }

    List<ScoreReportable> getDetectionsReport(String providerKey, Date dateStart, Date dateEnd, String username) {
        String query =
                "SELECT MQE_DETECTION_CODE, SUM(ATTRIBUTE_COUNT) as COUNT, coalesce(o1.SEVERITY , ds.SEVERITY) as SEVERITY, MESSAGE " +
                "FROM FACILITY_MESSAGE_COUNTS fmc " +
                "JOIN FACILITY f on f.facility_id = fmc.facility_id " +
                "JOIN FACILITY_DETECTION_COUNTS fdc on fdc.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID " +

                "LEFT JOIN DETECTION_SEVERITY_OVERRIDE o1 on o1.MQE_CODE = fdc.MQE_DETECTION_CODE " +
                "LEFT JOIN DETECTION_SEVERITY_OVERRIDE_GROUP o1g on o1.DETECTION_SEVERITY_OVERRIDE_GROUP_ID = o1g.DETECTION_SEVERITY_OVERRIDE_GROUP_ID " +
                "AND o1g.NAME = 'Unspecified' " +
                "JOIN DETECTION_SEVERITY ds on ds.MQE_CODE = fdc.MQE_DETECTION_CODE " +

                "JOIN ( " +
                        "SELECT DETECTION_ID, FACILITY_MESSAGE_COUNTS_ID, MAX(MESSAGE) as MESSAGE FROM MESSAGE_METADATA mm " +
                        "LEFT JOIN MESSAGE_DETECTION md on md.MESSAGE_METADATA_ID  = mm.MESSAGE_METADATA_ID " +
                        "GROUP BY DETECTION_ID " +
                " ) em on em.DETECTION_ID = MQE_DETECTION_CODE " +

                "AND em.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID " +
                "WHERE f.name = :providerIdentifier " +
                "AND fmc.username = :username " +
                "AND trunc(fmc.upload_date) >= :rangeStart " +
                "AND trunc(fmc.upload_date) <= :rangeEnd " +
                "AND coalesce(o1.SEVERITY , ds.SEVERITY) = 'ERROR' " +
                "GROUP BY MQE_DETECTION_CODE, em.MESSAGE " +
                "ORDER BY count DESC " +
                "LIMIT 10";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", dateEnd)
                .addValue("username", username)
                .addValue("rangeStart", dateStart)
                .addValue("providerIdentifier", providerKey);

        return jdbcTemplate.query(query, namedParameters, new RowMapper<ScoreReportable>() {
            @Override
            public ScoreReportable mapRow(ResultSet resultSet, int i) throws SQLException {
                ScoreReportable scoreReportable = new ScoreReportable(
                        resultSet.getString("SEVERITY"),
                        resultSet.getString("MQE_DETECTION_CODE"),
                        resultSet.getString("MESSAGE"),
                        resultSet.getInt("COUNT")
                );
                return scoreReportable;
            }
        });
    }

    int getErrorCount(String providerKey, Date dateStart, Date dateEnd, String username) {
        String query = "SELECT SUM(ATTRIBUTE_COUNT) as ERROR_COUNT\n" +
                "\n" +
                "FROM FACILITY_MESSAGE_COUNTS fmc\n" +
                "JOIN FACILITY f on f.facility_id = fmc.facility_id\n" +
                "JOIN FACILITY_DETECTION_COUNTS fdc on fdc.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n" +
                "\n" +
                "\n" +
                "LEFT JOIN DETECTION_SEVERITY_OVERRIDE o1 on o1.MQE_CODE = fdc.MQE_DETECTION_CODE\n" +
                "LEFT JOIN DETECTION_SEVERITY_OVERRIDE_GROUP o1g on o1.DETECTION_SEVERITY_OVERRIDE_GROUP_ID = o1g.DETECTION_SEVERITY_OVERRIDE_GROUP_ID\n" +
                "AND o1g.NAME = 'Unspecified'\n" +
                "JOIN DETECTION_SEVERITY ds on ds.MQE_CODE = fdc.MQE_DETECTION_CODE\n" +
                "\n" +
                "\n" +
                "WHERE f.name = 'Unspecified'\n" +
                "AND fmc.username = 'hossamt'\n" +
                "AND trunc(fmc.upload_date) >= '20200708'\n" +
                "AND trunc(fmc.upload_date) <= '20200708'\n" +
                "AND ds.SEVERITY = 'ERROR'\n";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", dateEnd)
                .addValue("username", username)
                .addValue("rangeStart", dateStart)
                .addValue("providerIdentifier", providerKey);

        try {
            return jdbcTemplate.queryForObject(query, namedParameters, new RowMapper<Integer>() {
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("ERROR_COUNT");
                }
            });
        } catch (EmptyResultDataAccessException er) {
            LOGGER.warn("no errors found");
            return 0;
        }
    }



    List<CollectionBucket> getCodeIssuesReport(String providerKey, Date dateStart, Date dateEnd, String username) {
        String query = "SELECT ATTRIBUTE, fcc.CODE_TYPE, SUM(CODE_COUNT) as count, fcc.CODE_STATUS, fcc.CODE_VALUE, MESSAGE\n" +
                "\n" +
                "FROM FACILITY_MESSAGE_COUNTS fmc\n" +
                "JOIN FACILITY f on f.facility_id = fmc.facility_id\n" +
                "JOIN FACILITY_CODE_COUNTS fcc on fcc.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n" +
                "\n" +
                "JOIN (\n" +
                "\tSELECT CODE_TYPE, CODE_VALUE, FACILITY_MESSAGE_COUNTS_ID, MAX(MESSAGE) as MESSAGE \n" +
                "\tFROM MESSAGE_METADATA mm\n" +
                "\tLEFT JOIN MESSAGE_CODE mc on mc.MESSAGE_METADATA_ID  = mm.MESSAGE_METADATA_ID \n" +
                "\tGROUP BY CODE_TYPE, CODE_VALUE\n" +
                ") em on em.CODE_TYPE = fcc.CODE_TYPE \n" +
                "AND em.CODE_VALUE = fcc.CODE_VALUE\n" +
                "AND em.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n" +
                "\n" +
                "WHERE f.name = :providerIdentifier " +
                "AND fmc.username = :username " +
                "AND trunc(fmc.upload_date) >= :rangeStart " +
                "AND trunc(fmc.upload_date) <= :rangeEnd " +
                "AND fcc.CODE_STATUS <> 'Valid'\n" +
                "GROUP BY fcc.CODE_TYPE, fcc.CODE_VALUE\n" +
                "ORDER BY count DESC\n" +
                "LIMIT 10";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", dateEnd)
                .addValue("username", username)
                .addValue("rangeStart", dateStart)
                .addValue("providerIdentifier", providerKey);

        return jdbcTemplate.query(query, namedParameters, new RowMapper<CollectionBucket>() {
            @Override
            public CollectionBucket mapRow(ResultSet resultSet, int i) throws SQLException {
                CollectionBucket scoreReportable = new CollectionBucket(
                        resultSet.getString("CODE_TYPE"),
                        resultSet.getString("ATTRIBUTE"),
                        resultSet.getString("CODE_VALUE"),
                        resultSet.getInt("COUNT"),
                        resultSet.getString("CODE_STATUS"),
                        resultSet.getString("MESSAGE")
                );
                return scoreReportable;
            }
        });
    }

    List<CollectionBucket> getVaccineCodesBuckets(String providerKey, Date dateStart, Date dateEnd, String username) {
        String query = "SELECT ADMINISTERED, VACCINE_CVX, SUM(COUNT) as COUNT \n" +
                "FROM FACILITY_VACCINE_COUNTS fvc\n" +
                "JOIN FACILITY_MESSAGE_COUNTS fmc on fmc.FACILITY_MESSAGE_COUNTS_ID  = fvc.FACILITY_MESSAGE_COUNTS_ID \n" +
                "JOIN FACILITY f on f.facility_id = fmc.facility_id\n" +
                "WHERE f.name = :providerIdentifier " +
                "AND fmc.username = :username " +
                "AND trunc(fmc.upload_date) >= :rangeStart " +
                "AND trunc(fmc.upload_date) <= :rangeEnd " +
                "GROUP BY ADMINISTERED, VACCINE_CVX " +
                "ORDER BY COUNT DESC";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", dateEnd)
                .addValue("username", username)
                .addValue("rangeStart", dateStart)
                .addValue("providerIdentifier", providerKey);

        return jdbcTemplate.query(query, namedParameters, new RowMapper<CollectionBucket>() {
            @Override
            public CollectionBucket mapRow(ResultSet resultSet, int i) throws SQLException {
                CollectionBucket scoreReportable = new CollectionBucket(
                        CodesetType.VACCINATION_CVX_CODE.getType(),
                        resultSet.getBoolean("ADMINISTERED") ? "Administered" : "Historical",
                        resultSet.getString("VACCINE_CVX"),
                        resultSet.getInt("COUNT")
                );

                Code c = codeRepo.getCodeFromValue(resultSet.getString("VACCINE_CVX"), CodesetType.VACCINATION_CVX_CODE);
                if (c != null) {
                    if (c.getCodeStatus() != null && StringUtils.isNotBlank(c.getCodeStatus().getStatus())) {
                        String status = c.getCodeStatus().getStatus();
                        scoreReportable.setStatus(status);
                    } else {
                        scoreReportable.setStatus("Unrecognized");
                    }
                    String description = c.getLabel();
                    scoreReportable.setLabel(description);
                } else {
                    scoreReportable.setStatus("Unrecognized");
                }
                return scoreReportable;
            }
        });

    }

}