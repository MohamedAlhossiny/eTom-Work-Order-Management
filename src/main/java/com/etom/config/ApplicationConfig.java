package com.etom.config;

import com.etom.resource.WorkOrderResource;
import com.etom.resource.CancelWorkOrderResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Register resources
        resources.add(WorkOrderResource.class);
        resources.add(CancelWorkOrderResource.class);
        
        // Register Jackson for JSON processing
        resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
        
        // Enable CORS
        resources.add(CorsFilter.class);
        
        return resources;
    }
} 