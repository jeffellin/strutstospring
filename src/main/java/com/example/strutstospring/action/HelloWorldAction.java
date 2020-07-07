package com.example.strutstospring.action;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.strutstospring.InspirationalBean;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.example.strutstospring.form.HelloWorldForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jndi.JndiTemplate;
/*import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;*/
import org.springframework.stereotype.Component;

import java.security.Security;
import java.util.Enumeration;

@Component("HelloWorldAction")
public class HelloWorldAction extends Action{

	public HelloWorldAction(InspirationalBean inspirationalBean) {
		this.inspirationalBean = inspirationalBean;
	}

	InspirationalBean inspirationalBean;

	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) throws Exception {

		JndiTemplate jndiTemplate = new JndiTemplate();

		String foo = (String)jndiTemplate.lookup("java:/comp/env/foo");

		for (Enumeration<Binding> e = jndiTemplate.getContext().listBindings("java:comp/env"); e.hasMoreElements();) {
			Binding bind = e.nextElement();
			System.out.println(bind.getName() + " : " + bind.getObject());
		}


		HelloWorldForm helloWorldForm = (HelloWorldForm) form;
		helloWorldForm.setMessage("Hello World! Struts: "+foo);
		
		return mapping.findForward("success");
	}
	
}