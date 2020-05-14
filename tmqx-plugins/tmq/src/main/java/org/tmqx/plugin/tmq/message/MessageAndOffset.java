package org.tmqx.plugin.tmq.message;

import org.tmqx.plugin.tmq.bean.TmqMessage;

/**
 * @author tf
 * @version 创建时间：2019年1月11日 下午9:21:41
 * @ClassName 消费迭代
 */
public class MessageAndOffset {

	public final TmqMessage message;

    public final long offset;

    public MessageAndOffset(TmqMessage message, long offset) {
        this.message = message;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return String.format("MessageAndOffset [offset=%s, message=%s]", offset, message);
    }
}
