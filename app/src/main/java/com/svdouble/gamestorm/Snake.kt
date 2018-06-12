package com.svdouble.gamestorm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import java.lang.Math.abs
import java.util.*


const val pieceSideSize : Float = 30f


class Piece( x1 : Float, y1 : Float )
{
    var x: Float = x1
    var y: Float = y1
}

class Snake
{
    var body : Vector< Piece > = Vector()

    var deltaX : Float = 0f
    var deltaY : Float = 0f

    var fieldWidth : Float = 0f
    var fieldHeight : Float = 0f

    fun makeHead()
    {
        var newX = body[0].x + deltaX
        var newY = body[0].y + deltaY
        if( newX * pieceSideSize >= fieldWidth - (fieldWidth % pieceSideSize))
            newX = 0f
        if( newX < 0f )
            newX = ( (fieldWidth - (fieldWidth % pieceSideSize))/ pieceSideSize ) -1f

        if( newY * pieceSideSize >= fieldHeight - (fieldHeight % pieceSideSize))
            newY = 0f
        if( newY < 0f )
            newY = ( (fieldHeight - (fieldHeight % pieceSideSize))/ pieceSideSize ) -1f

        body.insertElementAt( Piece( newX, newY ), 0 )
    }

    fun eat()
    {
        makeHead()
    }

    fun start( w : Float, h : Float )
    {
        fieldWidth = w
        fieldHeight = h
        body.addElement( Piece ( 0f, 0f ) )
    }

    fun move()
    {
        makeHead()
        body.removeElement( body.lastElement() )
    }
}

class Draw2D(context: Context) : View(context) {


    private val mPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var e1: Float = 0.0f
    private var e2: Float = 0.0f
    var widthPoints : Int = 0
    var heightPoints : Int = 0

    var snake : Snake = Snake()

    private var apple : Piece = Piece(0f,0f)

    fun checkBody( x : Float, y : Float ) : Boolean
    {
        for( el in snake.body )
            if( el.x == x && el.y == y )
                return  true
        return false
    }

    fun makeNewApple( )
    {

        do {
            e1 = abs(Random().nextInt() % widthPoints).toFloat() //abs(Random().nextInt() % (( h.toFloat() - (h.toFloat() % pieceSideSize)) / pieceSideSize ) )
            e2 = abs(Random().nextInt() % heightPoints).toFloat() //abs(Random().nextInt() % (( w.toFloat() - (w.toFloat() % pieceSideSize)) / pieceSideSize ) )
        } while( checkBody( e1, e2 ) )
        this.apple = Piece(e1,e2)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        widthPoints = w / pieceSideSize.toInt()
        heightPoints = h / pieceSideSize.toInt()
        snake.start(w.toFloat(),h.toFloat())
        makeNewApple()
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
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        mPaint.color = YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)
        mPaint.color = BLUE


        //if(apple.x == )
        canvas.drawLine(0f,snake.fieldHeight - (snake.fieldHeight % pieceSideSize), snake.fieldWidth - (snake.fieldWidth % pieceSideSize), snake.fieldHeight - (snake.fieldHeight % pieceSideSize), mPaint)
        canvas.drawLine(snake.fieldWidth - (snake.fieldWidth % pieceSideSize),0f, snake.fieldWidth - (snake.fieldWidth % pieceSideSize), snake.fieldHeight - (snake.fieldHeight % pieceSideSize), mPaint)

        mPaint.color = BLUE
        canvas.drawCircle( (apple.x * pieceSideSize + (apple.x + 1 )*pieceSideSize)/2, (apple.y * pieceSideSize + (apple.y + 1 )*pieceSideSize)/2,
                pieceSideSize/2,  mPaint )
        canvas.drawText("${this.e1}    ${this.e2}", 24f, 500f, mPaint)

        mPaint.color = RED
        for( el in snake.body ) {
            canvas.drawRect( el.x * pieceSideSize, el.y * pieceSideSize,
                    (el.x + 1 )*pieceSideSize, (el.y + 1 )*pieceSideSize, mPaint )
        }

    }

    fun onTimer()
    {
        if( apple.y == snake.body[0].y && apple.x == snake.body[0].x ) {
            snake.eat()
            makeNewApple()
        }
        else
            snake.move()
        postInvalidate()
    }

}

class TimerHandle( view1 : Draw2D ) : TimerTask() {
    var view = view1
    override fun run(){
        view.onTimer()}
}

open class SnakeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)

        setContentView(draw2D)

        Timer().schedule( TimerHandle(draw2D), 1000, 200 )

    }
}
