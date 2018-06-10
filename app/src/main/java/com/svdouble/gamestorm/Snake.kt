package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.os.Bundle

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

    var fieldWidth : Float = 0f
    var fieldHeight : Float = 0f

    fun makeHead()
    {
        var newX = body[0].x + deltaX
        var newY = body[0].y + deltaY
        if( newX * pieceSideSize >= fieldWidth )
            newX = 0f
        if( newX < 0f )
            newX = ( fieldWidth / pieceSideSize )

        if( newY * pieceSideSize >= fieldHeight )
            newY = 0f
        if( newY < 0f )
            newY = ( fieldHeight / pieceSideSize )

        body.insertElementAt( Piece( newX, newY ), 0 )
    }

    fun eat()
    {
        this.makeHead()
    }

    fun start( w : Float, h : Float )
    {
        fieldWidth = w
        fieldHeight = h
        body.addElement( Piece ( 0f, 0f ) )
    }

    fun move()
    {
        this.makeHead()
        body.removeElement( body.lastElement() )
    }
}

class Draw2D(context: Context) : View(context) {


    private val mPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f



    private var k: Float = 0f

    var snake : Snake = Snake()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        snake.start(w.toFloat(),h.toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val w: Float = width.toFloat()
            val h: Float = height.toFloat()
            this.xPath = event.x
            this.yPath = event.y

            if(this.xPath > 3f/4f*w && this.yPath < 3f/4f*h && this.yPath > h*1f/4f && snake.deltaX != -1f) {
                snake.deltaX = 1f;snake.deltaY = 0f }
            if(this.yPath > 3f/4f*h && this.xPath > w*1f/4f && this.xPath < w*3f/4f && snake.deltaY != -1f){
                snake.deltaY = 1f;snake.deltaX = 0f}
            if(this.xPath < 1f/4f*w && this.yPath < 3f/4f*h && this.yPath > h*1f/4f && snake.deltaX != 1f ){
                snake.deltaX = -1f;snake.deltaY = 0f}
            if(this.yPath < 1f/4f*h && this.xPath < w*3f/4f && this.xPath > w*1f/4f && snake.deltaY != 1f){
                snake.deltaY = -1f;snake.deltaX = 0f}
            if(this.xPath < 3f/4f*w && this.yPath < 3f/4f*h && this.yPath > h*1f/4f && this.xPath > 1f/4f*w)
                k=1f
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        mPaint.color = YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)


        mPaint.color = RED
        for( el in snake.body ) {
            canvas.drawRect( el.x * snake.pieceSideSize, el.y * snake.pieceSideSize,
                    (el.x + 1 )*snake.pieceSideSize, (el.y + 1 )*snake.pieceSideSize, mPaint )
        }

    }

    fun onTimer()
    {
        if(k==1f) {
            snake.eat()
            k = 0f
        }
        else
            snake.move()
        postInvalidate()
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

open class SnakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        setContentView(draw2D)
        Timer().schedule( TimerHandle(draw2D), 1000, 150 )

    }
}
