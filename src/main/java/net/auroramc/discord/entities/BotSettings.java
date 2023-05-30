/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
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
