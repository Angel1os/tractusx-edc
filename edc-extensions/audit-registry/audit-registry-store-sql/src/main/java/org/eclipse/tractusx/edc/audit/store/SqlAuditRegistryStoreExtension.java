/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.store;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.tractusx.edc.audit.spi.AuditRegistryStore;
import org.eclipse.tractusx.edc.audit.store.sql.PostgresAuditRegistryStatements;
import org.eclipse.tractusx.edc.audit.store.sql.SqlAuditRegistryStatements;
import org.eclipse.tractusx.edc.audit.store.sql.SqlAuditRegistryStore;

@Extension("Audit Registry SQL Store")
public class SqlAuditRegistryStoreExtension implements ServiceExtension {

    @Setting(value = "Datasource name used for the audit registry store",
            defaultValue = DataSourceRegistry.DEFAULT_DATASOURCE)
    public static final String DATASOURCE_SETTING = "edc.sql.store.audit.datasource";

    @Setting(value = "Database schema that hosts the audit table", defaultValue = "edc")
    public static final String SCHEMA_SETTING = "edc.sql.store.audit.schema";

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject
    private QueryExecutor queryExecutor;

    @Inject
    private TypeManager typeManager;

    private SqlAuditRegistryStatements statements;

    @Override
    public String name() {
        return "Audit Registry SQL Store";
    }

    @Provider
    public AuditRegistryStore auditRegistryStore(ServiceExtensionContext context) {
        var dataSourceName = context.getSetting(DATASOURCE_SETTING, DataSourceRegistry.DEFAULT_DATASOURCE);
        if (statements == null) {
            var schema = context.getSetting(SCHEMA_SETTING, "edc");
            statements = new PostgresAuditRegistryStatements(schema);
        }

        return new SqlAuditRegistryStore(
                dataSourceRegistry,
                dataSourceName,
                transactionContext,
                typeManager.getMapper(),
                queryExecutor,
                statements
        );
    }
}

