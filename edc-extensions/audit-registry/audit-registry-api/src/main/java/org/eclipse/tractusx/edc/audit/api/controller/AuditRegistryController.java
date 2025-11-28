/********************************************************************************
 Copyright (c) 2025 Contributors

 SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.edc.audit.api.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.tractusx.edc.audit.spi.AuditRegistryStore;
import org.eclipse.tractusx.edc.audit.spi.types.AuditRecord;

import java.util.List;

@Path("/audit")
@Produces(MediaType.APPLICATION_JSON)
public class AuditRegistryController {

    private final AuditRegistryStore store;

    public AuditRegistryController(AuditRegistryStore store) {
        this.store = store;
    }

    @GET
    public Response queryAuditRecords(@QueryParam("offset") Integer offset,
                                      @QueryParam("limit") Integer limit) {
        var query = QuerySpec.Builder.newInstance()
                .offset(offset != null ? offset : 0)
                .limit(limit != null ? limit : 50)
                .build();
        var result = store.query(query);
        if (result.failed()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(result.getFailureDetail())
                    .build();
        }

        List<AuditRecord> records = result.getContent().toList();
        return Response.ok(records).build();
    }

    @GET
    @Path("/{id}")
    public Response getAuditRecord(@PathParam("id") String id) {
        var record = store.findById(id);
        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Audit record %s not found".formatted(id))
                    .build();
        }
        return Response.ok(record).build();
    }
}

