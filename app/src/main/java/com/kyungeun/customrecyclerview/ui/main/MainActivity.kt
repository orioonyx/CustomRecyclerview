package com.kyungeun.customrecyclerview.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kyungeun.customrecyclerview.R
import com.kyungeun.customrecyclerview.databinding.ActivityMainBinding
import com.kyungeun.customrecyclerview.ui.banner.BannerActivity
import com.kyungeun.customrecyclerview.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.activity = this@MainActivity
    }

    fun onBannerClick() {
        val intent = Intent(this, BannerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    fun onhomeClick() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}