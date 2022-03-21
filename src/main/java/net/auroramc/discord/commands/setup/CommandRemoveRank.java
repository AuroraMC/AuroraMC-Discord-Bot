/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Rank;
import net.auroramc.discord.entities.SubRank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandRemoveRank extends Command {
    public CommandRemoveRank() {
        super("removerank", Collections.emptyList(), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            int id;
            try {
                id = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
                message.reply("Invalid syntax. Correct syntax: **!removerank [rank ID]**").queue();
                return;
            }
            Rank rank = Rank.getByID(id);
            if (rank != null) {
                if (GuildManager.getAllowedRanks(member.getGuild().getIdLong()).contains(rank)) {
                    GuildManager.removeAllowedRank(member.getGuild().getIdLong(), rank);
                    message.reply("Rank is no longer allowed.").queue();
                    message.getGuild().findMembersWithRoles(message.getGuild().getRoleById(GuildManager.getRankMappings(message.getGuild().getIdLong()).get(rank))).onSuccess((members) -> {

                        for (Member member1 : members) {
                            UUID uuid = DiscordBot.getDatabaseManager().getDiscord(member1.getIdLong());
                            List<SubRank> subranks = DiscordBot.getDatabaseManager().getSubRanks(uuid);
                            if (GuildManager.getAllowedRanks(message.getGuild().getIdLong()).contains(rank)) {
                                continue;
                            }
                            if (GuildManager.getAllowedSubRanks(message.getGuild().getIdLong()).stream().anyMatch(subranks::contains)) {
                                continue;
                            }

                            member1.getUser().openPrivateChannel().complete().sendMessageEmbeds(new EmbedBuilder()
                                    .setAuthor("The AuroraMC Network Leadership Team", "https://auroramc.net", "https://auroramc.net/styles/pie/img/AuroraMCLogoStaffPadded.png")
                                    .setTitle("Removed from Discord!")
                                    .setDescription("An allowed rank was removed from the **" + member1.getGuild().getName() + "** Discord, and as a result you were kicked from the Discord.")
                                    .build()).queue();
                            member1.kick("Allowed rank changed.").queue();
                        }
                    });
                } else {
                    message.reply("That rank is already disallowed here!").queue();
                }
            } else {
                message.reply("That is not a valid rank ID.").queue();
            }
        } else {
            message.reply("Invalid syntax. Correct syntax: **!removerank [rank ID]**").queue();
        }
    }
}
