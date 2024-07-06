package org.webproject.sso.model.dto

    import jakarta.validation.constraints.Email
    import jakarta.validation.constraints.NotBlank
    import jakarta.validation.constraints.NotNull
    import org.webproject.responsewrapper.enumModel.USERTYPE

    import org.webproject.sso.util.annotations.ValidUserType

data class UserDto(
    @field:NotNull(message = "password can not be empty")
    @field:NotBlank(message = "Name must not be blank")
    val password: String,
    @field:NotNull(message = "Email cannot be null")
    @field:Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    val email: String,
    val firstName: String,
    val lastName: String,
    val userPublicKey: Int,
    @ValidUserType
    val type: Int,

    var deviceModel: String?
){
    fun getUserType() = USERTYPE.returnUserType(type)
}