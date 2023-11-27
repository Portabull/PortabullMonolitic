package com.portabull.router.operation;

import com.portabull.utils.annotation.ServicePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class GenericRouterOperation {

    @Autowired
    ReflectionUtils reflectionUtils;

    public <T> T invokeServiceCall(T... data) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ServicePayload serviceBean = reflectionUtils.getServiceBean(data[0].toString());

        if (serviceBean == null)
            throw new RuntimeException();

        Class<?>[] methodParameters = getParameterType(serviceBean);

        Object[] array = new Object[data.length - 1];

        for (int i = 1; i < data.length; i++) {
            array[i - 1] = data[i];
        }

        return (T) serviceBean.getBean().getClass().getDeclaredMethod(serviceBean.getName(), methodParameters)
                .invoke(serviceBean.getBean(), array);
    }

    private Class<?>[] getParameterType(ServicePayload serviceBean) {

        Method[] methods = serviceBean.getBean().getClass().getMethods();

        for (Method method : methods) {
            if (serviceBean.getName().equals(method.getName())) {
                return method.getParameterTypes();
            }
        }

        return null;
    }


}
