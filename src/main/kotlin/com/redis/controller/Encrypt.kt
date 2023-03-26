package com.teste.port.`in`

import com.redis.controller.transferobject.request.EncryptRequest
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.security.SecureRandom
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Controller("/encrypt")
class Encrypt {

    @Post
    fun Execute(encryptRequest: EncryptRequest): String {


        Security.addProvider(BouncyCastleProvider())

        val keyBytes: ByteArray

        try {
            keyBytes = encryptRequest.sugar.toByteArray(charset("UTF8"))
            val secretkey = SecretKeySpec(keyBytes, "AES")
            val input = encryptRequest.palavra.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/GCM/NOPADDING", Security.getProvider(BouncyCastleProvider().name))
                cipher.init(Cipher.ENCRYPT_MODE, secretkey, GCMParameterSpec(128, generateIV()))

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                    input, 0, input.size,
                    cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return prependIV(cipher.iv, cipherText)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    fun prependIV(iv: ByteArray, cipherText: ByteArray): String {
        val ivString = iv.joinToString("") { "%02x".format(it) }
        println("IV as hex: $ivString")

        val cipherTextString = cipherText.joinToString("") { "%02x".format(it) }
        println("Cipher text as hex: $cipherTextString")

        val ivAndCipherText = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, ivAndCipherText, 0, iv.size)
        System.arraycopy(cipherText, 0, ivAndCipherText, iv.size, cipherText.size)
        return Base64.toBase64String(ivAndCipherText)
    }

    fun generateIV(): ByteArray {
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        return iv
    }

}