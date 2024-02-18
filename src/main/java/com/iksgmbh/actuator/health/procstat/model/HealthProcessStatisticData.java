package com.iksgmbh.actuator.health.procstat.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Health process statistic endpoint for in memory process statistic data -
 * statistic data bean.
 *
 * @author H. Jägle / IKS GmbH
 * @version 1.6 2023-10-02
 */
public class HealthProcessStatisticData implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(HealthProcessStatisticData.class);

    // Lock mutexes
    private final ReentrantLock errorListLock = new ReentrantLock();
    private final ReentrantLock statisticDataLock = new ReentrantLock();

    // Constants
    public final static String HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER = "Counter";
    public final static String HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP = "Timestamp";
    private final static int STATISTIC_DATA_INIT_SIZE = 8;

    /** Max numbers of entries in error list */
    @Value("${management.health.processStatistic.errorlist.maxsize:10}")
    private int errorListMaxSize;

    /** List of errors occurred since last service start. */
    private List<HealthErrorData> errorList = new ArrayList<>(errorListMaxSize);

    /** List of default health process statistic keys */
    private LinkedHashSet<String> defaultStatisticDataKeyList = new LinkedHashSet<>(STATISTIC_DATA_INIT_SIZE);

    /** List of health process statistic keys */
    private LinkedHashSet<String> statisticDataKeyList = new LinkedHashSet<>(STATISTIC_DATA_INIT_SIZE);

    /** Map of health process statistic data counters */
    private HashMap<String, Long> statisticDataCounterMap = new HashMap<>(STATISTIC_DATA_INIT_SIZE);

    /** Map of health process statistic data timestamps */
    private HashMap<String, OffsetDateTime> statisticDataTimestampMap = new HashMap<>(STATISTIC_DATA_INIT_SIZE);

    /** Starting time of service or timestamp of last reset. */
    private OffsetDateTime serviceStartTimestamp = OffsetDateTime.now();


    /**
     * Constructor.
     */
    public HealthProcessStatisticData() {
        super();
        init();
        reset();
    }

    /**
     * Initialize health process statistic data default key list.
     */
    private void init() {
        try {
            statisticDataLock.lock();
            for (HealthProcessStatisticDataKey healthProcessStatisticDataKey : EnumSet.allOf(HealthProcessStatisticDataKey.class)) {
                defaultStatisticDataKeyList.add(healthProcessStatisticDataKey.toString());
            }
            defaultStatisticDataKeyList.add(HealthProcessStatisticDataKey.error.toString());  // default data key "error", used by method addError()
        } finally {
            statisticDataLock.unlock();
        }
    }

    /**
     * Reset all statistic data attributes.
     */
    public void reset() {
        serviceStartTimestamp = OffsetDateTime.now();
        resetErrorList();
        resetStatisticData();

        log.info("Health endpoint process statistic data was reset.");
    }

    /**
     * Reset error list.
     */
    public void resetErrorList() {
        try {
            errorListLock.lock();
            errorList.clear();
        } finally {
            errorListLock.unlock();
        }
    }

    /**
     * Reset all statistic data maps.
     */
    public void resetStatisticData() {
        try {
            statisticDataLock.lock();
            statisticDataKeyList.clear();
            statisticDataCounterMap.clear();
            statisticDataTimestampMap.clear();
            for (String statisticDataKey : defaultStatisticDataKeyList) {
                addStatisticDataKey(statisticDataKey);
            }
        } finally {
            statisticDataLock.unlock();
        }
    }

    /**
     * Add statistic data key.
     *
     * @param statisticDataKey Name of statistic data key
     */
    protected void addStatisticDataKey(final String statisticDataKey) {
        if (!statisticDataKeyList.contains(statisticDataKey)) {
            statisticDataKeyList.add(statisticDataKey);
            statisticDataCounterMap.put(statisticDataKey, 0L);
            statisticDataTimestampMap.put(statisticDataKey, null);
        }
    }

    /**
     * Increment health process statistic data key value. Stores also timestamp of event.
     *
     * @param statisticDataKey of health process statistic data to increment
     */
    public void incrementCounter(final Enum<?> statisticDataKey) {
        incrementCounter(statisticDataKey.toString());
    }

    /**
     * Increment health process statistic data key value. Stores also timestamp of event.
     *
     * @param statisticDataKey of health process statistic data to increment
     */
    public void incrementCounter(final String statisticDataKey) {
        try {
            statisticDataLock.lock();

            addStatisticDataKey(statisticDataKey);

            Long counter = statisticDataCounterMap.get(statisticDataKey);
            statisticDataCounterMap.put(statisticDataKey, ++counter);
            statisticDataTimestampMap.put(statisticDataKey, OffsetDateTime.now());
        } finally {
            statisticDataLock.unlock();
        }
    }

    /**
     * Returns all statistic data attributes as map.
     *
     * @return Map of all statistic data attributes
     */
    public LinkedHashMap<String, Object> getHealthProcessStatisticDataMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(statisticDataKeyList.size() * 2 + 2);

        map.put("serviceStartTimestamp", serviceStartTimestamp);

        try {
            errorListLock.lock();
            map.put("errorList", errorList);
        } finally {
            errorListLock.unlock();
        }

        try {
            statisticDataLock.lock();
            for (String statisticDataKey : statisticDataKeyList) {
                map.put(statisticDataKey + HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_COUNTER,
                        statisticDataCounterMap.get(statisticDataKey));
                map.put(statisticDataKey + HEALTH_PROCESS_STATISTIC_DATA_SUBKEY_TIMESTAMP,
                        statisticDataTimestampMap.get(statisticDataKey));
            }
        } finally {
            statisticDataLock.unlock();
        }

        return map;
    }


    /**
     * Add error event.
     *
     * @param timestamp        Timestamp when error event occurred
     * @param function         Function where error event occurred
     * @param returnCode       Return code of error event
     * @param messageText      Message text of error event
     * @param instructionText  Instruction text for error event
     * @param referenceId      Reference IDs of error event
     */
    public void addError(final OffsetDateTime timestamp,
            final String function,
            final String returnCode,
            final String messageText,
            final String instructionText,
            final String referenceId) {
        addError(new HealthErrorData(timestamp,
                function,
                returnCode,
                messageText,
                instructionText,
                referenceId));
    }

    /**
     * Add error event.
     *
     * @param function         Function where error event occurred
     * @param returnCode       Return code of error event
     * @param messageText      Message text of error event
     * @param instructionText  Instruction text for error event
     * @param referenceId      Reference IDs of error event
     */
    public void addError(final String function,
            final String returnCode,
            final String messageText,
            final String instructionText,
            final String referenceId) {
        addError(new HealthErrorData(null,
                function,
                returnCode,
                messageText,
                instructionText,
                referenceId));
    }

    /**
     * Add error event.
     *
     * @param healthErrorData health error data
     */
    public void addError(final HealthErrorData healthErrorData) {

        if (healthErrorData.getTimestamp() == null) {
            healthErrorData.setTimestamp(OffsetDateTime.now());
        }

        try {
            errorListLock.lock();

            if (errorList.size() == errorListMaxSize) {
                // max list size reached, remove the oldest entry
                errorList.remove(0);
            }

            errorList.add(healthErrorData);
        } finally {
            errorListLock.unlock();
        }

        incrementCounter(HealthProcessStatisticDataKey.error.toString());
    }

    // Getter + setter

    public OffsetDateTime getServiceStartTimestamp() {
        return serviceStartTimestamp;
    }

    public void setServiceStartTimestamp(final OffsetDateTime serviceStartTimestamp) {
        this.serviceStartTimestamp = serviceStartTimestamp;
    }

    public int getErrorListMaxSize() {
        return errorListMaxSize;
    }

    public void setErrorListMaxSize(final int errorListMaxSize) {
        this.errorListMaxSize = errorListMaxSize;
    }

    public List<HealthErrorData> getErrorList() {
        try {
            errorListLock.lock();
            return new ArrayList<>(errorList);
        } finally {
            errorListLock.unlock();
        }
    }

    public List<String> getDefaultStatisticDataKeyList() {
        return new ArrayList<>(defaultStatisticDataKeyList);
    }

    public void setDefaultStatisticDataKeyList(final List<String> defaultStatisticDataKeyList) {
        setDefaultStatisticDataKeyList(new LinkedHashSet<>(defaultStatisticDataKeyList));
    }

    public void setDefaultStatisticDataKeyList(final LinkedHashSet<String> defaultStatisticDataKeyList) {
        try {
            statisticDataLock.lock();
            this.defaultStatisticDataKeyList = defaultStatisticDataKeyList;
        } finally {
            statisticDataLock.unlock();
        }
    }

    protected List<String> getStatisticDataKeyList() {
        try {
            statisticDataLock.lock();
            return new ArrayList<>(statisticDataKeyList);
        } finally {
            statisticDataLock.unlock();
        }
    }

    public Long getStatisticDataCounter(Enum<?> statisticDataKey) {
        return getStatisticDataCounter(statisticDataKey.toString());
    }

    public Long getStatisticDataCounter(String statisticDataKey) {
        try {
            statisticDataLock.lock();
            return statisticDataCounterMap.get(statisticDataKey);
        } finally {
            statisticDataLock.unlock();
        }
    }

    public OffsetDateTime getStatisticDataTimestamp(Enum<?> statisticDataKey) {
        return getStatisticDataTimestamp(statisticDataKey.toString());
    }

    public OffsetDateTime getStatisticDataTimestamp(String statisticDataKey) {
        try {
            statisticDataLock.lock();
            return statisticDataTimestampMap.get(statisticDataKey);
        } finally {
            statisticDataLock.unlock();
        }
    }
}
