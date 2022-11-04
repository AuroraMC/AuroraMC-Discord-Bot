/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.managers.PunishmentManager;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.LongStream;

public class InteractionListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId() != null) {
            String[] ids = event.getButton().getId().split("-");
            if (ids.length == 4 && ids[0].equals("ph")) {
                int page = Integer.parseInt(ids[1]);
                long id = Long.parseLong(ids[2]);
                long recipient = Long.parseLong(ids[3]);
                PunishmentManager.onPageChange(event, event.getMessage(), page, id, recipient);
            } else if (ids.length == 2 && ids[0].equals("roles")) {
                Role role = Objects.requireNonNull(event.getGuild()).getRoleById(Long.parseLong(ids[1]));
                if (role != null) {
                    if (Objects.requireNonNull(event.getMember()).getRoles().contains(role)) {
                        event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                        event.reply("The notification role `" + role.getName() + "` has been removed from you!").setEphemeral(true).queue();
                    } else {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        event.reply("The notification role `" + role.getName() + "` has been added from you!").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("I couldn't find that role, please let an admin know!").setEphemeral(true).queue();
                }
            } else if (ids.length == 3 && ids[0].equals("unlink")) {
                String confirmed = ids[1];
                long id = Long.parseLong(ids[2]);

                if (confirmed.equals("confirm")) {
                    DiscordBot.getDatabaseManager().deleteLink(id);
                    User user = event.getJDA().retrieveUserById(id).complete();
                    user.openPrivateChannel().queue((channel) -> {
                        channel.sendMessageEmbeds(new EmbedBuilder()
                                .setTitle("You've been unlinked!")
                                .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                                .setDescription("Your Discord account has been unlinked from your in-game account by a support member!\n" +
                                        " \n" +
                                        "All of your previous roles have been removed. You can now relink your account.\n" +
                                        "~AuroraMC Leadership Team")
                                .setColor(new Color(0, 170, 170))
                                .build()).queue();
                    });
                    for (Guild guild : user.getMutualGuilds()) {
                        Member member = guild.getMemberById(user.getIdLong());
                        assert member != null;
                        if (guild.getIdLong() == DiscordBot.getSettings().getMasterDiscord()) {
                            for (Role role : member.getRoles()) {
                                guild.removeRoleFromMember(member, role).queue();
                            }
                            guild.addRoleToMember(member, Objects.requireNonNull(guild.getRoleById(886329879002505217L))).queue();
                        } else {
                            member.kick("User unlinked").queue();
                        }
                    }
                    event.editMessage("User unlinked!").setEmbeds(Collections.emptyList()).setActionRow(Collections.emptyList()).queue();
                } else {
                    event.editMessage("Action cancelled.").setEmbeds(Collections.emptyList()).setActionRow(Collections.emptyList()).queue();
                }
            } else if (ids.length == 2 && ids[0].equals("poll")) {
                int id = Integer.parseInt(ids[1]);
                event.reply("Fetching poll results, one moment...").setEphemeral(true).queue();
                CommunityPoll poll = DiscordBot.getDatabaseManager().getPoll(id);
                if (poll != null) {
                    long total = poll.getResponses().values().stream().mapToLong(Long::longValue).sum();
                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle("Results")
                            .setColor(ChatColor.DARK_AQUA.getColor())
                            .setDescription("Results for poll: **" + poll.getQuestion() + "**\n \n" +
                                    "**Total Responses:** `" + total + "`");
                    for (CommunityPoll.PollAnswer answer : poll.getAnswers().values()) {
                        double value = ((poll.getResponses().getOrDefault(answer.getId(), 0L) / (double) total) * 10000);
                        long finalValue = Math.round(value);
                        builder.addField(answer.getId() + ") " + answer.getAnswer(), (finalValue / 100f) + "% of people chose this answer.\n**Total Responses:** `" + poll.getResponses().getOrDefault(answer.getId(), 0L) + "`", false);
                    }
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                } else {
                    event.getChannel().sendMessage("Unable to fetch results.").queue();
                }
            }
        }
    }
}
