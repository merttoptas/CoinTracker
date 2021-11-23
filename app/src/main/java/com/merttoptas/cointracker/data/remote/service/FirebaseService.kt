package com.merttoptas.cointracker.data.remote.service

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {

    fun setUser(userId: String, user: HashMap<String, Any>) {
        val ref = firebaseFirestore.collection("favoriteCoinsId").document(userId)
        ref.set(user).addOnSuccessListener(object : OnSuccessListener<Void> {
            override fun onSuccess(p0: Void?) {
            }
        })
    }

    fun insertFavoriteCoins(
        userId: String,
        coin: HashMap<String, Any>
    ): Pair<Boolean, String?>? {
        var data: Pair<Boolean, String>? = null

        firebaseFirestore.collection("users").document(userId).collection("favoriteCoinsId")
            .let { collectionReference ->
                collectionReference.document(coin["id"].toString())
                    .set(coin, SetOptions.merge())
                    .addOnSuccessListener { data = Pair(true, "") }
                    .addOnFailureListener { data = Pair(false, it.message.toString()) }
            }

        return data
    }

    fun deleteFavoriteCoin(userId: String, coin: HashMap<String, Any>): Pair<Boolean, String?>? {
        var data: Pair<Boolean, String>? = null

        firebaseFirestore.collection("users").document(userId).collection("favoriteCoinsId")
            .let { collectionReference ->
                collectionReference.document(coin["id"].toString())
                    .delete()
                    .addOnSuccessListener { data = Pair(true, "") }
                    .addOnFailureListener { data = Pair(false, it.message.toString()) }
            }

        return data
    }

    fun getFavoriteCoins(userId: String): CollectionReference {
        return firebaseFirestore.collection("users").document(userId).collection("favoriteCoinsId")
    }

    fun updateFavorite(userId: String, coin: HashMap<String, Any>): Pair<Boolean, String?>? {
        var data: Pair<Boolean, String>? = null

        firebaseFirestore.collection("users").document(userId).collection("favoriteCoinsId")
            .let { collectionReference ->
                collectionReference.document(coin["id"].toString())
                    .update(coin).addOnSuccessListener { data = Pair(true, "") }
                    .addOnFailureListener { data = Pair(false, it.message.toString()) }
            }
        return data
    }

    fun getUid(): String? = firebaseAuth.uid
}