package com.sirionrazzer.diary.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.sirionrazzer.diary.R
import com.sirionrazzer.diary.models.UserStorage
import com.sirionrazzer.diary.settings.SettingsActivity

import kotlinx.android.synthetic.main.activity_auth_setup.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class AuthSetupActivity : AppCompatActivity() {
    @Inject
    lateinit var userStorage: UserStorage
    var basePw = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_setup)
        setSupportActionBar(toolbar)

        pinSetupCancelButton.setOnClickListener {
            basePw = ""
            pinSetupCancelButton.visibility = View.GONE
            pinSetupText.setText("")
            pinSetupTitleText.text = getString(R.string.enter_pin)
        }
        pinSetupSubmitButton.setOnClickListener {
            val pw = pinSetupText.text.toString()
            when {
                pw.length < 4 -> {
                    Snackbar.make(
                        it,
                        "Password needs to be at least 4 characters",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener

                }
                basePw.isEmpty() -> {
                    basePw = pw
                    pinSetupCancelButton.visibility = View.VISIBLE
                    pinSetupText.setText("")
                    pinSetupTitleText.text = getString(R.string.RetypePinSetup)
                    Snackbar.make(it, "Now retype PIN", Snackbar.LENGTH_SHORT)
                        .show()
                }
                pw == basePw -> {
                    Snackbar.make(
                        it,
                        "PIN has been setup and you can use biometrics instead of pw.",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    userStorage.setPINLock(pw)
                    startActivity<SettingsActivity>()
                }
                pw != basePw -> {
                    Snackbar.make(
                        it,
                        "Pins don't match, try again",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }
    }
}
