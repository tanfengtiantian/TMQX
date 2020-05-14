package org.tmqx.plugin.tmq.log;


import org.tmqx.plugin.tmq.message.FileMessageSet;

import java.io.Closeable;
import java.io.File;


/**
 * @author tf
 * @version 创建时间：2018年12月30日 上午9:45:27
 * @ClassName 日志段
 * @Description 每个段落文件操作
 */
public interface ILogSegment extends Comparable<ILogSegment>, Closeable{

	 /** 第一个索引
     * @return 
     */
    long start();

    /** 索引总数
     * @return 
     */
    long size();

    /** 
     * @return check the range is emtpy
     */
    boolean isEmpty();

    /** 范围内
     * @param offset 检查值
     * @return 
     */
    boolean contains(long offset);
    
    /** 获取文件段
     * @return
     */
    File getFile();
    
    /** 获取文件段 FileChannel
     * @return
     */
    FileMessageSet getMessageSet();
    /**
     * 获取fileName
     * @return
     */
    String getName();
    
    /**
     * 设置删除标记
     * @param deleted
     */
    void setDeleted(boolean deleted);



}
