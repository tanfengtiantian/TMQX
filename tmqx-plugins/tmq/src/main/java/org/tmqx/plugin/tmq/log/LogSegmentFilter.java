package org.tmqx.plugin.tmq.log;

public interface LogSegmentFilter {

    boolean filter(ILogSegment segment);
}
