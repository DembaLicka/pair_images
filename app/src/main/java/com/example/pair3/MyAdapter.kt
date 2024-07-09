package com.example.pair3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pair3.R
import com.example.pair3.User
import com.squareup.picasso.Picasso

class MyAdapter(private val userList: ArrayList<User>) : RecyclerView.Adapter<MyAdapter.UserViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val profil: ImageView = itemView.findViewById(R.id.profil)
        val couronne: ImageView = itemView.findViewById(R.id.couronne)
        val userScore: TextView = itemView.findViewById(R.id.user_score)

        init {
            itemView.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
        }

        fun bind(user: User) {
            usernameTextView.text = getFirstWord(user.userName)
            userScore.text = user.score.toString()

            // Charge la photo de profil avec Picasso
            if (user.PhotoUrl.isNotEmpty()) {
                Picasso.get().load(user.PhotoUrl).into(profil)
            } else {
                // Affiche une photo par défaut si aucune photo n'est disponible
                profil.setImageResource(R.drawable.chien)
            }

            // Affiche la couronne en fonction du classement
            when (adapterPosition) {
                0 -> couronne.setImageResource(R.drawable.couronne)
                1 -> couronne.setImageResource(R.drawable.couronne2)
                2 -> couronne.setImageResource(R.drawable.couronne3)
                else -> couronne.setImageResource(R.drawable.couronne4)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    // Fonction pour obtenir le premier mot de la chaîne
    private fun getFirstWord(sentence: String): String {
        return sentence.split(" ").firstOrNull() ?: sentence
    }

    // Ajoutez cette fonction pour obtenir le liste des utilisateurs
    fun getUserList(): List<User> {
        return userList
    }

    // Ajoutez cette fonction pour mettre à jour la liste des utilisateurs
    fun updateUserList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}
