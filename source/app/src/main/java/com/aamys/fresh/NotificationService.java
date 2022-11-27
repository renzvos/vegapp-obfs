package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.aamys.fresh.MainActivity;import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class NotificationService extends Service {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    NotificationManager manager;
    NotificationChannel channel;
    String channelId = "AMYS";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("App-Notification", "onStartCommand: Service ");

        HashMap bhashMap = new HashMap();

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE) ;
        firestore.collection("orders").whereEqualTo("uid", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                {
                    bhashMap.put(documentSnapshot.getId(),false);
                    Log.i("App-Notification", "onEvent: Setting all to false");
                    firestore.collection("orders").document(documentSnapshot.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            FirebaseOrderClass orderClass = value.toObject(FirebaseOrderClass.class);

                            if (!(boolean) bhashMap.get(value.getId())) {
                                Log.i("App-Notification", "onEvent: Got Service Start Variable");
                                bhashMap.put(value.getId(), true);
                            } else {

                                if (orderClass.payment) {

                                    if (orderClass.cancelled) {

                                        addNotification("Your Order of order ID "+orderClass.timestamp+" has been cancelled");

                                    } else if (orderClass.approved) {

                                        if (orderClass.intransit) {

                                            if (orderClass.delivered) {

                                                  addNotification("Your Order with order ID "+orderClass.timestamp+" is Delivered");
                                            } else if (!orderClass.delivered) {

                                                addNotification("Your Order of order ID " + orderClass.timestamp + " is in Transit");
                                            }
                                        } else if (!orderClass.intransit) {

                                            addNotification("Your Order of order ID " + orderClass.timestamp + " has been approved");

                                        }
                                    } else if (!orderClass.approved) {

                                        addNotification("Your Order of order ID " + orderClass.timestamp + " has been placed");


                                    }

                                } else if (!orderClass.payment) {

                                    addNotification("Payment Failed");

                                }


                            }
                        }
                    });
                }


            }
        });


        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    private void addNotification(String title) {

       Notification.Builder builder;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(getApplicationContext(),"AMYS");
            builder.setSmallIcon(android.R.drawable.ic_dialog_alert);

            channel = new  NotificationChannel(channelId, "OrderStatus", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);

            builder = new Notification.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_logo))
                    .setContentIntent(pendingIntent);
        } else {

            builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_background))
                    .setContentIntent(pendingIntent);
        }
        manager.notify(1234, builder.build());

    }
}