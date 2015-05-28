package com.richeninfo.xrzgather.kernel.iterator;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * 
 * @author mz
 * @date 2013-8-21 17:37:04
 */
public abstract class InfoIterator<T> {
    protected List<T> list;
    
    protected Integer index;
    
    public InfoIterator build(String html, String listCssSelector){
        parse(html, listCssSelector);
        index = 0;
        return this;
    }
    
    protected abstract String getNextString();
    
    protected abstract void parse(String html, String listCssSelector);
    
    /**
     * if hasNext = true,此方法判断下一个列表标签中是否能定位到链接标签
     * @return 
     */
    public abstract boolean hasSelectLink(String linkCssSelector);
    
    /**
     * 当发现下一个列表标签 不能根据 链接选择器选择到链接标签时，可以使用此方法仅仅把指针移动到下一个
     */
    public void goNext(){
        index = index + 1;
    }
    
    public String next(){
        String nextString = getNextString();
        goNext();
        return nextString;
    }
    
    public boolean hasNext(){
        if(index == size()){
            return false;
        }
        return true;
    }
    
    public int size(){
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
        return list.size();
    }
    
    public int getIndex(){
        return index;
    }
}
