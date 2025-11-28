/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.store.sql;

import org.eclipse.edc.sql.translation.TranslationMapping;

class AuditRecordMapping extends TranslationMapping {

    AuditRecordMapping(SqlAuditRegistryStatements statements) {
        add("id", statements.getIdColumn());
        add("createdAt", statements.getCreatedAtColumn());
        add("eventType", statements.getEventTypeColumn());
        add("eventId", statements.getEventIdColumn());
        add("participantId", statements.getParticipantIdColumn());
        add("correlationId", statements.getCorrelationIdColumn());
        add("eventData", statements.getEventDataColumn());
        add("eventPayload", statements.getEventPayloadColumn());
        add("source", statements.getSourceColumn());
        add("timestamp", statements.getTimestampColumn());
    }
}

