package org.webproject.sso.util.authentication

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.ZoneOffset

object ResponseHandler {

    private const val MESSAGE = "message"
    private const val STATUS=  "status"
    private const val DATA = "data"
    private const val TIMESTAMP = "timeStamp"

    fun<T: Any?> generateResponse(message: String,status: HttpStatus,dataOBj: T?): ResponseEntity<Any?> {
        return HashMap<String,Any?>().apply {
            put(MESSAGE,message)
            put(STATUS,status.value())
            put(DATA,dataOBj)
            put(TIMESTAMP,LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        }.let { responseStruct->
            ResponseEntity<Any?>(responseStruct,status)
        }
    }

    fun<T: Any?> generateResponse(message: String,status: Int,dataOBj: T?): ResponseEntity<Any?> {
        if(status < 200 || status > 500){
            //TODO need a better exception to be handled later
            throw Exception()
        }
        return HashMap<String,Any?>().apply {
            put(MESSAGE,message)
            put(STATUS,status)
            put(DATA,dataOBj)
            put(TIMESTAMP,LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        }.let { responseStruct->
            ResponseEntity<Any?>(responseStruct,null,status)
        }

    }

}