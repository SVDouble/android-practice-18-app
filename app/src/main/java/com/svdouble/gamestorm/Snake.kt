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


private var pieceSideSize: Float = 0f
const val freeplace:Float = 3f/4f

data class Piece(val x: Float, val y: Float)

class Snake(colorIn:Int, size:Float) {

    var color = colorIn
    var body: Vector<Piece> = Vector()

    var deltaX: Float = 1f
    var deltaY: Float = 0f

    var fieldWidth: Float = 0f
    var fieldHeight: Float = 0f

    private var SIZE :Float = size
    private var tailisnear:Int = 0

    fun makeHead() {
        if((body[0].x + deltaX == body.lastElement().x && body[0].y + deltaY == body.lastElement().y) ||
                ((body[0].x + deltaX) * pieceSideSize >= fieldWidth - (fieldWidth % pieceSideSize) && body.lastElement().x == 0f) ||
                (body[0].x + deltaX < 0f && body.lastElement().x == ((fieldWidth - (fieldWidth % pieceSideSize)) / pieceSideSize) - 1f)||
                ((body[0].y + deltaY) * pieceSideSize >= fieldHeight - (fieldHeight % pieceSideSize) && body.lastElement().y == 0f)||
                (body[0].y + deltaY < 0f && body.lastElement().y == ((fieldHeight - (fieldHeight % pieceSideSize)) / pieceSideSize) - 1f))
        {
            body.removeElement(body.lastElement()); tailisnear = 1
        }
        else tailisnear = 0

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
        fieldHeight = h * freeplace
        pieceSideSize = w / SIZE
        body.addElement(Piece(0f, 0f))
    }

    fun move() {
        makeHead()
        if(tailisnear != 1){
            body.removeElement(body.lastElement()); tailisnear = 0}
    }
}

class SnakeDrawEngine2D(context: Context, col:Int, mp1:MediaPlayer, mp2:MediaPlayer, size:Float, val apple_amount: Int) : View(context) {

    private val mPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var e1: Float = 0.0f
    private var e2: Float = 0.0f
    private var widthPoints: Int = 0
    private var heightPoints: Int = 0
    private var k: Int = 0
    private var l: Int = 0
    private var etapl: Int = 0
    private var MP1 = mp1
    private var MP2: MediaPlayer = mp2
    private var snake: Snake = Snake(col, size)
    var timer: Timer = Timer()
    var timerBlink: Timer = Timer()
    private var timend: Long = 0L

    var startTime: Long = 0

    private var apples: MutableList<Piece> = arrayListOf()


    private fun checkBody(x: Float, y: Float): Boolean {
        for (el in snake.body)
            if (el.x == x && el.y == y)
                return true
        return false
    }

    private fun makeNewApple() {

        do {
            e1 = abs(Random().nextInt() % widthPoints).toFloat()
            e2 = abs(Random().nextInt() % ((heightPoints * freeplace).toInt())).toFloat()


        } while (checkBody(e1, e2))
        apples.add(Piece(e1, e2))
    }

    fun Start(speed: Int) {
        timer.schedule(TimerHandle(this), 500, speed.toLong())
        timerBlink.schedule(BlinkTimerHandle(this), 1000, 1000)
        startTime = System.currentTimeMillis()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        snake.start(w.toFloat(), h.toFloat())
        widthPoints = w / pieceSideSize.toInt()
        heightPoints = h / pieceSideSize.toInt()
        makeNewApple()
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val w: Float = width.toFloat()
            this.xPath = event.x
            this.yPath = event.y

            if (k == 1) {
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
        if (MP2.duration / 1000 == MP2.currentPosition / 1000) {
            MP2.seekTo(1)
        }
        if (MP1.duration / 1000 == MP1.currentPosition / 1000) {
            MP2.seekTo(1)
        }

        mPaint.color = YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)
        mPaint.color = BLUE
        mPaint.strokeWidth = 1.0f
        canvas.drawLine(0f, snake.fieldHeight - (snake.fieldHeight % pieceSideSize), snake.fieldWidth - (snake.fieldWidth % pieceSideSize), snake.fieldHeight - (snake.fieldHeight % pieceSideSize), mPaint)
        canvas.drawLine(snake.fieldWidth - (snake.fieldWidth % pieceSideSize), 0f, snake.fieldWidth - (snake.fieldWidth % pieceSideSize), snake.fieldHeight - (snake.fieldHeight % pieceSideSize), mPaint)

        mPaint.color = RED
        while (apples.size < apple_amount)
            makeNewApple()
        for (apple in apples)
            canvas.drawCircle((apple.x * pieceSideSize + (apple.x + 1) * pieceSideSize) / 2, (apple.y * pieceSideSize + (apple.y + 1) * pieceSideSize) / 2,
                    pieceSideSize / 2, mPaint)

        for (el_i in 0 until snake.body.size) {
            if (el_i == 0)
                mPaint.color = BLACK
            if (el_i == 1)
                mPaint.color = snake.color
            val el = snake.body[el_i]
            canvas.drawCircle((el.x * pieceSideSize + (el.x + 1) * pieceSideSize) / 2, (el.y * pieceSideSize + (el.y + 1) * pieceSideSize) / 2,
                    pieceSideSize / 2, mPaint)
        }



        if ((System.currentTimeMillis() / 1000) % 2L == 0L && l == 0) {
            mPaint.isAntiAlias = true
            mPaint.color = RED
            mPaint.textSize = 35.0f
            mPaint.strokeWidth = 2.0f
            mPaint.style = Paint.Style.STROKE
        } else {
            mPaint.isAntiAlias = true
            mPaint.color = GRAY
            mPaint.textSize = 45.0f
            mPaint.strokeWidth = 3.0f
            mPaint.style = Paint.Style.FILL_AND_STROKE
        }


        canvas.drawText(
                "game time:${(System.currentTimeMillis() - startTime) / 1000}",
                20f,
                snake.fieldHeight + 25f,
                mPaint
        )

        if (l == 1) {
            canvas.drawText(
                    "$etapl",
                    (snake.fieldWidth / 2f) - 15f,
                    snake.fieldHeight / 2f,
                    mPaint)
            canvas.drawText(
                    "game ended:$timend",
                    20f,
                    snake.fieldHeight + 65f,
                    mPaint
            )
        }


    }

    fun onBlinkTimer() {
        postInvalidate()
    }

    fun onTimer() {
        var eaten = false

        for (i in 0 until apples.size) {
            if ((apples[i].y == snake.body[0].y && apples[i].x == snake.body[0].x)) {
                apples.removeAt(i)
                snake.eat()
                makeNewApple()
                k = 1; etapl += 1
                eaten = true
                break
            }
        }
        if (!eaten) {
            snake.move()
            k = 1
        }
        for (i in 1..(snake.body.size - 1))
            if ((snake.body[i].x  == snake.body[0].x && snake.body[i].y == snake.body[0].y) || ((snake.body[i].x == snake.body[0].x + snake.deltaX && snake.body[i].x != snake.body.lastElement().x && snake.body[i].y == snake.body[0].y + snake.deltaY && snake.body[i].y != snake.body.lastElement().y))) {
                timer.cancel();MP1.stop();MP2.start();l = 1;timend = (System.currentTimeMillis() - startTime) / 1000
            }
        postInvalidate()

    }
}
class TimerHandle(view1: SnakeDrawEngine2D) : TimerTask() {
    var view = view1
    override fun run() {
        view.onTimer()
    }
}

class BlinkTimerHandle(view1: SnakeDrawEngine2D) : TimerTask() {
    var view = view1
    override fun run() {
        view.onBlinkTimer()
    }
}


class SnakeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sGame = Games.getInstance(this).games[1] as SGame
        sGame.startGame()
        setContentView(sGame.drawEngine)
    }

    override fun onStop() {
        val sGame = Games.getInstance(this).games[1] as SGame
        sGame.stopGame()
        setContentView(R.layout.almost_empty_layout)
        super.onStop()
    }
}

