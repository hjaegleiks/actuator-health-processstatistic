package com.iksgmbh.actuator.health.procstat.healthindicator;

import com.iksgmbh.actuator.health.procstat.config.HealthProcessStatisticConfig;
import com.iksgmbh.actuator.health.procstat.model.HealthErrorData;
import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = {
        HealthProcessStatisticConfig.class,
        HealthProcessStatisticHealthIndicator.class,
        HealthProcessStatisticStatusDecider.class
})
public class HealthProcessStatisticHealthIndicatorTest {

    @Autowired
    private HealthProcessStatisticData healthProcessStatisticData;

    @Autowired
    private HealthProcessStatisticHealthIndicator healthProcessStatisticHealthIndicator;


    @Test
    void health_UP_test() {

        // given
        healthProcessStatisticData.reset();

        // when
        Health result = healthProcessStatisticHealthIndicator.health();

        // then
        assertEquals("UP", result.getStatus().toString());
    }

    @Test
    void health_DOWN_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError(new HealthErrorData("function", "returncode",
                "messagetext", "instructiontext", "referenceid"));

        // when
        Health result = healthProcessStatisticHealthIndicator.health();

        // then
        assertEquals("DOWN", result.getStatus().toString());
    }

    @Test
    void health_WARNING_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter("failedRequest");

        // when
        Health result = healthProcessStatisticHealthIndicator.health();

        // then
        assertEquals("UP", result.getStatus().toString());  // "WARNING"
    }
}
