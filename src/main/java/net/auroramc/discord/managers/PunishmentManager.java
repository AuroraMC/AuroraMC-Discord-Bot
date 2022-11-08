/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.managers;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Punishment;
import net.auroramc.discord.entities.PunishmentLength;
import net.auroramc.discord.entities.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    public static void punishUser(SlashCommandInteraction message, long id, int weight, String reason) {
        Member member = message.getGuild().retrieveMemberById(id).complete();
        if (member.isTimedOut()) {
            message.reply("That user is already punished.").setEphemeral(true).queue();
            return;
        }
        PunishmentLength punishmentLength = generateLength(id, weight);
        long issued = System.currentTimeMillis();
        long expire = ((punishmentLength.getMsValue() == -1d)?-1:issued + Math.round(punishmentLength.getMsValue()));
        String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        DiscordBot.getDatabaseManager().punishUser(code, id, expire == -1d, issued, expire, reason, weight, message.getUser().getIdLong());

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
        message.reply("You have " + ((expire == -1)?"banned":"timed out") + " User " + member.getAsMention() + " for " + punishmentLength.getFormatted() + ". Punishment ID: **" + code + "**").queue();
        if (expire == -1d) {
            member.ban(7, TimeUnit.DAYS).reason(reason).queue();
        } else {
            member.timeoutUntil(Instant.ofEpochMilli(expire)).queue();
        }
    }

    public static void punishUser(SelfUser user, Guild guild, long id, int weight, String reason) {
        Member member = guild.retrieveMemberById(id).complete();
        if (member.isTimedOut()) {
            return;
        }
        PunishmentLength punishmentLength = generateLength(id, weight);
        long issued = System.currentTimeMillis();
        long expire = ((punishmentLength.getMsValue() == -1d)?-1:issued + Math.round(punishmentLength.getMsValue()));
        String code = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        DiscordBot.getDatabaseManager().punishUser(code, id, expire == -1d, issued, expire, reason, weight, user.getIdLong());
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
            member.ban(7, TimeUnit.DAYS).reason(reason).queue();
        } else {
            member.timeoutUntil(Instant.ofEpochMilli(expire)).queue();
        }
    }

    public static void getPunishmentHistory(Member user, SlashCommandInteraction message, long id) {
        List<Punishment> punishments = null;
        Map<Rank, Long> rankMappings = GuildManager.getRankMappings(user.getGuild().getIdLong());
        for (Role role : user.getRoles()) {
            for (Map.Entry<Rank, Long> entry : rankMappings.entrySet()) {
                if (entry.getValue().equals(role.getIdLong())) {
                    if (entry.getKey().getId() >= 11) {
                        punishments = DiscordBot.getDatabaseManager().getAllPunishments(id);
                    }
                }
            }
        }
        if (punishments == null) {
            punishments = DiscordBot.getDatabaseManager().getPunishmentsVisible(id);
        }
        if (punishments.isEmpty()) {
            message.reply("No punishment history found.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(170, 0, 0));
        builder.setTitle("Punishment History for User " + id + "");
        builder.setTimestamp(Instant.now());

        for (int i = 0;i <= 3 && i < punishments.size();i++) {
            Punishment punishment = punishments.get(i);
            String sb = "**Issued:** <t:" + punishment.getIssued() / 1000L + ":f> (<t:" + punishment.getIssued() / 1000L + ":R>)\n" +
                    "**Weight:** " + WEIGHTS[punishment.getWeight() - 1] + "\n" +
                    "**Reason:** " + punishment.getReason() + "\n" +
                    "**Length:** " + new PunishmentLength(punishment.getExpire() - punishment.getIssued()).getFormatted() + "\n" +
                    "**Expires:** " + ((punishment.getExpire() == -1)?"Never":"<t:" + punishment.getExpire() / 1000L + ":f> (<t:" + punishment.getExpire() / 1000L + ":R>)") + "\n" +
                    "**Punisher:** <@" + punishment.getPunisher() + ">\n" +
                    "**Evidence:** " + ((punishment.getEvidence() != null)?punishment.getEvidence():"None") + "\n" +
                    ((punishment.getRemover() != 0 && punishment.getRemovalReason() != null)?"**Remover**: <@" + punishment.getRemover() + ">\n"  +
                            "**Removal Reason:** " + punishment.getRemovalReason():"");
            builder.addField("Punishment " + punishment.getPunishmentCode(), sb, false);
        }
        builder.setFooter("Page 1/" + ((punishments.size() / 4) + 1));
        MessageEmbed embed = builder.build();
        Button button = Button.primary("ph-2-" + id + "-" + user.getIdLong(), "Next Page").withEmoji(Emoji.fromUnicode("U+27A1"));

        if (punishments.size() > 4) {
            message.replyEmbeds(embed).setActionRow(button).queue(message2 -> message2.deleteOriginal().queueAfter(5, TimeUnit.MINUTES));
        } else {
            message.replyEmbeds(embed).queue(message2 -> message2.deleteOriginal().queueAfter(5, TimeUnit.MINUTES));
        }
    }

    public static void onPageChange(ButtonInteractionEvent event, Message message, int page, long id, long recipient) {
        List<Punishment> punishments = null;
        Map<Rank, Long> rankMappings = GuildManager.getRankMappings(message.getGuild().getMemberById(recipient).getGuild().getIdLong());
        for (Role role : message.getGuild().getMemberById(recipient).getRoles()) {
            for (Map.Entry<Rank, Long> entry : rankMappings.entrySet()) {
                if (entry.getValue().equals(role.getIdLong())) {
                    if (entry.getKey().getId() >= 11) {
                        punishments = DiscordBot.getDatabaseManager().getAllPunishments(id);
                    }
                }
            }
        }
        if (punishments == null) {
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

        for (int i = (4*(page - 1));i <= (4*(page - 1)) + 3 && i < punishments.size();i++) {
            Punishment punishment = punishments.get(i);
            String sb = "**Issued:** <t:" + punishment.getIssued() / 1000L + ":f> (<t:" + punishment.getIssued() / 1000L + ":R>)\n" +
                    "**Weight:** " + WEIGHTS[punishment.getWeight() - 1] + "\n" +
                    "**Reason:** " + punishment.getReason() + "\n" +
                    "**Length:** " + new PunishmentLength(punishment.getExpire() - punishment.getIssued()).getFormatted() + "\n" +
                    "**Expires:** " + ((punishment.getExpire() == -1)?"Never":"<t:" + punishment.getExpire() / 1000L + ":f> (<t:" + punishment.getExpire() / 1000L + ":R>)") + "\n" +
                    "**Punisher:** <@" + punishment.getPunisher() + ">\n" +
                    "**Evidence:** " + ((punishment.getEvidence() != null)?punishment.getEvidence():"None") + "\n" +
                    ((punishment.getRemover() != 0 && punishment.getRemovalReason() != null)?"**Remover**: <@" + punishment.getRemover() + ">\n"  +
                            "**Removal Reason:** " + punishment.getRemovalReason():"");
            builder.addField("Punishment " + punishment.getPunishmentCode(), sb, false);
        }
        builder.setFooter("Page " + page + "/" + ((punishments.size() / 4) + 1));
        MessageEmbed embed = builder.build();
        Button next = Button.primary("ph-" + (page + 1) + "-" + id + "-" + recipient, "Next Page").withEmoji(Emoji.fromUnicode("U+27A1"));
        Button prev = Button.primary("ph-" + (page - 1) + "-" + id + "-" + recipient, "Previous Page").withEmoji(Emoji.fromUnicode("U+2B05"));
        if (punishments.size() > (4*(page - 1)) + 3) {
            if (page == 1) {
                event.editMessageEmbeds(embed).setActionRow(next).queue();
            } else {
                event.editMessageEmbeds(embed).setActionRow(prev, next).queue();
            }
        } else {
            event.editMessageEmbeds(embed).setActionRow(prev).queue();
        }
    }

    public static void removePunishment(SlashCommandInteraction message, String code, String reason) {
        Punishment punishment = DiscordBot.getDatabaseManager().getPunishment(code);
        if (punishment == null) {
            message.reply("That is not a valid punishment code.").setEphemeral(true).queue();
            return;
        }
        Member member = message.getGuild().retrieveMember(punishment.getPunished()).complete();
        if (member != null) {
            if (!member.isTimedOut()) {
                message.reply("That user is not currently punished.").setEphemeral(true).queue();
                return;
            }
            member.removeTimeout().queue();
        } else {
            Guild.Ban ban = message.getGuild().retrieveBan(punishment.getPunished()).complete();
            if (ban == null) {
                message.reply("That user is not currently punished.").setEphemeral(true).queue();
                return;
            }
            message.getGuild().unban(ban.getUser()).queue();
        }

        DiscordBot.getDatabaseManager().removePunishment(punishment.getPunishmentCode(), message.getUser().getIdLong(), reason);
        message.reply("User has been unpunished.").queue();
    }

    public static void attachEvidence(SlashCommandInteraction message, String code, String evidence) {
        Punishment punishment = DiscordBot.getDatabaseManager().getPunishment(code);
        if (punishment == null) {
            message.reply("That is not a valid punishment code.").setEphemeral(true).queue();
            return;
        }
        DiscordBot.getDatabaseManager().attachEvidence(punishment.getPunishmentCode(), evidence);
        message.reply("Evidence attached.").queue();
    }

    public static void hidePunishment(SlashCommandInteraction message, String code) {
        Punishment punishment = DiscordBot.getDatabaseManager().getPunishment(code);
        if (punishment == null) {
            message.reply("That is not a valid punishment code.").setEphemeral(true).queue();
            return;
        }
        DiscordBot.getDatabaseManager().hidePunishment(punishment.getPunishmentCode());
        message.reply("Punishment hidden.").queue();
    }

    public static void showPunishment(SlashCommandInteraction message, String code) {
        Punishment punishment = DiscordBot.getDatabaseManager().getPunishment(code);
        if (punishment == null) {
            message.reply("That is not a valid punishment code.").setEphemeral(true).queue();
            return;
        }
        DiscordBot.getDatabaseManager().showPunishment(punishment.getPunishmentCode());
        message.reply("Punishment now visible.").queue();
    }

}
