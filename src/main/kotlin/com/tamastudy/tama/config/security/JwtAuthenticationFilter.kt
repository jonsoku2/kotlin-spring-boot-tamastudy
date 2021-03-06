package com.tamastudy.tama.config.security


import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.http.HttpServletRequest

import javax.servlet.ServletException

import javax.servlet.FilterChain

import javax.servlet.ServletResponse

import javax.servlet.ServletRequest

import com.tamastudy.tama.config.security.JwtTokenProvider
import org.springframework.security.core.Authentication

import org.springframework.web.filter.GenericFilterBean
import java.io.IOException


class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val token = jwtTokenProvider.resolveToken((request as HttpServletRequest))
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val auth: Authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = auth
        }
        chain.doFilter(request, response)
    }
}