package com.example.hieunx.tagview

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.tag_view.view.*
import android.util.Log
import java.lang.Math.abs

interface TagEditTextReaction {
    fun removeTag(user: TagUser)
    fun tagAdded(tagView: TagEditText)
}
/**
 * Created by hieunx on 7/10/18.
 */
class TagEditText : RelativeLayout {
    var clickTime: Long = 0
    var overlapLeft = 0
    var overlapRight = 0
    var maxOverlapSize = 0
    var tags: ArrayList<TagEditText> = ArrayList<TagEditText>()
    var overlapTags: ArrayList<TagEditText> = ArrayList<TagEditText>()
    var delegate: TagEditTextReaction? = null
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        // Load the correct layout for this view
        inflate(context, R.layout.tag_view, this)
        resetArrowToCenter()
        hideRemoveIcon()

        var dX = 0F
        var dY = 0F

        var listener = View.OnTouchListener(function = {view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                //if user tap into tagview, then show/hide remove icon
                if (System.currentTimeMillis() - clickTime < 100){
                    displayOrRemoveIcon(x = motionEvent.x.toInt())
                }else{
                    checkOverlap()
                    checkOverlapWithParent()
                }
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //detect action press start
                dX = getX() - motionEvent.getRawX()
                dY = getY() - motionEvent.getRawY()
                clickTime = System.currentTimeMillis()
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                if (System.currentTimeMillis() - clickTime > 100){
                    resetOverlap()
                    removeOverlap()
                    //when user move, then force hide the remove icon
                    if (tag_close.visibility == View.VISIBLE) {
                        hideRemoveIcon()
                    }
                    //get new point value
                    val newX = motionEvent.getRawX() + dX
                    val newY = motionEvent.getRawY() + dY
                    val parent: RelativeLayout = this.parent as RelativeLayout
                    if (newX < 50 - width / 2 || newY < 0 || newX + width - 50 > parent.width || newY + height > parent.height){
                        //disable move outside parent view,keep tagview inside
                        Log.d("Move","Outside")
                    }else{
                        //move tagview with user move
                        view.animate()
                                .x(motionEvent.getRawX() + dX)
                                .y(motionEvent.getRawY() + dY)
                                .setDuration(0)
                                .start()
                        //reset arrow point
                        if (tagUser != null) {
                            tagUser!!.point = Point((x + width / 2).toInt(), (y + height / 2).toInt())
                            Log.d("Move","Point changed")
                        }
                    }
                }
            }
            true

        })

        setOnTouchListener(listener)
    }

    //tagUser is the main value
    var tagUser : TagUser? = null
        set(value) {
            field = value
            displayText()
        }

    //show username on tagview
    fun displayText() {
        if (tagUser != null) {
            tag_text.text = tagUser!!.userName
        } else {
            tag_text.text = ""
        }
        tag_text.invalidate()
        tag_text.requestLayout()
    }
    /**
     * Displays the remove icon if a listener is available
     */
    fun displayOrRemoveIcon(x: Int) {
        if (tag_close.visibility == View.GONE) {
            displayRemoveIcon()
        } else {
            if (x >= width * 2 / 3){
                if (delegate != null){
                    delegate!!.removeTag(this!!.tagUser!!)
                }
            }else{
                hideRemoveIcon()
            }
        }
    }

    fun displayRemoveIcon(){
        if (tag_close.visibility == View.GONE) {
            tag_close.visibility = View.VISIBLE
            x = x - 18
        }
    }
    fun hideRemoveIcon(){
        if (tag_close.visibility == View.VISIBLE) {
            tag_close.visibility = View.GONE
            x = x + 18
        }
    }
    //set arrow location
    fun resetArrowToCenter(){
        if (tagUser == null) {
            return
        }
        if ((overlapLeft != 0 && overlapRight != 0) || (overlapLeft == 0 && overlapRight == 0)){
            //when tag is not overlap or overlap for both side, then arrow is set to center
            x = tagUser!!.point!!.x.toFloat() - width / 2
            tag_arrow.x = (tag_text.width / 2).toFloat()
        }else if (overlapLeft > 0){
            //if tag is overlap from left, then move the view to right and arrow to left to keep arrow in a place
            x = tagUser!!.point!!.x.toFloat() - width / 2 - overlapLeft
            tag_arrow.x = (tag_text.width / 2 + overlapLeft).toFloat()
        }else{
            //if tag is overlap from right, then move the view to left and arrow to right to keep arrow in a place
            x = tagUser!!.point!!.x.toFloat() - width / 2 + overlapRight
            tag_arrow.x = (tag_text.width / 2 - overlapRight).toFloat()
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (tag_close.visibility == View.GONE){
            //reset tagview location depend on tagUser point and tagview size
            if (tagUser != null) {
                x = tagUser!!.point!!.x.toFloat() - w / 2
                y = tagUser!!.point!!.y.toFloat() - h
            }
            //maxOverlapSize to keep arrow not move out of the tagview
            maxOverlapSize = w/2 - 50
            if (delegate != null){
                delegate!!.tagAdded(this)
            }
        }
    }

    fun checkOverlap(){
        for (tag in tags){
            if (tag.tagUser!!.tagId != tagUser!!.tagId){
                if (isOverlapWithTag(tag)){
                    if (x - tag.x > 0){
                        tag.overlapLeft = minOf((tag.x + tag.width - x).toInt(), tag.maxOverlapSize)
                    }else{
                        tag.overlapRight = minOf((x + width - tag.x).toInt(), tag.maxOverlapSize)
                    }
                    overlapTags.add(tag)
                    tag.resetArrowToCenter()
                }
            }
        }
    }

    fun removeOverlap(){
        for (tag in overlapTags){
            tag.resetOverlap()
        }
        overlapTags.clear()
    }
    fun resetOverlap(){
        //reset the tagview with user tag location
        overlapRight = -overlapRight
        overlapLeft = -overlapLeft
        resetArrowToCenter()

        //move arrow to center
        overlapRight = 0
        overlapLeft = 0
        resetArrowToCenter()
    }

    fun checkOverlapWithParent(){
        //check when tagview move outside from left or right
        val parent: RelativeLayout = this.parent as RelativeLayout
        if (x + width > parent.width){
            overlapLeft = minOf((x + width - parent.width).toInt(), maxOverlapSize)
        }else if (x < 0){
            overlapRight = minOf(abs(x).toInt(), maxOverlapSize)
        }
        resetArrowToCenter()
    }
    fun isOverlapWithTag(tag: TagEditText): Boolean{
        return x < tag.x + tag.width && x + width > tag.x && y < tag.y + tag.height && y + height > tag.y
    }
}