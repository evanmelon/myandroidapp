package com.example.myapplication

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


//import com.google.firebase.quickstart.auth.R

class FirebaseUIActivity : AppCompatActivity(){
    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result
//    val data: Data = applicationContext as Data
//    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
    private lateinit var readWriteSnippets: ReadAndWriteSnippets
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
    // [END auth_fui_create_launcher]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)
        supportActionBar?.hide()
//        createSignInIntent() //登入的按鈕需要做的

//        val buttonLogin: Button = findViewById(R.id.buttonLogin)
//
//        buttonLogin.setOnClickListener {
//            val intent = Intent(this, Personal::class.java)
//            startActivity(intent)
//        }

        val dialogView = layoutInflater.inflate(R.layout.activity_firebase_ui, null)
        val buttonSignIn: Button = findViewById(R.id.buttonSignIn)
        val buttonSignUp: Button = findViewById(R.id.buttonSignUp)
        val account: EditText = findViewById(R.id.editTextEmail)
        val password: EditText = findViewById(R.id.editTextPassword)
        Log.d("login", "hi")
        buttonSignIn.setOnClickListener {
//            Log.d("login", "button click")
//            CoroutineScope(Dispatchers.IO).launch{
//                Log.d("login", "hi1")
//                login(account.text.toString(), password.text.toString())
//
//                withContext(Dispatchers.Main) {
//                    val intent = Intent(this@FirebaseUIActivity, Personal::class.java)
//                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@FirebaseUIActivity).toBundle())
//                }
//            }
            Firebase.auth.signInWithEmailAndPassword(account.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("login", "signInWithEmail:success")
                        val user = Firebase.auth.currentUser
                        user?.let {
                            Log.d("login", "user login2")
                            val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putString("USER_ID", it.uid)
                                putString("NAME", it.displayName)
                                putString("EMAIL", it.email)
                                apply()
                            }
                        }
                        val intent = Intent(this@FirebaseUIActivity, Personal::class.java)
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@FirebaseUIActivity).toBundle())
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("login", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }
        buttonSignUp.setOnClickListener {
            createSignInIntent()
        }
        readWriteSnippets = ReadAndWriteSnippets()
        readWriteSnippets.initializeDbRef()

    }

    private suspend fun login(email: String, password: String){
        Log.d("login", "email: $email, password: $password")
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("login", "signInWithEmail:success")
                    val user = Firebase.auth.currentUser
                    user?.let {
                        Log.d("login", "user login2")
                        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("USER_ID", it.uid)
                            putString("NAME", it.displayName)
                            putString("EMAIL", it.email)
                            apply()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.AppTheme_NoActionBar)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            user?.let {
                with (sharedPref.edit()) {
                    putString("USER_ID", it.uid)
                    putString("NAME", it.displayName)
                    putString("EMAIL", it.email)
                    apply()
                }
            }
            readWriteSnippets.writeNewUser(userId = user?.uid.toString(), name = user?.displayName.toString(), email = user?.email.toString(), postmsg = "")
//            val intent = Intent(this@FirebaseUIActivity, Personal::class.java)
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
    // [END auth_fui_result]

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_signout]
    }

    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_delete]
    }

    private fun themeAndLogo() {
        val providers = emptyList<AuthUI.IdpConfig>()

        // [START auth_fui_theme_logo]
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.my_great_logo) // Set logo drawable
            .setTheme(R.style.MySuperAppTheme) // Set theme
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_theme_logo]
    }

    private fun privacyAndTerms() {
        val providers = emptyList<AuthUI.IdpConfig>()
        // [START auth_fui_pp_tos]
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTosAndPrivacyPolicyUrls(
                "https://example.com/terms.html",
                "https://example.com/privacy.html",
            )
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_pp_tos]
    }

    open fun emailLink() {
        // [START auth_fui_email_link]
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName( // yourPackageName=
                "...", // installIfNotAvailable=
                true, // minimumVersion=
                null,
            )
            .setHandleCodeInApp(true) // This must be set to true
            .setUrl("https://google.com") // This URL needs to be whitelisted
            .build()

        val providers = listOf(
            EmailBuilder()
                .enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings)
                .build(),
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_email_link]
    }

    open fun catchEmailLink() {
        val providers: List<IdpConfig> = emptyList()

        // [START auth_fui_email_link_catch]
        if (AuthUI.canHandleIntent(intent)) {
            val extras = intent.extras ?: return
            val link = extras.getString("email_link_sign_in")
            // 獲取email link
            if (link != null) {
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setEmailLink(link)
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)
            }
        }
        // [END auth_fui_email_link_catch]
    }
}