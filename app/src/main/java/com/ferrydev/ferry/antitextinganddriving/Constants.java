package com.ferrydev.ferry.antitextinganddriving;

/**
 * Created by Mohamed on 7/15/2015.
 */
public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
        public static String PREV_ACTION = "com.truiton.foregroundservice.action.prev";
        public static String PLAY_ACTION = "com.truiton.foregroundservice.action.play";
        public static String NEXT_ACTION = "com.truiton.foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";

        public static String SETTINGS_NAME = "settings_pref";
        public static String APP_SWITCH = "app_switch";
        public static String CUSTOM_MESSAGE = "custom_message";
        public static String CUSTOM_SPEED = "custom_speed";
        public static String UNIT_KMH = "unit_kmh";
        public static String UNIT_MPH = "unit_mph";
        public static String CURRENT_SPEED = "current_speed";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}