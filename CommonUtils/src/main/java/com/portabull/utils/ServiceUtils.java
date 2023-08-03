package com.portabull.utils;

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import net.vidageek.mirror.dsl.Mirror;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class ServiceUtils {

    String server = "server";

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    public List<String> getRegisteredServices() {
        return discoveryClient.getServices();
    }

    public String getHomePageUrl(String serviceId) {
        try {
            if (serviceId != null) {
                return new StringBuilder(((DiscoveryEnabledServer) new Mirror()
                        .on(loadBalancerClient.choose(serviceId)).get().field(server)).getInstanceInfo().getHomePageUrl()).append("/").toString();
            }
        } catch (IllegalArgumentException e) {
            throw new RestClientException(serviceId + " Server is Down Please try after some time");
        }
        return null;
    }

    public boolean serviceAvailable(String serviceId) {
        return getRegisteredServices().contains(serviceId);
    }

}
