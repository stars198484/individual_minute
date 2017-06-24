package ru.geekbrains.android1.stas.individual_minute;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.sip.SipAudioCall;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    Button btnStart;
    Button btnStop;
    ListView lvData;
    DB db;
    SimpleCursorAdapter scAdapter;
    String dateNow, timeStart, timeEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DB(this);
        db.open();
        String[] from = new String[]{DB.COLUMN_DATE, DB.COLUMN_TIME};
        btnStart = (Button) findViewById(R.id.btStart);
        btnStop = (Button) findViewById(R.id.btStop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        int[] to = new int[]{R.id.txtDate, R.id.txtTime};
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(scAdapter);
        lvData.setOnItemClickListener(this);
        registerForContextMenu(lvData);
        getSupportLoaderManager().initLoader(0, null, this);
        Date date = new Date();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btStart: {
                btnStart.setEnabled(false);

                break;
            }
            case R.id.btStop: {
                btnStart.setEnabled(true);
                break;
            }
        }
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

//        @Override
//        public Cursor loadInBackground() {
//            return db.getNameData(tmpStr);
//        }
    }
}
