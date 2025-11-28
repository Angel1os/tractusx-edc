/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.store.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.tractusx.edc.audit.spi.AuditRegistryStore;
import org.eclipse.tractusx.edc.audit.spi.types.AuditRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

public class SqlAuditRegistryStore extends AbstractSqlStore implements AuditRegistryStore {

    private final SqlAuditRegistryStatements statements;

    public SqlAuditRegistryStore(DataSourceRegistry dataSourceRegistry,
                                 String dataSourceName,
                                 TransactionContext transactionContext,
                                 ObjectMapper objectMapper,
                                 QueryExecutor queryExecutor,
                                 SqlAuditRegistryStatements statements) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.statements = statements;
    }

    @Override
    public StoreResult<Void> save(AuditRecord record) {
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                queryExecutor.execute(connection, statements.getInsertTemplate(),
                        record.getId(),
                        record.getCreatedAt(),
                        record.getEventType(),
                        record.getEventId(),
                        record.getParticipantId(),
                        record.getCorrelationId(),
                        toJson(record.getEventData()),
                        record.getEventPayload(),
                        record.getSource(),
                        record.getTimestamp());
                return StoreResult.success();
            } catch (SQLException ex) {
                throw new EdcPersistenceException(ex);
            }
        });
    }

    @Override
    public AuditRecord findById(String id) {
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                return queryExecutor.single(connection, true, this::mapRecord, statements.getSelectByIdTemplate(), id);
            } catch (SQLException ex) {
                throw new EdcPersistenceException(ex);
            }
        });
    }

    @Override
    public StoreResult<Stream<AuditRecord>> query(QuerySpec querySpec) {
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var statement = statements.createQuery(querySpec);
                var records = queryExecutor.query(connection, true, this::mapRecord,
                        statement.getQueryAsString(), statement.getParameters()).toList();
                return StoreResult.success(records.stream());
            } catch (SQLException ex) {
                throw new EdcPersistenceException(ex);
            }
        });
    }

    @Override
    public int deleteOlderThan(long timestampMillis) {
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                return queryExecutor.execute(connection, statements.getDeleteOlderThanTemplate(), timestampMillis);
            } catch (SQLException ex) {
                throw new EdcPersistenceException(ex);
            }
        });
    }

    private AuditRecord mapRecord(ResultSet resultSet) throws SQLException {
        return AuditRecord.Builder.newInstance()
                .id(resultSet.getString(statements.getIdColumn()))
                .createdAt(resultSet.getLong(statements.getCreatedAtColumn()))
                .eventType(resultSet.getString(statements.getEventTypeColumn()))
                .eventId(resultSet.getString(statements.getEventIdColumn()))
                .participantId(resultSet.getString(statements.getParticipantIdColumn()))
                .correlationId(resultSet.getString(statements.getCorrelationIdColumn()))
                .eventData(readEventData(resultSet))
                .eventPayload(resultSet.getString(statements.getEventPayloadColumn()))
                .source(resultSet.getString(statements.getSourceColumn()))
                .timestamp(resultSet.getLong(statements.getTimestampColumn()))
                .build();
    }

    private Map<String, Object> readEventData(ResultSet resultSet) throws SQLException {
        var json = resultSet.getString(statements.getEventDataColumn());
        if (json == null) {
            return Map.of();
        }
        return fromJson(json, Map.class);
    }
}

