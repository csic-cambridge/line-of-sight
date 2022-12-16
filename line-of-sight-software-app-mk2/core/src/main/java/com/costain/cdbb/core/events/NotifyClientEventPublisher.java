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

package com.costain.cdbb.core.events;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

/**
 * Manages and dispatches queue of client notifications.
 */

@Component
public class NotifyClientEventPublisher implements
    ApplicationListener<NotifyClientEvent>,
    Consumer<FluxSink<NotifyClientEvent>> {

    private static boolean acceptLoopActive = false;
    private static final Object lock = new Object();

    private FluxSink<NotifyClientEvent> activeSink;
    private final Logger logger = LoggerFactory.getLogger(NotifyClientEventPublisher.class);
    private final Executor executor;
    private final BlockingQueue<NotifyClientEvent> queue = new LinkedBlockingQueue<>();


    NotifyClientEventPublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(NotifyClientEvent event) {
        this.queue.add(event);
    }

    @Override
    public void accept(FluxSink<NotifyClientEvent> sink) {
        // It seems that (re)connecting websockets will cause this method to be called multiple times
        // with new sink. a) we do not want multiple threads and b) we must use latest sink
        synchronized (lock) {
            activeSink = sink;
        }
        this.executor.execute(() -> {
            if (!acceptLoopActive) {
                acceptLoopActive = true;
                while (acceptLoopActive) {
                    try {
                        NotifyClientEvent event = queue.take();
                        activeSink.next(event);
                    } catch (InterruptedException e) {
                        logger.debug("accept - InterruptedException" + e);
                        acceptLoopActive = false;
                    }
                }
            }
        });
    }
}
