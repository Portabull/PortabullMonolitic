package com.portabull.router.operation;

import com.portabull.utils.annotation.ServiceMapping;
import com.portabull.utils.annotation.ServicePayload;
import com.portabull.utils.annotation.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class ReflectionUtils {

    @Autowired
    ApplicationContext applicationContext;

    Map<String, ServicePayload> serviceBeans;

    List<String> packageScan = Arrays.asList("com");

    public ServicePayload getServiceBean(String mapping) {
        return serviceBeans.get(mapping);
    }

    @PostConstruct
    public void loadBeans() {

        Map<String, ServicePayload> serviceBeans = new HashMap<>();

        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);

        provider.addIncludeFilter(new AnnotationTypeFilter(WebService.class));

        packageScan.forEach(packageScanner -> {

            Set<BeanDefinition> candidateComponents = provider.findCandidateComponents(packageScanner);

            candidateComponents.forEach(beanDefinition -> {

                loadServiceBean(beanDefinition, serviceBeans);
            });

        });

        this.serviceBeans = serviceBeans;

    }

    private void loadServiceBean(BeanDefinition beanDefinition, Map<String, ServicePayload> serviceBeans) {

        try {
            Class<?> seviceclass = Class.forName(beanDefinition.getBeanClassName());

            Object serviceBean = applicationContext.getBean(seviceclass);

            Method[] declaredMethods = seviceclass.getDeclaredMethods();

            String serviceCode;
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isAnnotationPresent(ServiceMapping.class)) {

                    serviceCode = prepareServiceCode(seviceclass, declaredMethod);

                    if (serviceBeans.containsKey(serviceCode)) {
                        throw new RuntimeException();
                    }

                    serviceBeans.put(serviceCode, new ServicePayload(declaredMethod.getName(), serviceBean));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String prepareServiceCode(Class<?> serviceClass, Method declaredMethod) {

        StringBuilder builder = new StringBuilder("/");

        builder.append(serviceClass.getAnnotation(WebService.class).value());

        builder.append("/");

        builder.append(declaredMethod.getAnnotation(ServiceMapping.class).value());

        return builder.toString().replaceAll("[\\\\/]+", "/");


    }


}
