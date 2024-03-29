/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {

    private final String mainCommand;
    private final String description;
    protected final Map<String, Command> subcommands;
    private final List<OptionData> options;

    public Command(String mainCommand, String description, List<OptionData> options) {
        this.mainCommand = mainCommand.toLowerCase();
        this.description = description;
        this.subcommands = new HashMap<>();
        this.options = options;
    }
    public abstract void execute(SlashCommandInteraction message, Member member, Map<String, String> args);

    public String getMainCommand() {
        return mainCommand;
    }

    public Command getSubcommand(String subCommand) {
        return subcommands.get(subCommand);
    }

    public Map<String, Command> getSubcommands() {
        return subcommands;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public SlashCommandData getAsSlashCommandData() {
        return Commands.slash(mainCommand, description)
                .addOptions(options)
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }
}

