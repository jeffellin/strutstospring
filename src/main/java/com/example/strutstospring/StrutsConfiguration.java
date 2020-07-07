package com.example.strutstospring;

import org.apache.struts.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import java.util.Arrays;


@Configuration
    public class StrutsConfiguration {

        private static Logger logger = LoggerFactory.getLogger(StrutsConfiguration.class);

        public StrutsConfiguration(ApplicationContext context, AutowireCapableBeanFactory beanFactory, ServletContext servletContext) {
            logger.info("*** CONFIGURING STRUTS ***");
            logger.info("Got beanFactory: " + (beanFactory != null));
            logger.info("Got servletContext: " + (servletContext != null));
            logger.info("Got context: " + (context != null));

            Arrays.stream(context.getBeanNamesForType(Action.class)).forEach(System.out::println);

            servletContext.setAttribute("spring.bean.factory", context);
        }
    }
