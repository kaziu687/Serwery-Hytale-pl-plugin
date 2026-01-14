package pl.ibcgames.serweryhytale.listener;

import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.protocol.ProtocolSettings;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import javax.annotation.Nonnull;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class QueryData {

    public static final byte[] REQUEST_MAGIC = "SHQUERY\0".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] RESPONSE_MAGIC = "SHREPLY\0".getBytes(StandardCharsets.US_ASCII);

    public static final byte TYPE_FULL = 0x01;

    public static final int MIN_REQUEST_SIZE = REQUEST_MAGIC.length + 1; // magic + type

    private final SerweryHytalePlugin plugin;

    private final String serverName;
    private final String authMode;
    private final String motd;
    private final List<PlayerRef> players;
    private final int maxPlayers;
    private final int port;
    private final String version;
    private final String revision;
    private final String patchline;
    private final int protocolVersion;
    private final String protocolHash;
    private final List<PluginBase> plugins;

    public QueryData(SerweryHytalePlugin plugin) {
        this.plugin = plugin;

        var hytaleServer = HytaleServer.get();
        var hytaleConfig = hytaleServer.getConfig();
        var players = Universe.get().getPlayers();

        this.serverName = hytaleConfig.getServerName();
        this.authMode = ServerAuthManager.getInstance().getAuthMode().name();
        this.motd = hytaleConfig.getMotd();
        this.players = players;
        this.maxPlayers = Math.max(hytaleConfig.getMaxPlayers(), 0);
        this.port = getHostPort();
        this.version = getVersion();
        this.revision = getRevision();
        this.patchline = getPatchline();
        this.protocolVersion = ProtocolSettings.PROTOCOL_VERSION;
        this.protocolHash = ProtocolSettings.PROTOCOL_HASH;
        this.plugins = PluginManager.get().getPlugins();
    }

    public ByteBuf createPacket(ByteBufAllocator alloc) {
        var buf = alloc.buffer();

        // Magic header
        buf.writeBytes(RESPONSE_MAGIC);

        // Response type
        buf.writeByte(TYPE_FULL);

        writeString(buf, this.serverName);
        writeString(buf, this.authMode);
        writeString(buf, this.motd);
        buf.writeIntLE(this.players.size());
        buf.writeIntLE(this.maxPlayers);
        buf.writeShortLE(this.port);
        writeString(buf, this.version);
        writeString(buf, this.revision);
        writeString(buf, this.patchline);
        buf.writeIntLE(this.protocolVersion);
        writeString(buf, this.protocolHash);

        var config = this.plugin.getConfig();
        // Player list
        if (config.sendPlayersList()) {
            for (var player : this.players) {
                writeString(buf, player.getUsername());
                writeUUID(buf, player.getUuid());
            }
        }

        // Plugin list
        if (config.sendPluginsList()) {
            buf.writeIntLE(this.plugins.size());
            for (var pl : this.plugins) {
                var id = pl.getIdentifier();
                writeString(buf, id.toString());
                writeString(buf, pl.getManifest().getVersion().toString());
                buf.writeBoolean(pl.isEnabled());
            }
        }

        writeString(buf, ""); // ???

        return buf;
    }

    public static boolean isQueryRequest(@Nonnull ByteBuf buf) {
        if (buf.readableBytes() < MIN_REQUEST_SIZE) {
            return false;
        }
        for (var i = 0; i < REQUEST_MAGIC.length; i++) {
            if (buf.getByte(i) != REQUEST_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte getQueryType(@Nonnull ByteBuf buf) {
        return buf.getByte(REQUEST_MAGIC.length);
    }

    private static int getHostPort() {
        try {
            var address = ServerManager.get().getNonLoopbackAddress();
            if (address != null) {
                return address.getPort();
            }
        } catch (SocketException ignored) {
        }
        return 5520; // Default port
    }

    private static String getVersion() {
        var version = ManifestUtil.getImplementationVersion();
        return version != null ? version : "UNKNOWN";
    }

    private static String getRevision() {
        var revision = ManifestUtil.getImplementationRevisionId();
        return revision != null ? revision : "UNKNOWN";
    }

    private static String getPatchline() {
        var patchline = ManifestUtil.getPatchline();
        return patchline != null ? patchline : "UNKNOWN";
    }

    /**
     * Write a string with 2-byte little-endian length prefix.
     */
    private static void writeString(@Nonnull io.netty.buffer.ByteBuf buf, @Nonnull String str) {
        var bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.writeShortLE(bytes.length);
        buf.writeBytes(bytes);
    }

    /**
     * Write a UUID as 16 bytes (MSB first, then LSB).
     */
    private static void writeUUID(@Nonnull io.netty.buffer.ByteBuf buf, @Nonnull UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public String toString() {
        var config = this.plugin.getConfig();

        var playerList = config.sendPlayersList() ? this.players
                .stream()
                .map(p -> p.getUsername() + " (" + p.getUuid() + ")")
                .toList() : "[]";

        var pluginList = config.sendPluginsList() ? this.plugins
                .stream()
                .map(p -> p.getIdentifier() + " v" + p.getManifest().getVersion() + " (enabled=" + p.isEnabled() + ")")
                .toList() : "[]";

        return "QueryData{" +
                "serverName='" + this.serverName + '\'' +
                ", authMode='" + this.authMode + '\'' +
                ", motd='" + this.motd + '\'' +
                ", playersCount=" + this.players.size() +
                ", maxPlayers=" + this.maxPlayers +
                ", port=" + this.port +
                ", version='" + this.version + '\'' +
                ", revision='" + this.revision + '\'' +
                ", patchline='" + this.patchline + '\'' +
                ", protocolVersion=" + this.protocolVersion +
                ", protocolHash='" + this.protocolHash + '\'' +
                ", players=" + playerList +
                ", pluginsSize=" + (config.sendPluginsList() ? this.plugins.size() : 0) +
                ", plugins=" + pluginList +
                '}';
    }
}
