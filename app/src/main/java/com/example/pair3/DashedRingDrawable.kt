import android.graphics.*
import android.graphics.drawable.Drawable

class DashedRingDrawable : Drawable() {

    private val mPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    private val mColorArray = intArrayOf(Color.RED, Color.GRAY , Color.GREEN)

    private var mRingOuterDiameter = 0f
    private var mRingOuterRadius = 0f
    private var mRingInnerRadius = 0f

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        check(bounds.width() == bounds.height()) {
            "Width must be equal to height. (It's a circle.)"
        }
        mRingOuterDiameter = bounds.width().toFloat()
        mRingOuterRadius = mRingOuterDiameter / 2
        mRingInnerRadius = (mRingOuterDiameter - STROKE_WIDTH) / 2
        val dashLength = getNewDashLength()
        mPaint.pathEffect = DashPathEffect(floatArrayOf(dashLength, GAP_LENGTH), 0f)
        mPaint.shader = SweepGradient(mRingOuterRadius, mRingOuterRadius, mColorArray, null)
    }

    override fun draw(canvas: Canvas) {
        // Effet de chemin pour les tirets
        mPaint.pathEffect = DashPathEffect(floatArrayOf(DASH_LENGTH, GAP_LENGTH), 0f)

        // Dessiner le rectangle avec le contour en tirets
        canvas.drawRect(0f, 0f, mRingOuterDiameter, mRingOuterDiameter, mPaint)
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    // Ajuste la longueur du trait pour que nous terminions sur un espace et non au milieu d'un trait.
    private fun getNewDashLength(): Float {
        val circumference = Math.PI.toFloat() * mRingInnerRadius * 2
        val dashCount = (circumference / (DASH_LENGTH + GAP_LENGTH)).toInt()
        val newDashLength = (circumference - dashCount * GAP_LENGTH) / dashCount
        return newDashLength
    }

    companion object {
        const val STROKE_WIDTH = 15f
        const val DASH_LENGTH = 50f
        const val GAP_LENGTH = 15f
    }
}
