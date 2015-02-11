/*
* Copyright 2015 Axibase Corporation or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License").
* You may not use this file except in compliance with the License.
* A copy of the License is located at
*
* https://www.axibase.com/atsd/axibase-apache-2.0.pdf
*
* or in the "license" file accompanying this file. This file is distributed
* on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied. See the License for the specific language governing
* permissions and limitations under the License.
*/
package com.axibase.tsd.model.data;

import com.axibase.tsd.util.SeverityDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

/**
 * @author Nikolay Malevanny.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Alert {
        private Long id;
        @JsonProperty("rule")
        private String ruleName;
        @JsonProperty("entity")
        private String entityName;
        @JsonProperty("metric")
        private String metricName;
        @JsonProperty("lastEventDate")
        private Long lastEventTime;
        private Double openValues;
        @JsonProperty("openDate")
        private Long openTime;
        private Integer repeatCount;
        private String message;
        private String textValue;
        @JsonDeserialize(using = SeverityDeserializer.class)
        private Severity severity;
        private Boolean acknowledged;
        private Map<String, String> tags;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getRuleName() {
                return ruleName;
        }

        public void setRuleName(String ruleName) {
                this.ruleName = ruleName;
        }

        public String getEntityName() {
                return entityName;
        }

        public void setEntityName(String entityName) {
                this.entityName = entityName;
        }

        public String getMetricName() {
                return metricName;
        }

        public void setMetricName(String metricName) {
                this.metricName = metricName;
        }

        public Long getLastEventTime() {
                return lastEventTime;
        }

        public void setLastEventTime(Long lastEventTime) {
                this.lastEventTime = lastEventTime;
        }

        public Double getOpenValues() {
                return openValues;
        }

        public void setOpenValues(Double openValues) {
                this.openValues = openValues;
        }

        public Long getOpenTime() {
                return openTime;
        }

        public void setOpenTime(Long openTime) {
                this.openTime = openTime;
        }

        public Integer getRepeatCount() {
                return repeatCount;
        }

        public void setRepeatCount(Integer repeatCount) {
                this.repeatCount = repeatCount;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public String getTextValue() {
                return textValue;
        }

        public void setTextValue(String textValue) {
                this.textValue = textValue;
        }

        public Severity getSeverity() {
                return severity;
        }

        public void setSeverity(Severity severity) {
                this.severity = severity;
        }

        public Boolean getAcknowledged() {
                return acknowledged;
        }

        public void setAcknowledged(Boolean acknowledged) {
                this.acknowledged = acknowledged;
        }

        public Map<String, String> getTags() {
                return tags;
        }

        public void setTags(Map<String, String> tags) {
                this.tags = tags;
        }

        @Override
        public String toString() {
                return "Alert{" +
                        "id=" + id +
                        ", ruleName='" + ruleName + '\'' +
                        ", entityName='" + entityName + '\'' +
                        ", metricName='" + metricName + '\'' +
                        ", lastEventTime=" + lastEventTime +
                        ", openValues=" + openValues +
                        ", openTime=" + openTime +
                        ", repeatCount=" + repeatCount +
                        ", message='" + message + '\'' +
                        ", textValue='" + textValue + '\'' +
                        ", severity=" + severity +
                        ", acknowledged=" + acknowledged +
                        ", tags=" + tags +
                        '}';
        }
}
