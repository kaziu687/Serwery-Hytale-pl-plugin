package pl.ibcgames.serweryhytale.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import pl.ibcgames.serweryhytale.Consts;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import java.util.concurrent.CompletableFuture;

public class WeryfikacjaCommand extends AbstractCommand {

    private final SerweryHytalePlugin plugin;
    private final RequiredArg<String> tokenArg;

    public WeryfikacjaCommand(SerweryHytalePlugin plugin) {
        super("sh-weryfikacja", "Weryfikuje serwer na Serwery-Hytale.pl (tylko konsola)");
        addAliases("sm-weryfikacja", "sh-verify", "sm-verify");
        requirePermission("shvotifier.admin");
        this.tokenArg = this.withRequiredArg("token", "Token weryfikacyjny (maks. 48 znakow)", ArgTypes.STRING);
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> execute(CommandContext context) {
        return CompletableFuture.runAsync(() -> {
            var sender = context.sender();
            if (!(sender instanceof ConsoleSender)) {
                sender.sendMessage(Consts.VERIFY_CONSOLE_ONLY);
                return;
            }

            var token = this.tokenArg.get(context);
            if (token.isBlank()) {
                sender.sendMessage(Consts.VERIFY_TOKEN_EMPTY);
                return;
            }

            if (token.length() > 48) {
                sender.sendMessage(Consts.VERIFY_TOKEN_TOO_LONG);
                return;
            }

            plugin.setVerifyToken(token);

            sender.sendMessage(Consts.VERIFY_SUCCESS);
            plugin.getLogger().atInfo().log("Token weryfikacyjny ustawiony");
        });
    }
}
