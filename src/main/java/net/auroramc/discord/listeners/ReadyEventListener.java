/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.discord.listeners;

import net.auroramc.discord.DiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyEventListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        Guild guild = e.getJDA().getGuildById(DiscordBot.getSettings().getMasterDiscord());
    }

}
