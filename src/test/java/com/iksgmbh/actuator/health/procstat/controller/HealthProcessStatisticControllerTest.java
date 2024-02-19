package com.iksgmbh.actuator.health.procstat.controller;

import com.iksgmbh.actuator.health.procstat.config.HealthProcessStatisticConfig;
import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(HealthProcessStatisticController.class)
@ContextConfiguration(classes = {
        HealthProcessStatisticConfig.class,
        HealthProcessStatisticController.class
})
public class HealthProcessStatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HealthProcessStatisticData healthProcessStatisticData;


    @Test
    void healthProcessStatisticGet_zero_test() throws Exception {

        // given
        healthProcessStatisticData.reset();

        // when

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/healthProcessStatistic/get/errorCounter"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("0"));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/healthProcessStatistic/get/errorTimestamp"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void healthProcessStatisticGet_one_test() throws Exception {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError("function", "returncode", "messagetext", null, "referenceId");

        // when

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/healthProcessStatistic/get/errorCounter"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/healthProcessStatistic/get/errorTimestamp"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void healthProcessStatisticReset_true_test() throws Exception {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError("function", "returncode", "messagetext", null, "referenceId");

        // when

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/healthProcessStatistic/reset?reset=true"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertEquals(0, healthProcessStatisticData.getErrorList().size());
    }

    @Test
    void healthProcessStatisticReset_false_test() throws Exception {

        // given
        healthProcessStatisticData.reset();
        healthProcessStatisticData.addError("function", "returncode", "messagetext", null, "referenceId");

        // when

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/healthProcessStatistic/reset?reset=false"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertEquals(1, healthProcessStatisticData.getErrorList().size());
    }
}