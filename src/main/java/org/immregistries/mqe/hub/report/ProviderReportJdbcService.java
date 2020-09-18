package org.immregistries.mqe.hub.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;
import org.immregistries.mqe.hub.report.viewer.MessageHistoryJdbcRepository;
import org.immregistries.mqe.validator.engine.codes.CodeRepository;
import org.immregistries.mqe.validator.report.ScoreReportable;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;
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

    ProviderReport getProviderReport(String providerKey, Date dateStart, Date dateEnd, String username) {
        ProviderReport report = new ProviderReport();
        report.setProvider(providerKey);
        report.setStartDate(dateStart);
        report.setEndDate(dateStart);
        StopWatch sw = new StopWatch();
        sw.start();

        report.setNumberOfMessage(messageHistoryJdbcRepository.getFacilityMessageCountByUsername(providerKey, dateStart, dateEnd, username));
        sw.stop();
        LOGGER.warn("messageHistoryJdbcRepository.getFacilityMessageCountByUsername took ["+sw.getTime() + "]ms");
        sw.reset();
        sw.start();
        LOGGER.warn("this.getErrorCount took ["+sw.getTime() + "]ms");
        sw.reset();
        sw.start();
        report.setNumberOfErrors(this.getDetectionCount(providerKey, dateStart, dateEnd, username, "ERROR"));
//        report.setNumberOfErrors(this.getErrorCount(providerKey, dateStart, dateEnd, username));
        report.setErrors(this.getDetectionsReport(providerKey, dateStart, dateEnd, username));
        sw.stop();
        LOGGER.warn("this.getDetectionsReport took ["+sw.getTime() + "]ms");
        sw.reset();
        sw.start();
        report.setCodeIssues(this.getCodeIssuesReport(providerKey, dateStart, dateEnd, username));
        sw.stop();
        LOGGER.warn("this.getCodeIssuesReport took ["+sw.getTime() + "]ms");
        sw.reset();
        sw.start();
        report.setVaccinationCodes(this.getVaccineCodesBuckets(providerKey, dateStart, dateEnd, username));
        sw.stop();
        LOGGER.warn("this.getVaccineCodesBuckets took ["+sw.getTime() + "]ms");
        /* new data */
        FacilitySummaryReport fsr = report.getCountSummary();
        fsr.getMessages().setTotal(report.getNumberOfMessage());
        fsr.getMessages().setErrors(report.getNumberOfErrors());
        fsr.getMessages().setWarnings(this.getDetectionCount(providerKey, dateStart, dateEnd, username, "WARN"));

        FacilitySummaryReport.PatientSummary ps = this.getPatientAgesByProvider(providerKey, username, dateStart, dateEnd);
        fsr.setPatients(ps);

        int total = 0;
        int administered = 0;
        int historical = 0;

        for(CollectionBucket cb: report.getVaccinationCodes()) {
            total += cb.getCount();
            if(cb.getAttribute().equals("Administered")) {
                administered += cb.getCount();
            }

            if(cb.getAttribute().equals("Historical")) {
                historical += cb.getCount();
            }
        }

        fsr.getVaccinations().setTotal(total);
        fsr.getVaccinations().setAdministered(administered);
        fsr.getVaccinations().setHistorical(historical);
        return report;
    }

    List<ScoreReportable> getDetectionsReport(String providerKey, Date dateStart, Date dateEnd, String username) {
        String query =
            "SELECT MQE_DETECTION_CODE,\n"
                + "       SUM(ATTRIBUTE_COUNT)               as detection_count,\n"
                + "       'MESSAGE_TEXT' as message, \n"
//                + "       max(message) as message,\n"
                + " (select count(distinct md.MESSAGE_METADATA_ID) from message_detection md"
                + " join MESSAGE_METADATA mdata on md.MESSAGE_METADATA_ID = mdata.MESSAGE_METADATA_ID"
                + " where md.DETECTION_ID = fdc.MQE_DETECTION_CODE"
                + " and mdata.FACILITY_MESSAGE_COUNTS_ID = fdc.FACILITY_MESSAGE_COUNTS_ID"
                + " ) as message_count,"
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
//                + "         left join MESSAGE_METADATA mm on mm.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n"
//                + "         LEFT JOIN MESSAGE_DETECTION md on md.message_metadata_id = mm.message_metadata_id \n"
//                + "                                       and md.detection_id = fdc.MQE_DETECTION_CODE \n"
                + "WHERE f.name = :providerIdentifier "
                + "AND fmc.username = :username "
                + "AND trunc(fmc.upload_date) >= :rangeStart "
                + "  AND coalesce(o1.SEVERITY, ds.SEVERITY) = 'ERROR'\n"
                + "AND trunc(fmc.upload_date) <= :rangeEnd "
                + "GROUP BY MQE_DETECTION_CODE, HOW_TO_FIX, WHY_TO_FIX, FDC.FACILITY_MESSAGE_COUNTS_ID \n"
                + "ORDER BY detection_count DESC\n"
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
//                        resultSet.getString("MESSAGE"),
                        null,
                        resultSet.getString("HOW_TO_FIX"),
                        resultSet.getString("WHY_TO_FIX"),
                        resultSet.getInt("detection_count"),
                        resultSet.getInt("message_count")
                );
                return scoreReportable;
            }
        });
    }


    MqeExampleMessage getExampleMessageForDetection(final String mqeCode, String providerKey, Date dateStart, Date dateEnd, String username) {
        String exampleMessageQuery =
            "\n"
                + "with chosen_message as (\n"
                + "    select mdt.MESSAGE_METADATA_ID, mdt.message, count(*)\n"
                + "    from message_detection md\n"
                + "             join MESSAGE_METADATA mdt on md.MESSAGE_METADATA_ID = mdt.MESSAGE_METADATA_ID\n"
                + "             join FACILITY_MESSAGE_COUNTS fmc on fmc.FACILITY_MESSAGE_COUNTS_ID = mdt.FACILITY_MESSAGE_COUNTS_ID\n"
                + "             join FACILITY F on fmc.FACILITY_ID = F.FACILITY_ID\n"
                + "    where md.DETECTION_ID = :mqe_code\n"
                + "      and fmc.UPLOAD_DATE <= :start_date --PARSEDATETIME(FORMATDATETIME('2020-08-12', 'yyyy-MM-dd'), 'yyyy-MM-dd')\n"
                + "      and fmc.UPLOAD_DATE >= :end_date --PARSEDATETIME(FORMATDATETIME('2020-08-12', 'yyyy-MM-dd'), 'yyyy-MM-dd')\n"
                + "      and fmc.USERNAME = :username\n"
                + "      and f.NAME = :facility_name \n"
                + "    group by mdt.MESSAGE_METADATA_ID\n"
                + "    order by count(*) desc\n"
                + "        fetch first 1 row only\n"
                + "    )\n"
                + "\n"
                + "select cm.MESSAGE, md.LOCATION_TXT\n"
                + "from chosen_message cm\n"
                + "         join MESSAGE_DETECTION md on md.MESSAGE_METADATA_ID = cm.MESSAGE_METADATA_ID"
                + " where md.DETECTION_ID = :mqe_code "
            ;

        SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("end_date", dateEnd)
            .addValue("start_date", dateStart)
            .addValue("username", username)
            .addValue("facility_name", providerKey)
            .addValue("mqe_code", mqeCode)
            ;

        List<MqeExampleMessage> list = jdbcTemplate.query(exampleMessageQuery, namedParameters, new RowMapper<MqeExampleMessage>() {
            @Override
            public MqeExampleMessage mapRow(ResultSet resultSet, int i) throws SQLException {
                MqeExampleMessage scoreReportable = new MqeExampleMessage(
                    resultSet.getString("MESSAGE"),
                    resultSet.getString("LOCATION_TXT"),
                    mqeCode
                );
                return scoreReportable;
            }
        });

        MqeExampleMessage mem = new MqeExampleMessage();
        if (list != null && list.size() > 0) {
            mem = list.get(0);
            if (list.size() > 1) {
                for (int x = 1; x<list.size(); x++) {
                    mem.getLocations().addAll(list.get(x).getLocations());
                }
            }
        }
        return mem;
    }


    private static final String EXAMPLE_MESSAGE_FOR_CODE_SQL =
        "    select mdt.message\n"
            + "    from message_code mc\n"
            + "             join MESSAGE_METADATA mdt on mc.MESSAGE_METADATA_ID = mdt.MESSAGE_METADATA_ID\n"
            + "             join FACILITY_MESSAGE_COUNTS fmc on fmc.FACILITY_MESSAGE_COUNTS_ID = mdt.FACILITY_MESSAGE_COUNTS_ID\n"
            + "             join FACILITY F on fmc.FACILITY_ID = F.FACILITY_ID\n"
            + "    where\n"
            + "          mc.CODE_VALUE = :code_value \n"
            + "      and mc.code_type = :code_type \n"
            + "      and fmc.UPLOAD_DATE <= :start_date \n"
            + "      and fmc.UPLOAD_DATE >= :end_date \n"
            //+ "      and fmc.UPLOAD_DATE <= PARSEDATETIME(FORMATDATETIME('2020-08-12', 'yyyy-MM-dd'), 'yyyy-MM-dd') \n"
            //+ "      and fmc.UPLOAD_DATE >= PARSEDATETIME(FORMATDATETIME('2020-08-12', 'yyyy-MM-dd'), 'yyyy-MM-dd') \n"
            + "      and fmc.USERNAME = :username \n"
            + "      and f.NAME = :facility_name \n"
            + "--\n"
            + "    order by mc.CODE_COUNT desc\n"
            + "        fetch first 1 row only "
        ;
    MqeExampleMessage getExampleMessageForCodeTypeAndValue(final String codeType, final String codeValue, String providerKey, Date dateStart, Date dateEnd, String username) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("end_date", dateEnd)
            .addValue("start_date", dateStart)
            .addValue("username", username)
            .addValue("facility_name", providerKey)
            .addValue("code_type", codeType)
            .addValue("code_value", codeValue)
            ;

        String message = jdbcTemplate.queryForObject(EXAMPLE_MESSAGE_FOR_CODE_SQL, namedParameters, String.class);
        return new MqeExampleMessage(message);
    }

    private int getDetectionCount(String providerKey, Date dateStart, Date dateEnd, String username, String severity) {
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
                "WHERE f.name = :providerIdentifier\n" +
                "AND fmc.username = :username\n" +
                "AND trunc(fmc.upload_date) >= :rangeStart\n" +
                "AND trunc(fmc.upload_date) <= :rangeEnd\n" +
                "AND ds.SEVERITY = :severity\n";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("rangeEnd", dateEnd)
                .addValue("username", username)
                .addValue("rangeStart", dateStart)
                .addValue("severity", severity)
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
//                      "with theten as (\n" +
                      "SELECT fcc.ATTRIBUTE,\n"
                    + "       fcc.CODE_TYPE,\n"
                    + "       SUM(fcc.CODE_COUNT) as count,\n"
                    + "       fcc.CODE_STATUS,\n"
                    + "       'EXAMPLE_MSG' as message,\n"
                    + "       fcc.CODE_VALUE,\n"
                    + "       fcc.FACILITY_MESSAGE_COUNTS_ID\n"
                    + "FROM FACILITY_MESSAGE_COUNTS fmc\n"
                    + "         JOIN FACILITY f on f.facility_id = fmc.facility_id\n"
                    + "         JOIN FACILITY_CODE_COUNTS fcc on fcc.FACILITY_MESSAGE_COUNTS_ID = fmc.FACILITY_MESSAGE_COUNTS_ID\n"
                    + "WHERE\n"
                    + "                      f.name = :providerIdentifier\n"
                    + "                AND fmc.username = :username\n"
                    + "                AND trunc(fmc.upload_date) >= :rangeStart\n"
                    + "                AND trunc(fmc.upload_date) <= :rangeEnd\n"
                    + "                AND\n"
                    + "--       f.name = 'testing'\n"
                    + "--       and fmc.USERNAME = 'testing'\n"
                    + "--       and fmc.upload_date = '2020-07-13'\n"
                    + "--       and\n"
                    + "fcc.CODE_STATUS <> 'Valid'\n"
                    + "GROUP BY fcc.ATTRIBUTE, fcc.CODE_TYPE, fcc.CODE_STATUS, fcc.CODE_VALUE\n"
                    + "       , fmc.FACILITY_MESSAGE_COUNTS_ID\n"
                    + ", f.name\n"
                    + ", fmc.username\n"
                    + ", fmc.UPLOAD_DATE\n"
                    + "ORDER BY count DESC\n"
                    + "LIMIT 10 "
//                    + ")\n"
//                    + "select *, 'EXAMPLE_MSG' as message \n"
////                    + "       (select max(message)\n"
////                    + "           from MESSAGE_METADATA mm\n"
////                    + "           join message_code mc\n"
////                    + "           on mc.MESSAGE_METADATA_ID = mm.MESSAGE_METADATA_ID\n"
////                    + "           where mm.FACILITY_MESSAGE_COUNTS_ID = tt.FACILITY_MESSAGE_COUNTS_ID\n"
////                    + "               and mc.CODE_TYPE = tt.CODE_TYPE\n"
////                    + "               and mc.CODE_VALUE = tt.CODE_VALUE\n"
////                    + "               and mc.ATTRIBUTE = tt.ATTRIBUTE\n"
////                    + "           ) as message\n"
//                    + "from theten tt"
            ;
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
//                        resultSet.getString("MESSAGE")
                        null
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
