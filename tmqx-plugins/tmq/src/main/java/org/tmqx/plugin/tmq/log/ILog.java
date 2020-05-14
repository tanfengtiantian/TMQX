package org.tmqx.plugin.tmq.log;




import org.tmqx.plugin.tmq.exception.InvalidMessageException;
import org.tmqx.plugin.tmq.message.ByteBufferMessageSet;
import org.tmqx.plugin.tmq.message.MessageSet;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * ILog(主题-分区)
 * top(主题)-0(分区)-Segments(List)日志段
 * @author tf
 *
 */
public interface ILog extends Closeable {

	List<Long> EMPTY_OFFSETS = Collections.emptyList();
	/**
	*将消息附加到日志
	*@param messages messages消息集
	*@返回所有消息偏移量，如果不支持，则返回空值
	*/
	List<Long> append(ByteBufferMessageSet messages) throws InvalidMessageException;


	/**
	 * 读取消息
	 * @param offset
	 * @param length
	 * @return
	 * @throws IOException
	 */
	MessageSet read(long offset, int length) throws IOException;
	
	/**
	 * 获取文件
	 * @return
	 */
	File getFile();

	/**
	 * 标记删除时间
	 * @param logSegmentFilter
	 * @return
	 * @throws IOException 
	 */
	List<ILogSegment> markDeletedWhile(LogSegmentFilter logSegmentFilter) throws IOException;

	/**
	 * 最后刷盘时间
	 * @return
	 */
	long getLastFlushedTime();

	/**
	 * 刷盘
	 */
	void flush() throws IOException;

	/**
	 *
	 * @return
	 */
	int delete();
	/**
	 * 获取top
	 * @return
	 */
	String getTopicName();

	/**
	 * 获取partition
	 * @return
	 */
	int getPartition();
	/**
	 * 获取top-partition
	 * @return
	 */
	String getDescription();

}
