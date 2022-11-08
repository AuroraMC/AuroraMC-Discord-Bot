/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageCache {

    private static final Map<Long, Message> messageCache;

    private static final ScheduledExecutorService scheduler;

    static {
        messageCache = new HashMap<>();
        scheduler = Executors.newScheduledThreadPool(10);
    }

    public static void onMessage(Message message) {
        messageCache.put(message.getIdLong(), message);
        scheduler.schedule(() -> {
            messageCache.remove(message.getIdLong());
        }, 1, TimeUnit.DAYS);
    }

    public static Message getMessage(long id) {
        return messageCache.get(id);
    }

    public static void removeMessage(long id) {
        messageCache.remove(id);
    }

}
