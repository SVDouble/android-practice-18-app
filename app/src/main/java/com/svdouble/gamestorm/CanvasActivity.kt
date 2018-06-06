package com.svdouble.gamestorm

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.BLUE
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.MotionEvent
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.widget.TextView
import android.text.method.Touch.onTouchEvent
import android.util.Log

const val TAG = "GameStorm"
const val bottomMargin : Float = 3f / 4f
const val squareSide : Int = 60


class Cell( num1:Int, y1:Float)
{
    var x:Float = (num1 * squareSide).toFloat()
    var y:Float = y1
    var bottom: Float = y1 + squareSide
    var right: Float = x + squareSide
    var type: Int = 0
    var num : Int = num1
    var bgcolor  = Color.TRANSPARENT

    fun Draw( mPaint : Paint, canvas : Canvas )
    {
        if( type == 1 )
            Cross(mPaint,canvas)
        else if( type == 2 )
            Circle(mPaint,canvas)
    }

    //крестик
    fun Cross( mPaint : Paint, canvas : Canvas ) {
        val arg11:Float = x - (x % side()) + side()/2
        val arg22:Float = y + side()/2 - (y % side())
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 7F
        canvas.drawLine(arg11 - (side()/2 - 5f), arg22 - (side()/2 - 5f), arg11 + (side()/2 - 5f), arg22 + (side()/2 - 5f), mPaint)
        canvas.drawLine(arg11 + (side()/2 - 5f), arg22 - (side()/2 - 5f), arg11 - (side()/2 - 5f), arg22 + (side()/2 - 5f), mPaint)
    }

    fun side() : Float
    {
        return squareSide.toFloat()
    }

    //нолик
    fun Circle( mPaint : Paint, canvas : Canvas ) {
        val arg11:Float = x - (x % side()) + side()/2
        val arg22:Float = y + side()/2 - (y % side())
        mPaint.isAntiAlias = true
        mPaint.color = Color.GREEN // установим зеленый цвет
        mPaint.style = STROKE
        mPaint.strokeWidth = 5F
        canvas.drawCircle(arg11, arg22, side()/2 - 4f, mPaint)
    }

    fun draw(mPaint : Paint, canvas : Canvas)
    {
        mPaint.color = bgcolor
        canvas.drawRect( x,y,right,bottom,mPaint )
        mPaint.color = Color.RED
        mPaint.strokeWidth = 7F
        canvas.drawLine( right, y, right, bottom, mPaint )
        canvas.drawLine( x, bottom, right, bottom, mPaint )
        if( type == 2 )
            Cross(mPaint,canvas)
        else if( type == 1 )
            Circle(mPaint,canvas)
    }

    fun onTouch( x1 : Float, y1 : Float, type1 : Int ) : Boolean
    {
        if( x1 < x || y1 < y || y1 > bottom || x1 > right || type != 0)
            return false
        type = type1
        return true;
    }

}

class Row( num1 : Int, width : Int )
{
    var num : Int = num1
    var arr = emptyArray<Cell>()

    init {
        this.arr = Array<Cell>( width/ squareSide ,{i->Cell(i,(num1* squareSide).toFloat())})

    }


    fun draw( mPaint : Paint, canvas : Canvas )
    {
//        canvas.drawLine( 0f, arr[0].y + arr[0]., w, (ii * squareSide).toFloat(), mPaint )
        for( el in arr )
            el.draw(mPaint,canvas)
    }

    fun bottom() : Float
    {
        return arr[ arr.size - 1 ].bottom
    }

    fun onTouch( x : Float, y : Float, type : Int ) : Cell?
    {
        for( el in arr )
            if( el.onTouch( x, y, type ) )
                return el
        return null
    }

}

class Draw2D(context: Context) : View(context) {
    private val mPaint = Paint()
    private val shadowPaint = Paint()
    private var xPath: Float = 0.0f
    private var yPath: Float = 0.0f
    private var urg:Int = 0
    private var k: Int = 0
    private var rows : Array<Row> = emptyArray()




    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            this.xPath = event.x
            this.yPath = event.y
            Log.d(TAG, "Touch detected!")
            urg = ( urg + 1 ) % 2
            for ( row in rows ) {

                val cell = row.onTouch(event.x, event.y, urg + 1)
                if ( cell != null ) {

                    //var upRow = rows[ row.num - 1 ]
                    //upRow.num

                    if ( cell.num > 0 && cell.num < row.arr.size - 1)
                        if (row.arr[cell.num - 1].type == cell.type && row.arr[cell.num + 1].type == cell.type) {
                            row.arr[cell.num - 1].bgcolor = Color.GREEN
                            row.arr[cell.num + 1].bgcolor = Color.GREEN
                            cell.bgcolor = Color.GREEN
                        }

                    break
                }
            }
            this.invalidate()
        }
        return super.onTouchEvent(event)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //фон
        mPaint.color = Color.YELLOW
        mPaint.style = Paint.Style.FILL
        canvas.drawPaint(mPaint)

        if( rows.isEmpty() )
        {
            rows  = Array<Row>( ( height.toFloat() * bottomMargin / squareSide).toInt() - 1, {i->Row(i,width) } )
        }

        for( el in rows )
            el.draw(mPaint,canvas)
// линии
//        val w: Float = width.toFloat()
//        val h: Float = height.toFloat()
//        mPaint.color = Color.RED
//        val strokewidth:Float = 7F
//        mPaint.strokeWidth = strokewidth
//        val numHorizLines : Float = (h * bottomMargin / squareSide).toInt().toFloat() - 1
//        val lastLineY = numHorizLines * squareSide
//        val numVertLines : Float = w / squareSide
//        //горизонтальные
//        for( ii in 0..numHorizLines.toInt() )
//        {
//            canvas.drawLine( 0f, (ii * squareSide).toFloat(), w, (ii * squareSide).toFloat(), mPaint )
//        }
//        //вертикальные
//        for( ii in 0..numVertLines.toInt() )
//        {
//            canvas.drawLine( (ii*squareSide).toFloat(), 0f, (ii*squareSide).toFloat(), lastLineY, mPaint )
//        }

        //текст
        shadowPaint.isAntiAlias = true
        shadowPaint.color = BLUE
        shadowPaint.textSize = 45.0f
        shadowPaint.strokeWidth = 3.0f
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.GRAY)
        shadowPaint.textSize = 50.0f
        shadowPaint.strokeWidth = 4.0f
        canvas.drawText("$xPath    $yPath", 20f, rows[ rows.size - 1 ].bottom() + 50f, shadowPaint)



//        if(yPath < lastLineY) {
//            if (urg % 2 == 0) {
//                Circle(xPath, yPath, squareSide)
//
//            }
//            else {
//                Cross(xPath, yPath, squareSide)
//            }
//        }

    }
}

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val draw2D = Draw2D(this)
        setContentView(draw2D)
    }
}

