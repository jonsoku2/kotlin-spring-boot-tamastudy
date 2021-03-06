package com.tamastudy.tama.controller

import com.auth0.jwt.exceptions.TokenExpiredException
import com.tamastudy.tama.config.property.JwtProperties
import com.tamastudy.tama.dto.*
import com.tamastudy.tama.service.user.UserService
import com.tamastudy.tama.util.PrincipalDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/api/v1/user")
class UserApiController(
        private val userService: UserService,
        private val jwtProperties: JwtProperties,
) {
    @PostMapping("/login")
    fun login(
            @RequestBody UserLoginRequestDto: UserLoginRequestDto,
            response: HttpServletResponse,
    ): ResponseEntity<UserLoginResponseDto> {
        val loginResponse = userService.loginUser(UserLoginRequestDto)
        val cookie = Cookie("refreshToken", loginResponse.refreshToken)
        cookie.maxAge = jwtProperties.accessMaxAge!! // 7일
        cookie.isHttpOnly = true
        response.addCookie(cookie)
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse)
    }

    @PostMapping("/join")
    fun join(@Valid @RequestBody userJoinRequestDto: UserJoinRequestDto): ResponseEntity<UserJoinResponseDto> {
        return ResponseEntity.status(HttpStatus.OK).body(userService.joinUser(userJoinRequestDto))
    }

    @PostMapping("/logout")
    fun logout(
            @RequestHeader(value = "Authorization") token: String,
            response: HttpServletResponse,
    ): ResponseEntity<Unit> {
        if (token.startsWith("Bearer ")) {
            val cookie = Cookie("refreshToken", "")
            cookie.maxAge = 0
            cookie.isHttpOnly = true
            response.addCookie(cookie)
            userService.logoutUser(token.substring(7))
        }
        return ResponseEntity.ok().build()
    }

    @PostMapping("/refresh")
    fun refreshToken(
            @RequestHeader(value = "Authorization") token: String,
            @CookieValue(value = "refreshToken") refreshToken: String,
            response: HttpServletResponse,
    ): ResponseEntity<UserLoginResponseDto> {
        if (token.startsWith("Bearer ")) {
            val loginResponse = userService.refreshToken(token.substring(7), refreshToken)
            val cookie = Cookie("refreshToken", loginResponse.refreshToken)
            cookie.maxAge = jwtProperties.accessMaxAge!! // 7일
            cookie.isHttpOnly = true
            response.addCookie(cookie)
            return ResponseEntity.ok().body(loginResponse)
        } else {
            throw AccessDeniedException("refresh error")
        }
    }

    @GetMapping("/authenticate")
    fun authenticate(): ResponseEntity<UserDto> {
        println("authenticate")
        if (SecurityContextHolder.getContext().authentication.principal == "anonymousUser") {
            throw TokenExpiredException("")
        }
        val userDto = (SecurityContextHolder.getContext().authentication.principal as PrincipalDetails).getUserDto()
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto)
    }
}