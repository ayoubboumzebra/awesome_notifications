package me.carda.awesome_notifications.notifications.managers;

import android.content.Context;

import java.util.List;

import me.carda.awesome_notifications.Definitions;
import me.carda.awesome_notifications.notifications.NotificationBuilder;
import me.carda.awesome_notifications.notifications.NotificationScheduler;
import me.carda.awesome_notifications.notifications.models.NotificationChannelModel;
import me.carda.awesome_notifications.notifications.models.NotificationModel;

public class ScheduleManager {

    private static final SharedManager<NotificationModel> shared = new SharedManager<>("ScheduleManager", NotificationModel.class);

    public static Boolean removeSchedule(Context context, NotificationModel received) {
        return shared.remove(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS, received.content.id.toString());
    }

    public static List<NotificationModel> listSchedules(Context context) {
        return shared.getAllObjects(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS);
    }

    public static void saveSchedule(Context context, NotificationModel received) {
        shared.set(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS, received.content.id.toString(), received);
    }

    public static NotificationModel getScheduleByKey(Context context, String actionKey){
        return shared.get(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS, actionKey);
    }

    public static void cancelAllSchedules(Context context) {
        List<NotificationModel> listSchedules = shared.getAllObjects(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS);
        if(listSchedules != null) {
            for (NotificationModel notificationModel : listSchedules) {
                NotificationScheduler.cancelSchedule(context, notificationModel.content.id);
            }
        }
    }

    public static void cancelSchedulesByChannelKey(Context context, String channelKey) {
        List<NotificationModel> listSchedules = shared.getAllObjects(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS);
        if(listSchedules != null) {
            for (NotificationModel notificationModel : listSchedules) {
                if (notificationModel.content.channelKey.equals(channelKey))
                    NotificationScheduler.cancelSchedule(context, notificationModel.content.id);
            }
        }
    }

    public static void cancelSchedulesByGroupKey(Context context, String groupKey) {
        List<NotificationModel> listSchedules = shared.getAllObjects(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS);
        if(listSchedules != null) {
            for (NotificationModel notificationModel : listSchedules) {
                NotificationChannelModel channelModel = ChannelManager.getChannelByKey(context, notificationModel.content.channelKey);
                String finalGroupKey = NotificationBuilder.getGroupKey(notificationModel.content, channelModel);
                if (finalGroupKey != null && finalGroupKey.equals(groupKey))
                    NotificationScheduler.cancelSchedule(context, notificationModel.content.id);
            }
        }
    }

    public static void cancelSchedule(Context context, Integer id) {
        NotificationModel schedule = shared.get(context, Definitions.SHARED_SCHEDULED_NOTIFICATIONS, id.toString());
        if(schedule != null)
            removeSchedule(context, schedule);
    }

    public static void commitChanges(Context context){
        shared.commit(context);
    }
}
