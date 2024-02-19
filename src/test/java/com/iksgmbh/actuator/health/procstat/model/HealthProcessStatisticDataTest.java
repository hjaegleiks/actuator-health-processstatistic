package com.iksgmbh.actuator.health.procstat.model;

import com.iksgmbh.actuator.health.procstat.config.HealthProcessStatisticConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {
        HealthProcessStatisticConfig.class
})
public class HealthProcessStatisticDataTest {

    @Autowired
    private HealthProcessStatisticData healthProcessStatisticData;


    @Test
    void reset_test() {

        // given
        OffsetDateTime startTimestamp = OffsetDateTime.now();

        healthProcessStatisticData.reset();
        healthProcessStatisticData.setServiceStartTimestamp(startTimestamp);
        healthProcessStatisticData.incrementCounter(HealthProcessStatisticDataKey.error);
        healthProcessStatisticData.incrementCounter(HealthProcessStatisticDataKey.request);
        healthProcessStatisticData.addError("function", "returncode", "messagetext", "instructiontext", "referenceid");

        // when
        healthProcessStatisticData.reset();

        // then
        assertNotNull(healthProcessStatisticData.getServiceStartTimestamp());

        List<HealthErrorData> resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(0, resultErrorList.size());

        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(6, resultHealthProcessStatisticDataMap.size());
        assertEquals(0L, resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
    }

    @Test
    void resetErrorList_test() {
        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError("function", "returncode", "messagetext", "instructiontext", "referenceid");

        // when
        healthProcessStatisticData.resetErrorList();

        // then
        List<HealthErrorData> resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(0, resultErrorList.size());
    }

    @Test
    void resetStatisticData_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(HealthProcessStatisticDataKey.error);
        healthProcessStatisticData.incrementCounter(HealthProcessStatisticDataKey.request);

        // when
        healthProcessStatisticData.resetStatisticData();

        // then
        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(6, resultHealthProcessStatisticDataMap.size());
        assertEquals(0L, resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
    }


    @Test
    void addStatisticDataKey_newKey_test() {

        // given
        String key = "testkey";

        healthProcessStatisticData.reset();
        int sizeBefore = healthProcessStatisticData.getStatisticDataKeyList().size();

        // when
        healthProcessStatisticData.addStatisticDataKey(key);

        // then
        List<String> resultStatisticDataKeyList = healthProcessStatisticData.getStatisticDataKeyList();
        assertEquals(sizeBefore + 1, resultStatisticDataKeyList.size());
        assertTrue(resultStatisticDataKeyList.contains(key));
    }

    @Test
    void addStatisticDataKey_existingKey_test() {

        // given
        String key = "testkey";

        healthProcessStatisticData.reset();
        healthProcessStatisticData.addStatisticDataKey(key);
        int sizeBefore = healthProcessStatisticData.getStatisticDataKeyList().size();

        // when
        healthProcessStatisticData.addStatisticDataKey(key);

        // then
        List<String> resultStatisticDataKeyList = healthProcessStatisticData.getStatisticDataKeyList();
        assertEquals(sizeBefore, resultStatisticDataKeyList.size());
        assertTrue(resultStatisticDataKeyList.contains(key));
    }


    @Test
    void incrementCounter_newKey_test() {

        // given
        String key = "testkey";
        healthProcessStatisticData.reset();
        int sizeBefore = healthProcessStatisticData.getHealthProcessStatisticDataMap().size();

        // when
        healthProcessStatisticData.incrementCounter(key);

        // then
        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(sizeBefore + 2, resultHealthProcessStatisticDataMap.size());
        assertEquals(1L, resultHealthProcessStatisticDataMap.get(
                key + HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
        assertNotNull(resultHealthProcessStatisticDataMap.get(
                key + HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP));
    }

    @Test
    void incrementCounter_existingKey_test() {

        // given
        String key = "testkey";
        healthProcessStatisticData.reset();
        healthProcessStatisticData.incrementCounter(key);
        int sizeBefore = healthProcessStatisticData.getHealthProcessStatisticDataMap().size();

        // when
        healthProcessStatisticData.incrementCounter(key);

        // then
        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(sizeBefore, resultHealthProcessStatisticDataMap.size());
        assertEquals(2L, resultHealthProcessStatisticDataMap.get(
                key + HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
        assertNotNull(resultHealthProcessStatisticDataMap.get(
                key + HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP));
    }


    @Test
    void addError1_test() {

        // given
        String function = "function test";
        String returnCode = "returncode test";
        String messageText = "messagetext test";
        String instructionText = "instructiontext test";
        String referenceId = "referenceid test";

        healthProcessStatisticData.reset();

        HealthErrorData error = new HealthErrorData(function, returnCode, messageText, instructionText, referenceId);

        // when
        healthProcessStatisticData.addError(error);

        // then
        List<HealthErrorData> resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(1, resultErrorList.size());

        HealthErrorData resultError = resultErrorList.get(0);
        assertNotNull(resultError.getTimestamp());
        assertEquals(function, resultError.getFunction());
        assertEquals(returnCode, resultError.getReturnCode());
        assertEquals(messageText, resultError.getMessageText());
        assertEquals(instructionText, resultError.getInstructionText());
        assertEquals(referenceId, resultError.getReferenceId());

        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(1L, resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
        assertNotNull(resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP));
    }

    @Test
    void addError2_test() {

        // given
        String function = "function test";
        String returnCode = "returncode test";
        String messageText = "messagetext test";
        String instructionText = "instructiontext test";
        String referenceId = "referenceid test";

        healthProcessStatisticData.reset();

        // when
        healthProcessStatisticData.addError(function, returnCode, messageText, instructionText, referenceId);

        // then
        List<HealthErrorData> resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(1, resultErrorList.size());

        HealthErrorData resultError = resultErrorList.get(0);
        assertNotNull(resultError.getTimestamp());
        assertEquals(function, resultError.getFunction());
        assertEquals(returnCode, resultError.getReturnCode());
        assertEquals(messageText, resultError.getMessageText());
        assertEquals(instructionText, resultError.getInstructionText());
        assertEquals(referenceId, resultError.getReferenceId());

        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(1L, resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
        assertNotNull(resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP));
    }

    @Test
    void addError3_test() {

        // given
        String function = "function test";
        String returnCode = "returncode test";
        String messageText = "messagetext test";

        healthProcessStatisticData.reset();

        // when
        healthProcessStatisticData.addError(function, returnCode, messageText);

        // then
        List<HealthErrorData> resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(1, resultErrorList.size());

        HealthErrorData resultError = resultErrorList.get(0);
        assertNotNull(resultError.getTimestamp());
        assertEquals(function, resultError.getFunction());
        assertEquals(returnCode, resultError.getReturnCode());
        assertEquals(messageText, resultError.getMessageText());
        assertNull(resultError.getInstructionText());
        assertNull(resultError.getReferenceId());

        Map<String, Object> resultHealthProcessStatisticDataMap = healthProcessStatisticData.getHealthProcessStatisticDataMap();
        assertEquals(1L, resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER));
        assertNotNull(resultHealthProcessStatisticDataMap.get(
                HealthProcessStatisticDataKey.error +
                HealthProcessStatisticData.HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP));
    }

    @Test
    void addError_limit5_test() {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.setErrorListMaxSize(5);

        // when
        healthProcessStatisticData.addError("function1", "returncode", "messagetext", "instructiontext", "referenceid");
        healthProcessStatisticData.addError("function2", "returncode", "messagetext", "instructiontext", "referenceid");
        healthProcessStatisticData.addError("function3", "returncode", "messagetext", "instructiontext", "referenceid");
        healthProcessStatisticData.addError("function4", "returncode", "messagetext", "instructiontext", "referenceid");
        healthProcessStatisticData.addError("function5", "returncode", "messagetext", "instructiontext", "referenceid");

        // then
        List<HealthErrorData> resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(5, resultErrorList.size());

        HealthErrorData resultError = resultErrorList.get(0);
        assertEquals("function1", resultError.getFunction());


        // when
        healthProcessStatisticData.addError("function6", "returncode", "messagetext", "instructiontext", "referenceid");

        // then
        resultErrorList = healthProcessStatisticData.getErrorList();
        assertEquals(5, resultErrorList.size());

        resultError = resultErrorList.get(0);
        assertEquals("function2", resultError.getFunction());
    }
}
