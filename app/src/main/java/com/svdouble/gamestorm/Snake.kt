package com.svdouble.gamestorm
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import java.lang.Math.abs
import java.util.*


const val pieceSideSize: Float = 35f


class Piece(x1: Float, y1: Float) {
    var x: Float = x1
    var y: Float = y1
}

class Snake(colorIn:Int) {

    var color = colorIn
    var body: Vector<Piece> = Vector()

    var deltaX: Float = 1f
    var deltaY: Float = 0f

    var fieldWidth: Float = 0f
    var fieldHeight: Float = 0f

    private fun makeHead() {
        var newX = body[0].x + deltaX
        var newY = body[0].y + deltaY
        if (newX * pieceSideSize >= fieldWidth - (fieldWidth % pieceSideSize))
            newX = 0f
        if (newX < 0f)
            newX = ((fieldWidth - (fieldWidth % pieceSideSize)) / pieceSideSize) - 1f

        if (newY * pieceSideSize >= fieldHeight - (fieldHeight % pieceSideSize))
            newY = 0f
        if (newY < 0f)
            newY = ((fieldHeight - (fieldHeight % pieceSideSize)) / pieceSideSize) - 1f

        body.insertElementAt(Piece(newX, newY), 0)
    }

    fun eat() {
        makeHead()
    }

    fun start(w: Float, h: Float) {
        fieldWidth = w
        fieldHeight = h
        body.addElement(Piece(0f, 0f))
    }

    fun move() {
        makeHead()
        body.removeElement(body.lastElement())
    }
}

class Draw2D(context: Context, col:Int, mp:MediaPlayer) : View(context) {


    private val mPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var e1: Float = 0.0f
    private var e2: Float = 0.0f
    private var widthPoints: Int = 0
    private var heightPoints: Int = 0
    private var k: Int = 0
    private var MP = mp
    private var snake: Snake = Snake( col )
    var timer: Timer = Timer()

    private var apple: Piece = Piece(0f, 0f)

    private fun checkBody(x: Float, y: Float): Boolean {
        for (el in snake.body)
            if (el.x == x && el.y == y)
                return true
        return false
    }

    private fun makeNewApple() {

        do {
            e1 = abs(Random().nextInt() % widthPoints).toFloat() //abs(Random().nextInt() % (( h.toFloat() - (h.toFloat() % pieceSideSize)) / pieceSideSize ) )
            e2 = abs(Random().nextInt() % heightPoints).toFloat() //abs(Random().nextInt() % (( w.toFloat() - (w.toFloat() % pieceSideSize)) / pieceSideSize ) )
        } while (checkBody(e1, e2))
        this.apple = Piece(e1, e2)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        widthPoints = w / pieceSideSize.toInt()
        heightPoints = h / pieceSideSize.toInt()
        snake.start(w.toFloat(), h.toFloat())
        makeNewApple()
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val w: Float = width.toFloat()
            this.xPath = event.x
            this.yPath = event.y

            if(k == 1) {
                if ((this.xPath > 1f / 2f * w && snake.deltaX == 1f) || (this.xPath < 1f / 2f * w && snake.deltaX == -1f)) {
                    snake.deltaX = 0f; snake.deltaY = 1f
                } else if ((this.xPath > 1f / 2f * w && snake.deltaX == -1f) || (this.xPath < 1f / 2f * w && snake.deltaX == 1f)) {
                    snake.deltaX = 0f; snake.deltaY = -1f
                } else if ((this.xPath > 1f / 2f * w && snake.deltaY == 1f) || (this.xPath < 1f / 2f * w && snake.deltaY == -1f)) {
                    snake.deltaX = -1f; snake.deltaY = 0f
                } else if ((this.xPath > 1f / 2f * w && snake.deltaY == -1f) || (this.xPath < 1f / 2f * w && snake.deltaY == 1f)) {
                    snake.deltaX = 1f; snake.deltaY = 0f
                }
                k = 0
            }
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

        canvas.drawLine(0f, snake.fieldHeight - (snake.fieldHeight % pieceSideSize), snake.fieldWidth - (snake.fieldWidth % pieceSideSize), snake.fieldHeight - (snake.fieldHeight % pieceSideSize), mPaint)
        canvas.drawLine(snake.fieldWidth - (snake.fieldWidth % pieceSideSize), 0f, snake.fieldWidth - (snake.fieldWidth % pieceSideSize), snake.fieldHeight - (snake.fieldHeight % pieceSideSize), mPaint)

        mPaint.color = RED
        canvas.drawCircle((apple.x * pieceSideSize + (apple.x + 1) * pieceSideSize) / 2, (apple.y * pieceSideSize + (apple.y + 1) * pieceSideSize) / 2,
                pieceSideSize / 2, mPaint)
        mPaint.color = snake.color
        for (el in snake.body) {
            canvas.drawRect(el.x * pieceSideSize, el.y * pieceSideSize,
                    (el.x + 1) * pieceSideSize, (el.y + 1) * pieceSideSize, mPaint)
        }

    }

    fun onTimer() {
        if (apple.y == snake.body[0].y && apple.x == snake.body[0].x) {
            snake.eat()
            makeNewApple()
            k = 1
        }
        else{
            snake.move();k = 1}
        for (i in 1..(snake.body.size - 1))
            if (snake.body[i].x == snake.body[0].x && snake.body[i].y == snake.body[0].y) {
                timer.cancel();MP.stop()
            }

        postInvalidate()
    }

}

class TimerHandle(view1: Draw2D) : TimerTask() {
    var view = view1
    override fun run() {
        view.onTimer()
    }
}

open class SnakeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mp = MediaPlayer.create(this, R.raw.pac)
        mp.start()
        val draw2D = Draw2D(this, GREEN, mp)
        setContentView(draw2D)
        draw2D.timer.schedule(TimerHandle(draw2D), 500, 150)

    }
}