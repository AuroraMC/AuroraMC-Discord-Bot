/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.auroramc.discord.entities.Permission;
import net.auroramc.discord.util.CommunityPoll;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandChangelog extends Command {

    public CommandChangelog() {
        super("changelog", Arrays.asList("newchangelog", "pushchangelog"), Collections.singletonList(Permission.ADMIN), null);
    }

    @Override
    public void execute(Message message, Member member, String aliasUsed, List<String> args) {
        if (args.size() >= 4) {
            String gameKey = args.remove(0);
            String version = args.remove(0);
            String url = args.remove(0);
            String title = String.join(" ", args);
            DiscordBot.getDatabaseManager().newChangelog(gameKey, version, url, title);
            message.reply("Changelog for game **" + gameKey + "** has been published. Please allow up to 60 minutes for the changelog to be reflected in-game!").queue();
        } else {
            message.reply("Invalid syntax. Correct syntax: **!changelog [game key] [version] [url] [title]**").queue();
        }
    }
}
