/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.api;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.eclipse.tractusx.edc.audit.api.controller.AuditRegistryController;
import org.eclipse.tractusx.edc.audit.spi.AuditRegistryStore;

@Extension("Audit Registry API")
public class AuditRegistryApiExtension implements ServiceExtension {

    @Inject
    private WebService webService;

    @Inject
    private AuditRegistryStore store;

    @Override
    public void initialize(ServiceExtensionContext context) {
        webService.registerResource(ApiContext.MANAGEMENT, new AuditRegistryController(store));
        context.getMonitor().info("Audit registry API available under /api/management/v3/audit");
    }
}

