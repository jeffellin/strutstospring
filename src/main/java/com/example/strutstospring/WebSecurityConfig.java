package com.example.strutstospring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {





    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(siteminderFilter())
                .authorizeRequests()
                .anyRequest().fullyAuthenticated();
    }
    public TestPreAuthenticatedProcessingFilter siteminderFilter() throws Exception {
        TestPreAuthenticatedProcessingFilter requestHeaderAuthenticationFilter = new TestPreAuthenticatedProcessingFilter();
        //requestHeaderAuthenticationFilter.setPrincipalRequestHeader("SM_USER");
        requestHeaderAuthenticationFilter.setAuthenticationManager(new ProviderManager(preAuthenticationProvider()));
        return requestHeaderAuthenticationFilter;
    }

    PreAuthenticatedAuthenticationProvider preAuthenticationProvider() {
        LdapUserDetailsService uds = new LdapUserDetailsService(filterBasedLdapUserSearch(contextSource()));
        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> udsw = new UserDetailsByNameServiceWrapper<>(uds);
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(udsw);
        return provider;
    }


    FilterBasedLdapUserSearch filterBasedLdapUserSearch(LdapContextSource contextSource){
        FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch("ou=people,dc=springframework,dc=org","(uid={0})",contextSource);
        filterBasedLdapUserSearch.setSearchSubtree(true);
        return filterBasedLdapUserSearch;
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        contextSource.setUrl("ldap://localhost:8389");


        return contextSource;
    }
}
