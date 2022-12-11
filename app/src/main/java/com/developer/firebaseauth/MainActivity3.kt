package com.developer.firebaseauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.developer.firebaseauth.databinding.ActivityMain3Binding

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root )


        val number=intent.getStringExtra("key name")
        binding.textNumber.text=number




    }
}