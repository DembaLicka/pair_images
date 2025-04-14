package com.example.pair3

import android.animation.AnimatorSet
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.jgabrielfreitas.core.BlurImageView

class MainActivity3 : AppCompatActivity() {

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
    lateinit var legumes : BlurImageView
    lateinit var miaw : ImageView

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    lateinit var score : TextView
    private lateinit var scoreReference: DatabaseReference
    lateinit var relascore : RelativeLayout
    lateinit var deplacer : RelativeLayout
    lateinit var laychro : RelativeLayout
    lateinit var parentim1 : RelativeLayout
    lateinit var framesoundoff : FrameLayout
    lateinit var parentim2 : RelativeLayout
    lateinit var nombredeminutesjoue : TextView
    lateinit var flipInnimator : AnimatorSet
    lateinit var flipOutnimator : AnimatorSet
    private lateinit var auth: FirebaseAuth


    val imageIds = intArrayOf( R.drawable.m7, R.drawable.cerise2, R.drawable.m24
        , R.drawable.m20, R.drawable.m6, R.drawable.m23, R.drawable.m8,
        R.drawable.m17, R.drawable.m3 , R.drawable.m22 , R.drawable.m4 , R.drawable.m27,
        R.drawable.m19 ,R.drawable.salad, R.drawable.m15,R.drawable.bol, R.drawable.m12,R.drawable.m2
        ,R.drawable.m26 ,R.drawable.m16,R.drawable.m29 ,R.drawable.pnier,R.drawable.m21,R.drawable.m9
        ,R.drawable.m18,R.drawable.m5,R.drawable.m25,R.drawable.m14,R.drawable.mmmm,R.drawable.m13)

    lateinit var gifImage : ImageView
    lateinit var gifImage2 : ImageView
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
    lateinit var nombredetoile : TextView
    lateinit var playerPositionTextView : TextView
    lateinit var selectedUserScoreTextView : TextView
    lateinit var selectedUserDurationTextView : TextView
    private var pairImageResourceId: Int = 0
    lateinit var resultImageView : ImageView
    lateinit var bohnomme : ImageView
    lateinit var miam : ImageView
    lateinit var etoile1 : ImageView
    lateinit var etoile2 : ImageView
    lateinit var etoile3 : ImageView
    lateinit var pluscind : TextView
    val totalTimeInMillis: Long = 3 * 60 * 1000 + 30 * 1000
    private var gameStartTimeMillis: Long = 0
    private var gameEndTimeMillis: Long = 0

    lateinit var consmere : RelativeLayout
    lateinit var etoileLayout : RelativeLayout
    lateinit var go : RelativeLayout
    lateinit var pasdeconnection : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

        myId()
        laychro = findViewById(R.id.laychro)
        chrono = findViewById(R.id.chrono)
        shuflleImage()
        timeToSeeImage()

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
                laychro.visibility = View.INVISIBLE
                //go.visibility = View.VISIBLE
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
                    val parentLayout = imageView.parent as? RelativeLayout
                    parentLayout?.setBackgroundResource(R.drawable.two_layer_drawable8)
                    imageView.setImageResource(R.drawable.two_layer_drawable8)
                }
            }
        }.start()

    }

}