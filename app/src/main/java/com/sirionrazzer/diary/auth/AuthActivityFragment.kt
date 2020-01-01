package com.sirionrazzer.diary.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sirionrazzer.diary.R
import com.sirionrazzer.diary.history.HistoryActivity
import com.sirionrazzer.diary.models.UserStorage
import kotlinx.android.synthetic.main.fragment_auth.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class AuthActivityFragment : Fragment() {

    @Inject
    lateinit var userStorage: UserStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        view!!.findViewById<Button>(R.id.verify_btn)!!.setOnClickListener {
            if (pinText.text.toString() == userStorage.getPIN()) {
                startActivity(Intent(context, HistoryActivity::class.java))
            } else {
                Snackbar.make(it, "Wrong PIN", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        return view
    }
}
