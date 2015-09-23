/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.eventhandling;

import org.axonframework.common.Subscription;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Allard Buijze
 */
public class SimpleEventBusTest {

    private Cluster listener1;
    private Cluster listener2;
    private EventBus testSubject;
    private Cluster listener3;

    @Before
    public void setUp() {
        listener1 = mock(Cluster.class);
        listener2 = mock(Cluster.class);
        listener3 = mock(Cluster.class);
        testSubject = new SimpleEventBus();
    }

    @Test
    public void testEventIsDispatchedToSubscribedListeners() throws Exception {
        testSubject.publish(newEvent());
        testSubject.subscribe(listener1);
        // subscribing twice should not make a difference
        Subscription subscription1 = testSubject.subscribe(listener1);
        testSubject.publish(newEvent());
        Subscription subscription2 = testSubject.subscribe(listener2);
        Subscription subscription3 = testSubject.subscribe(listener3);
        testSubject.publish(newEvent());
        subscription1.close();
        testSubject.publish(newEvent());
        subscription2.close();
        subscription3.close();
        // unsubscribe a non-subscribed listener should not fail
        subscription3.close();
        testSubject.publish(newEvent());

        verify(listener1, times(2)).handle(anyList());
        verify(listener2, times(2)).handle(anyList());
        verify(listener3, times(2)).handle(anyList());
    }

    private EventMessage newEvent() {
        return new GenericEventMessage<>(new Object());
    }
}
