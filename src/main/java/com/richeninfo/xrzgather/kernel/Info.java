package com.richeninfo.xrzgather.kernel;

/**
 * 定义资讯的通用字段
 * @author mz
 * @date 2013-8-8 10:48:39
 */
public enum Info {
    /**
     * 资讯的URL的key
     */
    INFO_URL,
    /**
     * 资讯的标题的key
     */
    INFO_TITLE,
    /**
     * 资讯原始标题的key。保留资讯在网页上的标题
     */
    ORIGINAL_INFO_TITLE,
    /**
     * 资讯的发布时间的key
     */
    INFO_PUBDATE,
    /**
     * 资讯的内容的key
     */
    INFO_TEXT,
    /**
     * 资讯的来源的key
     */
    infoSource,
    /**
     * 资讯的网站名称的key
     */
    INFO_NET_NAME,
    /**
     * 与附件相关的key
     */
    INFO_GROUP_CODE,
    /**
     * UUID,资讯的唯一标识
     */
    INFO_UUID,
    /**
     * 附件的名称
     */
    INFO_FILE_NAME,
    /**
     * 附件的byte数组
     */
    INFO_FILE_BYTES,
}
