package com.iksgmbh.actuator.health.procstat.config;

import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * Health process statistic endpoint for in memory process statistic data -
 * configuration bean.
 *
 * @author H. Jägle / IKS GmbH
 * @version 1.6 2023-10-02
 */
@Configuration
public class HealthProcessStatisticConfig {

    @Bean
    @ApplicationScope
    public HealthProcessStatisticData healthProcessStatisticData() {
        return new HealthProcessStatisticData();
    }
}
