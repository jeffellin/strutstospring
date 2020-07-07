package com.example.strutstospring;


import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CustomProcessor extends RequestProcessor {
    private Logger logger = LoggerFactory.getLogger(CustomProcessor.class);
    private Set<String> autowiredTypes = new HashSet<>();

    @Override
    protected Action processActionCreate(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws IOException {


        String actionType = mapping.getType();

        ApplicationContext beanFactory = (ApplicationContext) request.getServletContext().getAttribute("spring.bean.factory");
        return beanFactory.getBean(actionType, Action.class);
    }


}



