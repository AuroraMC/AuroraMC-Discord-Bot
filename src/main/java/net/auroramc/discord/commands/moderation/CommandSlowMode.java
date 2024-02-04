/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.commands.moderation;

import net.auroramc.discord.entities.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Map;

public class CommandSlowMode extends Command {
    public CommandSlowMode() {
        super("slowmode", "Enable a slowmode in the channel you are in.", Collections.singletonList(new OptionData(OptionType.INTEGER, "seconds", "The minumum number of seconds between user messages.", true)));
    }

    @Override
    public void execute(SlashCommandInteraction message, Member member, Map<String, String> args) {
        int id;
        try {
            id = Integer.parseInt(args.get("seconds"));
        } catch (NumberFormatException e) {
            message.reply("Invalid syntax. Correct syntax: **/slowmode [Seconds]**").setEphemeral(true).queue();
            return;
        }

        if (id > TextChannel.MAX_SLOWMODE) {
            message.reply("Slowmode must be less than 21600.").setEphemeral(true).queue();
            return;
        }

        message.getGuildChannel().asTextChannel().getManager().setSlowmode(id).queue();
        message.reply("Slowmode activated.").setEphemeral(true).queue();
    }
}
