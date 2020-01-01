package com.sirionrazzer.diary.auth

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sirionrazzer.diary.R
import com.sirionrazzer.diary.history.HistoryActivity
import com.sirionrazzer.diary.models.UserStorage
import kotlinx.android.synthetic.main.activity_auth.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class AuthActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt

    @Inject
    lateinit var userStorage: UserStorage
    private val TAG = "AuthActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setSupportActionBar(toolbar)
        if (!userStorage.isPinSetup()) {
            startActivity<HistoryActivity>()
        }

        biometricPrompt = createBiometricPrompt()
        biometricPrompt.authenticate(
            createPromptInfo()
        )
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    contentView?.let {
                        Snackbar.make(
                            it,
                            "Biometric verification was cancelled.",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Action", null).show()
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                contentView?.let {
                    Snackbar.make(it, "SUCC", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                startActivity<HistoryActivity>()
            }
        }

        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verification")
            .setSubtitle("Verify your identity using biometrics")
            .setNegativeButtonText("Cancel")
            .build()
    }
}

