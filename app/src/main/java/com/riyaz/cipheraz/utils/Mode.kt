package com.riyaz.cipheraz.utils

import javax.crypto.Cipher
enum class Mode(private val mode: Int) {
    ENCRYPT(Cipher.ENCRYPT_MODE),
    DECRYPT(Cipher.DECRYPT_MODE);

    fun getValue(): Int{
        return this.mode
    }
}