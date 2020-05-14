package org.tmqx.plugin.tmq.log.imp;

import org.tmqx.plugin.tmq.log.ILogSegment;
import org.tmqx.plugin.tmq.log.RollingStrategy;

import java.io.IOException;

import static java.lang.String.format;

/**
 * @author tf
 * @version 创建时间：2018年12月30日 下午10:03:48
 * @ClassName 此策略将在文件达到最大文件大小时滚动文件。
 */
public class FixedSizeRollingStrategy implements RollingStrategy {

	private int maxFileSize;
	
	public FixedSizeRollingStrategy(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
	
	@Override
	public boolean check(ILogSegment lastSegment) {
		return lastSegment.getMessageSet().getSizeInBytes() > maxFileSize;
	}

	@Override
	public void close() throws IOException {
		
	}
	
	@Override
    public String toString() {
        return format("FixedSizeRollingStrategy [maxFileSize=%d bytes(%dMB)", maxFileSize, maxFileSize / (1024 * 1024));
    }

}
