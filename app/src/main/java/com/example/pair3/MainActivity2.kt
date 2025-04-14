package com.example.pair3

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jgabrielfreitas.core.BlurImageView


class MainActivity2 : AppCompatActivity() {

    private lateinit var imageViews: Array<ImageView>
    private lateinit var relativeLayouts: Array<RelativeLayout>
    private var selectedIndexes = mutableListOf<Int>()
    private var isFirstClick = true
    lateinit var totalPair : TextView
    var mediaPlayer: MediaPlayer? = null
    lateinit var textViewTimer: TextView
    lateinit var countDownTimer: CountDownTimer
    var originalTextColor: Int = 0
    var scorer = 0
    lateinit var terminer : RelativeLayout
    lateinit var fiinish : ImageView
    private var isMuted = false


    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    lateinit var score : TextView
    private lateinit var scoreReference: DatabaseReference
    lateinit var relascore : RelativeLayout
    lateinit var laychro : RelativeLayout
    lateinit var parentim1 : RelativeLayout
    lateinit var parentim2 : RelativeLayout
    lateinit var flipInnimator : AnimatorSet
    lateinit var flipOutnimator : AnimatorSet
    private lateinit var auth: FirebaseAuth
    private val initialBackgrounds = mutableListOf<Int>() // Stockage des backgrounds initiaux
    private val initialImagesBackup = mutableListOf<Int>() // Stocke les images initiales
    private val initialLayoutsBackgrounds = mutableListOf<Int>()
    val currentLayoutsBackgrounds = mutableListOf<Int>()

    val imageIds = intArrayOf( R.drawable.l4, R.drawable.l51,R.drawable.l5
        , R.drawable.l12, R.drawable.l57, R.drawable.l44, R.drawable.l50,
        R.drawable.l38, R.drawable.l36 , R.drawable.m22 , R.drawable.l43 , R.drawable.l46,
        R.drawable.m19 ,R.drawable.l49, R.drawable.l32,R.drawable.l8, R.drawable.l7,R.drawable.l47
        ,R.drawable.l55 ,R.drawable.m16,R.drawable.l40 ,R.drawable.l1,R.drawable.l37,R.drawable.l39
        ,R.drawable.m18,R.drawable.l35,R.drawable.l3,R.drawable.l34,R.drawable.l41,R.drawable.l48)

    lateinit var gifImage : ImageView
    lateinit var gifImage2 : ImageView
    lateinit var gifJackpot : ImageView
    lateinit var rescusite : RelativeLayout

    private val normalDrawable: Drawable? by lazy { ContextCompat.getDrawable(this, R.drawable.two_layer_drawable8) }
    private val strokeVertDrawable: Drawable? by lazy { ContextCompat.getDrawable(this, R.drawable.stroke) }

    lateinit var chrono: TextView
    private var isAnimationComplete = false

    private var initialImages: List<Int> = emptyList()
    private var pairsFound = 0

    private var firstIndex = -1
    private var secondIndex = -1

    lateinit var im1 : BlurImageView
    lateinit var im2 : BlurImageView

    lateinit var userRecyclerView: RecyclerView
    lateinit var adapter: MyAdapter
    lateinit var userliste : ArrayList<User>
    private lateinit var mDatabase: DatabaseReference
    lateinit var pairtrouvelayout : RelativeLayout
    lateinit var layoutpluscinq : RelativeLayout
    lateinit var rejouer : RelativeLayout

    lateinit var loadingImage: LottieAnimationView
    lateinit var eclair : RelativeLayout
    lateinit var nombredetoile : TextView
    lateinit var selectedUserScoreTextView : TextView
    lateinit var selectedUserDurationTextView : TextView
    private var pairImageResourceId: Int = 0
    lateinit var resultImageView : ImageView
    lateinit var miam : ImageView

    lateinit var pluscind : TextView
    val totalTimeInMillis: Long = 3 * 60 * 1000 + 30 * 1000
    private var gameStartTimeMillis: Long = 0
    private var gameEndTimeMillis: Long = 0

    lateinit var consmere : RelativeLayout
    lateinit var etoileLayout : RelativeLayout
    lateinit var mute : RelativeLayout
    lateinit var pasdeconnection : TextView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        myId()
        mute = findViewById(R.id.arretson)
        mute.setOnClickListener {
            toggleMute()
        }

        pasdeconnection =  findViewById(R.id.pasdeconnection)


       // etoileLayout.visibility = View.INVISIBLE

        mediaPlayer = MediaPlayer.create(this, R.raw.explainer)
        originalTextColor = textViewTimer.currentTextColor
        loadingImage.visibility = View.VISIBLE
        relascore.visibility = View.INVISIBLE
        layoutpluscinq.visibility = View.INVISIBLE

        val textView = findViewById<TextView>(R.id.name)

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            textView.text = "Welcome, " + userName
        } else {
            // Handle the case where the user is not signed in
        }

        rejouer.setOnClickListener {
            val intent = Intent(this , MainActivity4::class.java)
            startActivity(intent)
            finish()

        }

        retrievePlayersFromFirebase()
        google()

        terminer.visibility = View.INVISIBLE
        miam.visibility = View.INVISIBLE

        flipInnimator = AnimatorInflater.loadAnimator(this ,R.anim.fip_in) as AnimatorSet
        flipOutnimator = AnimatorInflater.loadAnimator(this ,R.anim.fip_out) as AnimatorSet

        flipOutnimator.setTarget(consmere)
        flipInnimator.setTarget(consmere)

        shuflleImage()
        timeToSeeImage()
        logicGame()
        onFinishGame()
        aimantUnscrenn()
        aimantUnscrenn2()
        checkConnection()

        val itemList = listOf(
            ItemModel(R.drawable.derriere, "Licka"),
            ItemModel(R.drawable.derrieree3, "Mara"),
            ItemModel(R.drawable.ideojeu, "Cheikh"),
            ItemModel(R.drawable.derriere2, "Amy"),
            ItemModel(R.drawable.rounde_exepmle, "Ndio")
        )

        // Configurer le RecyclerView en mode horizontal
        userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userRecyclerView.adapter = ItemAdapter(itemList)

        rescusite.setOnClickListener {
            showPairHint()
        }

    }

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }
    private fun checkConnection() {
        if (isConnectedToInternet(this)) {
           // textView.visibility = View.INVISIBLE
        } else {
            loadingImage.visibility = View.GONE
            pasdeconnection.visibility = View.VISIBLE
            pasdeconnection.text = "Vérifiez votre connexion ⚠"
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this , MainActivity4::class.java)
        startActivity(intent)
        finish()
    }
    private fun aimantUnscrenn() {
        val gifimage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ContextCompat.getDrawable(this, R.drawable.box) as? AnimatedImageDrawable
        } else {
            TODO("VERSION.SDK_INT < P")
        }
        gifImage.setImageDrawable(gifimage)
        gifimage?.start()

    }
    private fun aimantUnscrenn2() {
        val gifimage2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ContextCompat.getDrawable(this, R.drawable.eclir) as? AnimatedImageDrawable
        } else {
            TODO("VERSION.SDK_INT < P")
        }
        gifImage2.setImageDrawable(gifimage2)
        gifimage2?.start()
    }

    private fun google() {
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
        // À l'intérieur de votre fonction onCreate après l'initialisation de votre TextView "moncodeperso"
    }
    private fun onFinishGame() {
        gameStartTimeMillis = System.currentTimeMillis() // Enregistre le temps de début du jeu

        countDownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / (1000 * 60)
                val seconds = (millisUntilFinished / 1000) % 60

                // Formater le temps restant et l'afficher sur le TextView
                val timerText = String.format("-%2d:%02ds", minutes, seconds)
                textViewTimer.text = timerText

                if (minutes == 1L && seconds == 0L) {
                    // blinkAnimation()
                }
            }

            override fun onFinish() {
                endGame(true)
            }
        }.start() // Démarrer la minuterie
    }
    private fun endGame(isTimerFinished: Boolean) {
        // Enregistrer le temps de fin du jeu
        gameEndTimeMillis = System.currentTimeMillis()

        // Calculer le temps joué en millisecondes
        val elapsedTimeInMillis = gameEndTimeMillis - gameStartTimeMillis

        // Convertir le temps joué en minutes et secondes
        val minutesPlayed = elapsedTimeInMillis / (1000 * 60)
        val secondsPlayed = (elapsedTimeInMillis / 1000) % 60

        // Formater le temps joué
        val timePlayedText = String.format("%d:%02ds", minutesPlayed, secondsPlayed)

        // Action à effectuer lorsque la minuterie est terminée ou toutes les paires sont trouvées
        terminer.visibility = View.VISIBLE
      //  fiinish.visibility = View.VISIBLE
      //  etoileLayout.visibility = View.VISIBLE

        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_2)
        terminer.startAnimation(slideDownAnimation)
       // fiinish.startAnimation(slideDownAnimation)
       // etoileLayout.startAnimation(slideDownAnimation)

        textViewTimer.text = "00:00" // Afficher "00:00" lorsque la minuterie est terminée

        for (imageView in imageViews) {
            imageView.isClickable = false
        }
        for (relativeLayout in relativeLayouts) {
            relativeLayout.isClickable = false
        }

        // Mettre à jour Firebase avec le score et le temps de jeu
        updateScoreAndTimeInFirebase(scorer, timePlayedText)

        if (isTimerFinished) {
        } else {
        }
    }
    private fun updateScoreAndTimeInFirebase(newScore: Int, timePlayed: String) {
        val currentUser = mAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = mDatabase.child(userId)

            // Mettre à jour le score et le temps de jeu dans Firebase
            userRef.child("score").setValue(newScore)
            userRef.child("timePlayed").setValue(timePlayed)
        }
    }
    private fun retrievePlayersFromFirebase() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference.child("users")

        val currentUser = mAuth.currentUser
        currentUser?.let { user ->
            val userId = user.displayName

            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userliste.clear()

                    for (userSnapshot in snapshot.children) {
                        val userIdFromFirebase = userSnapshot.key
                        val userName = userSnapshot.child("name").getValue(String::class.java)
                        val userScore = userSnapshot.child("score").getValue(Int::class.java) ?: 0
                        val userTimePlayed = userSnapshot.child("timePlayed").getValue(String::class.java) ?: "0:00s"
                        val photoUrl = userSnapshot.child("imageUrl").getValue(String::class.java) ?: ""

                        if (userIdFromFirebase != userId) {
                            userName?.let { userName ->
                                val user = User(userName, userScore, userTimePlayed, photoUrl)
                                userliste.add(user)
                            }
                        }
                    }

                    userliste.sortByDescending { it.score }

                    adapter.notifyDataSetChanged()
                    loadingImage.visibility = View.GONE

                    val currentUserIndex = userliste.indexOfFirst { it.userName == userId }
                    if (currentUserIndex != -1) {
                        val positionText = "${currentUserIndex + 1}"
                    } else {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainActivity2", "Failed to read user data", error.toException())
                }
            })
        }

        currentUser?.let { user ->
            val userId = user.uid
            scoreReference = mDatabase.child(userId).child("score")

            scoreReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentScore = snapshot.getValue(Int::class.java) ?: 0
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainActivity2", "Failed to read score", error.toException())
                }
            })
        }
    }
    private fun showPairHint() {
        val hiddenPairs = mutableListOf<Pair<Int, Int>>()
        val seenImages = mutableMapOf<Int, Int>()

        for (i in initialImages.indices) {
            if (selectedIndexes.contains(i)) continue // Ignorer les images déjà trouvées
            val imageId = initialImages[i]
            if (seenImages.containsKey(imageId)) {
                val firstIndex = seenImages[imageId]!!
                hiddenPairs.add(Pair(firstIndex, i))
            } else {
                seenImages[imageId] = i
            }
        }

        if (hiddenPairs.isNotEmpty()) {
            val (firstIndex, secondIndex) = hiddenPairs.random() // Prendre une paire au hasard
            val firstRelativeLayout = relativeLayouts[firstIndex]
            val secondRelativeLayout = relativeLayouts[secondIndex]
            val firstImageView = imageViews[firstIndex]
            val secondImageView = imageViews[secondIndex]

            // Sauvegarder le fond original
            val originalBg1 = firstRelativeLayout.background
            val originalBg2 = secondRelativeLayout.background
            val originalImage1 = firstImageView.drawable
            val originalImage2 = secondImageView.drawable

            // Changer le fond en arrondi et l'image en gris
            firstRelativeLayout.setBackgroundResource(R.drawable.rounded_background)
            secondRelativeLayout.setBackgroundResource(R.drawable.rounded_background)

            // Rendre les images visibles temporairement
            firstImageView.setImageResource(initialImages[firstIndex])
            secondImageView.setImageResource(initialImages[secondIndex])

            // Appliquer la couleur souhaitée sur l'image
            firstImageView.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP)
            secondImageView.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP)

            // Ajouter un effet de clignotement
            val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
            firstRelativeLayout.startAnimation(blinkAnimation)
            secondRelativeLayout.startAnimation(blinkAnimation)
            firstImageView.startAnimation(blinkAnimation)
            secondImageView.startAnimation(blinkAnimation)

            // Restaurer le fond et l'image après 2 secondes
            Handler(Looper.getMainLooper()).postDelayed({
                firstRelativeLayout.clearAnimation()
                secondRelativeLayout.clearAnimation()
                firstImageView.clearAnimation()
                secondImageView.clearAnimation()
                firstRelativeLayout.background = originalBg1
                secondRelativeLayout.background = originalBg2
                firstImageView.setImageDrawable(originalImage1)
                secondImageView.setImageDrawable(originalImage2)
                firstImageView.clearColorFilter()
                secondImageView.clearColorFilter()
            }, 2000)
        }
    }


    private fun logicGame() {
        val strokeVertDrawable = ContextCompat.getDrawable(this, R.drawable.two_layer_drawable8)
        val normalDrawable = ContextCompat.getDrawable(this, R.drawable.two_layer_drawable8)
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        var firstRelativeLayout: RelativeLayout? = null
        var secondRelativeLayout: RelativeLayout? = null

        for (i in 0 until 60) {
            val imageView = imageViews[i]
            val relativeLayout = relativeLayouts[i]

            imageView.setOnClickListener {
                if (isAnimationComplete && relativeLayout.isClickable && !selectedIndexes.contains(i)) {

                    // Animation flip d'ouverture
                    val rotateOut = ObjectAnimator.ofFloat(relativeLayout, "rotationY", 0f, 90f)
                    val rotateIn = ObjectAnimator.ofFloat(relativeLayout, "rotationY", -90f, 0f)
                    rotateOut.duration = 150
                    rotateIn.duration = 150

                    rotateOut.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            val initialImageId = initialImages[i]
                            imageView.setImageResource(initialImageId)
                            rotateIn.start()
                        }
                    })
                    rotateOut.start()

                    if (firstIndex == -1) {
                        firstIndex = i
                        firstRelativeLayout = relativeLayout
                        val drawable1 = ContextCompat.getDrawable(this, initialImages[firstIndex])
                        im1.setImageDrawable(drawable1)
                    } else if (secondIndex == -1) {
                        secondIndex = i
                        secondRelativeLayout = relativeLayout
                        val drawable2 = ContextCompat.getDrawable(this, initialImages[secondIndex])
                        im2.setImageDrawable(drawable2)

                        firstIndex = -1
                        secondIndex = -1

                        Handler(Looper.getMainLooper()).postDelayed({
                            im1.setImageDrawable(null)
                            im2.setImageDrawable(null)
                        }, 800)
                    }

                    selectedIndexes.add(i)
                    playSound(R.raw.explainer)

                    if (selectedIndexes.size == 2) {
                        val firstImageId = initialImages[selectedIndexes[0]]
                        val secondImageId = initialImages[selectedIndexes[1]]

                        if (firstImageId == secondImageId) {
                            playSound(R.raw.casino)
                            scorer += 5
                            score.text = scorer.toString()

                            pairImageResourceId = firstImageId
                            val resultDrawable = ContextCompat.getDrawable(this, pairImageResourceId)
                            resultImageView.setImageDrawable(resultDrawable)

                            miam.visibility = View.VISIBLE
                            miam.setImageResource(R.drawable.cinq)
                            layoutpluscinq.visibility = View.VISIBLE
                            pluscind.text = "+5"

                            val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                            resultImageView.startAnimation(zoomInAnimation)
                            animateImageView()
                            miam.visibility = View.INVISIBLE

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, 10))
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(100)
                            }

                            for (imageView in imageViews) {
                                imageView.isClickable = false
                            }

                            Handler(Looper.getMainLooper()).postDelayed({
                                removePair()
                                for (imageView in imageViews) {
                                    imageView.isClickable = true
                                }

                                pairsFound += 2
                                if (pairsFound == 120) {
                                    countDownTimer.cancel()
                                    endGame(false)
                                }
                            }, 800)
                        } else {
                            // Pas une paire → animation shake + flip retour
                            val firstIndexLocal = selectedIndexes[0]
                            val secondIndexLocal = selectedIndexes[1]

                            val firstLayout = relativeLayouts[firstIndexLocal]
                            val secondLayout = relativeLayouts[secondIndexLocal]

                            val firstImage = imageViews[firstIndexLocal]
                            val secondImage = imageViews[secondIndexLocal]

                            for (iv in imageViews) iv.isClickable = false

                            Handler(Looper.getMainLooper()).postDelayed({
                                val flipOut1 = ObjectAnimator.ofFloat(firstLayout, "rotationY", 0f, 90f)
                                val flipIn1 = ObjectAnimator.ofFloat(firstLayout, "rotationY", -90f, 0f)
                                flipOut1.duration = 150
                                flipIn1.duration = 150

                                flipOut1.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        firstLayout.setBackgroundResource(initialLayoutsBackgrounds[firstIndexLocal])
                                        firstImage.setImageResource(initialImagesBackup[firstIndexLocal])
                                        flipIn1.start()
                                    }
                                })

                                // Flip retour deuxième layout
                                val flipOut2 = ObjectAnimator.ofFloat(secondLayout, "rotationY", 0f, 90f)
                                val flipIn2 = ObjectAnimator.ofFloat(secondLayout, "rotationY", -90f, 0f)
                                flipOut2.duration = 150
                                flipIn2.duration = 150

                                flipOut2.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        secondLayout.setBackgroundResource(initialLayoutsBackgrounds[secondIndexLocal])
                                        secondImage.setImageResource(initialImagesBackup[secondIndexLocal])
                                        flipIn2.start()
                                    }
                                })

                                flipOut1.start()
                                flipOut2.start()

                                selectedIndexes.clear()

                               /* for (j in relativeLayouts.indices) {
                                    relativeLayouts[j].setBackgroundResource(initialBackgrounds[j])

                                }

                                */



                                for (iv in imageViews) iv.isClickable = true

                            }, 800)
                        }
                    }
                }
            }
        }
    }
    private fun shuflleImage() {
        val imageIds = imageIds.toList().shuffled()
        val selectedImages = imageIds.subList(0, 30)

        val duplicatedImages = (selectedImages + selectedImages).toMutableList()
        duplicatedImages.shuffle()

        initialImages = duplicatedImages.toList() // Sauvegarde des images initiales

        for (i in 0 until 60) {
            val imageView = imageViews[i]
            val relativeLayout = relativeLayouts[i]

            imageView.setImageResource(duplicatedImages[i])
            relativeLayout.isClickable = false // Désactiver les clics sur tous les RelativeLayouts initialement

            // Sauvegarder l'arrière-plan initial du RelativeLayout
           // initialLayoutsBackgrounds.add((relativeLayout.background as? ColorDrawable)?.color ?: R.drawable.two_layer_drawable8)
            initialLayoutsBackgrounds.add(R.drawable.two_layer_drawable8)

        }
    }
    private fun timeToSeeImage() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                chrono.text = "$secondsLeft"
            }

            override fun onFinish() {
                chrono.text = "00"
                isAnimationComplete = true // L'animation d'affichage est terminée

                // Réactiver les clics
                for (imageView in imageViews) {
                    imageView.isClickable = true
                }
                for (relativeLayout in relativeLayouts) {
                    relativeLayout.isClickable = true
                }

                // Liste des backgrounds utilisés pour cacher les images
                val backgrounds = listOf(
                    R.drawable.two_layer_drawable9,
                    R.drawable.two_layer_drawable11
                )

                // Nettoyer les anciennes sauvegardes
                initialBackgrounds.clear()
                initialImagesBackup.clear()
                initialLayoutsBackgrounds.clear()
                currentLayoutsBackgrounds.clear()

                // Appliquer des backgrounds aléatoires pour cacher les images
                for (i in imageViews.indices) {
                    val parentLayout = imageViews[i].parent as? RelativeLayout
                    val randomBg = backgrounds.random()

                    parentLayout?.setBackgroundResource(randomBg)
                    imageViews[i].setImageResource(randomBg)

                    // Sauvegarder les backgrounds pour gestion future
                    initialBackgrounds.add(randomBg)
                    initialImagesBackup.add(randomBg)
                    initialLayoutsBackgrounds.add(randomBg)
                }
            }
        }.start()
    }

    /* private fun timeToSeeImage() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                chrono.text = "$secondsLeft"
            }

            override fun onFinish() {
                chrono.text = "00"
                isAnimationComplete = true // Marquer l'animation comme complète

                // Réactiver les clics sur toutes les images une fois l'animation terminée
                for (imageView in imageViews) {
                    imageView.isClickable = true
                }
                for (relativeLayout in relativeLayouts) {
                    relativeLayout.isClickable = true
                }

                // Liste des 9 backgrounds possibles
                val backgrounds = listOf(
                    R.drawable.two_layer_drawable9, R.drawable.two_layer_drawable11
                )

             //   initialBackgrounds.clear()
               // initialImagesBackup.clear() // Réinitialiser la liste des images

                currentLayoutsBackgrounds.clear()


                for (i in imageViews.indices) {
                    val parentLayout = imageViews[i].parent as? RelativeLayout
                    val randomBg = backgrounds.random() // Choisir un background aléatoire

                    // Sauvegarder le background initial du RelativeLayout

                    parentLayout?.setBackgroundResource(randomBg)
                    imageViews[i].setImageResource(randomBg)

                    initialBackgrounds.add(randomBg)
                    initialImagesBackup.add(randomBg)
                    initialLayoutsBackgrounds.add(randomBg) //
                   /*initialLayoutsBackgrounds.add(randomBg)

                    parentLayout?.setBackgroundResource(randomBg) // Appliquer au RelativeLayout
                    imageViews[i].setImageResource(randomBg) // Appliquer à l'ImageView aussi

                    initialBackgrounds.add(randomBg) // Enregistrer le background initial
                    initialImagesBackup.add(randomBg) // Enregistrer l'image initiale

                    */
                }
            }
        }.start()
    }

    */
    @SuppressLint("ResourceAsColor")
    private fun removePair() {
        for (index in selectedIndexes) {
            pairsFound++
            totalPair.text = "$pairsFound/120"
            val imageView = imageViews[index]
            val relativeLayout = relativeLayouts[index]
            relativeLayout.isClickable = false
            imageView.setImageResource(R.drawable.vertpersonne)
            relativeLayout.post {
                relativeLayout.setBackgroundResource(R.drawable.forme10)
            }


            relativeLayout.alpha = 0.9f
        }
        selectedIndexes.clear()
        isFirstClick = true


    }
    private fun animateImageView() {
        val moveUp = AnimationUtils.loadAnimation(this, R.anim.upanimation)
        miam.startAnimation(moveUp)
    }
    private fun playSound(resourceId: Int) {
        // Vérifie si les sons sont activés avant de jouer le son
        if (isMuted) return // Si le jeu est en mode muet, ne joue pas le son

        mediaPlayer?.release() // Libère la ressource précédente si elle existe

        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
        }
    }
    fun toggleMute() {
        isMuted = !isMuted // Bascule l'état du son
        val message = if (isMuted) "Sons désactivés" else "Sons activés"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    private fun myId(){
        textViewTimer = findViewById(R.id.chronometre)
        gifImage = findViewById(R.id.gif)
        gifImage2 = findViewById(R.id.eclir)
        gifJackpot = findViewById(R.id.jackpot)
        eclair = findViewById(R.id.eclair)
        im1 = findViewById(R.id.im1)
        im2 = findViewById(R.id.im2)
      //  textView = findViewById(R.id.myTextView)
        totalPair = findViewById(R.id.totalpair)
        score = findViewById(R.id.scor)
        rescusite = findViewById(R.id.rescusite)
       // animateGradient(textView)

        consmere = findViewById(R.id.consmere)
        miam = findViewById(R.id.miam)
        parentim1 = findViewById(R.id.parentim1)
        resultImageView = findViewById(R.id.truee)
        parentim2 = findViewById(R.id.parentim2)
        rejouer = findViewById(R.id.rejouer5)
        pluscind = findViewById(R.id.plus5)
        layoutpluscinq = findViewById(R.id.layoutpluscinq)
        pairtrouvelayout = findViewById(R.id.pairtrouvelayout)
        chrono = findViewById(R.id.chrono)
        terminer = findViewById(R.id.terminer)
        loadingImage = findViewById(R.id.lotti)
        userRecyclerView = findViewById(R.id.recycler)
        selectedUserScoreTextView = findViewById(R.id.mosecondscore)
        selectedUserDurationTextView = findViewById(R.id.duree2)
        relascore = findViewById(R.id.relascore2)
        laychro = findViewById(R.id.laychro)
        nombredetoile = findViewById(R.id.nombredetoile)


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

    }

}

