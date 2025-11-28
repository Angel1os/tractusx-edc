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

package org.eclipse.tractusx.edc.audit.spi;

import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.tractusx.edc.audit.spi.types.AuditRecord;

import java.util.stream.Stream;

/**
 * Abstraction for persisting and querying {@link AuditRecord}s.
 */
public interface AuditRegistryStore {

    StoreResult<Void> save(AuditRecord record);

    AuditRecord findById(String id);

    StoreResult<Stream<AuditRecord>> query(QuerySpec querySpec);

    int deleteOlderThan(long timestampMillis);
}

