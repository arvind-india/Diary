package com.sirionrazzer.diary.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.sirionrazzer.diary.R
import com.sirionrazzer.diary.auth.AuthSetupActivity
import com.sirionrazzer.diary.main.MainViewModel
import com.sirionrazzer.diary.models.TrackItemDao
import com.sirionrazzer.diary.models.UserStorage
import io.realm.Realm
import kotlinx.android.synthetic.main.toolbar.*
import main.java.com.sirionrazzer.diary.boarding.AuthViewModel
import javax.inject.Inject


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        @Inject
        lateinit var userStorage: UserStorage
        private lateinit var mainViewModel: MainViewModel
        private lateinit var authViewModel: AuthViewModel


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
            authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

            val deleteHistoryBtn =
                findPreference<Preference>(getString(R.string.deleteHistoryButton))
            deleteHistoryBtn!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainViewModel.deleteAllTrackItems()
                Toast.makeText(context, getString(R.string.history_deleted), Toast.LENGTH_SHORT)
                    .show()
                true
            }
            val pinPref = findPreference<SwitchPreferenceCompat>("PINLockButton")
            pinPref!!.isChecked = userStorage.isPinSetup()
            (authViewModel.isAnonymous.value as Boolean).let {
                if (it) {
                    preferenceScreen.findPreference<SwitchPreferenceCompat>("auto_sync")
                        ?.isEnabled =
                        false
                }
            }
            pinPref.setOnPreferenceClickListener {
                startActivity(Intent(context, AuthSetupActivity::class.java))
                false
            }

        }

    }
}

private val Realm.trackItemDao: TrackItemDao
    get() {
        return TrackItemDao(this)
    }