/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.discord.entities;

public class Punishment {

    private final String punishmentCode;
    private final long punished;
    private final boolean ban;
    private final String reason;
    private final int weight;
    private final long punisher;
    private final long issued;
    private final long expire;
    private final String evidence;
    private final String removalReason;
    private final long remover;
    private final boolean visible;

    public Punishment(String punishmentCode, long punished, boolean ban, String reason, int weight, long punisher, long issued, long expire, String evidence, String removalReason, long remover, boolean visible) {
        this.punishmentCode = punishmentCode;
        this.punished = punished;
        this.ban = ban;
        this.reason = reason;
        this.weight = weight;
        this.punisher = punisher;
        this.issued = issued;
        this.expire = expire;
        this.evidence = evidence;
        this.removalReason = removalReason;
        this.remover = remover;
        this.visible = visible;
    }


    public long getPunished() {
        return punished;
    }

    public long getPunisher() {
        return punisher;
    }

    public long getRemover() {
        return remover;
    }

    public long getExpire() {
        return expire;
    }

    public long getIssued() {
        return issued;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getReason() {
        return reason;
    }

    public String getPunishmentCode() {
        return punishmentCode;
    }

    public String getRemovalReason() {
        return removalReason;
    }

    public boolean isBan() {
        return ban;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getWeight() {
        return weight;
    }
}

