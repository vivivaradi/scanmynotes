package hu.bme.aut.android.scanmynotes.data.network

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.scanmynotes.domain.models.DomainNote

interface FirebaseApi {

    suspend fun getNotes(uid: String): ArrayList<DomainNote>{
        val notes = ArrayList<DomainNote>()
        val notesRef = Firebase.firestore.collection("users").document(uid).collection("notes")
        notesRef.get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        for (document in result) {
                            val note = document.toObject<DomainNote>()
                            notes.add(note)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    exception.message?.let { Log.e("ERROR", it) }
                }
        return notes
    }

    suspend fun saveNewNote(uid: String, note: DomainNote) {
        val notesRef = Firebase.firestore.collection("users").document(uid).collection("notes")
        val data = hashMapOf(
            "title" to note.title,
            "content" to note.content
        )
        notesRef.add(data)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                exception.message?.let { Log.e("ERROR", it) }
            }
    }

    suspend fun getUserNote(id: String, uid: String) : DomainNote{
        var note = DomainNote()
        val docRef = Firebase.firestore.collection("users").document(uid).collection("notes").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    document.toObject<DomainNote>()?.let {  result ->
                        note = result
                    }
                }
            }
            .addOnFailureListener {

            }
        return note
    }

    suspend fun saveNote(uid: String, note: DomainNote) {
        val docRef = Firebase.firestore.collection("users").document(uid).collection("notes").document(note.id)
        val data = hashMapOf(
            "title" to note.title,
            "content" to note.content
        )
        docRef.set(data)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    suspend fun deleteNote(uid: String, id: String) {
        val docRef = Firebase.firestore.collection("users").document(uid).collection("notes").document(id)
        docRef.delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
    }
}