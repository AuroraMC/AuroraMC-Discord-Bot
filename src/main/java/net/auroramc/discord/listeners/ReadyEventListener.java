/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
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
