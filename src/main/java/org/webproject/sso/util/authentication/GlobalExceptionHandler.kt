package org.webproject.sso.util.authentication

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.webproject.responsewrapper.custom.exception.*
import org.webproject.responsewrapper.custom.response.ResponseHandler.generateResponse
import java.util.function.Consumer


@RestControllerAdvice
class GlobalExceptionHandler {

    /*@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        MethodArgumentNotValidException::class
    )
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): Map<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        })
        return errors
    }*/

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Any?> {
        println("******************")
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
            println("Error: $errorMessage")
        })
        val message = "Validation failed"
        return generateResponse<Map<String, String?>>(message, HttpStatus.BAD_REQUEST, null)
    }

    @ExceptionHandler(SupportedException::class)
    fun handleFieldException(ex: SupportedException): ResponseEntity<Any?> {
        val message = "An unexpected error occurred"
        return generateResponse(ex.message?: message, ex.status, null)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Any?> {
        val message = "An unexpected error occurred"
        ex.printStackTrace()
        return generateResponse(message, HttpStatus.INTERNAL_SERVER_ERROR, null)
    }


}