package pl.ibcgames.serweryhytale.listener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import pl.ibcgames.serweryhytale.SerweryHytalePlugin;

import javax.annotation.Nonnull;

public class QueryHandler extends ChannelInboundHandlerAdapter {

    private final SerweryHytalePlugin plugin;

    public QueryHandler(SerweryHytalePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isSharable() {
        return true;
    }

    @Override
    public void channelRead(@Nonnull ChannelHandlerContext ctx, @Nonnull Object msg) {
        if (msg instanceof DatagramPacket packet) {
            var content = packet.content();

            if (QueryData.isQueryRequest(content)) {
                handleQuery(ctx, packet);
                return;
            }
        }

        // Not a query packet
        ctx.fireChannelRead(msg);
    }

    private void handleQuery(@Nonnull ChannelHandlerContext ctx, @Nonnull DatagramPacket request) {
        try {
            var queryType = QueryData.getQueryType(request.content());

            this.plugin.getLogger().atFine().log("Query request (type=%d) from %s", queryType, request.sender());

            var data = new QueryData(this.plugin);
            var response = data.createPacket(ctx.alloc());
            ctx.writeAndFlush(new DatagramPacket(response, request.sender()));
        } catch (Exception e) {
            this.plugin.getLogger().atWarning().withCause(e).log("Failed to process query from %s", request.sender());
        } finally {
            request.release();
        }
    }

    @Override
    public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, @Nonnull Throwable cause) {
        this.plugin.getLogger().atWarning().withCause(cause).log("Exception in query handler");
        ctx.fireExceptionCaught(cause);
    }
}
