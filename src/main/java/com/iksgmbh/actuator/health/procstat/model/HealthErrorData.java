package com.iksgmbh.actuator.health.procstat.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Health process statistic endpoint for in memory process statistic data -
 * error data bean.
 *
 * @author H. Jägle / IKS GmbH
 * @version 1.6 2023-10-02
 */
public class HealthErrorData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Timestamp of error event. */
    private OffsetDateTime timestamp;

    /** Function where error event occurred. */
    private String function;

    /** Return code. */
    private String returnCode;

    /** Message text. */
    private String messageText;

    /** Instruction text. */
    private String instructionText;

    /** Reference Id(s). */
    private String referenceId;


    /**
     * Constructor.
     */
    public HealthErrorData() {
        super();
    }

    /**
     * Constructor.
     *
     * @param timestamp        Timestamp when error event occurred
     * @param function         Function where error event occurred
     * @param returnCode       Return code
     * @param messageText      Message text
     * @param instructionText  Instruction text
     * @param referenceId      Reference Id(s)
     */
    public HealthErrorData(final OffsetDateTime timestamp,
            final String function,
            final String returnCode,
            final String messageText,
            final String instructionText,
            final String referenceId) {
        super();
        this.timestamp = timestamp;
        this.function = function;
        this.returnCode = returnCode;
        this.messageText = messageText;
        this.instructionText = instructionText;
        this.referenceId = referenceId;
    }

    /**
     * Constructor.
     *
     * @param function         Function where error event occurred
     * @param returnCode       Return code
     * @param messageText      Message text
     * @param instructionText  Instruction text
     * @param referenceId      Reference Id(s)
     */
    public HealthErrorData(final String function,
            final String returnCode,
            final String messageText,
            final String instructionText,
            final String referenceId) {
        super();
        this.timestamp = null;
        this.function = function;
        this.returnCode = returnCode;
        this.messageText = messageText;
        this.instructionText = instructionText;
        this.referenceId = referenceId;
    }


    @Override
    public String toString() {
        return "HealthErrorData{" +
               "timestamp=" + timestamp +
               ", function='" + function + "'" +
               ", returnCode='" + returnCode + "'" +
               ", messageText='" + messageText + "'" +
               ", instructionText='" + instructionText + "'" +
               ", referenceId='" + referenceId + "'" +
               "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HealthErrorData that = (HealthErrorData) o;
        return Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(function, that.function) &&
               Objects.equals(returnCode, that.returnCode) &&
               Objects.equals(messageText, that.messageText) &&
               Objects.equals(instructionText, that.instructionText) &&
               Objects.equals(referenceId, that.referenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, function, returnCode, messageText, instructionText, referenceId);
    }

    // Getter + setter

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(final String function) {
        this.function = function;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(final String returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(final String messageText) {
        this.messageText = messageText;
    }

    public String getInstructionText() {
        return instructionText;
    }

    public void setInstructionText(final String instructionText) {
        this.instructionText = instructionText;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(final String referenceId) {
        this.referenceId = referenceId;
    }
}
