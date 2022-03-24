/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;

public class CommandForceUpdate extends Command {
    public CommandForceUpdate() {
        super("forceupdate", Collections.singletonList("fu"), Collections.singletonList(Permission.ADMIN));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        DiscordBot.forceRankUpdate();
        message.reply("Forced a rank update.").queue();
    }
}
