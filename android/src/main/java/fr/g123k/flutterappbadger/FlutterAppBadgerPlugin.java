package fr.g123k.flutterappbadger;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import fr.g123k.flutterappbadge.flutterappbadger.R;
import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * FlutterAppBadgerPlugin
 */
public class FlutterAppBadgerPlugin implements MethodCallHandler, FlutterPlugin {

    private static final String CHANNEL_NAME = "g123k/flutter_app_badger";
    private Context applicationContext;
    private MethodChannel channel;
    private NotificationManager notificationManager;
    private int notificationId = 0;

    public static boolean isOneBrand(String brand) {
        String manufacturer = Build.MANUFACTURER;
        Log.d("gogogo", manufacturer);
        return brand.equalsIgnoreCase(manufacturer);
    }

    /**
     * Plugin registration.
     */

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
        applicationContext = flutterPluginBinding.getApplicationContext();
        notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding flutterPluginBinding) {
        channel.setMethodCallHandler(null);
        applicationContext = null;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("updateBadgeCount")) {
            notificationManager.cancel(notificationId);
            notificationId++;

            Boolean brand = isOneBrand("xiaomi");
            Log.e("gogo", brand.toString());
            if (brand) {
                Notification.Builder builder = new Notification.Builder(applicationContext)
                        .setContentTitle("")
                        .setContentText("")
                        .setSmallIcon(R.drawable.app_icon);

                Notification notification = builder.build();
                ShortcutBadger.applyNotification(applicationContext, notification, Integer.parseInt(call.argument("count").toString()));
                notificationManager.notify(notificationId, notification);
            } else {
                ShortcutBadger.applyCount(applicationContext, Integer.parseInt(call.argument("count").toString()));
            }
            result.success(null);
        } else if (call.method.equals("removeBadge")) {
            ShortcutBadger.removeCount(applicationContext);
            result.success(null);
        } else if (call.method.equals("isAppBadgeSupported")) {
            result.success(ShortcutBadger.isBadgeCounterSupported(applicationContext));
        } else {
            result.notImplemented();
        }
    }
}
