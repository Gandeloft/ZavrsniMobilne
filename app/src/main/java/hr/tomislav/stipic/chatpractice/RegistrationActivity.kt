package hr.tomislav.stipic.chatpractice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewParent
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.widget.WithHint
import androidx.core.text.set
import androidx.core.view.updateLayoutParams
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.custom_edit_text_field.view.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.InputType
import android.view.inputmethod.InputMethodManager


class RegistrationActivity : AppCompatActivity() {

    var scale = 0f
    var b: Boolean = false
    private lateinit var hint: String
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var raul: LinearLayout
    private lateinit var rael: LinearLayout
    private lateinit var rapl: LinearLayout
    private lateinit var rarpl: LinearLayout

    public override fun onStart() {
        super.onStart()
        scale = this.resources.displayMetrics.density
        // Check if user is signed in (non-null) and update UI accordingly.
        // val currentUser = auth.currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Remove focus from an input field
        registration_activity_global_container.setOnClickListener {
            window.decorView.clearFocus()
        }

        // initialise the UI
        setupView()

        // open LoginActivity if already registered
        register_already_registered_text_view.setOnClickListener { goToLogin()}

        // Set on editText edit listeners for animations of the editText views
        raul.customEditTextBoxMain.setOnFocusChangeListener { view, b ->
                if((view.customEditTextBoxMain.hint as String).isNotEmpty()) {
                    hint = view.customEditTextBoxMain.hint as String }
            onEditTextAnimation(view, b, hint, scale) }

        rael.customEditTextBoxMain.setOnFocusChangeListener { view, b ->
            if((view.customEditTextBoxMain.hint as String).isNotEmpty()) {
                hint = view.customEditTextBoxMain.hint as String }
            onEditTextAnimation(view, b, hint, scale) }

        rapl.customEditTextBoxMain.setOnFocusChangeListener { view, b ->
            if((view.customEditTextBoxMain.hint as String).isNotEmpty()) {
                hint = view.customEditTextBoxMain.hint as String }
            onEditTextAnimation(view, b, hint, scale) }

        rarpl.customEditTextBoxMain.setOnFocusChangeListener { view, b ->
            if((view.customEditTextBoxMain.hint as String).isNotEmpty()) {
                hint = view.customEditTextBoxMain.hint as String }
            onEditTextAnimation(view, b, hint, scale) }

        // Check if the email address entered is valid
        rael.customEditTextBoxMain.addTextChangedListener(object:TextWatcher{override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // "count:" je koliko sam shvatio 0 ukoliko se maknulo slovo a 1 ukoliko se dodalo slovo, odnosno znak, u tom potezu
                // "s:" je unos u taj editText u tom potezu
                // "start:" je broj znakova trenutno u polju u tom potezu. Ovo ne radi baš kako spada odnosno nisi to točno shvatio
                // "before" je broj izbrisanih znakova u potezu
                if (s!!.length > 5) {
                    if(!s.toString().isEmailValid()) {
                        Log.d("VALID EMAIL", s.toString())
                        rael.customEditTextBoxHint.setText("Invalid email")
                        rael.imageView.visibility = View.VISIBLE
                        b = false
                    } else {
                        resetReal()
                        b = true
                    }
                } else {
                    resetReal()
                }
            }
        })

        //Check if the inputted password is valid
        rapl.customEditTextBoxMain.addTextChangedListener(object:TextWatcher{override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length in 1..6) {
                    rapl.customEditTextBoxHint.setText("Password too short!")
                    rapl.imageView.visibility = View.VISIBLE
                    b = false
                } else {
                    rapl.customEditTextBoxHint.setText(getString(R.string.rapl_hint))
                    rapl.imageView.visibility = View.GONE
                    b = true
                }
                password = s.toString()
            }
        })

        // Check if the repeated password matches the initial one
        rarpl.customEditTextBoxMain.addTextChangedListener(object:TextWatcher{override fun afterTextChanged(s: Editable?) {
        }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s.toString() == password).not()) {
                    rarpl.customEditTextBoxHint.setText("Password not matching the above one")
                    rarpl.imageView.visibility = View.VISIBLE
                    b = false
                } else {
                    rarpl.customEditTextBoxHint.setText(getString(R.string.rarpl_hint))
                    rarpl.imageView.visibility = View.GONE
                    b = true
                }
            }
        })


        // jiken1.customEditTextBoxMain.setText("WTF")

        registration_register_button.setOnClickListener {
            //notEmptyInput()
        }

    }

    fun notEmptyInput() {

        username = raul.customEditTextBoxMain.text.toString()
        email = rael.customEditTextBoxMain.text.toString()
        password = rapl.customEditTextBoxMain.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(baseContext, "None of the fields may be left empty",  Toast.LENGTH_LONG).show()
            return
        }

        registerButtonAction()
    }

    fun goToLogin() {
        Log.d("Main activity", "Try to show login activity")
        // launch the login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    // https://stackoverflow.com/questions/56214609/how-to-check-if-a-string-is-a-valid-email-in-android
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun registerButtonAction() {

        // Firebase Authentication to create a user with email and password
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Registration activity", "createUserWithEmail:success. UserID = ${task.result?.user?.uid}")
                    val user = auth.currentUser
                    goToLogin()
                    finish()
                    // Update UI
                    //updateUI(user)
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w("Registration activity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed. ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    //updateUI(null)
                }
            }
    }

    // Companion objects are a way to mimic Java static methods in Kotlin
    companion object {

        private fun pxToDP(px: Int, scale: Float): Int {
            return (px * scale + 0.5f).toInt()
        }
        fun onEditTextAnimation(view: View, b: Boolean, h: String, scale: Float) {
            val r = view.parent as FrameLayout
            val l = r.parent as LinearLayout
            if (b) {
                r.customEditTextBoxMain.hint = ""
                //r.custom_edit_frame_layout_container.layoutParams.height = pxToDP(37, scale)
                //l.customEditTextBoxHint.layoutParams.height = pxToDP(13, scale)
                l.customEditTextBoxHint.visibility = View.VISIBLE
                l.redLine.visibility = View.VISIBLE
            }
            if (!b) {
                r.customEditTextBoxMain.hint = h
                //r.custom_edit_frame_layout_container.layoutParams.height = pxToDP(50, scale)
                l.customEditTextBoxHint.visibility = View.GONE
                l.redLine.visibility = View.GONE
            }
        }
    }

    fun getLayoutId(l: LinearLayout): String {
        var name =l.context.resources.getResourceEntryName(l.id)
        Log.d("TESTESTEST", name)
        return l.context.resources.getResourceEntryName(l.id)
    }


    // Initialise UI elements
    fun setupView() {
        raul = registration_activity_username_layout as LinearLayout
        rael = registration_activity_email_layout as LinearLayout
        rapl = registration_activity_password_layout as LinearLayout
        rarpl = registration_activity_repeat_password_layout as LinearLayout
        raul.customEditTextBoxHint.text = getString(R.string.raul_hint)
        raul.customEditTextBoxMain.hint = "Username"
        rael.customEditTextBoxHint.text =  getString(R.string.rael_hint)
        rael.customEditTextBoxMain.hint = "Email"
        rapl.customEditTextBoxHint.text =  getString(R.string.rapl_hint)
        rapl.customEditTextBoxMain.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        rapl.customEditTextBoxMain.hint = "Password"
        rarpl.customEditTextBoxHint.text =  getString(R.string.rarpl_hint)
        rarpl.customEditTextBoxMain.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        rarpl.customEditTextBoxMain.hint = "Repeat password"

    }

    fun resetReal() {
        rael.customEditTextBoxHint.setText(getString(R.string.rael_hint))
        rael.imageView.visibility = View.GONE
    }

}