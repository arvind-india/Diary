package main.java.com.sirionrazzer.diary.boarding

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.sirionrazzer.diary.Diary
import com.sirionrazzer.diary.models.UserStorage
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject


interface AuthInterface {
    fun register(email: String, pw: String)
    fun anonymousRegister()
    fun linkAnonymousToPassword(email: String, pw: String)
    fun login(email: String, pw: String)
    fun logout()
    fun changePassword(oldPw: String, newPw: String, newPwAgain: String)
}

class AuthViewModel : ViewModel(), AuthInterface {
    @Inject
    lateinit var userStorage: UserStorage

    val isLoggedIn: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isAnonymous: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val authError: MutableLiveData<String?> by lazy { MutableLiveData<String?>(null) }
    val accountCreated: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        Diary.app.appComponent.inject(this)
        val user = firebaseAuth.currentUser
        if (user != null) {
            isAnonymous.value = user.isAnonymous
            isLoggedIn.value = true
        }
        accountCreated.value = userStorage.userSettings.accountCreated
    }

    override fun register(email: String, pw: String) {
        // try to login in case user tries to login through registration
        firebaseAuth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storeHashedPassword(pw)
                    updateEmail(email)
                    userStorage.updateSettings { lus ->
                        lus.accountCreated = true
                    }
                    isAnonymous.value = false
                    isLoggedIn.value = true
                } else {
                    // proceed with registration
                    firebaseAuth.createUserWithEmailAndPassword(email, pw)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                storeHashedPassword(pw)
                                updateEmail(email)
                                isAnonymous.value = false
                                isLoggedIn.value = true
                            } else {
                                failLogin(task.exception?.message)
                            }
                        }
                        .addOnFailureListener { failure ->
                            failLogin(failure.message)
                        }
                }
            }
    }

    override fun anonymousRegister() {
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeHashedPassword(null)
                    isAnonymous.value = true
                    isLoggedIn.value = true
                } else {
                    failLogin(task.exception?.message)
                }
            }
    }

    override fun linkAnonymousToPassword(email: String, pw: String) {
        val credential = EmailAuthProvider.getCredential(email, pw)
        firebaseAuth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeHashedPassword(pw)
                    updateEmail(email)
                    userStorage.updateSettings { lus ->
                        lus.accountCreated = true
                    }
                    isAnonymous.value = false
                    isLoggedIn.value = true
                } else {
                    failLogin(task.exception?.message)
                }
            }
            ?.addOnFailureListener { failure ->
                failLogin(failure.message)
            }
    }

    override fun login(email: String, pw: String) {
        firebaseAuth.signInWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeHashedPassword(pw)
                    updateEmail(email)
                    userStorage.updateSettings { lus ->
                        lus.accountCreated = true
                    }
                    isAnonymous.value = false
                    isLoggedIn.value = true
                } else {
                    failLogin(task.exception?.message)
                }
            }
            .addOnFailureListener { failure ->
                failLogin(failure.message)
            }
    }

    private fun failLogin(authError: String?) {
        isLoggedIn.value = false
        authError?.let { this.authError.value = authError }
    }

    override fun logout() {
        userStorage.clearPasswordDigest()
        isLoggedIn.value = false
    }

    override fun changePassword(oldPw: String, newPw: String, newPwAgain: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        // TODO reencrypt Realm
    }

    /**
     * Set password and store its hashed version
     * Create new temporary password for anonymouse user
     * @param pw is password, if null then generate temporary password
     */
    private fun storeHashedPassword(pw: String?) {
        var key = ByteArray(64)
        if (pw == null) {
            SecureRandom().nextBytes(key)
            MessageDigest.getInstance("SHA-256").digest(key, 0, key.size)
        } else {
            val decoded = Base64.decode(pw, Base64.NO_WRAP)
            (decoded.indices).forEach { i ->
                // TODO this doesn't work good
                key[i] = decoded[i]
            }

            MessageDigest.getInstance("SHA-256").digest(key, 0, key.size)
        }
        userStorage.storePasswordDigest(key)
    }

    private fun updateEmail(email: String) {
        userStorage.updateSettings { lus ->
            lus.email = email
        }
    }

    fun getEncryptedPassword(): ByteArray {
        return userStorage.loadPasswordDigest()
    }
}