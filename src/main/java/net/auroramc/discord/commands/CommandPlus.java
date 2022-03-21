/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.PlusManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandPlus extends Command {


    public CommandPlus() {
        super("plus", Collections.singletonList("checkplus"), Collections.singletonList(Permission.PLAYER));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        UUID uuid = DiscordBot.getDatabaseManager().getDiscord(member.getIdLong());
        PlusManager.onCommand(member, message, uuid);
    }
}
