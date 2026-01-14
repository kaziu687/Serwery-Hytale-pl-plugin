package pl.ibcgames.serweryhytale.cooldown;

import java.util.concurrent.*;

public class CooldownManager {

    private final ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap<>();
    private static final int cooldownSeconds = 60;

    public boolean isOnCooldown(String playerName) {
        var endTime = cooldowns.get(playerName);
        if (endTime == null) return false;

        if (System.currentTimeMillis() >= endTime) {
            cooldowns.remove(playerName);
            return false;
        }
        return true;
    }

    public void setCooldown(String playerName) {
        long endTime = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        cooldowns.put(playerName, endTime);
    }

    public long getRemainingTime(String playerName) {
        Long endTime = cooldowns.get(playerName);
        if (endTime == null) return 0;

        long remaining = (endTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
}
