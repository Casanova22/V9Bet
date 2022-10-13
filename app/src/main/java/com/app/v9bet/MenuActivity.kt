package com.app.v9bet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.app.v9bet.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var menuBg: ImageView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var imageTitle: ImageView
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_menu)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        imageTitle = findViewById(R.id.imageTitle)
        menuBg = findViewById(R.id.menuBg)


//        menuBg.alpha = 0f
//        menuBg.animate().alpha(1F).alphaBy(-50F).duration = 1000

        imageTitle.translationY = 50F
        imageTitle.animate().alpha(1F).translationYBy(-50F).duration = 3500

        btn1.alpha = 0f
        btn1.translationY = 50F
        btn1.animate().alpha(1F).translationYBy(-50F).duration = 3800

        btn2.alpha = 0f
        btn2.translationY = 50F
        btn2.animate().alpha(1F).translationYBy(-50F).duration = 4100

        btn3.alpha = 0f
        btn3.translationY = 50F
        btn3.animate().alpha(1F).translationYBy(-50F).duration = 4400

        fadeOutAndHideImage(menuBg)
        start()
    }

    private fun fadeOutAndHideImage(img: ImageView) {
        val fadeIn = AlphaAnimation(0F, 1F)
        fadeIn.interpolator = AccelerateInterpolator()
        fadeIn.duration = 1000

        fadeIn.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationEnd(animation:Animation) {
                menuBg.visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation:Animation) {}
            override fun onAnimationStart(animation:Animation) {}
        })
        img.startAnimation(fadeIn)
    }

    private fun start() {
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)

        btn1.setOnClickListener {
            val a = Intent(this, WebViewActivity::class.java)
            startActivity(a)
        }

        btn2.setOnClickListener {
            val b = Intent(this, WebViewActivity::class.java)
            startActivity(b)
        }

        btn3.setOnClickListener {
            val c = Intent(this, MainActivity::class.java)
            startActivity(c)
        }
    }

}