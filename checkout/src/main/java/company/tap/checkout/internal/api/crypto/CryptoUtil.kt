package company.tap.checkout.internal.api.crypto

import android.util.Base64
import androidx.annotation.RestrictTo
import company.tap.checkout.internal.api.exceptions.EmptyStringToEncryptException
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
//Util class for RSA encryption
@RestrictTo(RestrictTo.Scope.LIBRARY)
object  CryptoUtil {
    /**
     * Encrypt json string string.
     *
     * @param jsonString    the json string
     * @param encryptionKey the encryption key
     * @return the string
     */
    fun encryptJsonString(jsonString: String, encryptionKey: String): String? {
        if (jsonString.isEmpty()) {
            throw EmptyStringToEncryptException
        }
        return encrypt(jsonString, encryptionKey)
    }

    private fun encrypt(encrypt: String, encryptionKey: String): String? {
        var encryptionKey = encryptionKey
        var result = ""
        try {
            encryptionKey =
                encryptionKey.replace("\\n".toRegex(), "").replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
            val publicBytes = Base64.decode(encryptionKey, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(publicBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            val pubKey = keyFactory.generatePublic(keySpec)
            val encryptCipher = Cipher.getInstance("RSA/None/PKCS1Padding")
            encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey)
            result =
                Base64.encodeToString(encryptCipher.doFinal(encrypt.toByteArray()), Base64.DEFAULT)
                    .replace("\\n".toRegex(), "")
        } catch (ex: GeneralSecurityException) {
            ex.printStackTrace()
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return result
    }
}
