/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.SubRank;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;

public class CommandRemoveSubRank extends Command {
    public CommandRemoveSubRank() {
        super("removesubrank", Collections.emptyList(), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            int id;
            try {
                id = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ignored) {
                message.reply("Invalid syntax. Correct syntax: **!removesubrank [rank ID]**").queue();
                return;
            }
            SubRank rank = SubRank.getByID(id);
            if (rank != null) {
                if (GuildManager.getAllowedSubRanks(member.getGuild().getIdLong()).contains(rank)) {
                    GuildManager.removeAllowedSubRank(member.getGuild().getIdLong(), rank);
                    message.reply("SubRank is no longer allowed.").queue();

                } else {
                    message.reply("That subrank is already disallaowed here!").queue();
                }
            } else {
                message.reply("That is not a valid subrank ID.").queue();
            }
        } else {
            message.reply("Invalid syntax. Correct syntax: **!removesubrank [rank ID]**").queue();
        }
    }
}
