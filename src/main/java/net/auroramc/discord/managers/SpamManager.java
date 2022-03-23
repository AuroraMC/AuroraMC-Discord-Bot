/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpamManager {

    private static final double SIMILARITY_THRESHOLD = 0.8d;
    private static final int TAGS_THRESHOLD = 5;

    private static final List<Long> warnedSpamTagUsers;
    private static final List<Long> warnedSpamUsers;
    private static final Map<Long, Deque<String>> latestMessages;

    private static final ScheduledExecutorService scheduler;

    static {
        scheduler = Executors.newScheduledThreadPool(10);
    }

    static {
        warnedSpamTagUsers = new ArrayList<>();
        warnedSpamUsers = new ArrayList<>();
        latestMessages = new HashMap<>();
    }

    public static void onMessage(Message message) {
        if (message.getMentionedUsers().size() > TAGS_THRESHOLD) {
            message.delete().queue();
            if (warnedSpamTagUsers.contains(message.getAuthor().getIdLong())) {
                PunishmentManager.punishUser(message.getJDA().getSelfUser(), message.getGuild(), message.getAuthor().getIdLong(), 1, "[AutoPunish] Spam Tagging");
            } else {
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + ", please do not spam tag or you will be punished!").queue();
                warnedSpamTagUsers.add(message.getAuthor().getIdLong());
                scheduler.schedule(() -> {
                    warnedSpamTagUsers.remove(message.getAuthor().getIdLong());
                }, 5, TimeUnit.MINUTES);
            }
            return;
        }

        if (latestMessages.containsKey(message.getAuthor().getIdLong())) {
            Deque<String> deque = latestMessages.get(message.getAuthor().getIdLong());
            String msg = message.getContentStripped();
            latestMessages.get(message.getAuthor().getIdLong()).addLast(msg);
            scheduler.schedule(() -> {
                deque.remove(msg);
                if (deque.size() == 0) {
                    latestMessages.remove(message.getAuthor().getIdLong());
                }
            }, 5, TimeUnit.MINUTES);
            if (deque.size() >= 4) {
                if (deque.size() >= 5) {
                    deque.removeFirst();
                }
                double totalSimilarity = 0.0d;
                int comparisons = 0;
                for (int i = 0; i < deque.size()-1; i++) {
                    for (int k = i+1; k < deque.size(); k++) {
                        totalSimilarity += similarity(((LinkedList<String>)deque).get(i), ((LinkedList<String>)deque).get(k));
                        comparisons++;
                    }
                }

                //Get an average similarity between messages.
                totalSimilarity = totalSimilarity / comparisons;
                if (totalSimilarity >= SIMILARITY_THRESHOLD) {
                    if (warnedSpamUsers.contains(message.getAuthor().getIdLong())) {
                        PunishmentManager.punishUser(message.getJDA().getSelfUser(), message.getGuild(), message.getAuthor().getIdLong(), 1, "[AutoPunish] Spamming 4+ same or similar messages.");
                    } else {
                        message.getChannel().sendMessage(message.getAuthor().getAsMention() + ", please do not spam or you will be punished!").queue();
                        warnedSpamUsers.add(message.getAuthor().getIdLong());
                        scheduler.schedule(() -> {
                            warnedSpamUsers.remove(message.getAuthor().getIdLong());
                        }, 5, TimeUnit.MINUTES);
                    }
                }
            }
        } else {
            Deque<String> deque = new ArrayDeque<>();
            String msg = message.getContentStripped();
            deque.addFirst(msg);
            latestMessages.put(message.getAuthor().getIdLong(), deque);
            scheduler.schedule(() -> {
                deque.remove(msg);
                if (deque.size() == 0) {
                    latestMessages.remove(message.getAuthor().getIdLong());
                }
            }, 5, TimeUnit.MINUTES);
        }
    }

    public static double similarity(String first, String second) {
        String longer = first, shorter = second;
        if (first.length() < second.length()) { // longer should always have greater length
            longer = second; shorter = first;
        }
        int longerLength = longer.length();
        if (longerLength == 0) return 1.0; /* both strings are zero length */

        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength;

    }

}
