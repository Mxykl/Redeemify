package de.michaelbrauer.redeemify.models;

import java.time.LocalDateTime;
import java.util.List;

public class RedeemCode {
    
    private String code;
    private LocalDateTime expires;
    private int maxUses;
    private boolean perPlayer;
    private List<String> commands;

    public RedeemCode() {}

    public RedeemCode(String code, LocalDateTime expires, int maxUses, boolean perPlayer, List<String> commands) {
        this.code = code;
        this.expires = expires;
        this.maxUses = maxUses;
        this.perPlayer = perPlayer;
        this.commands = commands;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
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

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean isExpired() {
        return expires != null && LocalDateTime.now().isAfter(expires);
    }

    @Override
    public String toString() {
        return "RedeemCode{" +
                "code='" + code + '\'' +
                ", expires=" + expires +
                ", maxUses=" + maxUses +
                ", perPlayer=" + perPlayer +
                ", commands=" + commands +
                '}';
    }
}