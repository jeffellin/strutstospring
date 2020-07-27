package com.example.strutstospring;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.struts.action.ActionServlet;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class StrutsToSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(StrutsToSpringApplication.class, args);
	}
	@Bean
	public ServletRegistrationBean actionServlet() {
		ActionServlet servlet = new ActionServlet();
		ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "*.do");
		bean.addInitParameter("config", "/WEB-INF/struts-config.xml");
		bean.setLoadOnStartup(1);
		bean.setName("action");
		return bean;
	}

	/*@Bean
	ApplicationRunner applicationRunner(LdapTemplate ldapTemplate) {
		return args -> {
			List<String> cn = ldapTemplate

					.search(
							"ou=people,dc=springframework,dc=org",
							"uid=" + "bob",
							(AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());


			List<String> cn2 = ldapTemplate

					.search(
							"ou=groups,dc=springframework,dc=org",
							"uniqueMember=" + "uid=bob,ou=people,dc=springframework,dc=org",
							(AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());



			System.out.println(cn2);

		};
	}*/

/*	@Bean
	public LdapContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();

		contextSource.setUrl("ldap://localhost:8389");

		return contextSource;
	}*/

	/*@Bean
	public LdapTemplate ldapTemplate() {
		return new LdapTemplate(contextSource());
	}
*/
	@Bean
	public ServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory(){
			@Override
			protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
				//naming is not enabled by default
				tomcat.enableNaming();
				return new TomcatWebServer(tomcat, this.getPort() >= 0, this.getShutdown());
			}
		};
		factory.addContextCustomizers(new TomcatContextCustomizer() {
			@Override
			public void customize(Context context) {

				//add a Context Environment for each value.
				ContextEnvironment ce = new ContextEnvironment();
				ce.setName("foo");
				ce.setValue("1234");
				ce.setType("java.lang.String");

				context.getNamingResources().addEnvironment(ce);

			}
		});
		return factory;
	}

}
