package pl.ibcgames.serweryhytale.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import pl.ibcgames.serweryhytale.Consts;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class GlosujCommand extends AbstractCommand {

    private final SerweryHytalePlugin plugin;

    public GlosujCommand(SerweryHytalePlugin plugin) {
        super("sh-glosuj", "Wyswietla link do glosowania");
        addAliases("sm-glosuj");
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

        return CompletableFuture
                .runAsync(() -> player.sendMessage(Consts.GLOSUJ_LOADING))
                .thenCompose(v -> plugin.getApiClient().getServerResponse())
                .thenAccept(server -> {
                    var msg = Message.empty();
                    for (var message : server.getText()) {
                        msg.insert(Message.raw(message + "\n").color(Color.yellow));
                    }

                    var voteUrl = server.getVoteUrl();
                    msg.insert(Message.raw(voteUrl).link(voteUrl).color(Color.cyan));

                    player.sendMessage(msg);
                })
                .exceptionally(ex -> {
                    plugin.getLogger().atSevere().log("Nie udalo sie pobrac danych serwera", ex);
                    player.sendMessage(Consts.GLOSUJ_ERROR);
                    return null;
                });
    }
}
