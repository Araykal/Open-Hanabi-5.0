package life.hanabi.gui.notification

import kotlin.math.abs

class NotificationsManager {
    var notifications = ArrayList<Notification>()

    fun add(notification: Notification) {
        notification.y = (notifications.size * 25).toFloat()
        notifications.add(notification)
    }

    fun draw() {
        var remove: Notification? = null
        for (notification in notifications) {
            if (notification.x < 1f && notification.timer && notification.`in`) {
                notification.`in` = false
            }
            if (notification.x >= 18 && !notification.`in`) {
                remove = notification
            }
            if (notification.`in`) {
                notification.x = notification.animationUtils.animate(0f, notification.x, 0.1f)
            } else {
                notification.x =
                    notification.animationUtils.animate((20).toFloat(), notification.x, 0.15f)
            }
            notification.onRender()
        }
        if (remove != null) {
            notifications.remove(remove)
        }
    }
}