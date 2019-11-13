package org.immregistries.mqe.hub.report;

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
    private List<ScoreReportable> errors;
    private List<CollectionBucket> codeIssues;

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
