/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.commands;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Map;

public class CommandChangelog extends Command {

    public CommandChangelog() {
        super("changelog", "Publish a changelog in-game.", Arrays.asList(new OptionData(OptionType.STRING, "Game Key", "The database key for the game.", true),new OptionData(OptionType.STRING, "Version", "The game version of this update.", true),new OptionData(OptionType.STRING, "URL", "The Link to the update thread on the forums.", true),new OptionData(OptionType.STRING, "Title", "The title of the update.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        String gameKey = args.get("Game Key");
        String version = args.get("Version");
        String url = args.get("URL");
        String title = args.get("Title");
        DiscordBot.getDatabaseManager().newChangelog(gameKey, version, url, title);
        message.reply("Changelog for game **" + gameKey + "** has been published. Please allow up to 60 minutes for the changelog to be reflected in-game!").queue();
    }
}
