package com.atsushini.hedgedocportal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${metadata.location}")
    String assertingPartyMetadataLocation;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception{		
		MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);	
			
		http	
		.authorizeHttpRequests(authz -> authz	
			// URLごとの認可設定 
			// 認証したユーザのみがアクセス可能な画面として、"/"を用意しておく
			.requestMatchers(mvcMatcherBuilder.pattern("/")).authenticated()
			.anyRequest().permitAll()
		).saml2Login(saml2Login -> saml2Login	
			 // SAML 2.0プロトコルの認証設定をカスタマイズする場合は、ここにCustomizerを指定します
			 .defaultSuccessUrl("/") // 認証成功後のデフォルトのリダイレクト先を設定する場合など)
		 ).saml2Metadata(	
	    		Customizer.withDefaults()
		).csrf(	
			// CSRFキー有効化設定（デフォルト：有効） 
			Customizer.withDefaults()
		); 	
		return http.build();
    }

    // @Bean
    // public RelyingPartyRegistrationRepository relyingPartyRegistrations() {
    //     RelyingPartyRegistration registration = RelyingPartyRegistrations
    //             .fromMetadataLocation(assertingPartyMetadataLocation)
    //             .registrationId("example")
    //             .build();
    //     return new InMemoryRelyingPartyRegistrationRepository(registration);
    // }
}
