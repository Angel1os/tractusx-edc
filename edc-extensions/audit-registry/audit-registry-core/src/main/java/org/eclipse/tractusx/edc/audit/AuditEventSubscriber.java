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

package org.eclipse.tractusx.edc.audit;

import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventEnvelope;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.tractusx.edc.audit.spi.AuditRegistryStore;
import org.eclipse.tractusx.edc.audit.spi.types.AuditRecord;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Subscribes to every domain Event and persists an AuditRecord.
 */
public class AuditEventSubscriber implements EventSubscriber {

    private final AuditRegistryStore store;
    private final TypeManager typeManager;
    private final Monitor monitor;
    private final Clock clock;

    public AuditEventSubscriber(AuditRegistryStore store, TypeManager typeManager, Monitor monitor, Clock clock) {
        this.store = store;
        this.typeManager = typeManager;
        this.monitor = monitor;
        this.clock = clock;
    }

    @Override
    public <E extends Event> void on(EventEnvelope<E> eventEnvelope) {
        try {
            var record = createRecord(eventEnvelope);
            var result = store.save(record);
            if (result.failed()) {
                monitor.warning(String.format("Failed to store audit record for event %s: %s",
                        eventEnvelope.getId(),result.getFailureDetail()));
            }
        } catch (Exception ex) {
            monitor.severe("Error while processing audit event %s".formatted(eventEnvelope.getId()), ex);
        }
    }

    private <E extends Event> AuditRecord createRecord(EventEnvelope<E> envelope) {
        var payload = envelope.getPayload();
        var eventJson = typeManager.writeValueAsString(payload);
        return AuditRecord.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .createdAt(clock.millis())
                .eventId(envelope.getId())
                .eventType(payload.getClass().getName())
                .source(extractSource(payload))
                .participantId(extractParticipant(payload))
                .correlationId(extractCorrelationId(payload))
                .eventData(extractEventData(payload))
                .eventPayload(eventJson)
                .timestamp(clock.millis())
                .build();
    }

    private Map<String, Object> extractEventData(Event payload) {
        var result = new HashMap<String, Object>();
        result.put("type", payload.getClass().getSimpleName());
        result.put("id", extractField(payload, "getId"));
        return result;
    }

    private String extractField(Event payload, String methodName) {
        try {
            var method = payload.getClass().getMethod(methodName);
            var value = method.invoke(payload);
            return value != null ? value.toString() : null;
        } catch (Exception ex) {
            monitor.debug("Unable to extract %s for audit entry: %s".formatted(methodName, ex.getMessage()));
            return null;
        }
    }

    private String extractSource(Event payload) {
        var typeName = payload.getClass().getSimpleName().toLowerCase();
        if (typeName.contains("transfer")) {
            return "transfer-process";
        } else if (typeName.contains("contract")) {
            return "contract-negotiation";
        } else if (typeName.contains("asset")) {
            return "asset";
        }
        return "generic";
    }

    private String extractParticipant(Event payload) {
        try {
            var method = payload.getClass().getMethod("getParticipantId");
            var value = method.invoke(payload);
            return value != null ? value.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractCorrelationId(Event payload) {
        try {
            var method = payload.getClass().getMethod("getCorrelationId");
            var value = method.invoke(payload);
            return value != null ? value.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}

