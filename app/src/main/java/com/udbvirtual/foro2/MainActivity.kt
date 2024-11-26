package com.udbvirtual.foro2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val ivProfilePicture = findViewById<ImageView>(R.id.ivProfilePicture)
        val logoutButton = findViewById<Button>(R.id.btnLogout)

        // Obtener los datos del Intent
        val userName = intent.getStringExtra("USER_NAME")
        val userPhotoUrl = intent.getStringExtra("USER_PHOTO")

        // Mostrar el nombre
        tvUserName.text = userName

        // Cargar la imagen de forma asíncrona
        if (!userPhotoUrl.isNullOrEmpty()) {
            loadProfilePicture(userPhotoUrl, ivProfilePicture)
        }

        // Logout
        logoutButton.setOnClickListener {
            auth.signOut()

            val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadProfilePicture(photoUrl: String, imageView: ImageView) {
        // Usa una coroutine para descargar la imagen
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(photoUrl)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap) // Establece la imagen en el ImageView
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}