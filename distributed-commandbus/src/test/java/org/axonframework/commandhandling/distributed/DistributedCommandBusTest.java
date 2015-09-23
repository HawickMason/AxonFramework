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

package org.axonframework.commandhandling.distributed;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandDispatchInterceptor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.common.Subscription;
import org.junit.*;

import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author Allard Buijze
 */
public class DistributedCommandBusTest {

    private DistributedCommandBus testSubject;
    private CommandBusConnector mockConnector;
    private Subscription mockSubscription;
    private RoutingStrategy mockRoutingStrategy;
    private CommandHandler<Object> mockHandler;
    private CommandMessage<?> message;
    private FutureCallback<Object, Object> callback;
    private CommandDispatchInterceptor mockDispatchInterceptor;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        mockConnector = mock(CommandBusConnector.class);
        mockSubscription = mock(Subscription.class);
        when(mockConnector.subscribe(any(), any())).thenReturn(mockSubscription);
        mockRoutingStrategy = mock(RoutingStrategy.class);
        when(mockRoutingStrategy.getRoutingKey(isA(CommandMessage.class))).thenReturn("key");

        testSubject = new DistributedCommandBus(mockConnector, mockRoutingStrategy);
        mockHandler = mock(CommandHandler.class);
        message = new GenericCommandMessage<>(new Object());
        callback = new FutureCallback<>();
        mockDispatchInterceptor = mock(CommandDispatchInterceptor.class);
        when(mockDispatchInterceptor.handle(isA(CommandMessage.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        testSubject.setCommandDispatchInterceptors(Collections.singleton(mockDispatchInterceptor));
    }

    @Test
    public void testDispatchIsDelegatedToConnection_WithCallback() throws Exception {
        testSubject.dispatch(message, callback);

        verify(mockRoutingStrategy).getRoutingKey(message);
        verify(mockConnector).send("key", message, callback);
        verify(mockDispatchInterceptor).handle(message);
    }

    @Test
    public void testDispatchIsDelegatedToConnection_WithoutCallback() throws Exception {
        testSubject.dispatch(message);

        verify(mockRoutingStrategy).getRoutingKey(message);
        verify(mockConnector).send("key", message);
        verify(mockDispatchInterceptor).handle(message);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = CommandDispatchException.class)
    public void testDispatchErrorIsPropagated_WithCallback() throws Exception {
        doThrow(new Exception()).when(mockConnector).send(anyString(),
                                                          any(CommandMessage.class),
                                                          any(CommandCallback.class));
        testSubject.dispatch(message, callback);
    }

    @Test(expected = CommandDispatchException.class)
    public void testDispatchErrorIsPropagated_WithoutCallback() throws Exception {
        doThrow(new Exception("Mock")).when(mockConnector).send(anyString(), any(CommandMessage.class));
        testSubject.dispatch(message);
        // exception is logged.
    }

    @Test
    public void testSubscribeIsDoneOnConnector() {
        testSubject.subscribe(Object.class.getName(), mockHandler);

        verify(mockConnector).subscribe(Object.class.getName(), mockHandler);
    }

    @Test
    public void testUnsubscribeIsDoneOnConnector() {
        Subscription subscription = testSubject.subscribe(Object.class.getName(), mockHandler);
        subscription.stop();

        verify(mockSubscription).stop();
    }
}
