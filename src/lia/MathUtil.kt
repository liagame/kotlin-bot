package lia

object MathUtil {
    /**
     * Calculates the distance between the points (x1,y1) and (x2,y2).
     * @return distance
     */
    fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return kotlin.math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }

    /**
     * Calculates the angle of a vector from (x1,y1) to (x2,y2) relative to the x-axis.
     * Angles are measured from x-axis in counter-clockwise direction and between 0 and 360.
     * @return angle in degrees
     */
    fun angle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        var angle = Math.toDegrees(kotlin.math.atan2(y2 - y1, x2 - x1).toDouble()).toFloat()
        if (angle < 0) angle += 360f
        return angle
    }

    /**
     * Returns an angle between where is unit looking at (it's orientation) and the specified point.
     * If the angle is 0, unit looks directly at a point, if angle is negative the unit looks to the left
     * side of the point and needs to rotate right to decrease the angle and if the angle is positive the
     * unit looks to the right side of the point and it needs to turn left to look closer to the point.
     * @return angle in degrees between -180 to 180 degrees
     */
    fun angleBetweenUnitAndPoint(unit: UnitData, x: Float, y: Float): Float {
        return angleBetweenUnitAndPoint(unit.x, unit.y, unit.orientationAngle, x, y)
    }

    fun angleBetweenUnitAndPoint(unitX: Float, unitY: Float, unitOrientationAngle: Float,
                                 pointX: Float, pointY: Float): Float {

        val unitToPointAngle = angle(unitX, unitY, pointX, pointY)

        var angle = unitToPointAngle - unitOrientationAngle

        if (angle > 180) angle -= 360
        else if (angle < -180) angle += 360
        return angle
    }
}
