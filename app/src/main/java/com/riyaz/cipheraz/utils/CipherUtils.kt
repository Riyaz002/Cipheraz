package com.riyaz.cipheraz.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class CipherUtils {
    companion object {
        suspend fun encrypt(key: String, inputFile: File, outputFile: File){
            doCrypt(Cipher.ENCRYPT_MODE, key, inputFile, outputFile)
        }

        suspend fun decrypt(key: String, inputFile: File, outputFile: File){
            doCrypt(Cipher.DECRYPT_MODE, key, inputFile, outputFile)
        }

        private suspend fun doCrypt(cryptMode: Int,key: String, inputFile: File, outputFile: File) {
            withContext(Dispatchers.IO){
                try {
                    val secretKey: Key = SecretKeySpec(key.toByteArray(), "AES")
                    val cipher: Cipher = Cipher.getInstance("AES")
                    cipher.init(cryptMode, secretKey)

                    //input Stream
                    val inputStream = FileInputStream(inputFile)
                    val inputBytes = inputStream.readBytes()
                    cipher.doFinal(inputBytes)

                    val outputStream = FileOutputStream(outputFile)
                    outputStream.write(inputBytes)

                    inputStream.close()
                    outputStream.close()
                } catch(e: Exception){
                    throw IOException(e.message)
                }
            }
        }
    }
}