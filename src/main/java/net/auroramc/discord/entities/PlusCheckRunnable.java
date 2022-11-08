/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import net.auroramc.discord.DiscordBot;
import net.auroramc.discord.managers.PlusManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class PlusCheckRunnable implements Runnable {
    @Override
    public void run() {
        Guild guild = DiscordBot.getJda().getGuildById(DiscordBot.getSettings().getMasterDiscord());
        assert guild != null;
        guild.findMembersWithRoles(guild.getRoleById(955562965355085824L)).onSuccess((members) -> {
            for (Member member : members) {
                PlusManager.onCheck(member);
            }
        });

    }
}
