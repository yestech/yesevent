package org.yestech.event.event;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Responsible for Creating Events from and Interface. This factory assumes that the Event follow standard java beans
 * naming conviention: setX, getX, isX.  If thats not the case then this factory shouldn't be used currently.  
 *
 */
@SuppressWarnings("unchecked")
public class EventFactory {
    public static <E> E create(Class<E> event) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<?> interfaces[] = {event};
        Object proxy = Proxy.newProxyInstance(loader, interfaces, new EventInvocationHandler());
        return (E) proxy;
    }

    /**
     * Responsible for handling the execution of the methods in the Event
     */
    private static class EventInvocationHandler implements InvocationHandler {
        private Map<String, Object> methods = new ConcurrentHashMap<String, Object>();
        private static final String SET_METHOD_PREFIX = "set";
        private static final String GET_METHOD_PREFIX = "get";
        private static final String IS_METHOD_PREFIX = "is";

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;
            if (method != null) {
                String methodName = method.getName();
                if (StringUtils.startsWith(methodName, SET_METHOD_PREFIX)) {
                    String strippedMethodName = extractMethodName(methodName, SET_METHOD_PREFIX);
                    methods.put(strippedMethodName, args[0]);
                } else if (StringUtils.startsWith(methodName, GET_METHOD_PREFIX)) {
                    String strippedMethodName = extractMethodName(methodName, GET_METHOD_PREFIX);
                    result = methods.get(strippedMethodName);
                } else if (StringUtils.startsWith(methodName, IS_METHOD_PREFIX)) {
                    String strippedMethodName = extractMethodName(methodName, IS_METHOD_PREFIX);
                    result = methods.get(strippedMethodName);
                } else {
                    throw new RuntimeException("not a valid method current: " + methodName);
                }
            }
            return result;
        }

        private String extractMethodName(String methodName, final String prefixToStrip) {
            String strippedMethodName = StringUtils.removeStart(methodName, prefixToStrip);
            return strippedMethodName;
        }
    }
}
