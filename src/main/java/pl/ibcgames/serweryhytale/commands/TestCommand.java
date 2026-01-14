package pl.ibcgames.serweryhytale.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import pl.ibcgames.serweryhytale.Consts;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import java.util.concurrent.CompletableFuture;

public class TestCommand extends AbstractCommand {

    private final SerweryHytalePlugin plugin;

    public TestCommand(SerweryHytalePlugin plugin) {
        super("sh-test", "Testuje nagrody (admin)");
        addAliases("sm-test");
        requirePermission("shvotifier.admin");
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        if (!(context.sender() instanceof Player player)) {
            context.sendMessage(Consts.PLAYER_NEEDED);
            return CompletableFuture.completedFuture(null);
        }

        if (!plugin.isConfigValid()) {
            player.sendMessage(Consts.CONFIG_INVALID);
            return CompletableFuture.completedFuture(null);
        }

        String playerName = player.getDisplayName();

        return CompletableFuture.runAsync(() -> {
            player.sendMessage(Consts.TEST_INFO);
            plugin.getRewardExecutor().executeRewards(playerName);
        }).exceptionally(ex -> {
            plugin.getLogger().atSevere().log("Nie udalo sie przetestowac nagrody przez " + playerName, ex);
            player.sendMessage(Consts.ERROR_GENERIC);
            return null;
        });
    }
}
