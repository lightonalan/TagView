package com.example.hieunx.tagview

import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout


class MainActivity : AppCompatActivity(), ImageTagViewTapped {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val ll_main = findViewById<RelativeLayout>(R.id.main_layout) as RelativeLayout
        val img = ImageTagView(this)
        img.delegate = this
        ll_main.addView(img)
    }

    override fun tapAt(point: Point, imageTag: ImageTagView) {
        var user = TagUser()
        user.tagId = (imageTag.tags.size + 1).toString()
        user.userId = (imageTag.tags.size + 1).toString()
        user.userName = "タグを付ける"
        user.point = point
        imageTag.addTag(user)
    }
}
