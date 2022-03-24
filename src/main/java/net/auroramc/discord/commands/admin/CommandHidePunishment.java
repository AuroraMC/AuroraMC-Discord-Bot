/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.admin;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHidePunishment extends Command {

    public CommandHidePunishment() {
        super("hidepunishment", Arrays.asList("hp", "hide", "h"), Collections.singletonList(Permission.MODERATION));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            String code = args.remove(0);

            PunishmentManager.hidePunishment(message, code);
        } else {
            message.reply("Invalid syntax. Correct syntax: **!hidepunishment [code]**").queue();
        }
    }
}
