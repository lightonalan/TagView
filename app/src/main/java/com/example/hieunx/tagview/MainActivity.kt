package com.example.hieunx.tagview

import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val ll_main = findViewById<RelativeLayout>(R.id.main_layout) as RelativeLayout


        val img = ImageTagView(this)
        ll_main.addView(img)
    }

}
