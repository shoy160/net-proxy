package com.github.shoy160.proxy.util;

import com.yunzhicloud.core.utils.CommonUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author shay
 * @date 2020/12/2
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    public static void setContext(ApplicationContext context) {
        SpringUtils.context = context;
    }

    public static Object getObject(String id) {
        Object object = null;
        object = context.getBean(id);
        return object;
    }

    public static <T> T getObject(Class<T> tClass) {
        try {
            String[] names = context.getBeanNamesForType(tClass);
            if (CommonUtils.isEmpty(names)) {
                return null;
            }
            return context.getBean(tClass);
        } catch (Exception ex) {
            return null;
        }

    }

    public static Object getBean(String tClass) {
        return context.getBean(tClass);
    }

    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }
}