/*
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.serialization.upcasting.event;

import org.axonframework.eventhandling.AbstractSequencedDomainEventEntry;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.serialization.Serializer;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Steven van Beelen
 */
@Entity
@Table(indexes = @Index(columnList = "aggregateIdentifier,sequenceNumber,type", unique = true))
public class TestDomainEventEntry extends AbstractSequencedDomainEventEntry<String> {

    public TestDomainEventEntry(DomainEventMessage event, Serializer serializer) {
        super(event, serializer, String.class);
    }

    protected TestDomainEventEntry() {
    }
}
