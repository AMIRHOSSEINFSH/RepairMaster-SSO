package org.webproject.sso.util.authentication

import jakarta.servlet.http.HttpServletRequest
import org.webproject.sso.authentication.TokenManagement

fun HttpServletRequest.getTokenFromHeader(): TokenManagement.Token {
    return getAttribute("token") as? TokenManagement.Token ?: throw Exception("You did not provided token")
}