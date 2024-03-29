/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import net.auroramc.discord.DiscordBot;

public class BotSettings {

    private boolean commandsEnabled;
    private char commandPrefix;
    private long masterDiscord;

    public BotSettings(boolean commandsEnabled, char commandPrefix, long masterDiscord) {
        this.commandPrefix = commandPrefix;
        this.commandsEnabled = commandsEnabled;
        this.masterDiscord = masterDiscord;
    }

    public boolean isCommandsEnabled() {
        return commandsEnabled;
    }

    public char getCommandPrefix() {
        return commandPrefix;
    }

    public long getMasterDiscord() {
        return masterDiscord;
    }

    public void setCommandPrefix(char commandPrefix) {
        this.commandPrefix = commandPrefix;
        DiscordBot.getDatabaseManager().setCommandPrefix(commandPrefix);
    }

    public void setCommandsEnabled(boolean commandsEnabled) {
        this.commandsEnabled = commandsEnabled;
        DiscordBot.getDatabaseManager().setCommandsEnabled(commandsEnabled);
    }

    public void setMasterDiscord(long masterDiscord) {
        this.masterDiscord = masterDiscord;
        DiscordBot.getDatabaseManager().setMasterDiscord(masterDiscord);
    }
}
