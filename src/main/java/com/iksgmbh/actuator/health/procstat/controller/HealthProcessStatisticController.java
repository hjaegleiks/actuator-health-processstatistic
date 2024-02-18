package com.iksgmbh.actuator.health.procstat.controller;

import com.iksgmbh.actuator.health.procstat.model.HealthProcessStatisticData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health process statistic endpoint for in memory process statistic data -
 * REST controller.
 *
 * @author H. Jägle / IKS GmbH
 * @version 1.6 2023-10-02
 */
@RestController
public class HealthProcessStatisticController {

    final private HealthProcessStatisticData healthProcessStatisticData;

    /**
     * Constructor.
     *
     * @param healthProcessStatisticData  Health process statistic data bean
     */
    @Autowired
    public HealthProcessStatisticController(final HealthProcessStatisticData healthProcessStatisticData) {
        this.healthProcessStatisticData = healthProcessStatisticData;
    }


    // @GetMapping(value = "/actuator/healthProcessStatistic/get/{key}")
    @GetMapping(value = "${management.endpoints.web.base-path:/actuator}/healthProcessStatistic/get/{key}")
    public ResponseEntity<?> healthProcessStatisticGet(@PathVariable String key) {

        if (StringUtils.hasText(key)) {
            Object value = healthProcessStatisticData.getHealthProcessStatisticDataMap().get(key);
            if (value != null) {
                return ResponseEntity.ok(String.valueOf(value));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().body("400 - Bad request.");
        }
    }

    // @GetMapping(value = "/actuator/healthProcessStatistic/reset")
    @GetMapping(value = "${management.endpoints.web.base-path:/actuator}/healthProcessStatistic/reset")
    public ResponseEntity<?> healthProcessStatisticReset(@RequestParam(required = false) String reset) {

        if (Boolean.TRUE.toString().equals(reset)) {
            healthProcessStatisticData.reset();
            return ResponseEntity.ok("200 - Health endpoint process statistic data was reset.");
        } else {
            return ResponseEntity.badRequest().body("400 - Bad request.");
        }
    }
}
