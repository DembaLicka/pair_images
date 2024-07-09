package com.example.pair3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var avatar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()


        // Check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToMainActivity4()
        }

        avatar = findViewById(R.id.avatar2)
        avatar.setOnClickListener {
            signIn()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("MainActivity", "ID Token: ${account?.idToken}")
                account?.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.e("MainActivity", "Google sign in failed", e)
                Toast.makeText(this, "Échec de la connexion Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Authentification réussie, enregistrer le nom de l'utilisateur dans Firebase
                    val user = auth.currentUser
                    user?.let {
                        val database = FirebaseDatabase.getInstance()
                        val usersRef = database.reference.child("users")
                        val currentUserRef = usersRef.child(user.uid)
                        currentUserRef.child("name").setValue(user.displayName)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Connecté en tant que ${user.displayName}", Toast.LENGTH_SHORT).show()
                                goToMainActivity4()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Échec de l'enregistrement du nom de l'utilisateur: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Échec de l'authentification
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Échec de l'authentification : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMainActivity4() {
        val intent = Intent(this, MainActivity4::class.java)
        startActivity(intent)
        finish()
    }
}
