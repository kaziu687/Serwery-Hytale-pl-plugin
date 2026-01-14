package pl.ibcgames.serweryhytale.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import pl.ibcgames.serweryhytale.Consts;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class NagrodaCommand extends AbstractCommand {

    private final SerweryHytalePlugin plugin;

    public NagrodaCommand(SerweryHytalePlugin plugin) {
        super("sh-nagroda", "Odbierz nagrode za glosowanie");
        addAliases("sm-nagroda");
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

        var playerName = player.getDisplayName();

        if (plugin.getCooldownManager().isOnCooldown(playerName)) {
            var remaining = plugin.getCooldownManager().getRemainingTime(playerName);
            player.sendMessage(Consts.nagrodaCooldown(remaining));
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture
                .runAsync(() -> player.sendMessage(Consts.NAGRODA_CHECKING))
                .thenCompose(v -> plugin.getApiClient().checkVote(playerName))
                .thenAccept(response -> {
                    if (!response.isSuccess()) {
                        player.sendMessage(Message.raw(response.getMessage()).color(Color.red));
                        return;
                    }

                    if (!response.canClaimReward()) {
                        player.sendMessage(Consts.NAGRODA_CLAIM_FAILED);
                        return;
                    }

                    plugin.getRewardExecutor().executeRewards(playerName);
                    plugin.getCooldownManager().setCooldown(playerName);

                    player.sendMessage(Consts.NAGRODA_SUCCESS);
                })
                .exceptionally(ex -> {
                    plugin.getLogger().atSevere().log("Nie udalo sie sprawdzic glosu gracza " + playerName, ex);
                    player.sendMessage(Consts.ERROR_GENERIC);
                    return null;
                });
    }
}
