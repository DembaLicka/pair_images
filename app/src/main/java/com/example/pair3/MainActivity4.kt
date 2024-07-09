package com.example.pair3

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity4 : AppCompatActivity() {

    lateinit var jouer: RelativeLayout
    lateinit var jouer2: RelativeLayout
    lateinit var idprofil: ImageView
    lateinit var bestPlayerImage: ImageView
    lateinit var bestPlayerName2 : TextView
    lateinit var bestplayerduree : TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var bestPlayerScore: TextView
    lateinit var nomdujoueur: TextView
    var PICK_IMAGE_REQUEST = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        jouer = findViewById(R.id.jouer2)
        jouer2 = findViewById(R.id.jouer3)
        idprofil = findViewById(R.id.idprofil)
        nomdujoueur = findViewById(R.id.nomdujoueur)
        bestPlayerScore = findViewById(R.id.bestPlayerScore)
        bestPlayerImage = findViewById(R.id.profildujoueur)
        bestPlayerName2 = findViewById(R.id.bestplayername)
        bestplayerduree = findViewById(R.id.duree)

        idprofil.setOnClickListener {
            selectImageFromGallery()
        }

        jouer2.setOnClickListener {
            finish()
        }
        jouer.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
        mAuth = FirebaseAuth.getInstance()
        mDatabase = Firebase.database.reference

      //  google()

        displayUserName()
        displayProfileImage()
        loadBestPlayer()
        }

   /* private fun google() {
        var  textView = findViewById<TextView>(R.id.name)

        val auth = Firebase.auth
        val user = auth.currentUser



        if (user != null) {
            var userName = user.displayName.toString()
            textView.text = "Bienvenue, $userName"

            val database = FirebaseDatabase.getInstance()
            val usersRef = database.reference.child("users")
            val currentUserRef = usersRef.child(user.uid)

        } else {

        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    */

    private fun loadBestPlayer() {
        databaseReference = Firebase.database.reference.child("users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var highestScore = 0
                var bestPlayerNameStr: String? = null
                var bestPlayerImageUrl: String? = null
                var bestPlayerScoreInt = 0
                var bestPlayerTimeStr: String? = null // Variable pour stocker la durée

                for (snapshot in dataSnapshot.children) {
                    val userName = snapshot.child("name").getValue(String::class.java)
                    val userScore = snapshot.child("score").getValue(Int::class.java)
                    val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
                    val userTimePlayed = snapshot.child("timePlayed").getValue(String::class.java) // Récupération de la durée

                    if (userName != null && userScore != null && imageUrl != null && userTimePlayed != null && userScore > highestScore) {
                        highestScore = userScore
                        bestPlayerNameStr = userName
                        bestPlayerImageUrl = imageUrl
                        bestPlayerScoreInt = userScore
                        bestPlayerTimeStr = userTimePlayed // Assignation de la durée
                    }
                }

                if (bestPlayerNameStr != null && bestPlayerImageUrl != null) {
                    bestPlayerName2.text = bestPlayerNameStr
                    bestPlayerScore.text = "$bestPlayerScoreInt"
                    bestplayerduree.text = bestPlayerTimeStr // Affichage de la durée

                    Glide.with(this@MainActivity4)
                        .load(bestPlayerImageUrl)
                        .into(bestPlayerImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity4, "Failed to load score: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            idprofil.setImageURI(imageUri)

            val storageRef = Firebase.storage.reference.child("profile_images/${mAuth.currentUser?.uid}")
            val uploadTask = storageRef.putFile(imageUri!!)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    saveImageUrlToDatabase(downloadUri.toString())
                } else {
                    // Handle errors
                }
            }
        }
    }
    private fun saveImageUrlToDatabase(imageUrl: String) {
        val userId = mAuth.currentUser?.uid
        userId?.let {
            val database = Firebase.database
            val usersRef = database.reference.child("users")
            val currentUserRef = usersRef.child(userId)
            currentUserRef.child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Photo de profil mise à jour avec succès", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Échec de la mise à jour de la photo de profil : ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun displayProfileImage() {
        val userId = mAuth.currentUser?.uid
        userId?.let {
            val database = Firebase.database
            val usersRef = database.reference.child("users")
            val currentUserRef = usersRef.child(userId)

            currentUserRef.child("imageUrl").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@MainActivity4)
                            .load(imageUrl)
                            .into(idprofil)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity4, "Échec de la récupération de la photo de profil : ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun displayUserName() {
        val userId = mAuth.currentUser?.uid
        userId?.let {
            val database = Firebase.database
            val usersRef = database.reference.child("users")
            val currentUserRef = usersRef.child(userId)

            currentUserRef.child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.getValue(String::class.java)
                    if (!userName.isNullOrEmpty()) {
                        nomdujoueur.text = userName
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity4, "Échec de la récupération du nom de l'utilisateur : ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
