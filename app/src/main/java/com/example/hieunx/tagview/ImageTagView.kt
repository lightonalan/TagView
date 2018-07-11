package com.example.hieunx.tagview

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.tag_view.view.*

/**
 * Created by hieunx on 7/11/18.
 */
class ImageTagView : RelativeLayout, TagEditTextReaction {
    var clickTime: Long = 0
    var tags: ArrayList<TagEditText> = ArrayList<TagEditText>()
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        // Load the correct layout for this view
        inflate(context, R.layout.image_tag_view, this)

        val ll_main = findViewById<RelativeLayout>(R.id.main_layout) as RelativeLayout
        var listener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                //if finger down less than 0.1 sec then add the tag
                if (System.currentTimeMillis() - clickTime < 100){
                    val edit_text = TagEditText(this.context)
                    var user = TagUser()
                    user.tagId = (tags.size + 1).toString()
                    user.userId = (tags.size + 1).toString()
                    user.userName = "タグを付ける"
                    user.point = Point(motionEvent.x.toInt(), motionEvent.y.toInt())
                    edit_text.tagUser = user
                    edit_text.tags = tags
                    edit_text.reaction = this
                    ll_main.addView(edit_text)
                    tags.add(edit_text)
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                clickTime = System.currentTimeMillis()
            }
            true

        })
        setOnTouchListener(listener)
    }

    override fun removeTag(user: TagUser) {
        val ll_main = findViewById<RelativeLayout>(R.id.main_layout) as RelativeLayout
        for (tag in tags){
            if (tag.tagUser!!.tagId == user.tagId){
                ll_main.removeView(tag)
                tags.remove(tag)
            }
        }
    }
}