/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.managers.PunishmentManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandUnlink extends Command {
    public CommandUnlink() {
        super("unlink", Collections.singletonList("ul"), Arrays.asList(Permission.SUPPORT, Permission.ADMIN), Collections.singletonList(956868194491064370L));
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            String username = args.remove(0);
            long id = DiscordBot.getDatabaseManager().getDiscordID(username);
            if (id != -1) {
                message.replyEmbeds(
                        new EmbedBuilder()
                                .setTimestamp(Instant.now())
                                .setTitle("Are you sure?")
                                .setDescription("Are you sure you wish to unlink user " + username + "?")
                                .build()
                ).setActionRow(Button.success("unlink-confirm-" + id, "Confirm"), Button.danger("unlink-cancel-" + id, "Cancel")).queue();
            } else {
                message.reply("User not found.").queue();
            }
        } else {
            message.reply("Invalid syntax. Correct syntax: **!unlink [username]**").queue();
        }
    }
}
