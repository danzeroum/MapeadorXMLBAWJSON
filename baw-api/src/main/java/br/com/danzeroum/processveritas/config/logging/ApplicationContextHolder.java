package br.com.danzeroum.processveritas.config.logging;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        ctx = context;
    }

    public static ApplicationContext getContext() {
        return ctx;
    }
}
