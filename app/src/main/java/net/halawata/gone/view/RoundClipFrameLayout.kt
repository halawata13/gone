package net.halawata.gone.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import net.halawata.gone.R

class RoundClipFrameLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val path = Path()
    private val rect = RectF()

    private var cornerRadius: Int

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundClipFrameLayout, defStyleAttr, 0)
        cornerRadius = typedArray.getDimensionPixelSize(R.styleable.RoundClipFrameLayout_cornerRadius, 0)

        typedArray.recycle()
    }

    fun setCornerRadius(radiusPixel: Int) {
        if (cornerRadius != radiusPixel) {
            cornerRadius = radiusPixel
            rebuildPath()
            invalidate()
        }
    }

    private fun rebuildPath() {
        path.reset()
        path.addRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), Path.Direction.CW)
        path.close()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        rebuildPath()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }
}
