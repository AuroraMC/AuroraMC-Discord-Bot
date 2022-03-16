/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Rank {

    PLAYER(0, "Player", null, null, null, new Color(255, 255, 255)),
    ELITE(1, "Elite", "Elite",  Collections.singletonList(PLAYER), Collections.singletonList(Permission.ELITE), new Color(85, 255, 255)),
    MASTER(2, "Master", "Master", Collections.singletonList(ELITE), Collections.singletonList(Permission.MASTER), new Color(255, 85, 255)),
    YOUTUBE(5, "YouTube", "YouTube", Collections.singletonList(MASTER), Arrays.asList(Permission.DISGUISE, Permission.SOCIAL, Permission.CUSTOM_DISGUISE), new Color(255, 170, 0)),
    TWITCH(6, "Twitch", "Twitch", Collections.singletonList(MASTER), Arrays.asList(Permission.DISGUISE, Permission.SOCIAL, Permission.CUSTOM_DISGUISE), new Color(170, 0, 170)),
    BUILDER(7, "Builder", "Builder", Collections.singletonList(MASTER), Collections.singletonList(Permission.BUILD), new Color(85, 255, 85)),
    BUILD_TEAM_MANAGEMENT(8, "Build Team Management", "BTM", Collections.singletonList(BUILDER), Collections.singletonList(Permission.BUILD_TEAM_MANAGEMENT), new Color(85, 255, 85)),
    JUNIOR_MODERATOR(9, "Junior Moderator", "Jr.Mod", Collections.singletonList(MASTER), Collections.singletonList(Permission.MODERATION), new Color(85, 85, 255)),
    MODERATOR(10, "Moderator", "Mod", Collections.singletonList(JUNIOR_MODERATOR), Arrays.asList(Permission.BYPASS_APPROVAL, Permission.DISGUISE), new Color(85, 85, 255)),
    ROBOT(8001, "Robot", "Robot", Collections.singletonList(MASTER), Collections.singletonList(Permission.MODERATION), new Color(85, 85, 255)),
    ADMIN(11, "Administrator", "Admin", Collections.singletonList(MODERATOR), Arrays.asList(Permission.ADMIN, Permission.DISGUISE, Permission.CUSTOM_DISGUISE, Permission.SOCIAL, Permission.DEBUG_INFO, Permission.PANEL), new Color(255, 85, 85)),
    DEVELOPER(12, "Developer", "Dev", Collections.singletonList(MASTER), Arrays.asList(Permission.DEBUG_INFO, Permission.PANEL), new Color(85, 255, 85)),
    SENIOR_DEVELOPER(13, "Senior Developer", "Sr.Dev", Arrays.asList(DEVELOPER, JUNIOR_MODERATOR), Arrays.asList(Permission.ADMIN, Permission.DEBUG_ACTION, Permission.PANEL, Permission.DISGUISE, Permission.CUSTOM_DISGUISE), new Color(255, 85, 85)),
    OWNER(9001, "Owner", "Owner", Collections.singletonList(ADMIN), Collections.singletonList(Permission.ALL), new Color(255, 85, 85));

    private final int id;
    private final String name;
    private final String rankAppearance;
    private final List<Permission> permissions;
    private final List<Rank> inheritance;
    private final Color color;

    Rank(@NotNull int id, @NotNull String name, @Nullable String rankAppearance, @NotNull List<Rank> inherit, @NotNull List<Permission> permissions, @NotNull Color color) {
        this.id = id;
        this.name = name;
        this.rankAppearance = rankAppearance;
        this.permissions = permissions;
        this.inheritance = inherit;
        this.color = color;
    }

    public final String getName() {
        return name;
    }

    public final int getId() {
        return id;
    }

    public final List<Permission> getPermissions() {
        return permissions;
    }

    public final String getRankAppearance() {
        return rankAppearance;
    }


    public final boolean hasPermission(String node) {
        for (Permission permission : getPermissions()) {
            if (permission.getNode().equals(node) || permission.getNode().equals("all")) {
                return true;
            }
        }

        for (Rank rank : getInheritance()) {
            if (rank.hasPermission(node)) {
                return true;
            }
        }
        return false;
    }

    public final List<Rank> getInheritance() {
        return inheritance;
    }

    public final boolean hasPermission(int id) {
        for (Permission permission : getPermissions()) {
            if (permission.getId() == id || permission.getId() == -1) {
                return true;
            }
        }

        for (Rank rank : getInheritance()) {
            if (rank.hasPermission(id)) {
                return true;
            }
        }
        return false;
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

    /**
     * Checks whether the given rank is a parent of this rank.
     * @param rank The parent you want to check.
     * @return Whether rank is a parent or not.
     */
    public boolean isParent(Rank rank) {
        if (rank.equals(this)) {
            return true;
        }

        for (Rank rank2 : inheritance) {
            if (rank2.isParent(rank)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the given rank is a child of this rank.
     * @param rank The child you want to check.
     * @return Whether rank is a child or not.
     */
    public boolean isChild(Rank rank) {
        if (rank.equals(this)) {
            return true;
        }

        for (Rank rank2 : rank.inheritance) {
            if (isChild(rank2)) {
                return true;
            }
        }

        return false;
    }
}
