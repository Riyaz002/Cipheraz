package com.riyaz.cipheraz.utils

import javax.crypto.Cipher
enum class Mode(mode: Int) {
    ENCRYPT(Cipher.ENCRYPT_MODE),
    DECRYPT(Cipher.DECRYPT_MODE)
}