package com.library.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Library-specific configuration properties bound from application.yml.
 */
@Component
@ConfigurationProperties(prefix = "library")
public class LibraryProperties {

    private int loanDays = 14;
    private int finePerDay = 5;
    private int maxActiveIssues = 5;
    private boolean blockOnUnpaidFines = true;

    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int loanDays) { this.loanDays = loanDays; }

    public int getFinePerDay() { return finePerDay; }
    public void setFinePerDay(int finePerDay) { this.finePerDay = finePerDay; }

    public int getMaxActiveIssues() { return maxActiveIssues; }
    public void setMaxActiveIssues(int maxActiveIssues) { this.maxActiveIssues = maxActiveIssues; }

    public boolean isBlockOnUnpaidFines() { return blockOnUnpaidFines; }
    public void setBlockOnUnpaidFines(boolean blockOnUnpaidFines) { this.blockOnUnpaidFines = blockOnUnpaidFines; }
}
