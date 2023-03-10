/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public enum SubRank {

    QUALITY_ASSURANCE(2, "Quality Assurance", new Color(85, 255, 85)),
    QUALITY_ASSURANCE_MANAGEMENT(3, "Quality Assurance Management", new Color(85, 255, 85)),
    STAFF_MANAGEMENT(4, "Staff Management", new Color(255, 170, 0)),
    SUPPORT(5, "Support", new Color(85, 85, 255)),
    RECRUITMENT(6, "Recruitment", new Color(255, 170, 0)),
    SOCIAL_MEDIA(7, "Social Media", new Color(85, 85, 255)),
    EVENT_MANAGEMENT(8, "Event Management", new Color(0, 170, 0)),
    COMMUNITY_MANAGEMENT(9, "Community Management", new Color(0, 170, 0)),
    RULES_COMMITTEE(10, "Rules Committee", new Color(255, 170, 0)),
    APPEALS(11, "Appeals Team", new Color(0, 170, 0)),
    TESTER(12, "Alpha Tester", new Color(0, 170, 0));


    private final int id;
    private final String name;
    private final Color color;

    SubRank(@NotNull int id, @NotNull String name, @NotNull Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public final String getName() {
        return name;
    }


    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public static SubRank getByID(int id) {
        for (SubRank subRank : values()) {
            if (subRank.getId() == id) {
                return subRank;
            }
        }

        return null;
    }
}
