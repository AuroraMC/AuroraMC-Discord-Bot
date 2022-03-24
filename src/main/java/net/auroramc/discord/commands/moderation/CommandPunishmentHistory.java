/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.Collections;
import java.util.List;

public class CommandPunishmentHistory extends Command {


    public CommandPunishmentHistory() {
        super("punishmenthistory", Collections.singletonList("ph"), Collections.singletonList(Permission.MODERATION), Collections.singletonList(956630044225187851L));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            long id;
            try {
                id = Long.parseLong(args.get(0));
            } catch (NumberFormatException e) {
                message.reply("Invalid syntax. Correct syntax: **!ph [user ID]**").queue();
                return;
            }
            PunishmentManager.getPunishmentHistory(member, message, id);
        } else {
            message.reply("Invalid syntax. Correct syntax: **!ph [user ID]**").queue();
        }
    }
}
