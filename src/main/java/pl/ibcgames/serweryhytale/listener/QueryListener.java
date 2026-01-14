package pl.ibcgames.serweryhytale.listener;

import com.hypixel.hytale.server.core.io.ServerManager;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

public class QueryListener {

    private static final String HANDLER_NAME = "serweryhytalepl-query";

    private final SerweryHytalePlugin plugin;
    private final QueryHandler queryHandler;

    public QueryListener(SerweryHytalePlugin plugin) {
        this.plugin = plugin;
        this.queryHandler = new QueryHandler(plugin);
        onStart();
    }

    private void onStart() {
        ServerManager.get().waitForBindComplete();

        var registered = 0;
        for (var channel : ServerManager.get().getListeners()) {
            try {
                var pipeline = channel.pipeline();
                pipeline.addFirst(HANDLER_NAME, queryHandler);
                registered++;
                plugin.getLogger().atFine().log("Registered query handler on %s", channel.localAddress());
            } catch (Exception e) {
                plugin.getLogger().atWarning().withCause(e).log("Failed to register query handler on %s", channel.localAddress());
            }
        }

        plugin.getLogger().atInfo().log("Query protocol enabled on %d listener(s)", registered);
    }

    public void onShutdown() {
        var removed = 0;
        for (var channel : ServerManager.get().getListeners()) {
            try {
                var pipeline = channel.pipeline();
                if (pipeline.get(HANDLER_NAME) != null) {
                    pipeline.remove(HANDLER_NAME);
                    removed++;
                }
            } catch (Exception e) {
                plugin.getLogger().atFine().log("Handler already removed from %s", channel.localAddress());
            }
        }

        plugin.getLogger().atInfo().log("Query protocol disabled, removed from %d listener(s)", removed);
    }
}
