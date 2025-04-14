package com.example.pair3

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity4 : AppCompatActivity() {

    lateinit var jouer: RelativeLayout
    lateinit var jouer2: RelativeLayout
    lateinit var delete: RelativeLayout
    lateinit var idprofil: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    lateinit var nomdujoueur: TextView
    lateinit var nmbreroom: TextView
    lateinit var description: TextView
    var PICK_IMAGE_REQUEST = 1
    lateinit var edittextroom : EditText
    lateinit var createroom : RelativeLayout
    lateinit var layoutcretaeroom : RelativeLayout
    lateinit var joinrom : RelativeLayout
    lateinit var floati : RelativeLayout
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    val imageIds = intArrayOf( R.drawable.m7, R.drawable.cerise2, R.drawable.m24
        , R.drawable.m20, R.drawable.m6, R.drawable.m23, R.drawable.m8,
        R.drawable.m17, R.drawable.m3 , R.drawable.m22 , R.drawable.m4 , R.drawable.m27,
        R.drawable.m19 ,R.drawable.salad, R.drawable.m15,R.drawable.bol, R.drawable.m12,R.drawable.m2
        ,R.drawable.m26 ,R.drawable.m16,R.drawable.m29 ,R.drawable.pnier,R.drawable.m21,R.drawable.m9
        ,R.drawable.m18,R.drawable.m5,R.drawable.m25,R.drawable.m14,R.drawable.mmmm,R.drawable.m13)

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        jouer = findViewById(R.id.textView20)
        jouer2 = findViewById(R.id.textView21)
        nmbreroom = findViewById(R.id.nmbreroom)
        edittextroom = findViewById(R.id.editTextRoomCode)
        createroom = findViewById(R.id.createroom)
        layoutcretaeroom = findViewById(R.id.layoutcretaeroom)
        floati = findViewById(R.id.floati)
        layoutcretaeroom.visibility = View.INVISIBLE
        idprofil = findViewById(R.id.idprofil)
        description = findViewById(R.id.description)
        delete = findViewById(R.id.delete)
        joinrom = findViewById(R.id.joinrom)
        joinrom.setOnClickListener {
            val intent = Intent(this , MainActivity3::class.java)
            startActivity(intent)
        }

        mDatabase = FirebaseDatabase.getInstance().reference.child("rooms")


        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Compte le nombre total de salles
                val roomCount = snapshot.childrenCount
                // Affiche le nombre total de salles
                nmbreroom.text = "$roomCount"
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer les erreurs ici si nécessaire
                nmbreroom.text = "Erreur de chargement"
            }
        })


        floati.setOnClickListener {
            if (layoutcretaeroom.isVisible) {
                // Fade out animation
                layoutcretaeroom.animate()
                    .alpha(0f) // Set to fully transparent
                    .setDuration(300) // Duration of the animation
                    .withEndAction {
                        layoutcretaeroom.visibility = View.INVISIBLE // Set visibility to INVISIBLE after animation
                    }
            } else {
                // Make sure the view is visible and set alpha to 0
                layoutcretaeroom.visibility = View.VISIBLE
                layoutcretaeroom.alpha = 0f // Start from fully transparent

                // Fade in animation
                layoutcretaeroom.animate()
                    .alpha(1f) // Set to fully opaque
                    .setDuration(300) // Duration of the animation
            }
        }

        idprofil.setOnClickListener {
            selectImageFromGallery()
        }
        jouer.setOnClickListener {
            val intent = Intent(this , MainActivity2::class.java)
            startActivity(intent)
            finish()
        }
        mAuth = FirebaseAuth.getInstance()
        val auth = Firebase.auth
        val user = mAuth.currentUser
        mDatabase = Firebase.database.reference

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        jouer2.setOnClickListener {
            val intent = Intent(this , activity_room_list::class.java)
            startActivity(intent)
        }
        createroom.setOnClickListener {
            // Vérifier que le champ de texte n'est pas vide
            val roomName = edittextroom.text.toString()
            if (roomName.isNotEmpty()) {
                // Fade out animation
                layoutcretaeroom.animate()
                    .alpha(0f) // Définir l'alpha à 0 (complètement transparent)
                    .setDuration(300) // Durée de l'animation
                    .withEndAction {
                        layoutcretaeroom.visibility = View.INVISIBLE // Mettre la visibilité sur INVISIBLE après l'animation
                        // Appeler la fonction de création de la nouvelle salle après l'animation
                        createNewRoom(roomName)

                    }
            } else {
                Toast.makeText(this, "Please enter a room name", Toast.LENGTH_SHORT).show()
            }
        }

        displayProfileImage()
        displayPlayerCreatedRoom()

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
    private fun createNewRoom(roomName: String) {
        val playerId = mAuth.currentUser?.uid ?: return

        // Vérifie si le joueur a déjà un salon actif
        hasActiveRoom(playerId) { hasActiveRoom ->
            if (hasActiveRoom) {
                Toast.makeText(this, "Vous avez déjà un salon actif", Toast.LENGTH_SHORT).show()
                return@hasActiveRoom
            }

            // Création du nouveau salon si le joueur n'a pas de salon actif
            val roomId = mDatabase.child("rooms").push().key ?: return@hasActiveRoom
            val roomRef = mDatabase.child("rooms").child(roomId)

            // Création et mélange des images initiales
            val initialImages = shuffleAndDuplicateImages()

            // Initialisation du salon avec les images, les joueurs et l'état du jeu
            val room = Room(
                id = roomId,
                name = roomName,
                creator = playerId,
                players = mapOf(playerId to true),
                images = initialImages,
                gameState = "waiting" // On ajoute l'état du jeu au moment de la création
            )

            roomRef.setValue(room).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "New room created with ID $roomId")
                    Toast.makeText(this, "Salon créé avec succès", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(ContentValues.TAG, "Failed to create new room: ${task.exception}")
                    Toast.makeText(this, "Échec de la création du salon", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shuffleAndDuplicateImages(): List<Int> {
        val selectedImages = imageIds.toList().shuffled().subList(0, 30)
        val duplicatedImages = (selectedImages + selectedImages).toMutableList()
        duplicatedImages.shuffle()
        return duplicatedImages
    }

    private fun hasActiveRoom(playerId: String, callback: (Boolean) -> Unit) {
        mDatabase.child("rooms").orderByChild("creator").equalTo(playerId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Si le snapshot contient des données, cela signifie que le joueur a déjà un salon actif
                val hasActiveRoom = snapshot.childrenCount > 0
                callback(hasActiveRoom)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity4", "Failed to check player's rooms: ${error.message}")
                callback(false)
            }
        })
    }
    private fun displayPlayerCreatedRoom() {
        val playerId = mAuth.currentUser?.uid ?: return

        // Rechercher le salon créé par le joueur connecté
        mDatabase.child("rooms").orderByChild("creator").equalTo(playerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Récupérer le premier salon trouvé créé par ce joueur
                        for (roomSnapshot in snapshot.children) {
                            val room = roomSnapshot.getValue(Room::class.java)
                            if (room != null) {
                                // Afficher les informations du salon à l'écran
                                description.text = "${room.name}"
                                Log.d("MainActivity4", "${room.name}")

                                // Rendre le bouton de suppression visible
                                delete.visibility = View.VISIBLE

                                // Ajouter un listener pour le bouton de suppression
                                delete.setOnClickListener {
                                    deleteRoom(roomSnapshot.key) // Appeler la fonction de suppression
                                }
                            }
                        }
                    } else {
                        // Si aucun salon n'est trouvé
                        description.text = "Vous n'avez pas de salon actif"
                        delete.visibility = View.GONE // Cacher le bouton si pas de salon
                        Log.d("MainActivity4", "Aucun salon créé par le joueur.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    description.text = "Erreur de chargement"
                    Log.e("MainActivity4", "Erreur de Firebase: ${error.message}")
                }
            })
    }
    private fun deleteRoom(roomId: String?) {
        if (roomId != null) {
            mDatabase.child("rooms").child(roomId).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        delete.visibility = View.GONE // Cacher le bouton après suppression
                    } else {
                        Log.e("MainActivity4", "Erreur de suppression: ${task.exception?.message}")
                    }
                }
        }
    }

}
