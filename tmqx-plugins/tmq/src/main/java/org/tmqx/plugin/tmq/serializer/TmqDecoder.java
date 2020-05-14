package org.tmqx.plugin.tmq.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.tmqx.plugin.tmq.bean.TmqMessage;
import org.tmqx.plugin.tmq.processor.RequestKeys;
import org.tmqx.plugin.tmq.serializer.TmqDecoder.DecoderState;
import java.util.List;

public class TmqDecoder extends ReplayingDecoder<DecoderState> {

    private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;

    private final int maxBytesInMessage;

    public TmqDecoder() {
        this(DEFAULT_MAX_BYTES_IN_MESSAGE);
    }

    public TmqDecoder(int maxBytesInMessage) {
        super(DecoderState.READ_REQUSETKEY_HEADER);
        this.maxBytesInMessage = maxBytesInMessage;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        switch (state()) {
            case READ_REQUSETKEY_HEADER: try {
                int contentBufferSize = buffer.readInt();
                final short requestTypeId = buffer.readShort();
                final RequestKeys requestType = RequestKeys.valueOf(requestTypeId);
                //切换读取消息体
                checkpoint(DecoderState.READ_PAYLOAD);
                // fall through
            } catch (Exception cause) {
                out.add(invalidMessage(cause));
                return;
            }

            case READ_PAYLOAD: try {

                break;
            } catch (Exception cause) {
                out.add(invalidMessage(cause));
                return;
            }

            case BAD_MESSAGE:
                // Keep discarding until disconnection.

                break;

            default:
                // Shouldn't reach here.
                throw new Error();
        }
    }
    enum DecoderState {
        READ_REQUSETKEY_HEADER,
        READ_PAYLOAD,
        BAD_MESSAGE,
    }

    private TmqMessage invalidMessage(Throwable cause) {
        checkpoint(DecoderState.BAD_MESSAGE);
        return TmqMessage.newInvalidMessage(cause);
    }
}
