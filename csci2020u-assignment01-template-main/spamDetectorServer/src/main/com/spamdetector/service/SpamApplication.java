package main.com.spamdetector.service;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import main.com.spamdetector.filter.CorsResponseFilter;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class SpamApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CorsResponseFilter.class);
        classes.add(SpamResource.class);
        return classes;
    }
}