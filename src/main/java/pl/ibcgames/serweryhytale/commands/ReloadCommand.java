package pl.ibcgames.serweryhytale.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import pl.ibcgames.serweryhytale.Consts;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import java.util.concurrent.CompletableFuture;

public class ReloadCommand extends AbstractCommand {

    private final SerweryHytalePlugin plugin;

    public ReloadCommand(SerweryHytalePlugin plugin) {
        super("sh-reload", "Przeladowuje konfiguracje pluginu (admin)");
        addAliases("sm-reload");
        requirePermission("shvotifier.admin");
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        return CompletableFuture.runAsync(() -> {
            context.sender().sendMessage(Consts.RELOAD_LOADING);

            try {
                var success = plugin.reload();
                if (success) {
                    context.sender().sendMessage(Consts.RELOAD_SUCCESS);
                    plugin.getLogger().atInfo().log("Konfiguracja przeladowana przez " + context.sender().getDisplayName());
                } else {
                    plugin.getLogger().atSevere().log("Blad podczas przeladowywania konfiguracji");
                    context.sender().sendMessage(Consts.RELOAD_ERROR);
                }
            } catch (Exception ex) {
                plugin.getLogger().atSevere().log("Blad podczas przeladowywania konfiguracji", ex);
                context.sender().sendMessage(Consts.RELOAD_ERROR_EXCEPTION);
            }
        });
    }
}