package com.example.jdroidcoder.directory;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {

    public NotificationService() {
//        super("Wikkno");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int id = loadID();
                        CheckNotificationQuery.CheckModel checkModel = new CheckNotificationQuery(getApplicationContext(), id).accept();
                        if (id != checkModel.getId())
                            if (checkModel.getType() == 0) {
                                JSONNotificationModelFromClient clientModel =
                                        new GetNotificationFromClientQuery(getApplicationContext(), checkModel.getId()).accept();
                                String title = "", shortText = "", longText = "";
                                try {
                                    title = clientModel.getInfo().get(0).getTitle();
                                } catch (Exception e) {

                                }
                                try {
                                    shortText = clientModel.getInfo().get(0).getShort_text();
                                } catch (Exception e) {

                                }
                                try {
                                    longText = clientModel.getInfo().get(0).getLong_text();
                                } catch (Exception e) {

                                }
                                ModelNameList modelNameList = null;
                                try {
                                    modelNameList = clientModel.getClient().get(0);
                                } catch (Exception e) {
                                    createNotification(title, shortText, longText);
                                }

                                createNotification(title, shortText, longText, modelNameList);
                            } else if (checkModel.getType() == -1) {
                                JSONNotificationModelFromInfo info = new GetNotificationFromInfoQuery(getApplicationContext(), checkModel.getId()).accept();
                                String title = "", shortText = "", longText = "";
                                try {
                                    title = info.getInfo().get(0).getTitle();
                                } catch (Exception e) {

                                }
                                try {
                                    shortText = info.getInfo().get(0).getShort_text();
                                } catch (Exception e) {

                                }
                                try {
                                    longText = info.getInfo().get(0).getLong_text();
                                } catch (Exception e) {

                                }
                                createNotification(title, shortText, longText);
                            }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    System.out.println("yes");
                    try {
                        TimeUnit.HOURS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
////        while (true) {
////            try {
////                int id = loadID();
////                CheckNotificationQuery.CheckModel checkModel = new CheckNotificationQuery(getApplicationContext(), id).accept();
////                if (id != checkModel.getId())
////                    if (checkModel.getType() == 0) {
////                        JSONNotificationModelFromClient clientModel =
////                                new GetNotificationFromClientQuery(getApplicationContext(), checkModel.getId()).accept();
////                        String title = "", shortText = "", longText = "";
////                        try {
////                            title = clientModel.getInfo().get(0).getTitle();
////                        } catch (Exception e) {
////
////                        }
////                        try {
////                            shortText = clientModel.getInfo().get(0).getShort_text();
////                        } catch (Exception e) {
////
////                        }
////                        try {
////                            longText = clientModel.getInfo().get(0).getLong_text();
////                        } catch (Exception e) {
////
////                        }
////                        ModelNameList modelNameList = null;
////                        try {
////                            modelNameList = clientModel.getClient().get(0);
////                        } catch (Exception e) {
////                            createNotification(title, shortText, longText);
////                        }
////
////                        createNotification(title, shortText, longText, modelNameList);
////                    } else if (checkModel.getType() == -1) {
////                        JSONNotificationModelFromInfo info = new GetNotificationFromInfoQuery(getApplicationContext(), checkModel.getId()).accept();
////                        String title = "", shortText = "", longText = "";
////                        try {
////                            title = info.getInfo().get(0).getTitle();
////                        } catch (Exception e) {
////
////                        }
////                        try {
////                            shortText = info.getInfo().get(0).getShort_text();
////                        } catch (Exception e) {
////
////                        }
////                        try {
////                            longText = info.getInfo().get(0).getLong_text();
////                        } catch (Exception e) {
////
////                        }
////                        createNotification(title, shortText, longText);
////                    }
////            } catch (NullPointerException e) {
////                e.printStackTrace();
////            }
////            System.out.println("yes");
////            try {
////                TimeUnit.SECONDS.sleep(10);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
//    }

    private int loadID() {
        SharedPreferences sharedPreferences = getSharedPreferences("id", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void createNotification(String title, String shotText, String longText, ModelNameList client) {

        Intent clientIntent = new Intent(this, PersonalPageActivity.class);
        clientIntent.putExtra("person", client);
        clientIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), clientIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(longText);
        builder.setSmallIcon(R.drawable.ic_wikkno);
        builder.setTicker(shotText);
        Drawable d = getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap b = ((BitmapDrawable)d).getBitmap();
        builder.setLargeIcon(b);
        builder.setContentIntent(pendingIntent);
        builder.addAction(new NotificationCompat.Action((int) System.currentTimeMillis(), "Відкрити", pendingIntent));
        builder.setAutoCancel(true);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), builder.build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void createNotification(String title, String shotText, String longText) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(longText);
        builder.setSmallIcon(R.drawable.ic_wikkno);
        builder.setTicker(shotText);
        Drawable d = getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap b = ((BitmapDrawable)d).getBitmap();
        builder.setLargeIcon(b);
        builder.setAutoCancel(true);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}
