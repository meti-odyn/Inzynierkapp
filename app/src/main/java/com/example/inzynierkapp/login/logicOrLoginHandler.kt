package com.example.inzynierkapp.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    val firstName: String?,
    val lastName: String?,
    val email: String
)

fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignedIn: () -> Unit,
    onSignInError: (String) -> Unit // Callback for sign-in error
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onSignedIn()
            } else {
                // Handle sign-in failure
                onSignInError("Invalid email or password")
            }
        }
}


fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    onSignedIn: () -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                // Create a user profile in Firestore
                val userProfile = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                )

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users")
                    .document(user!!.uid)
                    .set(userProfile)
                    .addOnSuccessListener {
                        onSignedIn()
                    }
                    .addOnFailureListener {
                        //handle exception

                    }
            } else {
                // Handle sign-up failure

            }
        }
}

// Function to handle sign-in errors
fun onSignInError(errorMessage: String) {
    // Handle the sign-in error as needed
    // For now, we'll print the error message
    println("Sign-in error: $errorMessage")
}