/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;

public class CommandSetup extends Command {


    public CommandSetup() {
        super("setup", Collections.emptyList(), null, null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 2) {
            long serverLog, linkLog, mainChannel = message.getChannel().getIdLong();
            try {
                serverLog = Long.parseLong(args.get(0));
                linkLog = Long.parseLong(args.get(1));
            } catch (NumberFormatException ignored) {
                message.reply("Those are not valid channels.").queue();
                return;
            }
            if (message.getGuild().getTextChannelById(serverLog) == null) {
                message.reply("Those are not valid channels.").queue();
                return;
            }
            if (message.getGuild().getTextChannelById(linkLog) == null) {
                message.reply("Those are not valid channels.").queue();
                return;
            }

            GuildManager.onGuildSetup(message.getGuild(), mainChannel, serverLog, linkLog);
            message.reply("Discord successfully setup.").queue();
        } else {
            message.reply("Invalid syntax. Correct syntax: **!setup [server logging channel ID] [link logging channel ID]**").queue();
        }
    }
}
