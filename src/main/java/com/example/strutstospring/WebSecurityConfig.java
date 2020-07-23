package com.example.strutstospring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(siteminderFilter(),RequestHeaderAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().fullyAuthenticated();

    }
   @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        final List<AuthenticationProvider> providers = new ArrayList<>(1);
        providers.add(preauthAuthProvider());
        return new ProviderManager(providers);
    }

   @Bean(name = "preAuthProvider")
    PreAuthenticatedAuthenticationProvider preauthAuthProvider() throws Exception {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
        return provider;
    }

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() throws Exception {
        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> wrapper = new UserDetailsByNameServiceWrapper<>();
        wrapper.setUserDetailsService(userDetailsService);
        return wrapper;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {


      auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org");
    }

    @Bean(name = "siteminderFilter")
    public RequestHeaderAuthenticationFilter siteminderFilter() throws Exception {
        RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
        requestHeaderAuthenticationFilter.setPrincipalRequestHeader("SM_USER");
        requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return requestHeaderAuthenticationFilter;
    }




   @Bean
   public DefaultLdapAuthoritiesPopulator defaultAuthoritiesPopulato(LdapContextSource contextSource){
       DefaultLdapAuthoritiesPopulator defaultAuthoritiesPopulato = new DefaultLdapAuthoritiesPopulator
               (contextSource, "ou=groups");

       return defaultAuthoritiesPopulato;
   }

    @Bean
    public org.springframework.security.ldap.authentication.LdapAuthenticationProvider ldapAuthenticationProvider(LdapContextSource contextSource){
        org.springframework.security.ldap.authentication.BindAuthenticator bindAuthenticator =
                new org.springframework.security.ldap.authentication.BindAuthenticator(contextSource);
        bindAuthenticator.setUserSearch(filterBasedLdapUserSearch(contextSource));
        LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator);
        return ldapAuthenticationProvider;
    }


    @Bean FilterBasedLdapUserSearch filterBasedLdapUserSearch(LdapContextSource contextSource){
        FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch("ou=people,dc=springframework,dc=org","(uid={0})",contextSource);
        filterBasedLdapUserSearch.setSearchSubtree(true);
        return filterBasedLdapUserSearch;
    }

    @Bean
    public UserDetailsService userDetailsService(FilterBasedLdapUserSearch filterBasedLdapUserSearch){
        LdapUserDetailsService ldapUserDetailsService = new LdapUserDetailsService(filterBasedLdapUserSearch);
        return ldapUserDetailsService;
    }
}