package org.webproject.sso.authentication

import org.apache.tomcat.util.buf.HexUtils
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.KeyAgreement


object KeyExchange {

    const val EXCHANGE_ALGORITHM = "DiffieHellman"
    const val HMAC_ALGORITHM = "HmacSHA256"

    private var keyPair: KeyPair?= null
        private set(value) {
            secretKey= value?.private
            field = value
        }
    private var secretKey: PrivateKey ?=null

    fun generateSharedKey(otherSideRik: PublicKey): ByteArray {
        val keyAgreement = KeyAgreement.getInstance("DiffieHellman")
        keyAgreement.init(secretKey?: keyPair?.private)
        keyAgreement.doPhase(otherSideRik, true)
        return keyAgreement.generateSecret()
    }

    fun generateSharedKey(otherSideRik: String): ByteArray {
        val serverPublicKeyBytes: ByteArray = Base64.getDecoder().decode(otherSideRik)
        val keySpec: KeySpec = X509EncodedKeySpec(serverPublicKeyBytes)
        val keyFactory: KeyFactory = KeyFactory.getInstance("DiffieHellman")
        val publicKey: PublicKey = keyFactory.generatePublic(keySpec)
        return generateSharedKey(publicKey)
    }

    fun setSecretKey(secretKey: PrivateKey) {
        this.secretKey = secretKey
    }

    fun setSecretKey(secretKey: String) {
        val secretKeyByteArray = Base64.getDecoder().decode(secretKey)
        val keySpec: KeySpec = X509EncodedKeySpec(secretKeyByteArray)
        val keyFactory: KeyFactory = KeyFactory.getInstance("DiffieHellman")
        val privateKey: PrivateKey = keyFactory.generatePrivate(keySpec)
        setSecretKey(privateKey)
    }

    private fun generateRsa() {
        val keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman")
        keyPairGenerator.initialize(2048)
        keyPair = keyPairGenerator.generateKeyPair()
    }

    init {
        generateRsa()
    }




}

fun client() {

    // Generate a Diffie-Hellman key pair
    val keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman")
    keyPairGenerator.initialize(2048)
    val keyPair = keyPairGenerator.generateKeyPair()

    // Return the public key to the client
    val publicKeyBytes = keyPair.public.encoded
    val publicKeyString: String = Base64.getEncoder().encodeToString(publicKeyBytes)

    val serverPubKey = server(publicKeyString)

    // Decode the server's public key
    val serverPublicKeyBytes: ByteArray = Base64.getDecoder().decode(serverPubKey)
    val keySpec: KeySpec = X509EncodedKeySpec(serverPublicKeyBytes)
    val keyFactory: KeyFactory = KeyFactory.getInstance("DiffieHellman")
    val ServerPublicKey: PublicKey = keyFactory.generatePublic(keySpec)

    // Calculate the shared secret using the client's private key and the server's public key
    val keyAgreement = KeyAgreement.getInstance("DiffieHellman")
    keyAgreement.init(keyPair.private)
    keyAgreement.doPhase(ServerPublicKey, true)
    val sharedKey=  keyAgreement.generateSecret()


    println("Client Shared Key is ${HexUtils.toHexString(sharedKey)}")
}

fun server(clientPubKey: String): String {
    // Generate a Diffie-Hellman key pair
    val keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman")
    keyPairGenerator.initialize(2048)
    val keyPair = keyPairGenerator.generateKeyPair()


    // Decode the server's public key
    val publicKeyBytes: ByteArray = Base64.getDecoder().decode(clientPubKey)
    val keySpec: KeySpec = X509EncodedKeySpec(publicKeyBytes)
    val keyFactory: KeyFactory = KeyFactory.getInstance("DiffieHellman")
    val publicKey: PublicKey = keyFactory.generatePublic(keySpec)

    // Calculate the shared secret using the client's private key and the server's public key
    val keyAgreement = KeyAgreement.getInstance("DiffieHellman")
    keyAgreement.init(keyPair.private)
    keyAgreement.doPhase(publicKey, true)
    val sharedKey=  keyAgreement.generateSecret()

    println("Server Shared Key is ${HexUtils.toHexString(sharedKey)}")

    return Base64.getEncoder().encodeToString(keyPair.public.encoded)

}