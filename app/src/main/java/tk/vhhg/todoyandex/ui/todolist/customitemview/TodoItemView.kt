package tk.vhhg.todoyandex.ui.todolist.customitemview

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.VectorDrawable
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import tk.vhhg.todoyandex.R
import kotlin.math.roundToInt

class TodoItemView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    private fun dp(px: Int): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), resources.displayMetrics
    )

    private val isNight =
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }


    private var dateText: String = ""
        set(value) {
            field = value
            invalidate()
        }
    private var todoItemText: String = ""
        set(value) {
            field = value
            invalidate()
        }
    private var important: Boolean? = null
        set(value) {
            field = value
            invalidate()
        }
    private var checked: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var subtitleTextPaint: TextPaint = TextPaint().apply {
        textSize = dp(14)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TodoItemView).use { a ->
            dateText = a.getText(R.styleable.TodoItemView_dateText)?.toString() ?: ""
            todoItemText = a.getText(R.styleable.TodoItemView_android_text)?.toString() ?: ""
            important = a.getText(R.styleable.TodoItemView_important)?.contentEquals("true")
            checked = a.getText(R.styleable.TodoItemView_android_checked)?.contentEquals("true") ?: true
        }

        context.obtainStyledAttributes(
            attrs,
            intArrayOf(
                com.google.android.material.R.attr.colorOutline,
            )
        ).use { a ->
            subtitleTextPaint.color = a.getColor(0, Color.GREEN)
        }

    }


    private val titleTextPaint = TextPaint().apply {
        color = if (isNight) Color.WHITE else Color.BLACK
        textSize = dp(16)
    }


    private val checkBoxRect = with(dp(20)) {
        RectF(this, this, this + dp(16), this + dp(16))
    }

    private val checkedBoxRect = with(dp(16)) {
        RectF(this, this, this + dp(24), this + dp(24))
    }

    private val checkboxPaint = Paint().apply {
        val tv = TypedValue()
        context.obtainStyledAttributes(
            tv.data,
            intArrayOf(com.google.android.material.R.attr.colorOutlineVariant)
        ).use { a ->
            color = a.getColor(0, Color.GREEN)
        }
        //color = Color.BLACK
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = dp(2)
    }

    private val redCheckboxPaint = Paint().apply {
        val tv = TypedValue()
        context.obtainStyledAttributes(
            tv.data,
            intArrayOf(com.google.android.material.R.attr.colorError)
        ).use { a ->
            color = a.getColor(0, Color.GREEN)
        }
        style = Paint.Style.STROKE
        strokeWidth = dp(2)
    }

    private val redBgBgPaint = Paint().apply {
        val tv = TypedValue()
        context.obtainStyledAttributes(
            tv.data,
            intArrayOf(com.google.android.material.R.attr.colorSurfaceContainerHighest)
        ).use { a ->
            color = a.getColor(0, Color.GREEN)
        }
        style = Paint.Style.FILL
    }

    private val redBgPaint = Paint().apply {
        val tv = TypedValue()
        context.obtainStyledAttributes(
            tv.data,
            intArrayOf(com.google.android.material.R.attr.colorError)
        ).use { a ->
            color = a.getColor(0, Color.GREEN)
        }
        style = Paint.Style.FILL
        alpha = 255*16/100
    }

    private val infoPaint = Paint().apply {
        isAntiAlias = true
    }

    private val infoBitmap =
        with(AppCompatResources.getDrawable(context, R.drawable.info) as VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth * 6 / 7, intrinsicHeight * 6 / 7, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
            bitmap
        }

    private val greenCheckedBitmap =
        with(AppCompatResources.getDrawable(context, R.drawable.green_checkbox_checked) as VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
            bitmap
        }

    private val redCheckedBitmap =
        with(AppCompatResources.getDrawable(context, R.drawable.red_checkbox_checked) as VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
            bitmap
        }

    private val checkedBitmap get() = if (important == true) redCheckedBitmap else greenCheckedBitmap

    private var staticLayout: StaticLayout? = null


    override fun onDraw(canvas: Canvas) {
        if (checked) {
            canvas.drawBitmap(
                checkedBitmap,
                null,
                checkedBoxRect,
                infoPaint
            )
        } else if (important != true) {
            canvas.drawRoundRect(checkBoxRect, dp(1), dp(1), checkboxPaint)
        } else {
            canvas.drawRoundRect(checkBoxRect, dp(1), dp(1), redCheckboxPaint)
            canvas.drawRect(dp(5+16), dp(5+16), dp(5+16+14), dp(5+16+14), redBgBgPaint)
            canvas.drawRect(dp(5+16), dp(5+16), dp(5+16+14), dp(5+16+14), redBgPaint)
        }

        canvas.drawBitmap(
            infoBitmap,
            width - infoBitmap.width - dp(16),
            (height - infoBitmap.height) / 2f,
            infoPaint
        )

        canvas.save()
        canvas.translate(dp(24 + 16 * 2), dp(16))
        staticLayout?.let { sl ->
            sl.draw(canvas)
            canvas.drawText(dateText, 0, dateText.length, 0f, sl.height + dp(14), subtitleTextPaint)
        }

        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                MeasureSpec.getSize(widthMeasureSpec)
            }

            MeasureSpec.EXACTLY -> {
                MeasureSpec.getSize(widthMeasureSpec)
            }

            else -> {
                MeasureSpec.getSize(widthMeasureSpec)
            }
        }

        val heightSpecSpace = MeasureSpec.getSize(heightMeasureSpec)
        val preferredHeight = dp(56).roundToInt()
        val ww = (width - checkBoxRect.width() - infoBitmap.width - dp(16 * 4))
        var w = ww
        while (staticLayout == null || staticLayout!!.lineCount > 3) {
            val txt =
                TextUtils.ellipsize(todoItemText, titleTextPaint, w * 3, TextUtils.TruncateAt.END)
                    .toString()

            staticLayout = StaticLayout.Builder.obtain(
                txt, 0, txt.length, titleTextPaint, ww.toInt()
            ).build()
            w -= 2
        }

        val height: Int = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                minOf(
                    heightSpecSpace,
                    maxOf(
                        preferredHeight,
                        staticLayout!!.height + dp(32).toInt()
                    )
                )
                //preferredHeight.coerceAtMost(heightSpecSpace)
            }

            MeasureSpec.EXACTLY -> {
                heightSpecSpace
            }

            else -> {
                preferredHeight
            }
        }

        setMeasuredDimension(width, height)
    }
}