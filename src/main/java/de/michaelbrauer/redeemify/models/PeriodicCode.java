package de.michaelbrauer.redeemify.models;

import java.util.List;

public class PeriodicCode {
    
    private String name;
    private String pattern;
    private String schedule;
    private String duration;
    private int maxUses;
    private boolean perPlayer;
    private boolean autoReset;
    private List<String> commands;
    private boolean enabled;

    public PeriodicCode() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public boolean isPerPlayer() {
        return perPlayer;
    }

    public void setPerPlayer(boolean perPlayer) {
        this.perPlayer = perPlayer;
    }

    public boolean isAutoReset() {
        return autoReset;
    }

    public void setAutoReset(boolean autoReset) {
        this.autoReset = autoReset;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "PeriodicCode{" +
                "name='" + name + '\'' +
                ", pattern='" + pattern + '\'' +
                ", schedule='" + schedule + '\'' +
                ", duration='" + duration + '\'' +
                ", maxUses=" + maxUses +
                ", perPlayer=" + perPlayer +
                ", autoReset=" + autoReset +
                ", enabled=" + enabled +
                '}';
    }
}