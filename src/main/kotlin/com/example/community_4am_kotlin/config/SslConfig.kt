package com.example.community_4am_kotlin.config

import jdk.internal.org.jline.utils.Colors.s
import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SslConfig {

    @Bean
    fun servletContainer(): ServletWebServerFactory{

        val tomcat = object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: Context) {
                val securityConstraint = SecurityConstraint().apply {
                    userConstraint = "CONFIDENTIAL"
                }
                val collection = SecurityCollection().apply {
                    addPattern("/*")
                }
                securityConstraint.addCollection(collection)
                context.addConstraint(securityConstraint)
            }
        }
        // HTTP 요청을 HTTPS로 리다이렉트하기 위한 커넥터 추가
        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector())
        return tomcat
    }

    /*
        HTTP 요청을 HTTPS로 리다이렉트합니다.
        즉, http://localhost:8080 으로 들어오는 요청을 https://localhost:8443 으로 리다이렉트합니다.
    */
    private fun httpToHttpsRedirectConnector(): Connector {
        return Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL).apply {
            scheme = "http"
            port = 8080
            secure = false
            redirectPort = 8443
        }
    }

}