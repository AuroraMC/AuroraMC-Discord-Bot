/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.entities.Punishment;
import net.auroramc.discord.entities.PunishmentLength;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PunishmentManager {

    private final static int[] INITIAL_LENGTHS = new int[]{3, 24, 336, 672, -1};
    private final static String[] WEIGHTS = new String[]{"Light", "Medium", "Heavy", "Severe", "Extreme"};

    public static PunishmentLength generateLength(long id, int weight) {
        int number = DiscordBot.getDatabaseManager().getTotalValidPunishments(id, weight);
        double base = (INITIAL_LENGTHS[weight - 1] * (Math.pow(2, number)));
        if (base < 0 || base > 672) {
            return new PunishmentLength(-1);
        }
        return new PunishmentLength(base);
    }

    public static void punishUser(Message message, long id, int weight, String reason) {
        PunishmentLength punishmentLength = generateLength(id, weight);
        long issued = System.currentTimeMillis();
        long expire = ((punishmentLength.getMsValue() == -1d)?-1:issued + Math.round(punishmentLength.getMsValue()));
        String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        DiscordBot.getDatabaseManager().punishUser(code, id, expire == -1d, issued, expire, reason, weight, message.getAuthor().getIdLong());

        Member member = message.getGuild().retrieveMemberById(id).complete();
        member.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle("You've been punished")
                        .setTimestamp(Instant.now())
                        .setAuthor("AuroraMC", "https://auroramc.net")
                        .setDescription("You have been " + ((expire == -1)?"banned":"timed out") + " in the AuroraMC Discord Server.\n" +
                                " \n" +
                                "**Reason:** " + reason + "\n" +
                                "**Length:** " + punishmentLength.getFormatted() + "\n" +
                                "**Expires:** " + ((expire == -1)?"Never":"<t:" + expire / 1000L + ":f> (<t:" + expire / 1000L + ":R>)\n" +
                                " \n" +
                                "If you believe this was given in error, please submit an appeal at https://auroramc.net/appeals"))
                        .build()
        ).queue()));
        if (expire == -1d) {
            member.ban(7, reason).queue();
        } else {
            member.timeoutUntil(Instant.ofEpochMilli(expire)).queue();
        }
    }

    public static void punishUser(SelfUser user, Guild guild, long id, int weight, String reason) {
        PunishmentLength punishmentLength = generateLength(id, weight);
        long issued = System.currentTimeMillis();
        long expire = ((punishmentLength.getMsValue() == -1d)?-1:issued + Math.round(punishmentLength.getMsValue()));
        String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        DiscordBot.getDatabaseManager().punishUser(code, id, expire == -1d, issued, expire, reason, weight, user.getIdLong());

        Member member = guild.retrieveMemberById(id).complete();
        member.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle("You've been punished")
                        .setTimestamp(Instant.now())
                        .setAuthor("AuroraMC", "https://auroramc.net")
                        .setDescription("You have been " + ((expire == -1)?"banned":"timed out") + " in the AuroraMC Discord Server.\n" +
                                " \n" +
                                "**Reason:** " + reason + "\n" +
                                "**Length:** " + punishmentLength.getFormatted() + "\n" +
                                "**Expires:** " + ((expire == -1)?"Never":"<t:" + expire / 1000L + ":f> (<t:" + expire / 1000L + ":R>)\n" +
                                " \n" +
                                "If you believe this was given in error, please submit an appeal at https://auroramc.net/appeals"))
                        .build()
        ).queue()));
        if (expire == -1d) {
            member.ban(7, reason).queue();
        } else {
            member.timeoutUntil(Instant.ofEpochMilli(expire)).queue();
        }
    }

    public static void getPunishmentHistory(Member user, Message message, long id) {
        List<Punishment> punishments;
        if (CommandManager.hasPermission(user, Permission.ADMIN)) {
            punishments = DiscordBot.getDatabaseManager().getAllPunishments(id);
        } else {
            punishments = DiscordBot.getDatabaseManager().getPunishmentsVisible(id);
        }
        if (punishments.isEmpty()) {
            message.reply("No punishment history found.").queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(170, 0, 0));
        builder.setTitle("Punishment History for User " + id);
        builder.setTimestamp(Instant.now());

        for (int i = 0;i <= 5 && i <= punishments.size();i++) {
            Punishment punishment = punishments.get(i);
            String sb = "**Issued:** <t:" + punishment.getIssued() / 1000L + ":f> (<t:" + punishment.getIssued() / 1000L + ":R>)\n" +
                    "**Weight:** " + WEIGHTS[punishment.getWeight() - 1] +
                    "**Reason:** " + punishment.getReason() + "\n" +
                    "**Length:** " + new PunishmentLength(punishment.getExpire() - punishment.getIssued()).getFormatted() + "\n" +
                    "**Expires:** " + ((punishment.getExpire() == -1)?"Never":"<t:" + punishment.getExpire() / 1000L + ":f> (<t:" + punishment.getExpire() / 1000L + ":R>)") + "\n" +
                    "**Punisher:** <@" + punishment.getPunisher() + ">\n" +
                    "**Evidence:** " + ((punishment.getEvidence() != null)?punishment.getEvidence():"None") + "\n" +
                    ((punishment.getRemover() != -1)?"**Remover**: <@" + punishment.getRemover() + ">\n"  +
                            "**Removal Reason:** " + punishment.getRemovalReason():"");
            builder.addField("Punishment " + punishment.getPunishmentCode(), sb, false);
        }
        MessageEmbed embed = builder.build();
        Button button = Button.primary("ph-2-" + id + "-" + user.getIdLong(), "Next Page").withEmoji(Emoji.fromUnicode("U+27A1"));

        if (punishments.size() > 6) {
            message.replyEmbeds(embed).setActionRow(button).delay(5, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        } else {
            message.replyEmbeds(embed).delay(5, TimeUnit.MINUTES).flatMap(Message::delete).queue();
        }
    }

    public static void onPageChange(Message message, int page, long id, long recipient) {
        List<Punishment> punishments;
        if (CommandManager.hasPermission(Objects.requireNonNull(message.getGuild().getMemberById(recipient)), Permission.ADMIN)) {
            punishments = DiscordBot.getDatabaseManager().getAllPunishments(id);
        } else {
            punishments = DiscordBot.getDatabaseManager().getPunishmentsVisible(id);
        }
        if (punishments.isEmpty()) {
            message.reply("No punishment history found.").queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(170, 0, 0));
        builder.setTitle("Punishment History for User " + id);
        builder.setTimestamp(Instant.now());

        for (int i = (6*page);i <= (6*page) + 5 && i <= punishments.size();i++) {
            Punishment punishment = punishments.get(i);
            String sb = "**Issued:** <t:" + punishment.getIssued() / 1000L + ":f> (<t:" + punishment.getIssued() / 1000L + ":R>)\n" +
                    "**Weight:** " + WEIGHTS[punishment.getWeight() - 1] +
                    "**Reason:** " + punishment.getReason() + "\n" +
                    "**Length:** " + new PunishmentLength(punishment.getExpire() - punishment.getIssued()).getFormatted() + "\n" +
                    "**Expires:** " + ((punishment.getExpire() == -1)?"Never":"<t:" + punishment.getExpire() / 1000L + ":f> (<t:" + punishment.getExpire() / 1000L + ":R>)") + "\n" +
                    "**Punisher:** <@" + punishment.getPunisher() + ">\n" +
                    "**Evidence:** " + ((punishment.getEvidence() != null)?punishment.getEvidence():"None") + "\n" +
                    ((punishment.getRemover() != -1)?"**Remover**: <@" + punishment.getRemover() + ">\n"  +
                            "**Removal Reason:** " + punishment.getRemovalReason():"");
            builder.addField("Punishment " + punishment.getPunishmentCode(), sb, false);
        }
        MessageEmbed embed = builder.build();
        Button next = Button.primary("ph-" + (page + 1) + "-" + id + "-" + recipient, "Next Page").withEmoji(Emoji.fromUnicode("U+27A1"));
        Button prev = Button.primary("ph-" + (page - 1) + "-" + id + "-" + recipient, "Previous Page").withEmoji(Emoji.fromUnicode("U+2B05"));
        if (punishments.size() > 6) {
            if (page == 1) {
                message.editMessageEmbeds(embed).setActionRow(next).queue();
            } else {
                message.editMessageEmbeds(embed).setActionRow(prev, next).queue();
            }
        } else {
            message.editMessageEmbeds(embed).queue();
        }
    }

}
