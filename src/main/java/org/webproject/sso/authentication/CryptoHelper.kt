package org.webproject.sso.authentication

import com.google.gson.Gson
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    private val key: SecretKey by lazy {
        val decodedKey = Base64.getDecoder().decode("bOK3fcld7TaQJEplMq7K2gMB0biWJuWGL8zLLfGL100=")
        SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
    }

    private val ALGORITHM = "AES"
    private val HASH_ALGORITHM = "SHA-256"

    @Throws(Exception::class)
    fun<T> encrypt(data: T): String {
        val jsonData= Gson().toJson(data)
        val cipher: Cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes: ByteArray = cipher.doFinal(jsonData.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    @Throws(Exception::class)
    fun<T> decrypt(encryptedData: String,clazz: Class<T>): T {
        val cipher: Cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedBytes: ByteArray = Base64.getDecoder().decode(encryptedData)
        val decryptedBytes: ByteArray = cipher.doFinal(decodedBytes)
        return Gson().fromJson(String(decryptedBytes),clazz)
    }

    fun hashPassword(password: String): String {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes: ByteArray = digest.digest(password.toByteArray(StandardCharsets.UTF_8))
        val stringBuilder = StringBuilder()
        for (b in hashBytes) {
            stringBuilder.append(String.format("%02x", b))
        }
        return stringBuilder.toString()
    }



}