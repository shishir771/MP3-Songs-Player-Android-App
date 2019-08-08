package com.example.shishirnigam.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
//import static com.example.shishirnigam.PLa.MainActivity.*;
import static com.example.shishirnigam.player.MainActivity.*;
public class MyService extends Service {

    NotificationManager nm;
    Notification notification;
    Notification.Builder nb;

    public MyService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String option=intent.getStringExtra("option");
        switch (option){
            case "play":
                myPlay(pos);
                break;
            case "stop":
                myStop();
                break;
            case "prev":
                myPrev();
                break;
            case "pause":
                myPause();
                break;
            case "next":
                myNext();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void myStop(){
        if(mp!=null){
            myNotification(pos,false);
            mp.stop();
            mp=null;
        }

    }

    public void myPause(){
        if(mp!=null && mp.isPlaying()){
            myNotification(pos,false);
            mp.pause();
        }

    }

    public void myPlay(int pos) {
        if (mp == null) {
           myNotification(pos,true);
            mp= MediaPlayer.create(this, Uri.parse(pathArray.get(pos).toString()));
            //  seekBar.setMax(mp.getDuration());
            // tt.setText(""+(mp.getDuration()/1000));
            int c=(mp.getDuration()/1000);
            int m=c/60;
            int s=(c%60);
            seekBar.setMax(c*1000);
            tt.setText(+m+":"+s);
            mp.start();
        }
        else if(mp!=null && mp.isPlaying()){
            myStop();
            myPlay(pos);
        }
        else if(!mp.isPlaying()){
            mp.start();
        }
        getImage(pos);

    }
    public void myNext(){
        if(pos==pathArray.size()-1){
            pos=0;
        }
        else {
            pos++;
        }
        

        myStop();
        myPlay(pos);
        getImage(pos);
    }
    public void myPrev(){
        if(pos==0){
            pos=pathArray.size()-1;
        }
        else {
            pos--;
        }
        myStop();
        myPlay(pos);
        getImage(pos);
    }

    public void getImage(int pos){
        android.media.MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        mmr.setDataSource(pathArray.get(pos).toString());
        byte [] data=mmr.getEmbeddedPicture();
        if(data!=null){
            Bitmap b= BitmapFactory.decodeByteArray(data,0,data.length);
            image.setImageBitmap(b);
        }
        else{
            image.setImageResource(R.drawable.download);
        }
    }

    void myNotification(int pos,boolean b){
        nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nb=new Notification.Builder(this);
        nb.setContentText(nameArray.get(pos).toString());
        nb.setSubText("Music");
        nb.setSmallIcon(android.R.drawable.ic_media_play);
        nb.setOngoing(b);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.download);
        nb.setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));



        Intent i1= new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,1,i1,0);
        nb.setContentIntent(pi);

        Intent i2= new Intent(this,MyService.class);
        i2.putExtra("option","prev");
        Intent i3= new Intent(this,MyService.class);
        i3.putExtra("option","pause");
        Intent i4= new Intent(this,MyService.class);
        i4.putExtra("option","next");

        
 
        PendingIntent pi2=PendingIntent.getService(this,2,i2,0);
        PendingIntent pi3=PendingIntent.getService(this,3,i3,0);
        PendingIntent pi4=PendingIntent.getService(this,4,i4,0);


        nb.addAction(android.R.drawable.ic_media_previous,"prev",pi2);
        nb.addAction(android.R.drawable.ic_media_pause,"pause",pi3);
        nb.addAction(android.R.drawable.ic_media_next,"next",pi4);

        notification=nb.build();
        nm.notify(1,notification);
    }
}
