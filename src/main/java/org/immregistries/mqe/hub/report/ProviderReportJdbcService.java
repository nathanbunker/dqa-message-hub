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
            "SELECT MQE_DETECTION_CODE,\n"
                + "       SUM(ATTRIBUTE_COUNT)               as COUNT,\n"
                + "       max(message) as message,\n"
                + "       coalesce(o1.SEVERITY, ds.SEVERITY) as SEVERITY,\n"
                + "       HOW_TO_FIX,\n"
                + "       WHY_TO_FIX\n"
                + "FROM FACILITY_MESSAGE_COUNTS fmc\n"
                + "         JOIN FACILITY f on f.facility_id = fmc.facility_id\n"
                + "         JOIN FACILITY_DETECTION_COUNTS fdc on fdc.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n"
                + "         LEFT JOIN DETECTION_SEVERITY_OVERRIDE o1 on o1.MQE_CODE = fdc.MQE_DETECTION_CODE\n"
                + "         LEFT JOIN DETECTION_SEVERITY_OVERRIDE_GROUP o1g\n"
                + "                   on o1.DETECTION_SEVERITY_OVERRIDE_GROUP_ID = o1g.DETECTION_SEVERITY_OVERRIDE_GROUP_ID AND\n"
                + "                      o1g.NAME = 'Unspecified'\n"
                + "         JOIN DETECTION_SEVERITY ds on ds.MQE_CODE = fdc.MQE_DETECTION_CODE\n"
                + "         LEFT JOIN DETECTION_GUIDANCE gd on gd.MQE_CODE = MQE_DETECTION_CODE\n"
                + "         left join MESSAGE_METADATA mm on mm.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n"
                + "WHERE f.name = :providerIdentifier "
                + "AND fmc.username = :username "
                + "AND trunc(fmc.upload_date) >= :rangeStart "
                + "AND trunc(fmc.upload_date) <= :rangeEnd "
                + "  AND coalesce(o1.SEVERITY, ds.SEVERITY) = 'ERROR'\n"
                + "GROUP BY MQE_DETECTION_CODE, HOW_TO_FIX, WHY_TO_FIX\n"
                + "ORDER BY count DESC\n"
                + "LIMIT 10";

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
                        resultSet.getString("HOW_TO_FIX"),
                        resultSet.getString("WHY_TO_FIX"),
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
        String query =
                "SELECT fcc.ATTRIBUTE,\n"
                    + "       fcc.CODE_TYPE,\n"
                    + "       SUM(fcc.CODE_COUNT) as count,\n"
                    + "       fcc.CODE_STATUS,\n"
                    + "       fcc.CODE_VALUE,\n"
                    + "       max(select max(mm.message)\n"
                    + "           from MESSAGE_METADATA mm\n"
                    + "           join message_code mc\n"
                    + "           on mc.MESSAGE_METADATA_ID = mm.MESSAGE_METADATA_ID\n"
                    + "           where mm.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n"
                    + "               and mc.CODE_TYPE = fcc.CODE_TYPE\n"
                    + "               and mc.CODE_VALUE = fcc.CODE_VALUE\n"
                    + "               and mc.ATTRIBUTE = fcc.ATTRIBUTE\n"
                    + "           ) as message\n"
                    + "\n"
                    + "FROM FACILITY_MESSAGE_COUNTS fmc\n"
                    + "         JOIN FACILITY f on f.facility_id = fmc.facility_id\n"
                    + "         JOIN FACILITY_CODE_COUNTS fcc on fcc.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n"
                    + "WHERE\n"
                    + "                      f.name = :providerIdentifier\n"
                    + "                AND fmc.username = :username\n"
                    + "                AND trunc(fmc.upload_date) >= :rangeStart\n"
                    + "                AND trunc(fmc.upload_date) <= :rangeEnd\n"
                    + "                AND\n"
                    + "fcc.CODE_STATUS <> 'Valid'\n"
                    + "GROUP BY fcc.ATTRIBUTE, fcc.CODE_TYPE, fcc.CODE_STATUS, fcc.CODE_VALUE\n"
                    + ", f.name\n"
                    + ", fmc.username\n"
                    + ", fmc.UPLOAD_DATE\n"
                    + "ORDER BY count DESC\n"
                    + "LIMIT 10;";
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

    public FacilitySummaryReport.PatientSummary getPatientAgesByProvider(final String providerIdentifier, final String username, final  Date rangeStart, final  Date rangeEnd ) {

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", rangeEnd)
                .addValue("rangeStart", rangeStart)
                .addValue("username", username)
                .addValue("providerIdentifier", providerIdentifier);

        String query = getAdultChildCounts;
        LOGGER.debug("JDBC Query: " + query);
        try {
            return jdbcTemplate.queryForObject(query, namedParameters, new RowMapper<FacilitySummaryReport.PatientSummary>() {
                public FacilitySummaryReport.PatientSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                    int child = rs.getInt("adult_count");
                    int adult = rs.getInt("CHILD_count");
                    FacilitySummaryReport fsr = new FacilitySummaryReport();
                    FacilitySummaryReport.PatientSummary ps = fsr.getPatients();
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
