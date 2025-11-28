/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.store.sql;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.sql.translation.PostgresqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlOperatorTranslator;
import org.eclipse.edc.sql.translation.SqlQueryStatement;

import static java.lang.String.format;

public class PostgresAuditRegistryStatements implements SqlAuditRegistryStatements {

    private final String tableName;
    private final SqlOperatorTranslator operatorTranslator = new PostgresqlOperatorTranslator();

    public PostgresAuditRegistryStatements(String schema) {
        var prefix = (schema == null || schema.isBlank()) ? "" : schema + ".";
        this.tableName = prefix + "audit_record";
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getIdColumn() {
        return "id";
    }

    @Override
    public String getCreatedAtColumn() {
        return "created_at";
    }

    @Override
    public String getEventTypeColumn() {
        return "event_type";
    }

    @Override
    public String getEventIdColumn() {
        return "event_id";
    }

    @Override
    public String getParticipantIdColumn() {
        return "participant_id";
    }

    @Override
    public String getCorrelationIdColumn() {
        return "correlation_id";
    }

    @Override
    public String getEventDataColumn() {
        return "event_data";
    }

    @Override
    public String getEventPayloadColumn() {
        return "event_payload";
    }

    @Override
    public String getSourceColumn() {
        return "source";
    }

    @Override
    public String getTimestampColumn() {
        return "event_timestamp";
    }

    @Override
    public SqlQueryStatement createQuery(QuerySpec querySpec) {
        var select = format("SELECT * FROM %s", getTableName());
        return new SqlQueryStatement(select, querySpec, new AuditRecordMapping(this), operatorTranslator);
    }
}

