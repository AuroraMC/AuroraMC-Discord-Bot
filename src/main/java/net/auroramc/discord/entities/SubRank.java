/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SubRank {

    JUNIOR_QUALITY_ASSURANCE(2, "Junior Quality Assurance", Collections.singletonList(Permission.DEBUG_INFO), new Color(85, 255, 85)),
    SENIOR_QUALITY_ASSURANCE(3, "Senior Quality Assurance", Arrays.asList(Permission.DEBUG_INFO, Permission.DEBUG_ACTION, Permission.PANEL), new Color(85, 255, 85)),
    STAFF_MANAGEMENT(4, "Staff Management", Arrays.asList(Permission.STAFF_MANAGEMENT, Permission.DISGUISE, Permission.CUSTOM_DISGUISE, Permission.PANEL), new Color(255, 170, 0)),
    SUPPORT(5, "Support", Collections.singletonList(Permission.SUPPORT), new Color(85, 85, 255)),
    RECRUITMENT(6, "Recruitment", Arrays.asList(Permission.RECRUITMENT, Permission.DISGUISE, Permission.CUSTOM_DISGUISE), new Color(255, 170, 0)),
    SOCIAL_MEDIA(7, "Social Media", Collections.singletonList(Permission.SOCIAL_MEDIA), new Color(85, 85, 255)),
    EVENT_MANAGEMENT(8, "Event Management", Collections.singletonList(Permission.EVENT_MANAGEMENT), new Color(0, 170, 0)),
    COMMUNITY_MANAGEMENT(9, "Community Management", Collections.emptyList(), new Color(0, 170, 0)),
    RULES_COMMITTEE(10, "Rules Committee", Collections.singletonList(Permission.PANEL), new Color(255, 170, 0)),
    APPEALS(11, "Appeals Team", Collections.singletonList(Permission.PANEL), new Color(0, 170, 0)),
    TESTER(12, "Alpha Tester", Collections.emptyList(), new Color(0, 170, 0));


    private final int id;
    private final String name;
    private final List<Permission> permissions;
    private final Color color;

    SubRank(@NotNull int id, @NotNull String name, @NotNull List<Permission> permissions, @NotNull Color color) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
        this.color = color;
    }

    public final String getName() {
        return name;
    }

    public final List<Permission> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public final boolean hasPermission(String node) {
        for (Permission permission : getPermissions()) {
            if (permission.getNode().equals(node) || permission.getNode().equals("all")) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasPermission(int id) {
        for (Permission permission : getPermissions()) {
            if (permission.getId() == id || permission.getId() == -1) {
                return true;
            }
        }


        return false;
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
