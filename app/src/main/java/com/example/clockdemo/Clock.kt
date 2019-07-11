package com.example.clockdemo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.text.TextPaint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import android.content.Context.MODE_PRIVATE
import android.R.id.edit



class Clock : View
{

    private var mOuterRadius = 0.0F
    private var mHourLen = 0.0F
    private var mMinuteLen = 0.0F
    private var mSecondLen = 0.0F
    private var mPaint: Paint
    private var mHour = 0
    private var mMinute = 0
    private var mSecond = 0
    private var mDrawFilter: PaintFlagsDrawFilter
    private var mCenterX = 0.0f
    private var mCenterY = 0.0f
    private var mAmPm = 0
    private var mShowBoard = true

    private val AM = 0

    private val mTextArray = ArrayList<String>()



    constructor(context: Context):this(context, null)

    constructor(context: Context, attributes: AttributeSet?):this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int):super(context,attributes,defStyleAttr)
    {
        mPaint = Paint()
        mDrawFilter =  PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG)
        initPaint()
        initArray()
    }

    private fun initPaint()
    {
        mPaint.isAntiAlias = true
    }

    private fun initArray()
    {
        for(i in 0..11)
        {
            if(i == 0)
            {
                mTextArray.add("12")
            }
            else
            {
                mTextArray.add(String.format("%02d",i))
            }
        }
    }


    fun changeView()
    {
        mShowBoard = !mShowBoard
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mOuterRadius = max(0.0f, min(measuredHeight, measuredWidth).toFloat() * 0.8f) / 2

        mCenterX = measuredWidth.toFloat() / 2
        mCenterY = measuredHeight.toFloat() / 2

        val calendar = Calendar.getInstance()
        mHour = calendar.get(Calendar.HOUR)
        mMinute = calendar.get(Calendar.MINUTE)
        mSecond = calendar.get(Calendar.SECOND)
        mAmPm = calendar.get(Calendar.AM_PM)

        if(mShowBoard)
        {
            mHourLen = mOuterRadius * 0.4f
            mMinuteLen = mOuterRadius * 0.6f
            mSecondLen = mOuterRadius * 0.8f
            drawDegree(canvas)
            drawHoursValues(canvas)
            drawNeedles(canvas)
            drawCenter(canvas)
        }
        else
        {
            drawNumbers(canvas)
        }

        postInvalidateDelayed(1000)
    }

    private fun drawNumbers(cavans: Canvas?)
    {
        if(cavans == null) return
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        val rec = Rect()
        textPaint.textSize = measuredWidth.toFloat() * 0.2f
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER

        var text = String.format("%02d:%02d:%02d",mHour,mMinute,mSecond)

        textPaint.getTextBounds(text,0,text.length,rec)
        cavans.drawText(text,mCenterX,mCenterY + rec.height() / 2, textPaint)

        if(mAmPm == AM)
        {
            text = "AM"
        }
        else
        {
            text = "PM"
        }

        val width = rec.width()

        textPaint.textSize = measuredWidth.toFloat() * 0.03f
        textPaint.textAlign = Paint.Align.LEFT
        cavans.drawText(text,mCenterX + width / 2 + 10f, mCenterY + rec.height() / 2,textPaint)

    }


    private fun drawDegree(canvas: Canvas?)
    {
        if(canvas == null) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = mOuterRadius * 0.010f
        paint.color = Color.WHITE

        val innerRadius = 0.90f * mOuterRadius


        var i = 0
        while (i < 360) {

            if (i % 90 !== 0 && i % 15 != 0)
                paint.alpha = 144
            else {
                paint.alpha = 255
            }

            val startX = mCenterX + innerRadius * cos(Math.toRadians(i.toDouble())).toFloat()
            val startY = mCenterY - innerRadius * sin(Math.toRadians(i.toDouble())).toFloat()

            val stopX = mCenterX + mOuterRadius * cos(Math.toRadians(i.toDouble())).toFloat()
            val stopY = mCenterY - mOuterRadius * sin(Math.toRadians(i.toDouble())).toFloat()


            canvas.drawLine(startX, startY, stopX, stopY, paint)
            i += 6 /* Step */

        }
    }

    private fun drawHoursValues(cavans: Canvas?)
    {
        if(cavans == null) return
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = 35f
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.WHITE

        val textRadius = mOuterRadius * 0.8f
        for(i in 0..11)
        {
            val startX = mCenterX + textRadius * sin(i * Math.PI / 6).toFloat() - textPaint.measureText(mTextArray[i]) / 2
            val startY = mCenterY - textRadius * cos(i * Math.PI / 6).toFloat() + textPaint.measureText(mTextArray[i]) / 2
            cavans.drawText(mTextArray[i],startX,startY,textPaint)
        }


    }


    private fun drawNeedles(cavans: Canvas?)
    {
        if(cavans == null) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.strokeWidth = mOuterRadius * 0.050f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL

        val hourScale = (mHour % 12).toFloat() + mMinute.toFloat() / 60 + mSecond.toFloat() / 3600
        val minuteScale = mMinute.toFloat() + mSecond.toFloat() / 60

        //DrawHour
        var stopX = mCenterX + mHourLen * sin(Math.PI * hourScale / 6).toFloat()
        var stopY = mCenterY - mHourLen * cos(Math.PI * hourScale / 6).toFloat()
        cavans.drawLine(mCenterX,mCenterY,stopX,stopY,paint)

        //DrawMinute
        stopX = mCenterX + mMinuteLen * sin(Math.PI * minuteScale / 30).toFloat()
        stopY = mCenterY - mMinuteLen * cos(Math.PI * minuteScale / 30).toFloat()
        cavans.drawLine(mCenterX,mCenterY,stopX,stopY,paint)


        //DrawSecond
        stopX = mCenterX + mSecondLen * sin(Math.PI * mSecond / 30).toFloat()
        stopY = mCenterY - mSecondLen * cos(Math.PI * mSecond / 30).toFloat()
        paint.color = Color.parseColor("#FF3C81F7")
        cavans.drawLine(mCenterX,mCenterY,stopX,stopY,paint)

    }

    private fun drawCenter(cavans: Canvas?)
    {
        if(cavans == null) return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = Color.GRAY
        cavans.drawCircle(mCenterX,mCenterY,mOuterRadius * 0.05f,paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        cavans.drawCircle(mCenterX,mCenterY,mOuterRadius * 0.05f,paint)
    }

    fun setShowBoard(value: Boolean)
    {
        mShowBoard = value
        invalidate()
    }

    fun getShowBoard(): Boolean
    {
        return mShowBoard
    }

}