package pl.ibcgames.serweryhytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.command.system.CommandManager;

import pl.ibcgames.serweryhytale.config.SerweryHytaleConfig;
import pl.ibcgames.serweryhytale.api.VotifierApiClient;
import pl.ibcgames.serweryhytale.commands.*;
import pl.ibcgames.serweryhytale.cooldown.CooldownManager;
import pl.ibcgames.serweryhytale.listener.QueryListener;
import pl.ibcgames.serweryhytale.reward.RewardExecutor;

import java.nio.file.*;

public class SerweryHytalePlugin extends JavaPlugin {

    private HytaleLogger logger;
    private Path configPath;
    private SerweryHytaleConfig config;
    private VotifierApiClient apiClient;
    private CooldownManager cooldownManager;
    private RewardExecutor rewardExecutor;
    private QueryListener queryListener;

    public SerweryHytalePlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        logger = getLogger();

        try {
            cooldownManager = new CooldownManager();
            configPath = Paths.get("mods", "Serwery-Hytale-PL", "config.json"); // getDataDirectory();

            var cm = CommandManager.get();
            cm.register(new GlosujCommand(this));
            cm.register(new NagrodaCommand(this));
            cm.register(new TestCommand(this));
            cm.register(new ReloadCommand(this));
            cm.register(new TokenCommand(this));

            queryListener = new QueryListener(this);

            if (this.reload()) {
                logger.atInfo().log("Serwery-Hytale-PL uruchomiony pomyslnie!");
            }
        } catch (Exception ex) {
            logger.atSevere().log("Blad krytyczny podczas inicjalizacji pluginu", ex);
        }
    }

    @Override
    protected void shutdown() {
        if (queryListener != null) {
            queryListener.onShutdown();
        }
    }

    public boolean reload() throws Exception {
        config = new SerweryHytaleConfig();
        config.load(configPath);

        apiClient = new VotifierApiClient(config);
        rewardExecutor = new RewardExecutor(config);

        return true;
    }

    public void saveToken(String token) throws Exception {
        // Jeśli config nie istnieje, utwórz nowy
        if (config == null) {
            config = new SerweryHytaleConfig();
            config.load(configPath);
        }

        config.saveToken(configPath, token);
        reload();
    }

    public boolean isConfigValid() {
        return config != null && config.isValid();
    }

    public VotifierApiClient getApiClient() {
        return apiClient;
    }

    public RewardExecutor getRewardExecutor() {
        return rewardExecutor;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public SerweryHytaleConfig getConfig() {
        return config;
    }
}

