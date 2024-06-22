package org.webproject.sso.model.dto.response

data class RegisterUserResponse (
     val verifyToken: String,
     val serverPublicKey: Int?
)