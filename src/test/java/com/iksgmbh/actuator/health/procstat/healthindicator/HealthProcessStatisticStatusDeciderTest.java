package com.iksgmbh.actuator.health.procstat.healthindicator;

import com.iksgmbh.actuator.health.procstat.config.HealthProcessStatisticConfig;
import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {
        HealthProcessStatisticConfig.class,
        HealthProcessStatisticStatusDecider.class
})
public class HealthProcessStatisticStatusDeciderTest {

    @Autowired
    private HealthProcessStatisticData healthProcessStatisticData;

    @Autowired
    private HealthProcessStatisticStatusDecider healthProcessStatisticStatusDecider;


    @Test
    void checkHealth_UP_test() {

        // given
        healthProcessStatisticData.reset();

        // when
        Health result = healthProcessStatisticStatusDecider.checkHealth();

        // then
        assertEquals("UP", result.getStatus().toString());
    }

    @Test
    void checkHealth_DOWN1_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError("function", "returncode", "messagetext", "instructiontext", "referenceid");

        // when
        Health result = healthProcessStatisticStatusDecider.checkHealth();

        // then
        assertEquals("DOWN", result.getStatus().toString());
    }

    @Test
    void checkHealth_DOWN2_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError("function1", "returncode1", "messagetext1", "instructiontext1", "referenceid1");
        healthProcessStatisticData.addError("function2", "returncode2", "messagetext2", "instructiontext2", "referenceid2");

        // when
        Health result = healthProcessStatisticStatusDecider.checkHealth();

        // then
        assertEquals("DOWN", result.getStatus().toString());
    }

    @Test
    void checkHealth_WARNING1_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter("function1Failed");

        // when
        Health result = healthProcessStatisticStatusDecider.checkHealth();

        // then
        assertEquals("UP", result.getStatus().toString());  // "WARNING"
    }

    @Test
    void checkHealth_WARNING2_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter("function1Failed");
        healthProcessStatisticData.incrementCounter("function2Failed");

        // when
        Health result = healthProcessStatisticStatusDecider.checkHealth();

        // then
        assertEquals("UP", result.getStatus().toString());  // "WARNING"
    }


    @Test
    void isCounterGreaterZero1_test() {

        // given
        String key = "error";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(key);

        // when
        boolean result = healthProcessStatisticStatusDecider.isCounterGreaterZero(key);

        // then
        assertTrue(result);
    }

    @Test
    void isCounterGreaterZero2_test() {

        // given
        String key = "error";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(key);
        healthProcessStatisticData.incrementCounter(key);

        // when
        boolean result = healthProcessStatisticStatusDecider.isCounterGreaterZero(key);

        // then
        assertTrue(result);
    }

    @Test
    void isCounterGreaterZero3_test() {

        // given
        String key = "error";
        healthProcessStatisticData.reset();

        // when
        boolean result = healthProcessStatisticStatusDecider.isCounterGreaterZero(key);

        // then
        assertFalse(result);
    }


    @Test
    void isErrorTimestampGreaterSuccessTimestamp_errorAfterSuccess_test() throws InterruptedException {

        // given
        String successKey = "success";
        String errorKey = "error";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(successKey);
        Thread.sleep(1);
        healthProcessStatisticData.incrementCounter(errorKey);

        // when
        boolean result = healthProcessStatisticStatusDecider.isErrorTimestampGreaterSuccessTimestamp(errorKey, successKey);

        // then
        assertTrue(result);
    }

    @Test
    void isErrorTimestampGreaterSuccessTimestamp_successAfterError_test() throws InterruptedException {

        // given
        String successKey = "success";
        String errorKey = "error";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(errorKey);
        Thread.sleep(1);
        healthProcessStatisticData.incrementCounter(successKey);

        // when
        boolean result = healthProcessStatisticStatusDecider.isErrorTimestampGreaterSuccessTimestamp(errorKey, successKey);

        // then
        assertFalse(result);
    }

    @Test
    void isErrorTimestampGreaterSuccessTimestamp_successOnly_test() {

        // given
        String successKey = "success";
        String errorKey = "error";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(successKey);

        // when
        boolean result = healthProcessStatisticStatusDecider.isErrorTimestampGreaterSuccessTimestamp(errorKey, successKey);

        // then
        assertFalse(result);
    }

    @Test
    void isErrorTimestampGreaterSuccessTimestamp_errorOnly_test() {

        // given
        String successKey = "success";
        String errorKey = "error";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(errorKey);

        // when
        boolean result = healthProcessStatisticStatusDecider.isErrorTimestampGreaterSuccessTimestamp(errorKey, successKey);

        // then
        assertTrue(result);
    }

    @Test
    void isErrorTimestampGreaterSuccessTimestamp_noTimestamps_test() {

        // given
        String successKey = "success";
        String errorKey = "error";
        healthProcessStatisticData.reset();

        // when
        boolean result = healthProcessStatisticStatusDecider.isErrorTimestampGreaterSuccessTimestamp(errorKey, successKey);

        // then
        assertFalse(result);
    }
}
