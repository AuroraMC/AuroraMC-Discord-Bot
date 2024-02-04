/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.setup;

import net.auroramc.discord.entities.Command;
import net.auroramc.discord.managers.GuildManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Map;

public class CommandSetup extends Command {


    public CommandSetup() {
        super("setup", "Do not fuck with this command. This command is for use by Block2Block Only. I will scream at you.", Arrays.asList(new OptionData(OptionType.NUMBER, "server-logging-channel-id", "The ID for the channel you wish for server log messages to be sent.", true), new OptionData(OptionType.NUMBER, "link-logging-channel-id", "The ID for the channel you wish for link log messages to be send in.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        message.deferReply().queue();
        long serverLog, linkLog, mainChannel = message.getChannel().getIdLong();
        try {
            serverLog = Long.parseLong(args.get("server-logging-channel-id"));
            linkLog = Long.parseLong(args.get("link-logging-channel-id"));
        } catch (NumberFormatException ignored) {
            message.getHook().sendMessage("Those are not valid channels.").setEphemeral(true).queue();
            return;
        }
        if (message.getGuild().getTextChannelById(serverLog) == null) {
            message.getHook().sendMessage("Those are not valid channels.").setEphemeral(true).queue();
            return;
        }
        if (message.getGuild().getTextChannelById(linkLog) == null) {
            message.getHook().sendMessage("Those are not valid channels.").setEphemeral(true).queue();
            return;
        }
        GuildManager.onGuildSetup(message.getGuild(), mainChannel, serverLog, linkLog);
        message.getHook().sendMessage("Discord successfully setup.").setEphemeral(true).queue();
    }
}
