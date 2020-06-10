package org.immregistries.mqe.hub.report;

import java.util.ArrayList;
import org.immregistries.mqe.validator.report.ScoreReportable;
import org.immregistries.mqe.validator.report.codes.CollectionBucket;

import java.util.Date;
import java.util.List;

public class ProviderReport {

    private String provider;
    private Date startDate;
    private Date endDate;
    private int numberOfMessage;
    private int numberOfErrors;
    private List<ScoreReportable> errors = new ArrayList<>();
    private List<CollectionBucket> codeIssues = new ArrayList<>();
    private FacilitySummaryReport countSummary = new FacilitySummaryReport();
    private FacilityIdentifiers facilityIdentifiers = new FacilityIdentifiers();
    private String commonMessage;
    private List<CollectionBucket> vaccinationCodes = new ArrayList<>();

    public FacilitySummaryReport getCountSummary() {
        return countSummary;
    }

    public void setCountSummary(FacilitySummaryReport countSummary) {
        this.countSummary = countSummary;
    }

    public FacilityIdentifiers getFacilityIdentifiers() {
        return facilityIdentifiers;
    }

    public void setFacilityIdentifiers(FacilityIdentifiers facilityIdentifiers) {
        this.facilityIdentifiers = facilityIdentifiers;
    }

    public String getCommonMessage() {
        return commonMessage;
    }

    public void setCommonMessage(String commonMessage) {
        this.commonMessage = commonMessage;
    }

    public List<CollectionBucket> getVaccinationCodes() {
        return vaccinationCodes;
    }

    public void setVaccinationCodes(
        List<CollectionBucket> vaccinationCodes) {
        this.vaccinationCodes = vaccinationCodes;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNumberOfMessage() {
        return numberOfMessage;
    }

    public void setNumberOfMessage(int numberOfMessage) {
        this.numberOfMessage = numberOfMessage;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public List<ScoreReportable> getErrors() {
        return errors;
    }

    public void setErrors(List<ScoreReportable> errors) {
        this.errors = errors;
    }

    public List<CollectionBucket> getCodeIssues() {
        return codeIssues;
    }

    public void setCodeIssues(List<CollectionBucket> codeIssues) {
        this.codeIssues = codeIssues;
    }
}
