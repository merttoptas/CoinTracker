package com.merttoptas.cointracker.data.remote.service

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.merttoptas.cointracker.data.model.CoinResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
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
        coin: HashMap<String, Any>,
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

    fun getFavoriteCoinList(userId: String): Flow<Pair<ArrayList<CoinResponse>, String?>> =
        channelFlow {
            val coinList = ArrayList<CoinResponse>()
            val callback = getFavoriteCoins(userId).get().addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    result.forEach {
                        val id = it.data["id"].toString()
                        val name = it.data["name"].toString()
                        val symbol = it.data["symbol"].toString()
                        val image = it.data["image"].toString()
                        val currentPrice = it.data["currentPrice"].toString()
                        val changePercent = it.data["changePercent"].toString()

                        val coin = CoinResponse(
                            id,
                            symbol,
                            name,
                            image,
                            currentPrice = currentPrice.toDoubleOrNull(),
                            changePercent = changePercent.toDoubleOrNull()
                        )
                        coinList.add(coin)
                    }
                    trySendBlocking(Pair(coinList, null))
                }
            }.addOnFailureListener {
                trySendBlocking(Pair(arrayListOf(), it.message))
            }
            awaitClose { callback.isCanceled() }
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

    fun login(email: String, password: String): Flow<LoginData> = channelFlow {
        val callback = firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful) {
                        trySendBlocking(LoginData(true, null))
                    } else {
                        trySendBlocking(LoginData(false, task.exception?.message))

                    }
                }
            })
        awaitClose { callback.isCanceled() }
    }

    fun register(email: String, password: String): Flow<LoginData> = channelFlow {
        val callBack = firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.uid?.let {
                        val data = HashMap<String, Any>()
                        data["email"] = email
                        setUser(it, data)
                    }
                    trySendBlocking(LoginData(true, null))
                } else {
                    trySendBlocking(LoginData(false, task.exception?.message))
                }
            }
        awaitClose { callBack.isCanceled() }
    }

    fun getUid(): String? = firebaseAuth.uid
}

data class LoginData(val result: Boolean, val error: String?)