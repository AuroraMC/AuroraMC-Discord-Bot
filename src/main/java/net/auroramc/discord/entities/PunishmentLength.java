/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.discord.entities;

public class PunishmentLength {

    private final static String[] SUFFIXES = new String[]{"Hours", "Days"};

    private final double value;
    private final int suffix;

    public PunishmentLength(double hours) {
        if (hours < 0 || hours > 1440) {
            value = suffix = -1;
        } else {
            if (hours >= 24) {
                value = (hours / 24);
                suffix = 2;
            } else {
                value = hours;
                suffix = 1;
            }
        }

    }

    public String getFormatted() {
        if (value == -1) {
            return "Permanent";
        }
        double finalValue = (Math.round(value * 10))/10.0;
        return finalValue + " " + SUFFIXES[suffix - 1];
    }

    public double getMsValue() {
        if (value == -1) {
            return -1d;
        }

        double ms = value*3600000d;

        if (suffix == 2) {
            ms *= 24d;
        }

        return ms;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
