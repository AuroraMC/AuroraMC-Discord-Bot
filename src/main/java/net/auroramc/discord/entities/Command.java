/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {

    private final String mainCommand;
    private final List<String> aliases;
    protected final Map<String, Command> subcommands;
    private final List<Permission> permission;
    private final List<Long> allowedChannels;

    public Command(String mainCommand, List<String> aliases, List<Permission> permission, List<Long> allowedChannels) {
        this.mainCommand = mainCommand.toLowerCase();
        this.aliases = aliases;
        this.subcommands = new HashMap<>();
        this.permission = permission;
        this.allowedChannels = allowedChannels;
    }
    public abstract void execute(Message message, Member member, String aliasUsed, List<String> args);

    protected void registerSubcommand(String subcommand, List<String> aliases, Command command) {
        subcommands.put(subcommand.toLowerCase(), command);
        for (String alias : aliases) {
            subcommands.put(alias.toLowerCase(), command);
        }
    }

    public String getMainCommand() {
        return mainCommand;
    }

    public Command getSubcommand(String subCommand) {
        return subcommands.get(subCommand);
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<Permission> getPermission() {
        return permission;
    }

    public List<Long> getAllowedChannels() {
        return allowedChannels;
    }
}

