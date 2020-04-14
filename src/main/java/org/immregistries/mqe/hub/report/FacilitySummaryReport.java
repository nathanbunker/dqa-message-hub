package org.immregistries.mqe.hub.report;

public class FacilitySummaryReport {
    private MessagesSummary messages = new MessagesSummary();
    private PatientSummary patients = new PatientSummary();
    private VaccinationSummary vaccinations = new VaccinationSummary();

    public MessagesSummary getMessages() {
        return messages;
    }

    public void setMessages(MessagesSummary messages) {
        this.messages = messages;
    }

    public PatientSummary getPatients() {
        return patients;
    }

    public void setPatients(PatientSummary patients) {
        this.patients = patients;
    }

    public VaccinationSummary getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(
        VaccinationSummary vaccinations) {
        this.vaccinations = vaccinations;
    }

    public class MessagesSummary    {
        int errors;
        int warnings;
        int total;

        public int getErrors() {
            return errors;
        }

        public void setErrors(int errors) {
            this.errors = errors;
        }

        public int getWarnings() {
            return warnings;
        }

        public void setWarnings(int warnings) {
            this.warnings = warnings;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public class PatientSummary {
        int children;
        int adults;
        int total;

        public int getChildren() {
            return children;
        }

        public void setChildren(int children) {
            this.children = children;
        }

        public int getAdults() {
            return adults;
        }

        public void setAdults(int adults) {
            this.adults = adults;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public class VaccinationSummary {
        int administered;
        int historical;
        int refusals;
        int deletes;
        int other;
        int total;

        public int getAdministered() {
            return administered;
        }

        public void setAdministered(int administered) {
            this.administered = administered;
        }

        public int getHistorical() {
            return historical;
        }

        public void setHistorical(int historical) {
            this.historical = historical;
        }

        public int getRefusals() {
            return refusals;
        }

        public void setRefusals(int refusals) {
            this.refusals = refusals;
        }

        public int getDeletes() {
            return deletes;
        }

        public void setDeletes(int deletes) {
            this.deletes = deletes;
        }

        public int getOther() {
            return other;
        }

        public void setOther(int other) {
            this.other = other;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
