package com.example.pair3

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class MainActivity5 : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var editTextRoomCode: EditText
    private lateinit var buttonJoinRoom: Button
    private lateinit var imageViews: Array<ImageView>
    private var initialImages: List<Int> = emptyList()
    lateinit var textViewCountdown : TextView


    private val imageIds = intArrayOf(
        R.drawable.m7, R.drawable.cerise2, R.drawable.m24, R.drawable.m20, R.drawable.m6,
        R.drawable.m23, R.drawable.m8, R.drawable.m17, R.drawable.m3, R.drawable.m22,
        R.drawable.m4, R.drawable.m27, R.drawable.m19, R.drawable.soup2, R.drawable.m15,
        R.drawable.bol, R.drawable.m12, R.drawable.m2, R.drawable.m26, R.drawable.m16,
        R.drawable.m29, R.drawable.panierr, R.drawable.m21, R.drawable.m9, R.drawable.m18,
        R.drawable.m5, R.drawable.m25, R.drawable.m14, R.drawable.mmmm, R.drawable.m13)


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        myId()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        editTextRoomCode = findViewById(R.id.editTextRoomCode)
        buttonJoinRoom = findViewById(R.id.buttonJoinRoom)

        buttonJoinRoom.setOnClickListener {
            val roomCode = editTextRoomCode.text.toString()
            if (roomCode.isNotEmpty()) {
                joinRoom(roomCode)
            } else {
                Toast.makeText(this, "Please enter a room code", Toast.LENGTH_SHORT).show()
            }
        }

        initializeFirebaseListeners()
        logicGame()
    }

    private fun checkPlayerCountAndStartGame(roomRef: DatabaseReference) {
        roomRef.child("players").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerCount = snapshot.childrenCount
                if (playerCount == 2L) {
                    synchronizeImages(roomRef)
                    startCountdownToRevealImages()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to check player count: $error")
            }
        })
    }
    private fun startCountdownToRevealImages() {
        val countdownTime = 10000L // 10 secondes en millisecondes
        object : CountDownTimer(countdownTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val timerText = String.format("-%2d:%02ds", minutes, seconds)
                textViewCountdown.text = timerText
                textViewCountdown.visibility = View.VISIBLE
                Log.d(TAG, "Countdown: $timerText")
            }

            override fun onFinish() {
                revealAllImages()
                textViewCountdown.visibility = View.GONE
            }
        }.start()
    }
    private fun revealAllImages() {
        // Afficher les images de dos après un délai de 10 secondes
        Handler(Looper.getMainLooper()).postDelayed({
            for (imageView in imageViews) {
                imageView.setImageResource(R.drawable.heloping2)
            }
        }, 0L) // 10 secondes en millisecondes
    }
    private fun joinRoom(roomCode: String) {
        val roomRef = database.child("rooms").orderByChild("roomCode").equalTo(roomCode)
        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val playerId = auth.currentUser?.uid ?: return
                        childSnapshot.ref.child("players").child(playerId).setValue(true).addOnCompleteListener { firstPlayerTask ->
                            if (firstPlayerTask.isSuccessful) {
                                Log.d(TAG, "Player added to room")
                                checkPlayerCountAndStartGame(childSnapshot.ref)
                            } else {
                                Log.e(TAG, "Failed to add player to room: ${firstPlayerTask.exception}")
                            }
                        }
                    }
                } else {
                    createNewRoom(roomCode)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to join room: $error")
            }
        })
    }
    private fun createNewRoom(roomCode: String) {
        val playerId = auth.currentUser?.uid ?: return
        val newRoomRef = database.child("rooms").push()
        newRoomRef.child("roomCode").setValue(roomCode)

        // Création et mélange des images initiales
        val initialImages = shuffleAndDuplicateImages()

        newRoomRef.child("initialImages").setValue(initialImages).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "New room created with code $roomCode")
                newRoomRef.child("players").child(playerId).setValue(true).addOnCompleteListener { playerTask ->
                    if (playerTask.isSuccessful) {
                        checkPlayerCountAndStartGame(newRoomRef)
                    } else {
                        Log.e(TAG, "Failed to add player to new room: ${playerTask.exception}")
                    }
                }
            } else {
                Log.e(TAG, "Failed to create new room: ${task.exception}")
            }
        }
    }
    private fun shuffleAndDuplicateImages(): List<Int> {
        val selectedImages = imageIds.toList().shuffled().subList(0, 30)
        val duplicatedImages = (selectedImages + selectedImages).toMutableList()
        duplicatedImages.shuffle()
        return duplicatedImages
    }
    private fun synchronizeImages(roomRef: DatabaseReference) {
        roomRef.child("initialImages").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = snapshot.getValue(object : GenericTypeIndicator<List<Int>>() {})
                if (images != null) {
                    initialImages = images
                    for (i in imageViews.indices) {
                        imageViews[i].setImageResource(images[i])
                    }
                } else {
                    Log.e(TAG, "No initial images found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve images: $error")
            }
        })
    }
    private fun initializeFirebaseListeners() {
        val roomsRef = database.child("rooms")
        roomsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                updateCardState(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                updateCardState(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to listen to card changes: ${error.message}")
            }
        })
    }
    private fun handleCardClick(index: Int, roomCode: String) {
        val roomRef = database.child("rooms").orderByChild("roomCode").equalTo(roomCode)
        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val cardRef = childSnapshot.ref.child("cards").child(index.toString())
                        cardRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(cardSnapshot: DataSnapshot) {
                                val cardState = cardSnapshot.getValue(CardState::class.java)
                                if (cardState == null || !cardState.isRevealed) {
                                    val updatedCardState = CardState(isRevealed = true)
                                    cardRef.setValue(updatedCardState).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "Card $index revealed")
                                        } else {
                                            Log.e(TAG, "Failed to reveal card $index: ${task.exception}")
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(TAG, "Failed to retrieve card state: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve room: ${error.message}")
            }
        })
    }

    private fun updateCardState(snapshot: DataSnapshot) {
        val roomCode = snapshot.child("roomCode").getValue(String::class.java) ?: return
        val cardsSnapshot = snapshot.child("cards")

        for (cardSnapshot in cardsSnapshot.children) {
            val cardIndex = cardSnapshot.key?.toIntOrNull() ?: continue
            val cardState = cardSnapshot.getValue(CardState::class.java) ?: continue

            val imageView = imageViews[cardIndex]
            val imageResId = if (cardState.isRevealed) initialImages[cardIndex] else R.drawable.heloping2 // Utilisez votre propre drawable pour l'image de dos
            imageView.setImageResource(imageResId)

            // Mettre à jour l'état des clics
            imageView.isClickable = !cardState.isRevealed
        }
    }

    private fun logicGame() {
        for (i in 0 until 60) {
            val imageView = imageViews[i]
            imageView.setOnClickListener {
                val roomCode = editTextRoomCode.text.toString()
                if (roomCode.isNotEmpty()) {
                    handleCardClick(i, roomCode)
                } else {
                    Toast.makeText(this, "Please enter a room code", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun myId(){
      imageViews = arrayOf(
          findViewById(R.id.image1),
          findViewById(R.id.image2),
          findViewById(R.id.image3),
          findViewById(R.id.image4),
          findViewById(R.id.image5),
          findViewById(R.id.image6),
          findViewById(R.id.image7),
          findViewById(R.id.image8),
          findViewById(R.id.image9),
          findViewById(R.id.image10),
          findViewById(R.id.image11),
          findViewById(R.id.image12),
          findViewById(R.id.image13),
          findViewById(R.id.image14),
          findViewById(R.id.image15),
          findViewById(R.id.image16),
          findViewById(R.id.image17),
          findViewById(R.id.image18),
          findViewById(R.id.image19),
          findViewById(R.id.image20),
          findViewById(R.id.image21),
          findViewById(R.id.image22),
          findViewById(R.id.image23),
          findViewById(R.id.image24),
          findViewById(R.id.image25),
          findViewById(R.id.image26),
          findViewById(R.id.image27),
          findViewById(R.id.image28),
          findViewById(R.id.image29),
          findViewById(R.id.image30),
          findViewById(R.id.image31),
          findViewById(R.id.image32),
          findViewById(R.id.image33),
          findViewById(R.id.image34),
          findViewById(R.id.image35),
          findViewById(R.id.image36),
          findViewById(R.id.image37),
          findViewById(R.id.image38),
          findViewById(R.id.image39),
          findViewById(R.id.image40),
          findViewById(R.id.image41),
          findViewById(R.id.image42),
          findViewById(R.id.image43),
          findViewById(R.id.image44),
          findViewById(R.id.image45),
          findViewById(R.id.image46),
          findViewById(R.id.image47),
          findViewById(R.id.image48),
          findViewById(R.id.image49),
          findViewById(R.id.image50),
          findViewById(R.id.image111),
          findViewById(R.id.image222),
          findViewById(R.id.image333),
          findViewById(R.id.image444),
          findViewById(R.id.image555),
          findViewById(R.id.image666),
          findViewById(R.id.image777),
          findViewById(R.id.image888),
          findViewById(R.id.image999),
          findViewById(R.id.image1000))

        textViewCountdown = findViewById(R.id.compteur)


  }

}