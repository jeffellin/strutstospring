package com.example.strutstospring;

import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests().antMatchers("/login.html").permitAll()
                .and()
                .authorizeRequests().antMatchers("/login").permitAll()
                .and().authorizeRequests()
                .anyRequest().hasRole("MANAGERS").and()
                .formLogin().loginProcessingUrl("/login").loginPage("/login.html").permitAll();
    }
    // @formatter:on

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .userSearchFilter("(uid dd={0})")
                .groupSearchBase("ou=groups")
                .groupRoleAttribute("cn")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org");

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

    @Bean org.springframework.security.ldap.search.FilterBasedLdapUserSearch filterBasedLdapUserSearch(LdapContextSource contextSource){
        FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch("ou=people,dc=springframework,dc=org","(uid={0})",contextSource);
        filterBasedLdapUserSearch.setSearchSubtree(true);
        return filterBasedLdapUserSearch;
    }
    //@Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }
}