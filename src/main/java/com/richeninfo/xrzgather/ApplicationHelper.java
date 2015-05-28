package com.richeninfo.xrzgather;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author mz
 * @date 2013-8-13 15:21:23
 */
public class ApplicationHelper implements ApplicationContextAware{

    private static ApplicationHelper helper = new ApplicationHelper();

    public ApplicationHelper() {
    }
	
	public static ApplicationHelper getInstance() {
		return helper;
	}
	
	private ApplicationContext context; 

	public ApplicationContext getContext() {
		return context;
	}
	
	public Object getBean(String beanName){
		Object obj =getContext().getBean(beanName);
		return obj;
	}


	public void setContext(ApplicationContext context) {
		this.context = context;
	}

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        getInstance().context = ac;
    }

}
