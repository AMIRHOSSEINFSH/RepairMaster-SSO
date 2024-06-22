package org.webproject.sso.authentication

import org.springframework.stereotype.Component
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.roundToInt


@Component
class KeyExchange {

    private val prime: Int = 263
    private val generator: Int = 19
    private val keyLen: Int = 32

    private val r: Random = Random()
    private var lastSecret: Int = 0
    private val alphaSeed by lazy {
        val str = "ejklRSTaop7AmWXYZGz125BJPbcd834CDHIfg0KLMNOqrst6nQuvwhEFUVi9xy"
        val splt = str.split("")
        splt.subList(2, splt.count() - 3)
    }

    private fun generateSecret(): Int {
        val rnd: Double = r.nextGaussian()
        lastSecret = (((rnd * 10 + 50).roundToInt() % 50) + 1)
        return lastSecret
    }

    private fun calculateMode(): Int {
        return (generator.toBigDecimal().pow(lastSecret).rem(prime.toBigDecimal())).toInt()
    }

    fun calculateShareSecret(otherSideSecret: Int): Int {
        return (otherSideSecret.toBigDecimal().pow(lastSecret).rem(prime.toBigDecimal())).toInt()
    }

    init {
        generateSecret()
    }

    fun setSecretKey(key: Int) {
        lastSecret = key
    }

    fun getSecretKey(): Int {
        return lastSecret
    }

    fun getShareSecret(): Int {
        return calculateMode()
    }

    fun generateKey(secret: Int, nnc: Int): String {
        var len = alphaSeed.count()
        val nonce = (nnc % 200) + 100
        val centerKey = nonce % len
        var start = centerKey - (keyLen / 2)
        var end = centerKey + (keyLen / 2)
        if (start < 0) {
            start = 0
            end = keyLen
        } else if (end >= len) {
            end = len - 1
            start = end - keyLen
        }

        val keySlice = alphaSeed.subList(start+1, end+1).toMutableList()
        len = keySlice.count()
        val shareSec = calculateShareSecret(secret)
        var sec = shareSec.toBigDecimal().multiply(nonce.toBigDecimal())
        var index = 0
        while (sec > BigDecimal.ZERO) {
            var rem = sec.multiply(nonce.toBigDecimal().add((index + 1).toBigDecimal())).rem(len.toBigDecimal())
            if (rem <= BigDecimal.ZERO) {
                rem = BigDecimal(1)
            }
            sec = sec.minus(rem)
            Collections.swap(keySlice, index, rem.toInt())
            index = (index + 1) % len
        }
        return keySlice.joinToString("")
    }



    companion object {
        private val HMAC_SHA512 = "HmacSHA512"

        fun hash(data: String): String {
            try {
                val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
                val encodedhash: ByteArray = digest.digest(data.toByteArray(StandardCharsets.UTF_8))
                return bytesToHex(encodedhash)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }

        private fun bytesToHex(hash: ByteArray): String {
            val hexString = StringBuilder(2 * hash.size)
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        }

        fun makeHmac(data: String, key: String): String {
            val sha512Hmac: Mac
            var result = ""
            try {
                val byteKey: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
                sha512Hmac = Mac.getInstance(HMAC_SHA512)
                val keySpec = SecretKeySpec(byteKey, HMAC_SHA512)
                sha512Hmac.init(keySpec)
                val macData: ByteArray = sha512Hmac.doFinal(data.toByteArray(StandardCharsets.UTF_8))



                // Can either base64 encode or put it right into hex
                result = Base64.getEncoder().encodeToString(macData)
                //result = bytesToHex(macData);
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: InvalidKeyException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return result

        }
    }


}

/*
const val EXCHANGE_ALGORITHM=  "DiffieHellman"
fun client() {

    // Generate a Diffie-Hellman key pair
    val keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman")
    keyPairGenerator.initialize(512)
    val keyPair = keyPairGenerator.generateKeyPair()

    val value = keyPair.public as DHPublicKey
    value.y.modPow((keyPair.private as DHPrivateKey).x, value.params.g)

    // Return the public key to the client
    val publicKeyBytes = keyPair.public.encoded
    val format= keyPair.public.format
    val size= publicKeyBytes.size
    val publicKeyString: String = Base64.getEncoder().encodeToString(publicKeyBytes)

    val serverPubKey = server(publicKeyString)

    // Decode the server's public key
    val serverPublicKeyBytes: ByteArray = Base64.getDecoder().decode(serverPubKey)
    val keySpec: KeySpec = X509EncodedKeySpec(serverPublicKeyBytes)
    val keyFactory: KeyFactory = KeyFactory.getInstance(EXCHANGE_ALGORITHM)
    val ServerPublicKey: PublicKey = keyFactory.generatePublic(keySpec)

    // Calculate the shared secret using the client's private key and the server's public key
    val keyAgreement = KeyAgreement.getInstance(EXCHANGE_ALGORITHM)
    keyAgreement.init(keyPair.private)
    keyAgreement.doPhase(ServerPublicKey, true)
    val sharedKey=  keyAgreement.generateSecret()


    println("Client Shared Key is ${HexUtils.toHexString(sharedKey)}")
}

fun server(clientPubKey: String): String {
    // Generate a Diffie-Hellman key pair
    val keyPairGenerator = KeyPairGenerator.getInstance(EXCHANGE_ALGORITHM)
    keyPairGenerator.initialize(512, SecureRandom())
    val keyPair = keyPairGenerator.generateKeyPair()


    // Decode the server's public key
    val publicKeyBytes: ByteArray = Base64.getDecoder().decode(clientPubKey)
    val keySpec: KeySpec = X509EncodedKeySpec(publicKeyBytes)
    val keyFactory: KeyFactory = KeyFactory.getInstance(EXCHANGE_ALGORITHM)
    val publicKey: PublicKey = keyFactory.generatePublic(keySpec)

    // Calculate the shared secret using the client's private key and the server's public key
    val keyAgreement = KeyAgreement.getInstance(EXCHANGE_ALGORITHM)
    keyAgreement.init(keyPair.private)
    keyAgreement.doPhase(publicKey, true)
    val sharedKey=  keyAgreement.generateSecret()

    println("Server Shared Key is ${HexUtils.toHexString(sharedKey)}")

    return Base64.getEncoder().encodeToString(keyPair.public.encoded)

}*/
