/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.store.sql;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.statement.SqlStatements;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

public interface SqlAuditRegistryStatements extends SqlStatements {

    String getTableName();

    String getIdColumn();

    String getCreatedAtColumn();

    String getEventTypeColumn();

    String getEventIdColumn();

    String getParticipantIdColumn();

    String getCorrelationIdColumn();

    String getEventDataColumn();

    String getEventPayloadColumn();

    String getSourceColumn();

    String getTimestampColumn();

    SqlQueryStatement createQuery(QuerySpec querySpec);

    default String getInsertTemplate() {
        return """
                INSERT INTO %s (
                    %s, %s, %s, %s, %s,
                    %s, %s, %s, %s, %s
                ) VALUES (?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?)
                """.formatted(getTableName(), getIdColumn(), getCreatedAtColumn(), getEventTypeColumn(),
                getEventIdColumn(), getParticipantIdColumn(), getCorrelationIdColumn(), getEventDataColumn(),
                getEventPayloadColumn(), getSourceColumn(), getTimestampColumn());
    }

    default String getSelectByIdTemplate() {
        return "SELECT * FROM %s WHERE %s = ?".formatted(getTableName(), getIdColumn());
    }

    default String getSelectAllTemplate() {
        return "SELECT * FROM %s".formatted(getTableName());
    }

    default String getDeleteOlderThanTemplate() {
        return "DELETE FROM %s WHERE %s < ?".formatted(getTableName(), getCreatedAtColumn());
    }
}

