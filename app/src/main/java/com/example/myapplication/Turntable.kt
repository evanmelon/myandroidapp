package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
//import com.baijiayun.liveuibase.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class Turntable : View  {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_turntable)
//    }
    constructor(context: Context) : super(context) {
        init(null)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    // 默认尺寸
    private val defaultSize = 150
    // 扇形半径比例
    private val sectorProportion = 0.84F
    // 外圈小圆半径比例
    private val littleCircleProportion = 0.02F
    // 中心圆比例
    private val centerProportion = 0.35F
    // 中心圆间隔比例
    private val centerDistanceProportion = 0.05F
    // 转盘文字比例
    private val sectorTextSizeProportion = 0.2F
    // 中心文字比例
    private val centerTextProportion = 0.3F
    // 背景颜色（大圆）颜色
    private var bgColor = 0
    // 扇形颜色
    private var sectorColor = 0
    // 中心圆圈边框颜色
    private var centerStrokeColor = 0
    // 中心圆圈底层颜色
    private var centerBottomColor = 0
    // 开始按钮背景颜色 = 扇形选中背景颜色
    private var startColor = 0
    // 扇形文字颜色
    private var sectorTextColor = 0
    // 开始按钮文字颜色 = 扇形选中文字颜色
    private var startTextColor = 0
    // 选项列表
    private var selectList = arrayListOf("1", "2", "3", "4", "5", "6")
    // 当前选中的选项下标
    private var selectedIndex = -1
    // 即将选中的选项下标
    private var willSelectIndex = 4
    // 转盘转动的时间 seconds
    private var turnTime = 5
    // 转盘旋转
    private var disposableOfTurn: Disposable? = null
    // 转盘按钮文字
    private var topText = ""
    // 初始速度 40ms 一个
    private var startSpeed = 40
    // 结束速度 200ms 一个
    private var endSpeed = 200
    // 速度列表 1s 一个速度
    private var speedList = mutableListOf<Int>()
    // 当前速度
    private var currentSpeed = 1
    // 初始是否可点击
    private var initialClickable = false

    private val mPaint by lazy {
        Paint()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getRealSize(widthMeasureSpec)
        val height = getRealSize(heightMeasureSpec)
        val final = min(width, height)
        setMeasuredDimension(final, final)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint.isAntiAlias = true
        drawLargeCircle(canvas)
        drawSector(canvas)
        drawCenter(canvas)
        drawText(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isClickCenter(event.x, event.y)) {
                    performClick()
                }
                return isClickCenter(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                return false
            }
            MotionEvent.ACTION_UP -> {
                return false
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun setClickable(clickable: Boolean) {
        super.setClickable(clickable)
        invalidate()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * 设置选项列表
     * @param list 选项列表，size >= 3
     */
    fun setSelectList(list: ArrayList<String>) {
        if (list.size < 3) {
            return
        }
        selectList = list
    }

    /**
     * 设置要选中的选项下标
     * @param index 选项下标
     */
    private fun setWillSelectIndex(index: Int) {
        if (willSelectIndex != index) {
            willSelectIndex = index
        }
        if (willSelectIndex >= selectList.size) {
            willSelectIndex = selectList.size - 1
        }
        if (willSelectIndex < 0) {
            willSelectIndex = 0
        }
        willSelectIndex = (willSelectIndex + 3) % selectList.size
    }

    /**
     * 设置转盘转动时间
     * @param time 转盘转动时间 s
     */
    private fun setTurnTime(time: Int) {
        turnTime = time
    }

    /**
     * 设置背景颜色
     */
    override fun setBackgroundColor(color: Int) {
        this.bgColor = color
        invalidate()
    }

    /**
     * 设置扇形颜色
     */
    fun setSectorColor(color: Int) {
        this.sectorColor = color
        invalidate()
    }

    /**
     * 设置阴影颜色
     */
    fun setStrokeColor(color: Int) {
        this.centerStrokeColor = color
        invalidate()
    }

    /**
     * 设置中心圆底层颜色
     */
    fun setCenterBottomColor(color: Int) {
        this.centerBottomColor = color
        invalidate()
    }

    /**
     * 设置开始按钮背景颜色和扇形选中颜色
     */
    fun setStartBackgroundColor(color: Int) {
        this.startColor = color
        invalidate()
    }

    /**
     * 设置扇形文字颜色
     */
    fun setSectorTextColor(color: Int) {
        this.sectorTextColor = color
        invalidate()
    }

    /**
     * 设置开始按钮文字颜色
     */
    fun setStartTextColor(color: Int) {
        this.startTextColor = color
        invalidate()
    }

    /**
     * 开始转动转盘
     * @param selectIndex 最终要选中的值
     * @param time 转盘转动时间
     */
    fun startTurn(selectIndex: String, time: Int) {
        setWillSelectIndex(selectList.indexOf(selectIndex))
        release()
        setTurnTime(time)
        topText = turnTime.toString()
        invalidate()
        computeSpeed()
        currentSpeed = speedList[0]
        initialClickable = isClickable
        isClickable = false
        disposableOfTurn = Observable.interval(0, 1, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it >= currentSpeed && it % currentSpeed == 0L) {
                    selectedIndex ++
                    selectedIndex %= selectList.size
                    invalidate()
                }
                if (it >= 1000 && it % 1000 == 0L) {
                    currentSpeed = speedList[(it / 1000).toInt()]
                    topText = (turnTime - it / 1000).toString()
                    invalidate()
                }
                if (it == turnTime * 1000L + 500) {
                    if (initialClickable) {
                        isClickable = true
                        topText = "开始"
                    } else {
                        topText = ""
                    }
                    disposableOfTurn?.dispose()
                    invalidate()
                }
            }
    }

    /**
     * 转盘是否正在转动
     */
    fun isTurning() = disposableOfTurn != null && !disposableOfTurn!!.isDisposed

    /**
     * 释放资源，父容器销毁时须调用
     */
    fun release() {
        disposableOfTurn?.dispose()
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TurntableView)
        bgColor = typedArray.getColor(R.styleable.TurntableView_background_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_background_color))
        sectorColor = typedArray.getColor(R.styleable.TurntableView_sector_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_sector_color))
        centerStrokeColor = typedArray.getColor(R.styleable.TurntableView_center_stroke_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_center_stroke_color))
        centerBottomColor = typedArray.getColor(R.styleable.TurntableView_center_bottom_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_center_bottom_color))
        sectorTextColor = typedArray.getColor(R.styleable.TurntableView_sector_text_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_sector_text_color))
        startColor = typedArray.getColor(R.styleable.TurntableView_start_btn_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_start_btn_color))
        startTextColor = typedArray.getColor(R.styleable.TurntableView_start_text_color, ContextCompat.getColor(context, R.color.bjy_base_turntable_default_start_text_color))
        typedArray.recycle()
    }

    private fun getRealSize(measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return if (mode == MeasureSpec.EXACTLY) {
            size
        } else {
            defaultSize
        }
    }

    private fun drawLargeCircle(canvas: Canvas?) {
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight / 2F
        val radius = measuredWidth / 2F
        mPaint.color = bgColor
        canvas?.drawCircle(centerX, centerY, radius - 10, mPaint)
        mPaint.color = centerStrokeColor
        mPaint.style = Paint.Style.STROKE
        for (i in 0 until 10) {
            mPaint.alpha = 5 * (i + 1)
            canvas?.drawCircle(centerX, centerY, radius - i, mPaint)
        }
    }

    private fun drawSector(canvas: Canvas?) {
        val sectorRadius = (measuredWidth / 2) * sectorProportion
        val sectorCenterRadius = (measuredWidth / 2) * 0.07f
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight / 2F
        mPaint.color = sectorColor
        mPaint.style = Paint.Style.FILL
        var angle = 360F / selectList.size
        for (i in 0 until selectList.size) {
            if ((i + 2) % selectList.size == selectedIndex) {
                mPaint.color = startColor
            } else {
                mPaint.color = sectorColor
            }
            val cx = centerX + cos(angleToRadian(angle * i)).toFloat() * sectorCenterRadius
            val cy = centerY + sin(angleToRadian(angle * i)).toFloat() * sectorCenterRadius
            val oval = RectF(cx - sectorRadius, cy - sectorRadius, cx + sectorRadius, cy + sectorRadius)
            canvas?.drawArc(oval, (angle * i) - (angle / 2), angle, true, mPaint)
        }
        mPaint.color = sectorColor
        mPaint.alpha = 50
        val littleCircleCount = selectList.size * 2
        angle = 360F / littleCircleCount
        for (i in 0 until littleCircleCount) {
            val littleCenterX = centerX + cos(angleToRadian(angle * i)).toFloat() * (((measuredWidth / 2) + sectorRadius + sectorCenterRadius) / 2F - 5)
            val littleCenterY = centerY + sin(angleToRadian(angle * i)).toFloat() * (((measuredWidth / 2) + sectorRadius + sectorCenterRadius) / 2F - 5)
            canvas?.drawCircle(littleCenterX, littleCenterY, (measuredWidth / 2) * littleCircleProportion, mPaint)
        }
    }

    private fun drawCenter(canvas: Canvas?) {
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight / 2F
        mPaint.color = bgColor
        mPaint.alpha = 255
        canvas?.drawCircle(centerX, centerY, (measuredWidth / 2) * centerProportion, mPaint)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = centerStrokeColor
        mPaint.strokeWidth = 1F
        for (i in 0..(centerX * centerDistanceProportion).toInt()) {
            mPaint.alpha = 5 * i
            canvas?.drawCircle(centerX, centerY, centerX * centerProportion - i, mPaint)
        }
        mPaint.color = bgColor
        mPaint.style = Paint.Style.FILL
        canvas?.drawCircle(centerX, centerY, (centerX * centerProportion) -  (centerX * centerDistanceProportion), mPaint)
        mPaint.color = centerBottomColor
        canvas?.drawCircle(centerX, centerY, (centerX * centerProportion) -  ((centerX * centerDistanceProportion) * 1.5F), mPaint)
        if (isClickable) {
            mPaint.color = startColor
        } else {
            mPaint.color = bgColor
        }
        canvas?.drawCircle(centerX, centerY, (centerX * centerProportion) -  ((centerX * centerDistanceProportion) * 2), mPaint)
    }

    private fun drawText(canvas: Canvas?) {
        val radius = measuredWidth / 3F
        val centerX = measuredWidth / 2F
        val centerY = measuredHeight / 2F
        val angle = 360F / selectList.size
        mPaint.color = sectorTextColor
        mPaint.textSize = centerX * sectorTextSizeProportion
        for (i in 0 until selectList.size) {
            if (selectedIndex == (i + 2) % selectList.size) {
                mPaint.color = Color.WHITE
            } else {
                mPaint.color = sectorTextColor
            }
            val cx = centerX + cos(angleToRadian(angle * i)).toFloat() * radius - ((measuredWidth / 2) * sectorTextSizeProportion * 0.42F)
            val cy = centerY + sin(angleToRadian(angle * i)).toFloat() * radius + ((measuredWidth / 2) * sectorTextSizeProportion * 0.42F)
            canvas?.drawText(selectList[(i + 2) % selectList.size], cx, cy, mPaint)
        }
        if (isClickable) {
            mPaint.color = startTextColor
            mPaint.textSize = centerX * sectorTextSizeProportion * 0.8f
            topText = "开始"
            canvas?.drawText(topText, centerX * (1 - 0.8F * sectorTextSizeProportion), centerY * (1 + 0.3F * sectorTextSizeProportion), mPaint)
        } else {
            mPaint.color = sectorTextColor
            mPaint.textSize = centerX * centerTextProportion
            canvas?.drawText(topText, centerX * (1 - 0.3F * centerTextProportion), centerY * (1 + 0.3F * centerTextProportion), mPaint)
        }
    }

    private fun angleToRadian(angle: Float) = angle * PI / 180

    private fun isClickCenter(x: Float, y: Float): Boolean {
        val centerX = measuredWidth / 2
        val centerY = measuredHeight / 2
        val radius = (measuredWidth / 2) * centerProportion
        if (x < centerX - radius || x > centerX + radius) {
            return false
        }
        if (y > centerY + radius || y < centerY - radius) {
            return false
        }
        return true
    }


    // 确保最后可以选中要选择的
    private fun computeSpeed() {
        if (selectedIndex < 0) {
            selectedIndex = 0
        }
        selectedIndex %= selectList.size
        var distance = if (willSelectIndex >= selectedIndex) {
            willSelectIndex   - selectedIndex
        } else {
            selectList.size - selectedIndex + willSelectIndex
        }
        distance %= selectList.size
        val speedDistance = (endSpeed - startSpeed) / (selectList.size - 2)
        speedList.clear()
        speedList.add(startSpeed)
        for (i in 1 until selectList.size - 1) {
            speedList.add(startSpeed + (i * speedDistance))
        }
        speedList.add(endSpeed)
        for (i in 0 until speedList.size) {
            var count = (1000 / speedList[i])
            if (count < selectList.size) {
                count = selectList.size
            } else {
                count -= (count % selectList.size)
            }
            speedList[i] = 1000 / count
        }
        for (i in 0 until distance) {
            val count = (1000 / speedList[i]) + 1
            speedList[i] = 1000 / count
        }
    }


}