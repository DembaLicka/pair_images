package com.example.pair3

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.AnimatedImageDrawable
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.window.OnBackInvokedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.jgabrielfreitas.core.BlurImageView


class MainActivity2 : AppCompatActivity() {

    private lateinit var imageViews: Array<ImageView>
    private lateinit var relativeLayouts: Array<RelativeLayout>
    private var selectedIndexes = mutableListOf<Int>()
    private var isFirstClick = true
    lateinit var totalPair : TextView
    private lateinit var mediaPlayer: MediaPlayer
    lateinit var textViewTimer: TextView
    lateinit var countDownTimer: CountDownTimer
    var originalTextColor: Int = 0
    var scorer = 0
    lateinit var terminer : RelativeLayout
    lateinit var legumes : BlurImageView

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    lateinit var score : TextView
    private lateinit var scoreReference: DatabaseReference
    lateinit var relascore : RelativeLayout
    lateinit var deplacer : RelativeLayout
    lateinit var parentim1 : RelativeLayout
    lateinit var parentim2 : RelativeLayout
    lateinit var nombredeminutesjoue : TextView
    lateinit var flipInnimator : AnimatorSet
    lateinit var flipOutnimator : AnimatorSet
    private lateinit var auth: FirebaseAuth


    val imageIds = intArrayOf( R.drawable.m7, R.drawable.cerise2, R.drawable.m24
        , R.drawable.m20, R.drawable.m6, R.drawable.m23, R.drawable.m8,
        R.drawable.m17, R.drawable.m3 , R.drawable.m22 , R.drawable.m4 , R.drawable.m27,
        R.drawable.m19 ,R.drawable.soup2, R.drawable.m15,R.drawable.bol, R.drawable.m12,R.drawable.m2
        ,R.drawable.m26 ,R.drawable.m16,R.drawable.m29 ,R.drawable.panierr,R.drawable.m21,R.drawable.m9
        ,R.drawable.m18,R.drawable.m5,R.drawable.m25,R.drawable.m14,R.drawable.mmmm,R.drawable.m13)

    lateinit var gifImage : ImageView
    lateinit var seticon : ImageView
    lateinit var imageanimer : ImageView
    lateinit var gifJackpot : ImageView

    lateinit var chrono: TextView
    private var isAnimationComplete = false

    private var initialImages: List<Int> = emptyList()
    lateinit var textView : TextView
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
    lateinit var scortermine : TextView
    lateinit var playerPositionTextView : TextView
    lateinit var selectedUserScoreTextView : TextView
    lateinit var selectedUserDurationTextView : TextView
    private var pairImageResourceId: Int = 0
    lateinit var resultImageView : ImageView
    lateinit var miam : ImageView
    lateinit var tiret2 : ImageView
    lateinit var pluscind : TextView
    val totalTimeInMillis: Long = 3 * 60 * 1000 + 30 * 1000
    private var gameStartTimeMillis: Long = 0
    private var gameEndTimeMillis: Long = 0
    lateinit var blurSeekbar : SeekBar
    lateinit var consmere : RelativeLayout
    lateinit var layoutseekbar : RelativeLayout


    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        myId()

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

        myRecycler()
        retrievePlayersFromFirebase()
        google()

        terminer.visibility = View.INVISIBLE
        miam.visibility = View.INVISIBLE
        layoutseekbar.visibility = View.INVISIBLE
        seticon.setOnClickListener {
            if (layoutseekbar.isVisible){
                layoutseekbar.visibility = View.INVISIBLE
            }else{
                layoutseekbar.visibility = View.VISIBLE
            }


        }

        flipInnimator = AnimatorInflater.loadAnimator(this ,R.anim.fip_in) as AnimatorSet
        flipOutnimator = AnimatorInflater.loadAnimator(this ,R.anim.fip_out) as AnimatorSet

        flipOutnimator.setTarget(consmere)
        flipInnimator.setTarget(consmere)

        tiret2.setOnClickListener {
            if (consmere.rotationY==0f){
                flipOutnimator.start()
            }else{
                flipInnimator.start()
            }
        }



        shuflleImage()
        timeToSeeImage()
        logicGame()
        onFinishGame()
        aimantUnscrenn()

        blurSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                legumes.setBlur(progress) // Mettre à jour l'effet de floutage
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Rien à faire ici
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Rien à faire ici
            }
        })

        // Définir une valeur initiale pour le SeekBar
        blurSeekbar.progress = 1 // Valeur initiale de floutage (ajustez selon vos besoins)
    }




    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this , MainActivity4::class.java)
        startActivity(intent)
        finish()
    }
    private fun flipLayout() {
        val flipStart = AnimationUtils.loadAnimation(this, R.anim.fip_in)
        val flipEnd = AnimationUtils.loadAnimation(this, R.anim.fip_out)

        flipStart.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // Rien à faire ici
            }

            override fun onAnimationEnd(animation: Animation) {
                consmere.startAnimation(flipEnd)
            }

            override fun onAnimationRepeat(animation: Animation) {
                // Rien à faire ici
            }
        })

        consmere.startAnimation(flipStart)
    }

    private fun aimantUnscrenn() {
        val gifimage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ContextCompat.getDrawable(this, R.drawable.aimantunscreen) as? AnimatedImageDrawable
        } else {
            TODO("VERSION.SDK_INT < P")
        }
        gifImage.setImageDrawable(gifimage)
        gifimage?.start()

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
        }
    }
    private fun timeToSeeImage() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                chrono.text = "$secondsLeft"
            }

            override fun onFinish() {
                chrono.text = ""
                //  legumes.visibility = View.VISIBLE
                isAnimationComplete = true // Marquer l'animation comme complète
                // Réactiver les clics sur toutes les images une fois l'animation terminée
                for (imageView in imageViews) {
                    imageView.isClickable = true

                }
                for (relativeLayout in relativeLayouts) {
                    relativeLayout.isClickable = true
                }
                // Après le compte à rebours, retournez toutes les images vers l'image "jok"
                for (imageView in imageViews) {
                    imageView.setImageResource(R.drawable.heloping2)
                }
            }
        }.start()

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
        textViewTimer.text = "00:00" // Afficher "00:00" lorsque la minuterie est terminée

        for (imageView in imageViews) {
            imageView.isClickable = false
        }
        for (relativeLayout in relativeLayouts) {
            relativeLayout.isClickable = false
        }

        // Mettre à jour Firebase avec le score et le temps de jeu
        updateScoreAndTimeInFirebase(scorer, timePlayedText)
        nombredeminutesjoue.text = timePlayedText

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
                        playerPositionTextView.text = positionText
                    } else {
                        playerPositionTextView.text = "Joueur non trouvé"
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
    private fun myRecycler() {
        userliste = ArrayList()
        adapter = MyAdapter(userliste)
        userRecyclerView.layoutManager = LinearLayoutManager(this ,LinearLayoutManager.HORIZONTAL,false)
        userRecyclerView.adapter = adapter

    }

    private fun logicGame() {
        for (i in 0 until 60) {
            val imageView = imageViews[i]
            val relativeLayout = relativeLayouts[i]
            imageView.setOnClickListener {
                if (isAnimationComplete && relativeLayout.isClickable && !selectedIndexes.contains(i)) {
                    if (firstIndex == -1) {
                        firstIndex = i
                        val drawable1 = ContextCompat.getDrawable(this, initialImages[firstIndex])
                        im1.setImageDrawable(drawable1)
                    } else if (secondIndex == -1) {
                        secondIndex = i
                        val drawable2 = ContextCompat.getDrawable(this, initialImages[secondIndex])
                        im2.setImageDrawable(drawable2)

                        firstIndex = -1
                        secondIndex = -1

                        Handler(Looper.getMainLooper()).postDelayed({
                            im1.setImageDrawable(null)
                            im2.setImageDrawable(null)
                        }, 800)
                    }

                    val initialImageId = initialImages[i]
                    imageView.setImageResource(initialImageId)
                    selectedIndexes.add(i)
                    playSound(R.raw.explainer) // Jouer un son au clic
                    if (selectedIndexes.size == 2) {
                        val firstImageId = initialImages[selectedIndexes[0]]
                        val secondImageId = initialImages[selectedIndexes[1]]
                        if (firstImageId == secondImageId) {

                            playSound(R.raw.casino)

                            scorer += 5
                            score.text = scorer.toString()
                            scortermine.text = scorer.toString()

                            pairImageResourceId = initialImages[selectedIndexes[0]]

                            val resultDrawable = ContextCompat.getDrawable(this, pairImageResourceId)
                            resultImageView.setImageDrawable(resultDrawable)

                            miam.visibility =  View.VISIBLE
                            miam.setImageResource(R.drawable.yam)
                            layoutpluscinq.visibility = View.VISIBLE
                            pluscind.text ="+5"

                            val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                            resultImageView.startAnimation(zoomInAnimation)
                            animateImageView()
                            miam.visibility =  View.INVISIBLE



                            for (imageView in imageViews) {
                                imageView.isClickable = false
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                removePair()
                                for (imageView in imageViews) {
                                    imageView.isClickable = true
                                }

                                pairsFound += 2 // Update pairs found count
                                if (pairsFound == 120) {
                                    countDownTimer.cancel()
                                    endGame(false)
                                }
                            }, 800)
                        } else {
                            val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake)
                            parentim1.startAnimation(shakeAnimation)
                            parentim2.startAnimation(shakeAnimation)

                            for (imageView in imageViews) {
                                imageView.isClickable = false
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                im1.setImageDrawable(null)
                                im2.setImageDrawable(null)
                                parentim1.clearAnimation()
                                parentim2.clearAnimation()
                                imageView.setImageResource(R.drawable.heloping2)
                                val firstImageView = imageViews[selectedIndexes[0]]
                                firstImageView.setImageResource(R.drawable.heloping2)

                                selectedIndexes.clear()
                                for (layout in relativeLayouts) {
                                    layout.setBackgroundResource(R.drawable.two_layer_drawable)
                                }
                                for (imageView in imageViews) {
                                    imageView.isClickable = true
                                }
                            }, 800)
                        }
                    }
                    relativeLayout.setBackgroundResource(R.drawable.cornelay)
                }
            }
        }
    }
    private fun animateImageView() {
        val moveUp = AnimationUtils.loadAnimation(this, R.anim.upanimation)
        miam.startAnimation(moveUp)
    }
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
    private fun animateGradient(textView: TextView) {
        val animator = ObjectAnimator.ofInt(textView, "textColor", 0xff0000, 0xff0000)
        animator.duration = 4000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.setEvaluator(ArgbEvaluator())
        animator.start()
    }
    private fun removePair() {
        for (index in selectedIndexes) {
            pairsFound++
            totalPair.text = "$pairsFound/120"
            val imageView = imageViews[index]
            val relativeLayout = relativeLayouts[index]
            relativeLayout.isClickable = false
            imageView.visibility = View.INVISIBLE
            relativeLayout.setBackgroundResource(R.drawable.two_layer_drawable)
            relativeLayout.alpha = 0.3f



        }
        selectedIndexes.clear()
        isFirstClick = true


    }
    private fun playSound(resourceId: Int) {
        val mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
    private fun updateScoreInFirebase(newScore: Int) {
        scoreReference.setValue(newScore)
    }

    private fun myId(){
        textViewTimer = findViewById(R.id.chronometre)
        gifImage = findViewById(R.id.gif)
        gifJackpot = findViewById(R.id.jackpot)
        eclair = findViewById(R.id.eclair)
        scortermine = findViewById(R.id.scortermine)
        im1 = findViewById(R.id.im1)
        im2 = findViewById(R.id.im2)
        textView = findViewById(R.id.myTextView)
        totalPair = findViewById(R.id.totalpair)
        score = findViewById(R.id.scor)
        animateGradient(textView)
        tiret2 = findViewById(R.id.tiret2)
        seticon = findViewById(R.id.seticon)
        layoutseekbar = findViewById(R.id.layoutseekbar)
        consmere = findViewById(R.id.consmere)
        miam = findViewById(R.id.miam)

        blurSeekbar = findViewById(R.id.blurseekbar)
        parentim1 = findViewById(R.id.parentim1)
        resultImageView = findViewById(R.id.truee)
        parentim2 = findViewById(R.id.parentim2)
        rejouer = findViewById(R.id.rejouer5)
        pluscind = findViewById(R.id.plus5)
        layoutpluscinq = findViewById(R.id.layoutpluscinq)
        legumes = findViewById(R.id.legumes)
        pairtrouvelayout = findViewById(R.id.pairtrouvelayout)
        nombredeminutesjoue =  findViewById(R.id.nombredeminutesjoue)
        chrono = findViewById(R.id.chrono)
        terminer = findViewById(R.id.terminer)
        loadingImage = findViewById(R.id.lotti)
        userRecyclerView = findViewById(R.id.recycler)
        playerPositionTextView = findViewById(R.id.rang)
        selectedUserScoreTextView = findViewById(R.id.mosecondscore)
        selectedUserDurationTextView = findViewById(R.id.duree2)
        relascore = findViewById(R.id.relascore2)


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

