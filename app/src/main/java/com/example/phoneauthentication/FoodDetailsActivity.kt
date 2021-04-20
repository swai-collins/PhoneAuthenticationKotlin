package com.example.phoneauthentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_food_details.*

class FoodDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)


        var bundle: Bundle? = intent.extras
        imageView.setImageResource(bundle!!.getInt("image"))
        textView.text = bundle!!.getString("name")
        textView2.text = bundle!!.getString("description")
    }
}