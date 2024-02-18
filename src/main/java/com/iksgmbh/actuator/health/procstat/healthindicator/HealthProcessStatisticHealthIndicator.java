package com.iksgmbh.actuator.health.procstat.healthindicator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health process statistic endpoint for in memory process statistic data -
 * health indicator.
 * URL: http://host:port/app/actuator/health/processStatistic
 *
 * @author H. Jägle / IKS GmbH
 * @version 1.6 2023-10-02
 */
@Component("processStatistic")
@ConditionalOnEnabledHealthIndicator("processStatistic")
public class HealthProcessStatisticHealthIndicator implements HealthIndicator {

    private HealthProcessStatisticStatusDecider healthProcessStatisticStatusDecider;


    /**
     * Constructor.
     *
     * @param healthProcessStatisticStatusDecider Health process statistic status decider implementation
     */
    @Autowired
    public HealthProcessStatisticHealthIndicator(final HealthProcessStatisticStatusDecider healthProcessStatisticStatusDecider) {
        this.healthProcessStatisticStatusDecider = healthProcessStatisticStatusDecider;
    }

    /**
     * Determine current application health status like UP, DOWN, ERROR, WARNING, etc.
     *
     * @return Health status object
     */
    public Health health() {
        return healthProcessStatisticStatusDecider.checkHealth();
    }
}
