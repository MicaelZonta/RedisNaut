package com.teste.port.`in`

import com.redis.controller.transferobject.request.EncryptRequest
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


@Controller("/decrypt")
class Decrypt {

    @Post
    fun Execute(encryptRequest: EncryptRequest): String {
        Security.addProvider(BouncyCastleProvider())

        val keyBytes: ByteArray

        try {

            val par = extractIVAndCipherText(encryptRequest.palavra)
            val iv = par!!.first
            val cifra = par!!.second

            //Chave
            keyBytes = encryptRequest.sugar.toByteArray(charset("UTF8"))
            val secretkey = SecretKeySpec(keyBytes, "AES")

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC")

                cipher.init(Cipher.DECRYPT_MODE, secretkey, GCMParameterSpec(128, iv))

                val plainText = ByteArray(cipher.getOutputSize(cifra.size))
                var ptLength = cipher.update(cifra, 0, cifra.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    fun extractIVAndCipherText(ivAndCipherText: String): Pair<ByteArray, ByteArray>? {
        val ivAndCipherTextBytes = Base64.decode(ivAndCipherText)

        val ivSize = 12 // assuming 12 bytes IV for AES GCM mode
        if (ivAndCipherTextBytes.size < ivSize) {
            println("Invalid IV and CipherText")
            return null
        }

        val iv = ByteArray(ivSize)
        System.arraycopy(ivAndCipherTextBytes, 0, iv, 0, ivSize)
        val cipherText = ByteArray(ivAndCipherTextBytes.size - ivSize)
        System.arraycopy(ivAndCipherTextBytes, ivSize, cipherText, 0, ivAndCipherTextBytes.size - ivSize)

        println("IV: " + Base64.toBase64String(iv))
        println("CipherText: " + String(cipherText, Charsets.UTF_8))

        return Pair(iv, cipherText)
    }
}
