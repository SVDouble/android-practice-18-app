package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import android.util.Log
import java.util.*


class Piece( x1 : Float, y1 : Float )
{
    var x: Float = x1
    var y: Float = y1
}

class Snake
{
    var pieceSideSize : Float = 25f
    var body : Vector< Piece > = Vector()

    var deltaX : Float = 0f
    var deltaY : Float = 0f

    fun eat()
    {
        body.insertElementAt( Piece( body[0].x + deltaX, body[0].y + deltaY ), 0 )
    }

    fun start()
    {
        body.addElement( Piece ( 0f, 0f ) )
    }

    fun move()
    {
        body.insertElementAt( Piece( body[0].x + deltaX, body[0].y + deltaY ), 0 )
        body.removeElement( body.lastElement() )
    }
}

class Draw2D(context: Context) : View(context) {


    private val mPaint = Paint()
    private val shadowPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f



    private var k: Float = 0f
    private var n: Float = 0f
    var arr2 : Array<Piece> = emptyArray()

    var snake : Snake = Snake()

    init
    {
        snake.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val w: Float = width.toFloat()
            val h: Float = height.toFloat()
            this.xPath = event.x
            this.yPath = event.y
            if(this.xPath > 3f/4f*w && this.yPath < 3f/4f*h && this.yPath > h*1f/4f) {
                snake.deltaX = 1f;snake.deltaY = 0f }
            if(this.yPath > 3f/4f*h && this.xPath > w*1f/4f && this.xPath < w*3f/4f){
                snake.deltaY = 1f;snake.deltaX = 0f}
            if(this.xPath < 1f/4f*w && this.yPath < 3f/4f*h && this.yPath > h*1f/4f){
                snake.deltaX = -1f;snake.deltaY = 0f}
            if(this.yPath < 1f/4f*h && this.xPath < w*3f/4f && this.xPath > w*1f/4f){
                snake.deltaY = -1f;snake.deltaX = 0f}
            if(this.xPath < 3f/4f*w && this.yPath < 3f/4f*h && this.yPath > h*1f/4f && this.xPath > 1f/4f*w)
                k=1f
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        val w: Int = width
        val h: Int = height
        mPaint.color = YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)

//        val l = System.currentTimeMillis() / 1000
//
//        if((l % 1).toInt() != 1) {
//
//            shadowPaint.isAntiAlias = true
//            shadowPaint.color = BLUE
//            shadowPaint.textSize = 45.0f
//            shadowPaint.strokeWidth = 3.0f
//            shadowPaint.style = Paint.Style.FILL
//            canvas.drawText("$w    $h", 20f, 250f, shadowPaint)
//            canvas.drawText("$l", 20f, 350f, shadowPaint)
//            canvas.drawText("$j", 20f, 650f, shadowPaint)
//            canvas.drawText("$xPath $yPath", 20f, 550f, shadowPaint)
//
//            if (k + 25f > w)
//                k = 0f
//            if (k < 0f)
//                k = w.toFloat() - 25f
//
//            if (n + 25f > h)
//                n = 0f
//            if (n < 0f)
//                n = h.toFloat() - 25f
//
//            mPaint.color = RED
//            when (j) {
//                1f -> n += 1f
//
//                2f -> k += 1f
//
//                3f -> k -= 1f
//
//                4f -> n -= 1f
//            }

        mPaint.color = RED
        for( el in snake.body ) {
            if( (el.x + 1) * snake.pieceSideSize < width && (el.y + 1)*snake.pieceSideSize < height  )
                canvas.drawRect( el.x * snake.pieceSideSize, el.y * snake.pieceSideSize,
                        (el.x + 1 )*snake.pieceSideSize, (el.y + 1 )*snake.pieceSideSize, mPaint )
        }

//            canvas.drawRect(0f + k, 0f + n, 25f + k, 25f + n, mPaint)
//            canvas.drawText("$n    $k", 20f, 450f, shadowPaint)
//        }
        //this.invalidate()
    }

    fun onTimer()
    {
        if(k==1f) {
            snake.eat()
            k=0f
        }
        else
            snake.move()
        postInvalidate()
//        invalidate()
    }

}

class TimerHandle( view1 : Draw2D ) : TimerTask() {
    var view = view1
    override fun run()
    {
        //view.invalidate()
        view.onTimer()
    }
}

open class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)

        setContentView(draw2D)

        Timer().schedule( TimerHandle(draw2D), 1000, 100 )

    }
}