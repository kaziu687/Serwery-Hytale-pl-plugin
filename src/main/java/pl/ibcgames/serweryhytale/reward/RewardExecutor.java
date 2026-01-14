package pl.ibcgames.serweryhytale.reward;

import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import pl.ibcgames.serweryhytale.config.SerweryHytaleConfig;

public class RewardExecutor {

    private final SerweryHytaleConfig config;

    public RewardExecutor(SerweryHytaleConfig config) {
        this.config = config;
    }

    public void executeRewards(String playerName) {
        for (String command : config.getRewardCommands()) {
            String processedCommand = command.replace("{GRACZ}", playerName);
            CommandManager.get().handleCommand(ConsoleSender.INSTANCE, processedCommand);
        }
    }
}
