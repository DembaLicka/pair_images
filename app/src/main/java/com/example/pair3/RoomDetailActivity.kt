package com.example.pair3

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class RoomDetailActivity : AppCompatActivity() {

    private lateinit var imageViews: Array<ImageView>
    private lateinit var relativeLayouts: Array<RelativeLayout>
    private var initialImages: List<Int> = emptyList()
    private lateinit var roomId: String
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var playerId: String? = null
    private lateinit var chronoTextView: TextView
    private var isClickable: Boolean = false
    private var isImagesDisplayed: Boolean = false
    private val requiredPlayerCount = 2
    private val imageDisplayDuration: Long = 30000
    private var currentTurnPlayerId: String? = null
    private var clickCount: Int = 0
    private lateinit var clickedImages: MutableSet<Int>
    private lateinit var opponentNameTextView1: TextView
    private lateinit var opponentNameTextView2: TextView
    private lateinit var opponentScoreTextView1: TextView
    private lateinit var opponentScoreTextView2: TextView
    lateinit var profileImageView : ImageView
    lateinit var adversaireImage : ImageView
    lateinit var hoteimage : ImageView
    lateinit var  rougecolere : ImageView
    lateinit var  emotionHapp    : ImageView
    lateinit var  emotionhola    : ImageView
    lateinit var  emotioncoler  : ImageView
    private var emotionHandler: Handler? = null
    private var emotionRunnable: Runnable? = null
    lateinit var hoteTurnIndicator : ImageView
    lateinit var adversaireTurnIndicator : ImageView
    lateinit var im1 : ImageView
    lateinit var im2 : ImageView
    lateinit var hour : ImageView
    lateinit var parentIm1 : RelativeLayout
    lateinit var parentIm2 : RelativeLayout
    lateinit var quitterlayout : RelativeLayout
    lateinit var nombredeminjouer : RelativeLayout
    lateinit var layoutermine : RelativeLayout
    private var startTime: Long = 0 // Temps de début en millisecondes
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private lateinit var textViewTime: TextView
    private lateinit var clickSoundPlayer: MediaPlayer
    lateinit var adView : AdView
    lateinit var adversairphoto : ImageView
    lateinit var hotephoto : ImageView
    lateinit var quitter : TextView
    private lateinit var pairsFoundTextView: TextView
    private lateinit var countdownRef: DatabaseReference
    private var pairsFound = 0
    private lateinit var hostScoreTextView: TextView
    private lateinit var opponentScoreTextView: TextView
    private lateinit var finishnme1: TextView
    private lateinit var finishname2: TextView
    private lateinit var oh: ImageView
    private lateinit var febr: ImageView
    private lateinit var npi: ImageView
    private lateinit var angry: ImageView
    private lateinit var bonhomme: ImageView
    private lateinit var quitterdefinif: RelativeLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        playerId = auth.currentUser?.uid

        initializeViews()
        quitterlayout.visibility = View.INVISIBLE
        bonhomme.visibility = View.INVISIBLE
        clickedImages = mutableSetOf()

        roomId = intent.getStringExtra("ROOM_ID") ?: return

        hoteTurnIndicator.visibility = View.GONE
        adversaireTurnIndicator.visibility = View.GONE

        clickSoundPlayer = MediaPlayer.create(this, R.raw.explainer)

        MobileAds.initialize(this) {}

        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        adView.loadAd(adRequest)
        countdownRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId).child("countdown")



        setupEmotionClickListener()
        setupEmotionListener()
        checkIfCreator()
        addPlayerAndMonitorRoom(roomId)
        setupRealtimeListeners()
        updateScores()
        displayUserProfilePicture()
        setupTurnIndicators()

        nombredeminjouer.visibility = View.INVISIBLE
        layoutermine.visibility = View.INVISIBLE
        handler = Handler(Looper.getMainLooper()) // Initialiser le Handler

        quitterdefinif.setOnClickListener {

            disconnectPlayer()
            finish()
        }
        updateTimeRunnable = object : Runnable {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime
                val minutes = (elapsedTime / 1000 / 60).toInt()
                val seconds = (elapsedTime / 1000 % 60).toInt()
                textViewTime.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000) // Met à jour toutes les secondes
            }
        }
        retrieveMessageFromFirebase()

    }

    private fun initializeViews() {
        chronoTextView = findViewById(R.id.chronometre)
        opponentNameTextView1 = findViewById(R.id.opponent1)
        opponentNameTextView2 = findViewById(R.id.opponent2)
        opponentScoreTextView1 = findViewById(R.id.points)
        opponentScoreTextView2 = findViewById(R.id.points2)
        profileImageView = findViewById(R.id.profileImageView)
        adversaireImage  = findViewById(R.id.adversaire)
        hoteimage  = findViewById(R.id.hote)
        rougecolere = findViewById(R.id.textView12)
        emotionHapp = findViewById(R.id.textView11)
        hoteTurnIndicator = findViewById(R.id.hoteTurnIndicator)
        adversaireTurnIndicator = findViewById(R.id.adversaireTurnIndicator)
        im1 = findViewById(R.id.im1)
        im2 = findViewById(R.id.im2)
        parentIm1 = findViewById(R.id.parentim1)
        parentIm2 = findViewById(R.id.parentim2)
        hour = findViewById(R.id.hour)
        textViewTime = findViewById(R.id.textViewTime)
        nombredeminjouer = findViewById(R.id.nombredeminjouer)
        adversairphoto = findViewById(R.id.adversairphoto)
        hotephoto = findViewById(R.id.hotephoto)
        quitter = findViewById(R.id.quitter)
        quitterlayout = findViewById(R.id.quitterlayout)
        pairsFoundTextView = findViewById(R.id.pairsFoundTextView)
        layoutermine = findViewById(R.id.layoutermine)
        hostScoreTextView = findViewById(R.id.finishscorehote)
        opponentScoreTextView= findViewById(R.id.opponentScoreTextView)
        finishnme1= findViewById(R.id.finishnme1)
        finishname2= findViewById(R.id.finishname2)
        oh= findViewById(R.id.textView13)
        febr= findViewById(R.id.textView14)
        npi= findViewById(R.id.textView15)
        angry = findViewById(R.id.textView16)
        bonhomme = findViewById(R.id.bonhomme)
        quitterdefinif = findViewById(R.id.quitterdussalon)

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
            findViewById(R.id.image1000)
        )
        relativeLayouts = arrayOf(
            findViewById(R.id.RelativeLayout),
            findViewById(R.id.RelativeLayout2),
            findViewById(R.id.RelativeLayout3),
            findViewById(R.id.RelativeLayout4),
            findViewById(R.id.RelativeLayout5),
            findViewById(R.id.RelativeLayout6),
            findViewById(R.id.RelativeLayout7),
            findViewById(R.id.RelativeLayout8),
            findViewById(R.id.RelativeLayout9),
            findViewById(R.id.RelativeLayout10),
            findViewById(R.id.RelativeLayout11),
            findViewById(R.id.RelativeLayout12),
            findViewById(R.id.RelativeLayout13),
            findViewById(R.id.RelativeLayout14),
            findViewById(R.id.RelativeLayout15),
            findViewById(R.id.RelativeLayout16),
            findViewById(R.id.RelativeLayout17),
            findViewById(R.id.RelativeLayout18),
            findViewById(R.id.RelativeLayout19),
            findViewById(R.id.RelativeLayout20),
            findViewById(R.id.RelativeLayout21),
            findViewById(R.id.RelativeLayout22),
            findViewById(R.id.RelativeLayout23),
            findViewById(R.id.RelativeLayout24),
            findViewById(R.id.RelativeLayout25),
            findViewById(R.id.RelativeLayout26),
            findViewById(R.id.RelativeLayout27),
            findViewById(R.id.RelativeLayout28),
            findViewById(R.id.RelativeLayout29),
            findViewById(R.id.RelativeLayout30),
            findViewById(R.id.RelativeLayout31),
            findViewById(R.id.RelativeLayout32),
            findViewById(R.id.RelativeLayout33),
            findViewById(R.id.RelativeLayout34),
            findViewById(R.id.RelativeLayout35),
            findViewById(R.id.RelativeLayout36),
            findViewById(R.id.RelativeLayout37),
            findViewById(R.id.RelativeLayout38),
            findViewById(R.id.RelativeLayout39),
            findViewById(R.id.RelativeLayout40),
            findViewById(R.id.RelativeLayout41),
            findViewById(R.id.RelativeLayout42),
            findViewById(R.id.RelativeLayout43),
            findViewById(R.id.RelativeLayout44),
            findViewById(R.id.RelativeLayout45),
            findViewById(R.id.RelativeLayout46),
            findViewById(R.id.RelativeLayout47),
            findViewById(R.id.RelativeLayout48),
            findViewById(R.id.RelativeLayout49),
            findViewById(R.id.RelativeLayout50),
            findViewById(R.id.textView),
            findViewById(R.id.textView2),
            findViewById(R.id.textView3),
            findViewById(R.id.textView4),
            findViewById(R.id.textView5),
            findViewById(R.id.textView6),
            findViewById(R.id.textView7),
            findViewById(R.id.textView8),
            findViewById(R.id.textView9),
            findViewById(R.id.textView10))

        setupImageClickListeners()
    }
    private fun displayUserProfilePicture() {
        val userRef = database.child("users").child(playerId!!).child("imageUrl")
        userRef.get().addOnSuccessListener { snapshot ->
            val profilePictureUrl = snapshot.getValue(String::class.java)
            if (profilePictureUrl != null) {
                // Utilisez Picasso ou Glide pour charger l'image à partir de l'URL
                Picasso.get().load(profilePictureUrl).into(profileImageView)
            } else {
                Log.e("RoomDetailActivity", "URL de la photo de profil non trouvée")
            }
        }.addOnFailureListener {
            Log.e("RoomDetailActivity", "Échec de la récupération de l'URL de la photo de profil : ${it.message}")
        }
    }
    private fun setupImageClickListeners() {
        for (imageView in imageViews) {
            imageView.setOnClickListener {
                onImageClick(imageView)
            }
        }
    }
    private fun addPlayerAndMonitorRoom(roomId: String) {
        val roomRef = database.child("rooms").child(roomId)

        roomRef.child("players").child(playerId!!).setValue(true).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("RoomDetailActivity", "Player added to room")
                // Initialize player's score
                roomRef.child("scores").child(playerId!!).setValue(0)
                monitorRoomPlayers(roomRef)
            } else {
                Log.e("RoomDetailActivity", "Failed to add player: ${it.exception}")
            }
        }
    }
    private fun monitorRoomPlayers(roomRef: DatabaseReference) {
        roomRef.child("players").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                checkAndRetrieveImages(roomRef)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                checkAndRetrieveImages(roomRef)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle player removal if necessary
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Failed to monitor players: ${error.message}")
            }
        })
    }
    private fun checkAndRetrieveImages(roomRef: DatabaseReference) {
        roomRef.child("players").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val players = snapshot.children.mapNotNull { it.key }
                if (players.size == requiredPlayerCount && !isImagesDisplayed) {
                    retrieveImages(roomId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Failed to check players: ${error.message}")
            }
        })
    }
    private fun retrieveImages(roomCode: String) {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomCode)

        roomRef.child("images").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val images = snapshot.getValue(object : GenericTypeIndicator<List<Int>>() {})
                if (images != null) {
                    initialImages = images
                    displayImages()
                    showImagesTemporarily()
                    isImagesDisplayed = true
                } else {
                    Log.e("RoomDetailActivity", "Aucune image trouvée")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération des images : $error")
            }
        })
    }
    private fun displayImages() {
        // Dupliquer les images pour les 60 ImageView
        val duplicatedImages = initialImages + initialImages
        for (i in imageViews.indices) {
            if (i < duplicatedImages.size) {
                imageViews[i].setImageResource(duplicatedImages[i]) // Afficher l'image de l'aperçu
                imageViews[i].tag = duplicatedImages[i] // Stocker la ressource de l'image derrière
            }
        }
    }
   private fun showImagesTemporarily() {
       object : CountDownTimer(imageDisplayDuration, 1000) {
           override fun onTick(millisUntilFinished: Long) {
               val secondsRemaining = millisUntilFinished / 1000
               chronoTextView.text = secondsRemaining.toString()
           }

           override fun onFinish() {
               for (imageView in imageViews) {
                   val parentLayout = imageView.parent as? RelativeLayout
                   parentLayout?.setBackgroundResource(R.drawable.heloping5)
                   imageView.setImageResource(R.drawable.two_layer_drawable6)
               }

               // Mettre à jour Firebase
               countdownRef.child("imagesShown").setValue(true)

               startCountDown()
               chronoTextView.visibility = View.INVISIBLE
               hour.visibility = View.INVISIBLE
           }
       }.start()
   }
    private fun startCountDown() {
        isClickable = false // Désactiver les clics

        countdownRef.child("playersReady").child(playerId!!).setValue(true)

        countdownRef.child("playersReady").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val readyPlayers = snapshot.children.count { it.getValue(Boolean::class.java) == true }
                val totalPlayers = 2 // Nombre total de joueurs

                if (readyPlayers == totalPlayers) {
                    object : CountDownTimer(0, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val secondsRemaining = millisUntilFinished / 1000
                            chronoTextView.text = secondsRemaining.toString()
                        }

                        override fun onFinish() {
                            isClickable = true
                            determineFirstTurn()
                        }
                    }.start()

                    countdownRef.child("playersReady").removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération de l'état des joueurs : ${error.message}")
            }
        })
    }



    private fun determineFirstTurn() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)
        roomRef.child("creator").get().addOnSuccessListener {
            val creatorId = it.getValue(String::class.java)
            if (creatorId == playerId) {
                currentTurnPlayerId = playerId
                roomRef.child("currentTurn").setValue(playerId)
                // Le créateur commence à jouer
            } else {
                currentTurnPlayerId = creatorId
            }
            nombredeminjouer.visibility = View.VISIBLE
            startTimer()

        }.addOnFailureListener {
            Log.e("RoomDetailActivity", "Échec de la récupération du créateur : ${it.message}")
        }
    }
    private fun passTurnToNextPlayer() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)
        roomRef.child("players").get().addOnSuccessListener { snapshot ->
            val players = snapshot.children.mapNotNull { it.key }
            val currentIndex = players.indexOf(currentTurnPlayerId)
            val nextIndex = (currentIndex + 1) % players.size
            currentTurnPlayerId = players[nextIndex]
            roomRef.child("currentTurn").setValue(currentTurnPlayerId)
        }
    }
    private fun onImageClick(imageView: ImageView) {

        if (!isClickable || currentTurnPlayerId != playerId) return
        playSound(R.raw.explainer)


        val imagePosition = imageViews.indexOf(imageView)
        val imageResId = imageView.tag as? Int ?: return

        if (clickedImages.contains(imagePosition)) {
            return
        }

        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)
        roomRef.child("pairedImages").child(imagePosition.toString()).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Si l'image est appariée, on ne fait rien
                return@addOnSuccessListener
            }

            imageView.setImageResource(imageResId)
            clickedImages.add(imagePosition)
            roomRef.child("revealedImages").child(imagePosition.toString()).setValue(imageResId)



            // Afficher les images cliquées dans les ImageView dédiés
            if (clickedImages.size == 1) {
                im1.setImageResource(imageResId)
                roomRef.child("clickedImages").child("first").setValue(imageResId)
            } else if (clickedImages.size == 2) {
                im2.setImageResource(imageResId)
                roomRef.child("clickedImages").child("second").setValue(imageResId)
            }

            clickCount++
            if (clickCount >= 2) {
                val clickedImagesList = clickedImages.toList()
                val firstImageResId = imageViews[clickedImagesList[0]].tag as? Int
                val secondImageResId = imageViews[clickedImagesList[1]].tag as? Int

                if (firstImageResId == secondImageResId) {
                    val uniqueImageResId = R.drawable.drawablevret
                    roomRef.child("pairedImages").updateChildren(mapOf(
                        clickedImagesList[0].toString() to uniqueImageResId,
                        clickedImagesList[1].toString() to uniqueImageResId
                    ))

                    // Enlever `stroke_vert` des images appariées
                    for (position in clickedImagesList) {
                        val pairedImageView = imageViews[position]
                        val parentLayout = pairedImageView.parent as? RelativeLayout
                        pairedImageView.setImageResource(uniqueImageResId)
                        parentLayout?.setBackgroundResource(0) // Enlève le drawable de fond
                        pairedImageView.isClickable = false
                    }

                    updatePlayerScore()
                } else {
                    val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)


                    parentIm1.startAnimation(shakeAnimation)
                    parentIm2.startAnimation(shakeAnimation)

                    hideImagesWithDelay()
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    parentIm1.clearAnimation()
                    parentIm2.clearAnimation()


                }, 800)


                clickCount = 0
                clickedImages.clear()
                passTurnToNextPlayer()
                roomRef.child("shouldFlipImages").setValue(true)
            }
        }.addOnFailureListener {
            Log.e("RoomDetailActivity", "Échec de la vérification des images appariées : ${it.message}")
        }

        // Appliquer le drawable `stroke_vert` au layout parent de l'image cliquée
        val parentLayout = imageView.parent as? RelativeLayout
        parentLayout?.setBackgroundResource(R.drawable.stroke_vert)
    }
    private fun setupRealtimeListeners() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)



        roomRef.child("revealedImages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (imageSnapshot in snapshot.children) {
                    val imagePosition = imageSnapshot.key?.toIntOrNull() ?: continue
                    val imageResId = imageSnapshot.getValue(Int::class.java) ?: continue

                    if (imagePosition in imageViews.indices) {
                        imageViews[imagePosition].setImageResource(imageResId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération des images révélées : ${error.message}")
            }
        })

        roomRef.child("pairedImages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (imageSnapshot in snapshot.children) {
                    val imagePosition = imageSnapshot.key?.toIntOrNull() ?: continue
                    val imageResId = imageSnapshot.getValue(Int::class.java) ?: continue

                    if (imagePosition in imageViews.indices) {
                        imageViews[imagePosition].setImageResource(imageResId)
                        // Désactiver le clic sur les images appariées
                        imageViews[imagePosition].isClickable = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération des images paires : ${error.message}")
            }
        })
        roomRef.child("pairsFound").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pairsFound = snapshot.getValue(Int::class.java) ?: 0
                pairsFoundTextView.text = "$pairsFound / 30"

                // Vérifier si toutes les paires ont été trouvées (ici 30 paires)
                if (pairsFound >= 30) {
                    endGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération du nombre de paires trouvées : ${error.message}")
            }
        })

        roomRef.child("currentTurn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentTurnPlayerId = snapshot.getValue(String::class.java)
                isClickable = currentTurnPlayerId == playerId
                // Réinitialiser le compteur de clics lorsque le tour change
                clickCount = 0

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération du tour actuel : ${error.message}")
            }
        })

        roomRef.child("shouldFlipImages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shouldFlipImages = snapshot.getValue(Boolean::class.java) ?: return
                if (shouldFlipImages) {
                    // Retourner toutes les images sauf les paires
                    roomRef.child("revealedImages").removeValue()
                    roomRef.child("pairedImages").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(pairedSnapshot: DataSnapshot) {
                            for (i in imageViews.indices) {
                                val imageView = imageViews[i]
                                val parentLayout = (imageView.parent as? RelativeLayout)
                                if (pairedSnapshot.child(i.toString()).exists().not()) {
                                    imageView.setImageResource(R.drawable.two_layer_drawable6) // Mettre `heloping` sur les images non appariées
                                    parentLayout?.setBackgroundResource(R.drawable.heloping5) // Mettre `heloping3` sur le parent
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération des images paires : ${error.message}")
                        }
                    })

                    // Réinitialiser le champ dans Firebase
                    roomRef.child("shouldFlipImages").setValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération du signal de retournement des images : ${error.message}")
            }
        })
    }
    private fun checkIfCreator() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)
        val database = FirebaseDatabase.getInstance().reference

        roomRef.child("creator").get().addOnSuccessListener { creatorSnapshot ->
            val creatorId = creatorSnapshot.getValue(String::class.java)
            if (creatorId != null) {
                // Vérifiez si l'utilisateur actuel est le créateur
                if (creatorId == playerId) {
                    // Si le joueur actuel est le créateur
                    database.child("users").child(creatorId).child("name").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(nameSnapshot: DataSnapshot) {
                            val creatorName = nameSnapshot.getValue(String::class.java)
                            opponentNameTextView2.text = creatorName ?: "Nom inconnu"
                            finishname2.text = creatorName ?: "Nom inconnu"
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération du nom du créateur : ${error.message}")
                        }
                    })

                    database.child("users").child(creatorId).child("imageUrl").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(imageUrlSnapshot: DataSnapshot) {
                            val imageUrl = imageUrlSnapshot.getValue(String::class.java)
                            Glide.with(this@RoomDetailActivity)
                                .load(imageUrl)
                                .into(hotephoto) // Assurez-vous d'avoir un ImageView pour l'image du créateur
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération de l'image du créateur : ${error.message}")
                        }
                    })

                    roomRef.child("players").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(playersSnapshot: DataSnapshot) {
                            for (playerSnapshot in playersSnapshot.children) {
                                val playerId = playerSnapshot.key
                                if (playerId != creatorId && playerId != null) {
                                    database.child("users").child(playerId).child("name").addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(nameSnapshot: DataSnapshot) {
                                            val opponentName = nameSnapshot.getValue(String::class.java)
                                            opponentNameTextView1.text = opponentName ?: "Nom inconnu"
                                            finishnme1.text = opponentName ?: "Nom inconnu"
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("RoomDetailActivity", "Échec de la récupération du nom du joueur : ${error.message}")
                                        }
                                    })

                                    database.child("users").child(playerId).child("imageUrl").addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(imageUrlSnapshot: DataSnapshot) {
                                            val imageUrl = imageUrlSnapshot.getValue(String::class.java)
                                            Glide.with(this@RoomDetailActivity)
                                                .load(imageUrl)
                                                .into(adversairphoto) // Assurez-vous d'avoir un ImageView pour l'image de l'adversaire
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("RoomDetailActivity", "Échec de la récupération de l'image de l'adversaire : ${error.message}")
                                        }
                                    })
                                    break // On suppose qu'il n'y a qu'un seul autre joueur
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération des joueurs : ${error.message}")
                        }
                    })

                } else {
                    // Si le joueur actuel n'est pas le créateur
                    database.child("users").child(creatorId).child("name").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(nameSnapshot: DataSnapshot) {
                            val creatorName = nameSnapshot.getValue(String::class.java)
                            opponentNameTextView1.text = creatorName ?: "Nom inconnu"
                            finishnme1.text = creatorName ?: "Nom inconnu"
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération du nom du créateur : ${error.message}")
                        }
                    })

                    database.child("users").child(creatorId).child("imageUrl").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(imageUrlSnapshot: DataSnapshot) {
                            val imageUrl = imageUrlSnapshot.getValue(String::class.java)
                            Glide.with(this@RoomDetailActivity)
                                .load(imageUrl)
                                .into(adversairphoto) // Assurez-vous d'avoir un ImageView pour l'image du créateur
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération de l'image du créateur : ${error.message}")
                        }
                    })

                    roomRef.child("players").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(playersSnapshot: DataSnapshot) {
                            for (playerSnapshot in playersSnapshot.children) {
                                val playerId = playerSnapshot.key
                                if (playerId != creatorId && playerId != null) {
                                    database.child("users").child(playerId).child("name").addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(nameSnapshot: DataSnapshot) {
                                            val opponentName = nameSnapshot.getValue(String::class.java)
                                            opponentNameTextView2.text = opponentName ?: "Nom inconnu"
                                            finishname2.text = opponentName ?: "Nom inconnu"
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("RoomDetailActivity", "Échec de la récupération du nom du joueur : ${error.message}")
                                        }
                                    })

                                    database.child("users").child(playerId).child("imageUrl").addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(imageUrlSnapshot: DataSnapshot) {
                                            val imageUrl = imageUrlSnapshot.getValue(String::class.java)
                                            Glide.with(this@RoomDetailActivity)
                                                .load(imageUrl)
                                                .into(hotephoto) // Assurez-vous d'avoir un ImageView pour l'image de l'adversaire
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("RoomDetailActivity", "Échec de la récupération de l'image de l'adversaire : ${error.message}")
                                        }
                                    })
                                    break // On suppose qu'il n'y a qu'un seul autre joueur
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("RoomDetailActivity", "Échec de la récupération des joueurs : ${error.message}")
                        }
                    })
                }
            }
        }.addOnFailureListener {
            Log.e("RoomDetailActivity", "Échec de la récupération du créateur : ${it.message}")
        }
    }
    private fun hideImagesWithDelay() {
        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Optionally update UI with countdown
            }

            override fun onFinish() {
                for (clickedImagePosition in clickedImages) {
                    val imageView = imageViews[clickedImagePosition]
                    val parentLayout = imageView.parent as? RelativeLayout
                    imageView.setImageResource(R.drawable.heloping5)
                    parentLayout?.setBackgroundResource(R.drawable.stroke_vert) // Ré-appliquer le drawable de fond pour les images non appariées
                }
            }
        }.start()
    }
    private fun playSound(resourceId: Int) {
        val mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }

    }


    private fun setupTurnIndicators() {
        // Assurez-vous que currentTurnPlayerId est récupéré et mis à jour
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)

        roomRef.child("currentTurn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentTurnPlayerId = snapshot.getValue(String::class.java)

                // Ne rien faire si currentTurnPlayerId est nul
                if (currentTurnPlayerId == null) {
                    return
                }

                if (currentTurnPlayerId == playerId) {
                    // C'est le tour du joueur actuel
                    hoteTurnIndicator.visibility = View.VISIBLE
                    createJumpAnimation(hoteTurnIndicator)

                    // Cacher et arrêter l'animation de l'indicateur de l'adversaire
                    adversaireTurnIndicator.visibility = View.INVISIBLE
                    adversaireTurnIndicator.clearAnimation()
                } else {
                    adversaireTurnIndicator.visibility = View.VISIBLE
                    createJumpAnimation(adversaireTurnIndicator)

                    // Cacher et arrêter l'animation de l'indicateur de l'hôte
                    hoteTurnIndicator.visibility = View.INVISIBLE
                    hoteTurnIndicator.clearAnimation()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération du tour actuel : ${error.message}")
            }
        })
    }
    private fun createJumpAnimation(view: View) {
        val animation = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, 0f)
        animation.duration = 1000 // Durée de l'animation en millisecondes
        animation.repeatCount = ObjectAnimator.INFINITE // Répéter l'animation indéfiniment
        animation.start()
    }
    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmer la déconnexion")
            .setMessage("Êtes-vous sûr de vouloir quitter le salon ?")
            .setPositiveButton("Oui") { _, _ ->
                // Si l'utilisateur confirme, déconnectez-le et envoyez le message
                handlePlayerExit()
            }
            .setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss() // Annulez la déconnexion
            }
            .create()
            .show()
    }
    private fun handlePlayerExit() {
        getOpponentId { opponentId ->
            if (opponentId != null) {
                // Envoyez un message à l'adversaire avec l'ID de l'adversaire
                sendExitMessageToFirebase(opponentId)

                // Déconnectez le joueur du salon
                disconnectPlayer()

                // Revenez à l'écran précédent ou fermez l'activité
                finish()
            } else {
                Log.e("HandlePlayerExit", "Impossible de récupérer l'ID de l'adversaire.")
            }
        }
    }
    private fun disconnectPlayer() {
       val database = FirebaseDatabase.getInstance().reference
       val roomRef = database.child("rooms").child(roomId)
       val playerRef = roomRef.child("players").child(playerId!!)

       // Supprimez le joueur du salon
       playerRef.removeValue().addOnSuccessListener {
           Log.d("Firebase", "Joueur déconnecté avec succès")

           // Vérifiez s'il reste des joueurs dans le salon
           roomRef.child("players").addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   if (!snapshot.exists()) {
                       // Il n'y a plus de joueurs, donc supprimez le salon
                       roomRef.removeValue().addOnSuccessListener {
                           Log.d("Firebase", "Salon supprimé car tous les joueurs sont partis")
                       }.addOnFailureListener { exception ->
                           Log.e("Firebase", "Échec de la suppression du salon : ${exception.message}")
                       }
                   }
               }

               override fun onCancelled(error: DatabaseError) {
                   Log.e("Firebase", "Échec de la vérification des joueurs restants : ${error.message}")
               }
           })
       }.addOnFailureListener { exception ->
           Log.e("Firebase", "Échec de la déconnexion du joueur : ${exception.message}")
       }
   }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showExitConfirmationDialog()
    }
    private fun sendExitMessageToFirebase(opponentId: String) {
        val roomId = roomId // Remplacez par l'ID de la salle
        val message = "Victoire!! Votre adversaire a quitté"

        val database = FirebaseDatabase.getInstance().reference
        val messageRef = database.child("rooms").child(roomId).child("messages")

        messageRef.setValue(message)
            .addOnSuccessListener {
                Log.d("Firebase", "Message envoyé avec succès")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Échec de l'envoi du message : ${exception.message}")
            }
    }
    private fun retrieveMessageFromFirebase() {
        val roomId = roomId // Remplacez par l'ID de la salle
        val database = FirebaseDatabase.getInstance().reference
        val messageRef = database.child("rooms").child(roomId).child("messages")

        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.getValue(String::class.java)

                if (message != null) {
                    quitterlayout.visibility = View.VISIBLE
                    quitter.text = message
                } else {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Échec de la récupération du message : ${error.message}")
            }
        })
    }
    private fun getOpponentId(callback: (String?) -> Unit) {
      val database = FirebaseDatabase.getInstance().reference
      val playersRef = database.child("rooms").child(roomId).child("players")

      playersRef.addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
              // Suppose que tu as deux joueurs dans la salle
              val opponentId = snapshot.children.map { it.key }.filter { it != playerId }.firstOrNull()
              callback(opponentId) // Appelle le callback avec l'ID de l'adversaire
          }

          override fun onCancelled(error: DatabaseError) {
              Log.e("Firebase", "Échec de la récupération de l'ID de l'adversaire : ${error.message}")
              callback(null) // Appelle le callback avec null en cas d'erreur
          }
      })
  }

    private fun updatePlayerScore() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)

        roomRef.child("scores").child(playerId!!).get().addOnSuccessListener { scoreSnapshot ->
            val currentScore = scoreSnapshot.getValue(Int::class.java) ?: 0
            val newScore = currentScore + 10 // Ajouter 10 points pour chaque paire trouvée
            roomRef.child("scores").child(playerId!!).setValue(newScore)
        }

        // Mettre à jour le compteur de paires trouvées dans la base de données
        roomRef.child("pairsFound").get().addOnSuccessListener { snapshot ->
            val currentPairsFound = snapshot.getValue(Int::class.java) ?: 0
            val updatedPairsFound = currentPairsFound + 1
            roomRef.child("pairsFound").setValue(updatedPairsFound).addOnSuccessListener {
                pairsFoundTextView.text = "$updatedPairsFound / 30"

                // Vérifier si toutes les paires ont été trouvées (ici 30 paires)
                if (updatedPairsFound >= 30) {
                    endGame()
                }
            }
        }
    }
    fun endGame(){
        layoutermine.visibility = View.VISIBLE
    }
    private fun setupEmotionListener() {
        val roomId = roomId // Remplace par l'ID de la salle
        val database = FirebaseDatabase.getInstance().reference
        val emotionsRef = database.child("rooms").child(roomId).child("emotions")
        emotionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Effacez les images précédentes avant de mettre à jour
                hoteimage.setImageResource(0)
                adversaireImage.setImageResource(0)

                for (child in snapshot.children) {
                    val playerId = child.key
                    val emotion = child.getValue(String::class.java)

                    if (playerId != null && emotion != null) {
                        // Vérifier si le joueur qui a envoyé l'émotion est le joueur connecté
                        if (playerId == this@RoomDetailActivity.playerId) { // Remplace 'playerId' par l'ID du joueur connecté
                            updateEmotionDisplay(emotion, isCurrentPlayer = true)
                        } else {
                            updateEmotionDisplay(emotion, isCurrentPlayer = false)
                        }
                    }
                }

                // Ajouter un délai de 2 secondes avant de supprimer les images
                resetEmotionTimer()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Échec de la récupération des émotions: ${error.message}")
            }
        })
    }
    private fun startTimer() {
        startTime = System.currentTimeMillis()
        handler.post(updateTimeRunnable)
    }
    private fun updateEmotionDisplay(emotion: String, isCurrentPlayer: Boolean) {
        when (emotion) {
            "sadd" -> if (isCurrentPlayer) {
                hoteimage.setImageResource(R.drawable.sadd) // Afficher l'image pour le joueur connecté
            } else {
                adversaireImage.setImageResource(R.drawable.sadd) // Afficher l'image pour l'adversaire
            }
            "happy" -> if (isCurrentPlayer) {
                hoteimage.setImageResource(R.drawable.cry) // Afficher l'image pour le joueur connecté
            } else {
                adversaireImage.setImageResource(R.drawable.cry)
            }// Afficher l'image pour l'adversaire
            "coler" -> if (isCurrentPlayer) {
                hoteimage.setImageResource(R.drawable.colererb) // Afficher l'image pour le joueur connecté
            }else{
                adversaireImage.setImageResource(R.drawable.colererb) // Afficher l'image pour le joueur connecté
            }
            "oh" -> if (isCurrentPlayer){
                hoteimage.setImageResource(R.drawable.ohnon) // Afficher l'image pour le joueur connecté
            } else {
                adversaireImage.setImageResource(R.drawable.ohnon) // Afficher l'image pour l'adversaire
            }
            "febr" -> if (isCurrentPlayer){
                hoteimage.setImageResource(R.drawable.febr) // Afficher l'image pour le joueur connecté
            } else {
                adversaireImage.setImageResource(R.drawable.febr) // Afficher l'image pour l'adversaire
            }
            "npi" -> if (isCurrentPlayer){
                hoteimage.setImageResource(R.drawable.npi) // Afficher l'image pour le joueur connecté
            } else {
                adversaireImage.setImageResource(R.drawable.npi) // Afficher l'image pour l'adversaire
            }
            "angry" -> if (isCurrentPlayer){
                hoteimage.setImageResource(R.drawable.angry) // Afficher l'image pour le joueur connecté
            } else {
                adversaireImage.setImageResource(R.drawable.angry) // Afficher l'image pour l'adversaire
            }

            // Ajoute d'autres émotions si nécessaire
        }
    }
    private fun setupEmotionClickListener() {
        rougecolere.setOnClickListener {
            handleEmotionClick("sadd") // Exemple avec 'happy'
        }

        emotionHapp.setOnClickListener {
            handleEmotionClick("happy") // Exemple avec 'angry'
        }
        oh.setOnClickListener {
            handleEmotionClick("oh")
        }

        febr.setOnClickListener {
            handleEmotionClick("febr")
        }
        npi.setOnClickListener {
            handleEmotionClick("npi")
        }
        angry.setOnClickListener {
            handleEmotionClick("angry")
        }
    }
    private fun handleEmotionClick(emotion: String) {
        sendEmotionToFirebase(emotion) // Envoyer l'émotion à Firebase
        resetEmotionTimer() // Réinitialiser le timer des émotions
    }
    private fun resetEmotionTimer() {
        // Annuler l'ancien timer s'il existe
        emotionHandler?.removeCallbacks(emotionRunnable!!)

        // Créer un nouveau Handler et Runnable pour effacer les images après 2 secondes
        emotionHandler = Handler(Looper.getMainLooper())
        emotionRunnable = Runnable {
            // Réinitialiser les images après 2 secondes
            hoteimage.setImageResource(0)
            adversaireImage.setImageResource(0)

            // Supprimer les émotions de Firebase pour permettre de nouvelles émotions
            val roomId = roomId // Remplace par l'ID de la salle
            val database = FirebaseDatabase.getInstance().reference
            val emotionsRef = database.child("rooms").child(roomId).child("emotions")
            emotionsRef.removeValue()
        }

        // Exécuter le Runnable après 2 secondes
        emotionHandler?.postDelayed(emotionRunnable!!, 2000) // 2000 millisecondes = 2 secondes
    }
    private fun updateScores() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("rooms").child(roomId)

        roomRef.child("scores").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var creatorScore = 0
                var opponentScore = 0

                for (scoreSnapshot in snapshot.children) {
                    val playerId = scoreSnapshot.key
                    val score = scoreSnapshot.getValue(Int::class.java) ?: 0

                    if (playerId == auth.currentUser?.uid) {
                        // Score du joueur actuel
                        creatorScore = score
                    } else {
                        // Score de l'adversaire
                        opponentScore = score
                    }
                }

                // Mettre à jour les TextView des scores en cours de jeu
                opponentScoreTextView2.text = creatorScore.toString()
                opponentScoreTextView1.text = opponentScore.toString()

                // Vérifiez si toutes les paires ont été trouvées
                roomRef.child("pairsFound").get().addOnSuccessListener { pairsFoundSnapshot ->
                    val pairsFound = pairsFoundSnapshot.getValue(Int::class.java) ?: 0

                    if (pairsFound >= 30) { // Vérifiez si toutes les paires ont été trouvées
                        // Mettre à jour le layout de fin de jeu avec les scores
                        hostScoreTextView.text = "$creatorScore"
                        opponentScoreTextView.text = "$opponentScore"


                        layoutermine.visibility = View.VISIBLE
                        bonhomme.visibility = View.VISIBLE
                    }
                }.addOnFailureListener {
                    Log.e("RoomDetailActivity", "Échec de la récupération des paires trouvées : ${it.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomDetailActivity", "Échec de la récupération des scores : ${error.message}")
            }
        })
    }
    private fun sendEmotionToFirebase(emotion: String) {
        val roomId = roomId // Remplace par l'ID de la salle
        val playerId = playerId // Remplace par l'ID du joueur actuel

        val database = FirebaseDatabase.getInstance().reference
        val emotionRef = database.child("rooms").child(roomId).child("emotions").child(playerId!!)

        emotionRef.setValue(emotion)
            .addOnSuccessListener {
                Log.d("Firebase", "Emotion envoyée avec succès")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Échec de l'envoi de l'émotion: ${exception.message}")
            }
    }

}