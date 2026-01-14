package pl.ibcgames.serweryhytale.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import pl.ibcgames.serweryhytale.Consts;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import java.util.concurrent.CompletableFuture;

public class TokenCommand extends AbstractCommand {

    private final SerweryHytalePlugin plugin;
    private final RequiredArg<String> tokenArg;

    public TokenCommand(SerweryHytalePlugin plugin) {
        super("sh-token", "Ustawia token API w konfiguracji (admin)");
        addAliases("sh-identyfikator", "sm-token", "sm-identyfikator");
        requirePermission("shvotifier.admin");
        this.tokenArg = this.withRequiredArg("identyfikator", "Identyfikator, wiecej: https://serwery-hytale.pl/nagrody-za-glosowanie", ArgTypes.STRING);
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        return CompletableFuture.runAsync(() -> {
            var token = this.tokenArg.get(context);

            if (token.isBlank()) {
                context.sender().sendMessage(Consts.TOKEN_USAGE);
                return;
            }

            context.sender().sendMessage(Consts.TOKEN_SAVING);

            try {
                plugin.saveToken(token);
                context.sender().sendMessage(Consts.TOKEN_SUCCESS);
                plugin.getLogger().atInfo().log("Token API zmieniony przez " + context.sender().getDisplayName());
            } catch (Exception ex) {
                plugin.getLogger().atSevere().log("Blad podczas zapisywania tokena", ex);
                context.sender().sendMessage(Consts.TOKEN_ERROR);
            }
        });
    }
}
