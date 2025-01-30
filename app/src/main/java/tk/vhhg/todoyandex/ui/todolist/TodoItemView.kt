package tk.vhhg.todoyandex.ui.todolist

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import tk.vhhg.todoyandex.R
import kotlin.math.roundToInt

class TodoItemView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    companion object {
        private const val TITLE_TEXT_SIZE = 16
        private const val SUBTITLE_TEXT_SIZE = 14
        private const val UNCHECKED_CHECKBOX_STROKE_WIDTH = 2
        private const val RED_CHECKBOX_INNER_COLOR_ALPHA = 255 * 16 / 100
        private const val TEXT_MAX_LINES = 3
        private const val SOMETHING_WENT_WRONG_COLOR = Color.GREEN
        private const val CB_ANIMATION_RADIUS = 16
    }

    // <Util functions>

    private fun dp(px: Int): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), resources.displayMetrics
    )

    private fun Paint.applyPaintColor(color: Int, attrs: AttributeSet? = null) {
        context.obtainStyledAttributes(attrs, intArrayOf(color)).use { a ->
            this.color = a.getColor(0, SOMETHING_WENT_WRONG_COLOR)
        }
    }

    // </Util functions>

    private val isNight =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    // <Paints>

    private var subtitleTextPaint = TextPaint().apply {
        textSize = dp(SUBTITLE_TEXT_SIZE)
        applyPaintColor(com.google.android.material.R.attr.colorOutline, attrs)
    }

    private val titleTextPaint = TextPaint().apply {
        color = if (isNight) Color.WHITE else Color.BLACK
        textSize = dp(TITLE_TEXT_SIZE)
    }

    private val strikeThroughTitleTextPaint = TextPaint().apply {
        color = Color.GRAY
        textSize = dp(TITLE_TEXT_SIZE)
        isStrikeThruText = true
    }

    private val checkboxPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = dp(UNCHECKED_CHECKBOX_STROKE_WIDTH)
        applyPaintColor(com.google.android.material.R.attr.colorOutlineVariant, attrs)
    }

    private val redCheckboxPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(UNCHECKED_CHECKBOX_STROKE_WIDTH)
        applyPaintColor(com.google.android.material.R.attr.colorError, attrs)
    }

    private val redBgBgPaint = Paint().apply {
        style = Paint.Style.FILL
        applyPaintColor(com.google.android.material.R.attr.colorSurfaceContainerHighest, attrs)
    }

    private val redBgPaint = Paint().apply {
        style = Paint.Style.FILL
        applyPaintColor(com.google.android.material.R.attr.colorError, attrs)
        alpha = RED_CHECKBOX_INNER_COLOR_ALPHA
    }

    private val infoPaint = Paint().apply {
        isAntiAlias = true
    }

    private val cbAnimationPaint = Paint().apply {
        color = Color.GRAY
        alpha = 80
        style = Paint.Style.FILL
    }

    // </Paints>


    // <Fields>

    var dateText: String = ""
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }
    var text: String = ""
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }
    var isImportant: Boolean? = null
        set(value) {
            field = value
            invalidate()
        }

    private var checkedClickListener: OnClickListener? = null
    fun setCheckedClickListener(l: OnClickListener?) {
        checkedClickListener = l
    }

    private var cbAnimRadius = 0F

    private val checkboxAnimator = ValueAnimator.ofFloat(0F, dp(CB_ANIMATION_RADIUS)).apply {
        duration = 250
        addUpdateListener { v ->
            cbAnimRadius = v.animatedValue as Float
            if (cbAnimRadius >= dp(16)) cbAnimRadius = 0F
            invalidate()
        }
    }

    var checked: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var infoClickListener: OnClickListener? = null
    fun setInfoClickListener(l: OnClickListener) {
        infoClickListener = l
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.TodoItemView).use { a ->
            dateText = a.getText(R.styleable.TodoItemView_dateText)?.toString() ?: ""
            text = a.getText(R.styleable.TodoItemView_android_text)?.toString() ?: ""
            isImportant = a.getText(R.styleable.TodoItemView_important)?.contentEquals("true")
            checked =
                a.getText(R.styleable.TodoItemView_android_checked)?.contentEquals("true") ?: false
        }
    }

    // </Fields>


    // <Rects>

    private val uncheckedCheckBoxRect = dp(20).let { rectStart ->
        val rectWidth = dp(16)
        RectF(rectStart, rectStart, rectStart + rectWidth, rectStart + rectWidth)
    }

    private val checkedCheckBoxRect = dp(16).let { rectStart ->
        val rectWidth = dp(24)
        RectF(rectStart, rectStart, rectStart + rectWidth, rectStart + rectWidth)
    }

    // </Rects>


    // <Bitmaps>

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

    private val greenCheckedBitmap = with(
        AppCompatResources.getDrawable(
            context, R.drawable.green_checkbox_checked
        ) as VectorDrawable
    ) {
        val bitmap = Bitmap.createBitmap(
            intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        bitmap
    }

    private val redCheckedBitmap = with(
        AppCompatResources.getDrawable(
            context, R.drawable.red_checkbox_checked
        ) as VectorDrawable
    ) {
        val bitmap = Bitmap.createBitmap(
            intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        bitmap
    }

    private val checkedBitmap get() = if (isImportant == true) redCheckedBitmap else greenCheckedBitmap

    // </Bitmaps>


    // <StaticLayout>
    private var staticLayout: StaticLayout? = null
    private var strikeThruStaticLayout: StaticLayout? = null
    // </StaticLayout>


    // <View lifecycle>

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width: Int = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(widthMeasureSpec)

            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)

            else -> MeasureSpec.getSize(widthMeasureSpec)
        }

        val heightSpecSpace = MeasureSpec.getSize(heightMeasureSpec)
        val preferredHeight = dp(16 + 24 + 16).roundToInt()
        val ww =
            width - uncheckedCheckBoxRect.width() - infoBitmap.width - dp(16 * 4) // 4 = Two paddings around checkbox + two paddings around infoBitmap
        var w = ww
        staticLayout = null
        strikeThruStaticLayout = null
        while ((staticLayout?.lineCount ?: Int.MAX_VALUE) > TEXT_MAX_LINES) {
            val txt = TextUtils.ellipsize(
                text, titleTextPaint, w * TEXT_MAX_LINES, TextUtils.TruncateAt.END
            ).toString()

            staticLayout = StaticLayout.Builder.obtain(
                txt, 0, txt.length, titleTextPaint, ww.toInt()
            ).build()
            w -= 2 // arbitrary small number
        }
        strikeThruStaticLayout = StaticLayout.Builder.obtain(
            staticLayout!!.text,
            0,
            staticLayout!!.text.length,
            strikeThroughTitleTextPaint,
            ww.toInt()
        ).build()

        val textVerticalPadding = dp(32).toInt()
        val height: Int = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST -> {
                minOf(
                    heightSpecSpace, maxOf(
                        preferredHeight, staticLayout!!.height + textVerticalPadding
                    )
                )
            }

            MeasureSpec.EXACTLY -> heightSpecSpace

            else -> maxOf(
                preferredHeight, staticLayout!!.height + textVerticalPadding
            )
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        // Checkbox
        if (checked) {
            canvas.drawBitmap(checkedBitmap, null, checkedCheckBoxRect, infoPaint)
        } else if (isImportant != true) {
            canvas.drawRoundRect(uncheckedCheckBoxRect, dp(1), dp(1), checkboxPaint)
        } else {
            // See red unchecked checkbox resource for more info
            canvas.drawRoundRect(uncheckedCheckBoxRect, dp(1), dp(1), redCheckboxPaint)
            canvas.drawRect(dp(5 + 16), dp(5 + 16), dp(5 + 16 + 14), dp(5 + 16 + 14), redBgBgPaint)
            canvas.drawRect(dp(5 + 16), dp(5 + 16), dp(5 + 16 + 14), dp(5 + 16 + 14), redBgPaint)
        }
        canvas.drawCircle(dp(16+12), dp(16+12), cbAnimRadius, cbAnimationPaint)

        // InfoBitmap
        canvas.drawBitmap(
            infoBitmap,
            width - infoBitmap.width - dp(16),
            (height - infoBitmap.height) / 2f,
            infoPaint
        )

        // Text
        canvas.save()
        canvas.translate(dp(24 + 16 * 2), dp(16))
        if (checked) {
            strikeThruStaticLayout?.draw(canvas)
        } else {
            staticLayout?.draw(canvas)
        }
        staticLayout?.let { sl ->
            canvas.drawText(dateText, 0, dateText.length, 0f, sl.height + dp(14), subtitleTextPaint)
        }

        canvas.restore()
    }

    // </View lifecycle>


    // <Обработка нажатий>

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            private val pivot = dp(16 + 24 + 16)
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if ((e.getX(0)) <= pivot) {
                    cbAnimRadius = 0F
                    checkboxAnimator.start()
                    checked = !checked
                    checkedClickListener?.onClick(this@TodoItemView)
                } else {
                    infoClickListener?.onClick(this@TodoItemView)
                }
                return true
            }
        })

    override fun onTouchEvent(event: MotionEvent?): Boolean =
        event?.let { gestureDetector.onTouchEvent(event) } != null

    // </Обработка нажатий>


    // <Сохранение состояний>

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putChar(
            "i", when (isImportant) {
                true -> 't'
                false -> 'f'
                null -> 'n'
            }
        )
        bundle.putString("title", text)
        bundle.putString("date", dateText)
        bundle.putBoolean("checked", checked)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            isImportant = when (state.getChar("i", 'n')) {
                'f' -> false
                't' -> true
                else -> null
            }
            text = state.getString("title", "")
            dateText = state.getString("date", "")
            checked = state.getBoolean("checked", false)
        }
        super.onRestoreInstanceState((state as? Bundle)?.getParcelable("superState"))
    }

    // </Сохранение состояний>
}