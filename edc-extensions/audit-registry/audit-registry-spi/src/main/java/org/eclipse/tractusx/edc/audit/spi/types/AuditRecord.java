/********************************************************************************
 * Copyright (c) 2025 Contributors
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.spi.types;

import java.util.Map;

/**
 * Immutable representation of an audit trail entry persisted by the registry.
 */
public class AuditRecord {

    private String id;
    private long createdAt;
    private String eventType;
    private String eventId;
    private String participantId;
    private String correlationId;
    private Map<String, Object> eventData;
    private String eventPayload;
    private String source;
    private long timestamp;

    public String getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public String getSource() {
        return source;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static class Builder {

        private final AuditRecord record = new AuditRecord();

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder id(String id) {
            record.id = id;
            return this;
        }

        public Builder createdAt(long createdAt) {
            record.createdAt = createdAt;
            return this;
        }

        public Builder eventType(String eventType) {
            record.eventType = eventType;
            return this;
        }

        public Builder eventId(String eventId) {
            record.eventId = eventId;
            return this;
        }

        public Builder participantId(String participantId) {
            record.participantId = participantId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            record.correlationId = correlationId;
            return this;
        }

        public Builder eventData(Map<String, Object> eventData) {
            record.eventData = eventData;
            return this;
        }

        public Builder eventPayload(String eventPayload) {
            record.eventPayload = eventPayload;
            return this;
        }

        public Builder source(String source) {
            record.source = source;
            return this;
        }

        public Builder timestamp(long timestamp) {
            record.timestamp = timestamp;
            return this;
        }

        public AuditRecord build() {
            return record;
        }
    }
}

