package ru.geekbrains.android1.stas.individual_minute;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.sip.SipAudioCall;
import android.renderscript.Sampler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    Button btnStart;
    Button btnStop;
    ListView lvData;
    TextView tvInfo;
    DB db;
    SimpleCursorAdapter scAdapter;
    String dateNow, time;
    Long startMillis, stopMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB(this);
        db.open();
        btnStart = (Button) findViewById(R.id.btStart);
        btnStop = (Button) findViewById(R.id.btStop);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setOnItemClickListener(this);
        registerForContextMenu(lvData);
        btnStop.setEnabled(false);
        tvInfo.setText(getString(R.string.info_ready));
    }

    public void showData(String date, String time) {
        String[] from = new String[]{date, time};
        int[] to = new int[]{R.id.txtDate, R.id.txtTime};
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lvData.setAdapter(scAdapter);
        getSupportLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void onClick(View view) {
        Date date = new Date();
        switch (view.getId()) {
            case R.id.btStart: {
                tvInfo.setText(getString(R.string.info_process));
                btnStop.setEnabled(true);
                btnStart.setEnabled(false);
//                Date hireDay = calendar.getTime();
                startMillis = date.getTime();

                Calendar now = Calendar.getInstance(TimeZone.getDefault());
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH);
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                dateNow = String.valueOf(day) + '.' +String.valueOf(month) + '.' + String.valueOf(year) + ' ' + String.valueOf(hour) + ':' + String.valueOf(minute);
//                Toast toast = Toast.makeText(getApplicationContext(),
//                        dateNow + ' ' + startMillis, Toast.LENGTH_LONG);
//                toast.show();
                break;
            }
            case R.id.btStop: {
                tvInfo.setText(getString(R.string.info_ready));
                btnStart.setEnabled(true);
                stopMillis = date.getTime();
                time = String.valueOf(((stopMillis - startMillis) / 1000));
                db.addRec(dateNow, time);
                Toast toast = Toast.makeText(getApplicationContext(),
                       getString(R.string.end), Toast.LENGTH_LONG);
                toast.show();
                btnStop.setEnabled(false);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_db) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.q_menu_clear_db));
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.clearDB();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (item.getItemId() == R.id.menu_clear_list) {
           lvData.setAdapter(null);
        }
        if (item.getItemId() == R.id.menu_result) {
            showData(DB.COLUMN_DATE, DB.COLUMN_TIME);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            db.delRec(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class MyCursorLoader extends CursorLoader {
        DB db;

        MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            return db.getAllData();
        }
    }
}
