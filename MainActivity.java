package com.example.shishirnigam.player;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Sensor sensor;
    SensorManager sm;
    static ListView lv;
    ContentResolver contentResolver;
    Uri songUri;
    Cursor cursor;
    static int pos;
    static ArrayList nameArray,pathArray;
    static MediaPlayer mp;
    ImageButton play,stop,pause,next,prev;
    static SeekBar seekBar;
    Thread th;
    static ImageView image;
    static TextView ct,tt;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sm.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

        image=findViewById(R.id.imageView);
        play=findViewById(R.id.imageButton);
        stop=findViewById(R.id.imageButton4);
        pause=findViewById(R.id.imageButton2);
        next=findViewById(R.id.imageButton5);
        prev=findViewById(R.id.imageButton3);
        ct=findViewById(R.id.textView);
        tt=findViewById(R.id.textView2);

        seekBar=findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                  



  if(mp!=null)
                        mp.seekTo(progress);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lv=findViewById(R.id.lv);
        registerForContextMenu(lv);
        nameArray=new ArrayList();
        pathArray=new ArrayList();
        contentResolver=getContentResolver();
        songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        cursor=contentResolver.query(songUri,null,null,null,null);
        if(cursor!=null && cursor.moveToFirst()){
            int songId=cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle=cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int path=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                long currentID=cursor.getLong(songId);
                String currentTitle=cursor.getString(songTitle);
                String filePath=cursor.getString(path);
                pathArray.add(filePath);
                nameArray.add(currentTitle);
            } while (cursor.moveToNext());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, nameArray);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                pos=position;
                Intent intent=new Intent(MainActivity.this,MyService.class);
                intent.putExtra("option","play");
                startService(intent);
            }
        });
        th=new Thread(new Runnable() {
            @Override
            public void run() {

                while(true){
                    if(mp!=null && mp.isPlaying()){
                        final int c = (mp.getCurrentPosition()/1000);
                        final int m=c/60;
                        final int s=(c%60);
                        seekBar.setProgress(c*1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ct.setText(+m+":"+s);
                            }
                        });

                    }

                }
            }
        });

        th.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(MainActivity.this,MyService.class);
                intent.putExtra("option","play");
                startService(intent);*/

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyService.class);
                intent.putExtra("option","stop");
                startService(intent);

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyService.class);
                intent.putExtra("option","pause");
                startService(intent);

            }
        });

        


next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyService.class);
                intent.putExtra("option","next");
                startService(intent);


            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyService.class);
                intent.putExtra("option","prev");
                startService(intent);
            }
        });
 }

    public void onSensorChanged(SensorEvent sensorEvent) {
        float values[]=sensorEvent.values;
        float x=values[0];
        float y=values[1];
        float z=values[2];
        if(x>=4){
            Intent intent=new Intent(this,MyService.class);
            intent.putExtra("option","prev");
            startService(intent);
        }
        else if(x<=-4){
            Intent intent=new Intent(this,MyService.class);
            intent.putExtra("option","next");
            startService(intent);

        }
        else if(y<=-4){
            Intent intent=new Intent(this,MyService.class);
            intent.putExtra("option","pause");
            startService(intent);
        }
        else if(y>=4){
            Intent intent=new Intent(this,MyService.class);
            intent.putExtra("option","play");
            startService(intent);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.mymenu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo a=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        pos=a.position;
        if(item.getTitle().equals("Play")){
            Intent intent=new Intent(MainActivity.this,MyService.class);
            intent.putExtra("option","play");
            startService(intent);
            // getImage(pos);

        }
        else if(item.getTitle().equals("Pause")){
            Intent intent=new Intent(MainActivity.this,MyService.class);
            intent.putExtra("option","pause");
            startService(intent);
        }
        else if(item.getTitle().equals("Stop")){
            Intent intent=new Intent(MainActivity.this,MyService.class);
            intent.putExtra("option","stop");
            startService(intent);
        }


        return super.onContextItemSelected(item);
    }

}
