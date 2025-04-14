package com.example.pair3

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.random.Random


class activity_room_list : AppCompatActivity(), RoomAdapter.RoomClickListener {

    lateinit var myrecycler_room_list: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var adapter: RoomAdapter
    private val roomList = mutableListOf<Room>()
    private lateinit var auth: FirebaseAuth
    lateinit var r1 : ImageView
    lateinit var r5 : RelativeLayout
    lateinit var nombredesalons : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)



        r5 = findViewById(R.id.pp)
        nombredesalons = findViewById(R.id.nombredesalons)

        myrecycler_room_list = findViewById(R.id.myrecycler_room_list)
        database = FirebaseDatabase.getInstance().reference.child("rooms")
        auth = FirebaseAuth.getInstance()
        adapter = RoomAdapter(roomList, this)


        myrecycler_room_list.layoutManager = LinearLayoutManager(this)
        myrecycler_room_list.adapter = adapter

        fetchRooms()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Compte le nombre total de salles
                val roomCount = snapshot.childrenCount
                // Affiche le nombre total de salles
                nombredesalons.text = "$roomCount"
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer les erreurs ici si nécessaire
                nombredesalons.text = "Erreur de chargement"
            }
        })

    }

    private fun fetchRooms() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomList.clear()
                for (roomSnapshot in snapshot.children) {
                    val room = roomSnapshot.getValue(Room::class.java)
                    val roomId = roomSnapshot.key

                    // Vérifier si le gameState est "waiting"
                    val gameState = roomSnapshot.child("gameState").getValue(String::class.java)

                    if (room != null && roomId != null && gameState == "waiting") {
                        roomList.add(room.copy(id = roomId))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomListActivity", "Failed to retrieve rooms: ${error.message}")
                Toast.makeText(this@activity_room_list, "Failed to retrieve rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun isPlayerInAnotherRoom(playerId: String, callback: (Boolean) -> Unit) {
        database.child("rooms").orderByChild("players/$playerId").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Si le snapshot contient des données, cela signifie que le joueur est déjà dans un autre salon
                val isInAnotherRoom = snapshot.childrenCount > 0
                callback(isInAnotherRoom)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RoomListActivity", "Failed to check player's rooms: ${error.message}")
                callback(false)
            }
        })
    }

    override fun onRoomClick(roomId: String) {
        val playerId = auth.currentUser?.uid ?: return

        // Vérifie si le joueur est déjà dans un autre salon
        isPlayerInAnotherRoom(playerId) { isInAnotherRoom ->
            if (isInAnotherRoom) {
                Toast.makeText(this@activity_room_list, "Vous êtes déjà dans un autre salon", Toast.LENGTH_SHORT).show()
                return@isPlayerInAnotherRoom
            }

            // Si le joueur n'est pas dans un autre salon, poursuit la logique pour rejoindre le salon
            database.child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val players = snapshot.child("players").childrenCount
                    val isPlayerInRoom = snapshot.child("players").hasChild(playerId)

                    if (isPlayerInRoom) {
                        // Le joueur est déjà dans la salle, donc on ne l'ajoute pas à nouveau
                        val intent = Intent(this@activity_room_list, RoomDetailActivity::class.java)
                        intent.putExtra("ROOM_ID", roomId)
                        startActivity(intent)
                    } else if (players == 1L) {
                        // Un autre joueur rejoint la salle, ce qui fait deux joueurs au total
                        database.child(roomId).child("players").child(playerId).setValue(true)
                        database.child(roomId).child("gameState").setValue("playing")
                        startGame(roomId) // Démarrer le jeu
                    } else if (players < 2L) {
                        // Le salon n'est pas encore plein (moins de 2 joueurs), donc on ajoute ce joueur
                        database.child(roomId).child("players").child(playerId).setValue(true)
                        val intent = Intent(this@activity_room_list, RoomDetailActivity::class.java)
                        intent.putExtra("ROOM_ID", roomId)
                        startActivity(intent)
                    } else {
                        // Le salon est plein (2 joueurs), on ne peut pas ajouter ce joueur
                        Toast.makeText(this@activity_room_list, "Room is full", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@activity_room_list, "Failed to check room players", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun startGame(roomId: String) {
        // Code pour démarrer le jeu, par exemple en redirigeant vers une activité de jeu
        val intent = Intent(this@activity_room_list, RoomDetailActivity::class.java)
        intent.putExtra("ROOM_ID", roomId)
        startActivity(intent)
        finish() // Ferme l'activité actuelle si nécessaire
    }

    private fun animateViewRandomly(view: View) {
        // Obtenir les dimensions du parent
        val parentWidth = r5.width
        val parentHeight = r5.height

        // Générer des positions aléatoires
        val randomX = Random.nextInt(parentWidth - view.width)
        val randomY = Random.nextInt(parentHeight - view.height)

        // Créer des animations de translation pour les déplacer
        val animatorX = ObjectAnimator.ofFloat(view, "translationX", randomX.toFloat())
        val animatorY = ObjectAnimator.ofFloat(view, "translationY", randomY.toFloat())

        animatorX.interpolator = AccelerateDecelerateInterpolator()
        animatorY.interpolator = AccelerateDecelerateInterpolator()

        animatorX.duration = 1000 // durée en millisecondes
        animatorY.duration = 1000 // durée en millisecondes

        // Démarrer les animations
        animatorX.start()
        animatorY.start()
    }
}


