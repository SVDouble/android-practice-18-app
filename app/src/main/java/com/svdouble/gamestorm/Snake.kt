package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import java.util.*


class Draw2D(context: Context) : View(context) {

    private val mPaint = Paint()
    private val shadowPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var urg:Int = 0
    var timer: Timer? = null
    var mTimerTask: TimerTask? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            this.xPath = event.x
            this.yPath = event.y
            urg+=1
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.color = Color.YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)

        val w: Float = width.toFloat()
        val h: Float = height.toFloat()
        var i = 0


        /*shadowPaint.isAntiAlias = true
        shadowPaint.color = Color.BLUE
        shadowPaint.textSize = 45.0f
        shadowPaint.strokeWidth = 3.0f
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.GRAY)
        shadowPaint.textSize = 50.0f
        shadowPaint.strokeWidth = 4.0f
        canvas.drawText("${xPath}    ${yPath}", 20f, 540f, shadowPaint)*/

        mPaint.color = Color.RED
        /*while(i < timer) {
            canvas.drawRect(0f+i*25f, 0f, 25f+i*25f, 25f, mPaint)
            i+=1*
        }*/
        this.invalidate()

    }

}

class Snake: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        setContentView(draw2D)
    }
}
