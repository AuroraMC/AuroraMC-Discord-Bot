/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public enum Rank {

    PLAYER(0, "Player", "Player", new Color(255, 255, 255)),
    ELITE(1, "Elite", "Elite", new Color(85, 255, 255)),
    MASTER(2, "Master", "Master", new Color(255, 85, 255)),
    YOUTUBE(5, "YouTube", "YouTube", new Color(255, 170, 0)),
    TWITCH(6, "Twitch", "Twitch", new Color(170, 0, 170)),
    PARTNER(14, "Partner", "Partner", new Color(255, 85, 85)),
    BUILDER(7, "Builder", "Builder", new Color(85, 255, 85)),
    BUILD_TEAM_MANAGEMENT(8, "Build Team Management", "BTM", new Color(85, 255, 85)),
    JUNIOR_MODERATOR(9, "Junior Moderator", "Jr.Mod", new Color(85, 85, 255)),
    MODERATOR(10, "Moderator", "Mod", new Color(85, 85, 255)),
    ROBOT(8001, "Robot", "Robot", new Color(85, 85, 255)),
    ADMIN(11, "Administrator", "Admin", new Color(170, 0, 0)),
    DEVELOPER(12, "Developer", "Dev", new Color(85, 255, 85)),
    SENIOR_DEVELOPER(13, "Senior Developer", "Sr.Dev", new Color(170, 0, 0)),
    OWNER(9001, "Owner", "Owner", new Color(170, 0, 0));

    private final int id;
    private final String name;
    private final String rankAppearance;
    private final Color color;

    Rank(@NotNull int id, @NotNull String name, @Nullable String rankAppearance, @NotNull Color color) {
        this.id = id;
        this.name = name;
        this.rankAppearance = rankAppearance;
        this.color = color;
    }

    public final String getName() {
        return name;
    }

    public final int getId() {
        return id;
    }

    public final String getRankAppearance() {
        return rankAppearance;
    }


    public Color getColor() {
        return color;
    }

    public static Rank getByID(int id) {
        for (Rank rank : Rank.values()){
            if (rank.getId() == id) {
                return rank;
            }
        }

        return null;
    }

}
