package org.webproject.sso.util.authentication

import jakarta.servlet.http.HttpServletRequest
import org.webproject.responsewrapper.custom.exception.*
import org.webproject.sso.authentication.TokenManagement

fun HttpServletRequest.getTokenFromHeader(): TokenManagement.Token {
    return getAttribute("token") as? TokenManagement.Token ?: throw DefaultSupportedException("You did not provided token")
}