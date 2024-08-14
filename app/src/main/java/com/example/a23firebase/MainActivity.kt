package com.example.a23firebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.a23firebase.activity.FetchingActivity
import com.example.a23firebase.activity.InsertionActivity
import com.example.a23firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInsertData.setOnClickListener {
            chuyenManHinh(InsertionActivity())
        }
        binding.btnFetchData.setOnClickListener {
            chuyenManHinh(FetchingActivity())
        }
    }

    private fun chuyenManHinh(activity: Activity) {
        var i = Intent(this, activity::class.java)
        startActivity(i)
    }
}