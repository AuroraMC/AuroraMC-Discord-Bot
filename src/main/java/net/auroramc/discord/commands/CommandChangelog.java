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
        super("changelog", "Publish a changelog in-game.", Arrays.asList(new OptionData(OptionType.STRING, "game-key", "The database key for the game.", true),new OptionData(OptionType.STRING, "version", "The game version of this update.", true),new OptionData(OptionType.STRING, "url", "The Link to the update thread on the forums.", true),new OptionData(OptionType.STRING, "title", "The title of the update.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        String gameKey = args.get("game-key");
        String version = args.get("version");
        String url = args.get("url");
        String title = args.get("title");
        DiscordBot.getDatabaseManager().newChangelog(gameKey, version, url, title);
        message.reply("Changelog for game **" + gameKey + "** has been published. Please allow up to 60 minutes for the changelog to be reflected in-game!").queue();
    }
}
