package com.app.softlancer.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.password.CompromisedPasswordChecker
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker
import org.springframework.security.web.firewall.HttpFirewall
import org.springframework.security.web.firewall.StrictHttpFirewall
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


@ConfigurationProperties(prefix = "web")
class WebProperties(
    /**
     * List of routes that do not require authentication.
     * These routes are accessible without any security checks.
     */
    @Value("\${unprotected-routes}") val unprotectedRoutes: List<String>,
    @Value("\${jwt-secret}") val jwtSecret: String,
    @Value("\${jwt-valid-seconds}") val jwtValidSeconds: Int,
)

@Configuration
class SpringSecurityConfig(
    val webProperties: WebProperties,
    val requestMappingHandlerMapping: RequestMappingHandlerMapping,
) {

    private val logger = LoggerFactory.getLogger(SpringSecurityConfig::class.java)

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
    ): SecurityFilterChain {
        http
            .sessionManagement { sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
//            .addFilterBefore(userJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
//            .addFilterAfter(identityJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .cors { cors -> cors.configurationSource(withDefaultCorsConfigurationSource()) }
            .authorizeHttpRequests(withDefaultChain())
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun httpFirewall(): HttpFirewall {
        val defaultHttpFirewall = StrictHttpFirewall()
        defaultHttpFirewall.setAllowUrlEncodedSlash(true)
        defaultHttpFirewall.setAllowBackSlash((true))
        defaultHttpFirewall.setAllowSemicolon(true)
        return defaultHttpFirewall
    }

    fun withDefaultChain(): Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {
        return Customizer { auth ->
            auth
                .requestMatchers(*webProperties.unprotectedRoutes.toTypedArray()).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
        }
    }

//    fun userJwtAuthenticationFilter(): UserJwtAuthenticationFilter {
//        return UserJwtAuthenticationFilter(authService, webProperties.unprotectedRoutes)
//    }
//
//    fun identityJwtAuthenticationFilter(): IdentityJwtAuthenticationFilter {
//        return IdentityJwtAuthenticationFilter(authService, requestMappingHandlerMapping)
//    }
//
//    fun persistentContextSwitchFilter(): PersistentContextSwitchFilter {
//        return PersistentContextSwitchFilter(tenantIdentifierResolver, schemaService)
//    }

    fun withDefaultCorsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOriginPatterns = listOf("*")
        config.allowedMethods = listOf("*")
        config.allowCredentials = true;
        config.allowedHeaders = listOf("*")
        config.maxAge = 3600L;
        val source = UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    fun defaultPasswordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun defaultCompromisedPasswordChecker(): CompromisedPasswordChecker {
        return HaveIBeenPwnedRestApiPasswordChecker()
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val chainOfRoles = "ADMIN > USER > GUEST"
        val hierarchy = RoleHierarchyImpl.withDefaultRolePrefix()
        val roleAsList = chainOfRoles.split(">")
        for (idx in roleAsList.indices) {
            if (idx == 0) continue
            val previousRole = roleAsList[idx - 1].trim()
            val currentRole = roleAsList[idx].trim()
            hierarchy.role(previousRole).implies(currentRole)
        }

        return hierarchy.build()
    }

    // and, if using pre-post method security also add
    @Bean
    fun methodSecurityExpressionHandler(roleHierarchy: RoleHierarchy): MethodSecurityExpressionHandler {
        val expressionHandler = DefaultMethodSecurityExpressionHandler()
        expressionHandler.setRoleHierarchy(roleHierarchy)
        return expressionHandler
    }


}