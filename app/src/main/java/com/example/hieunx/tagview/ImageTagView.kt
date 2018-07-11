package com.example.hieunx.tagview

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

/**
 * Created by hieunx on 7/11/18.
 */
interface ImageTagViewTapped{
    fun tapAt(point: Point, imageTag: ImageTagView)
}
class ImageTagView : RelativeLayout, TagEditTextReaction {
    var clickTime: Long = 0
    var tags: ArrayList<TagEditText> = ArrayList<TagEditText>()
    var delegate: ImageTagViewTapped? = null
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        // Load the correct layout for this view
        inflate(context, R.layout.image_tag_view, this)


        var listener = View.OnTouchListener(function = { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                //if finger down less than 0.1 sec then add the tag
                if (System.currentTimeMillis() - clickTime < 100 && delegate != null){
                    delegate!!.tapAt(Point(motionEvent.x.toInt(), motionEvent.y.toInt()), this)
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

    override fun tagAdded(tagView: TagEditText) {
        tagView.checkOverlap()
        tagView.checkOverlapWithParent()
    }

    fun addTag(user: TagUser){
        val ll_main = findViewById<RelativeLayout>(R.id.main_layout) as RelativeLayout
        val edit_text = TagEditText(this.context)

        edit_text.tagUser = user
        edit_text.tags = tags
        edit_text.delegate = this
        ll_main.addView(edit_text)
        tags.add(edit_text)
    }
}