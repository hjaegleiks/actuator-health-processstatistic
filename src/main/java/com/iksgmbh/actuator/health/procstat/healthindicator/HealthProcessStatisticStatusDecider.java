package com.iksgmbh.actuator.health.procstat.healthindicator;

import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticData;
import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticDataKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Health process statistic endpoint for in memory process statistic data -
 * health status decider for {@link HealthProcessStatisticHealthIndicator}
 *
 * @author H. Jägle / IKS GmbH
 * @version 1.6 2023-10-02
 */
@Component
public class HealthProcessStatisticStatusDecider {

    private HealthProcessStatisticData healthProcessStatisticData;


    /**
     * Constructor.
     *
     * @param healthProcessStatisticData Health process statistic data bean
     */
    @Autowired
    public HealthProcessStatisticStatusDecider(final HealthProcessStatisticData healthProcessStatisticData) {
        this.healthProcessStatisticData = healthProcessStatisticData;
    }

    /**
     * Implementation to determine current application health status like UP, DOWN, ERROR, WARNING, etc.
     *
     * @return Health status object
     */
    public Health checkHealth() {

        // DOWN - Error occurred after last request
        if (isErrorTimestampGreaterSuccessTimestamp(HealthProcessStatisticDataKey.error,
                                                    HealthProcessStatisticDataKey.request)) {
        //     || isErrorTimestampGreaterSuccessTimestamp(HealthProcessStatisticDataKey.error1,
        //                                                HealthProcessStatisticDataKey.request1)
        //     || isErrorTimestampGreaterSuccessTimestamp(HealthProcessStatisticDataKey.error2,
        //                                                HealthProcessStatisticDataKey.request2)) {
            return Health.down()
                    .withDetails(healthProcessStatisticData.getHealthProcessStatisticDataMap())
                    .build();
        }

        // WARNING - Errors occurred in the past
        if (isCounterGreaterZero(HealthProcessStatisticDataKey.error)) {
           return Health.status("WARNING")
                    .withDetails(healthProcessStatisticData.getHealthProcessStatisticDataMap())
                    .build();
        }

        // UP - Everything ok
        return Health.up()
                .withDetails(healthProcessStatisticData.getHealthProcessStatisticDataMap())
                .build();
    }

    /**
     * Check counter of given statistic data key is greater than zero.
     *
     * @param statisticDataKey Statistic data key
     * @return true if counter is not null and greater than zero, otherwise false
     */
    protected boolean isCounterGreaterZero(final Enum<?> statisticDataKey) {
        return isCounterGreaterZero(statisticDataKey.toString());
    }

    /**
     * Check counter of given statistic data key is greater than zero.
     *
     * @param statisticDataKey Statistic data key
     * @return true if counter is not null and greater than zero, otherwise false
     */
    protected boolean isCounterGreaterZero(final String statisticDataKey) {
        Long sdcs = healthProcessStatisticData.getStatisticDataCounter(statisticDataKey);
        return (sdcs != null && sdcs > 0L);
    }

    /**
     * Check last timestamp of given error statistic data key is after than or equal to
     * corresponding last timestamp of given success statistic data key.
     *
     * @param statisticDataKeyError Statistic data key of last error processing
     * @param statisticDataKeySuccess Statistic data key of last success processing
     * @return true if last timestamp of error is after than or equal to last success timestamp, otherwise false
     */
    protected boolean isErrorTimestampGreaterSuccessTimestamp(final Enum<?> statisticDataKeyError,
            final Enum<?> statisticDataKeySuccess) {
        return isErrorTimestampGreaterSuccessTimestamp(statisticDataKeyError.toString(),
                statisticDataKeySuccess.toString());
    }

    /**
     * Check last timestamp of given error statistic data key is after than or equal to
     * corresponding last timestamp of given success statistic data key.
     *
     * @param statisticDataKeyError Statistic data key of last error processing
     * @param statisticDataKeySuccess Statistic data key of last success processing
     * @return true if last error timestamp is after than or equal to last success timestamp, otherwise false
     */
    protected boolean isErrorTimestampGreaterSuccessTimestamp(final String statisticDataKeyError,
            final String statisticDataKeySuccess) {
        OffsetDateTime errorTimestamp = healthProcessStatisticData.getStatisticDataTimestamp(statisticDataKeyError);
        OffsetDateTime successTimestamp = healthProcessStatisticData.getStatisticDataTimestamp(statisticDataKeySuccess);
        return (errorTimestamp != null &&
                (successTimestamp == null ||
                 !errorTimestamp.isBefore(successTimestamp)));
    }
}
