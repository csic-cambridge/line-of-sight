/*
 * Copyright © 2022 Costain Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.costain.cdbb.core.websocket;




import com.costain.cdbb.core.events.NotifyClientEvent;
import com.costain.cdbb.core.events.NotifyClientEventPublisher;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

/**
 * Sets up and controls web sockets to clients.
 */
@Configuration
public class ReactiveWebSocketConfiguration {

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/cdbb-ws", wsh));
                setOrder(10);
            }
        };
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(NotifyClientEventPublisher eventPublisher) {

        Flux<NotifyClientEvent> publish = Flux.create(eventPublisher).share();

        return session -> {
            Flux<WebSocketMessage> messageFlux = publish.map(evt -> {
                return evt.toJson();
            }).map(str -> {
                return session.textMessage(str);
            });
            return session.send(messageFlux);
        };
    }

}
