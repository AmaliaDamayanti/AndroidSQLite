package com.ad.mynotesapp.DB.DML;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ad.mynotesapp.DB.DDL.DatabaseHelper;
import com.ad.mynotesapp.Entity.Note;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.ad.mynotesapp.DB.DDL.DatabaseContract.TABLE_NOTE;
import static com.ad.mynotesapp.DB.DDL.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.ad.mynotesapp.DB.DDL.DatabaseContract.NoteColumns.DATE;
import static com.ad.mynotesapp.DB.DDL.DatabaseContract.NoteColumns.TITLE;

/**
 *  melakukan proses manipulasi data yang berada di dalam tabel seperti query
 *  untuk pembacaan data yang diurutkan secara descending (menurun),
 *  penyediaan fungsi pencarian catatan berdasarkan judul, pembaruan catatan, dan penghapusan catatan.
 */
public class NoteHelper {

    private static String DATABASE_TABLE = TABLE_NOTE;
    private Context context;
    private DatabaseHelper dataBaseHelper;

    private SQLiteDatabase database;

    public NoteHelper(Context context){
        this.context = context;
    }

    public NoteHelper open() throws SQLException {
        dataBaseHelper = new DatabaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    public ArrayList<Note> query(){
        ArrayList<Note> arrayList = new ArrayList<Note>();
        Cursor cursor = database.query(DATABASE_TABLE,null,null,null,null,null,_ID +" DESC",null);
        cursor.moveToFirst();
        Note note;
        if (cursor.getCount()>0) {
            do {

                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));

                arrayList.add(note);
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Untuk proses penambahan data pada NoteHelper dijabarkan dalam bentuk seperti berikut dengan obyek Note sebagai parameter input-nya
     * @param note
     * @return
     */
    public long insert(Note note){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(TITLE, note.getTitle());
        initialValues.put(DESCRIPTION, note.getDescription());
        initialValues.put(DATE, note.getDate());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Sementara itu, pembaharuan data dijabarkan dalam bentuk berikut dengan obyek Note terbaru (Catatan : _id sebagai referensinya).
     * @param note
     * @return
     */
    public int update(Note note){
        ContentValues args = new ContentValues();
        args.put(TITLE, note.getTitle());
        args.put(DESCRIPTION, note.getDescription());
        args.put(DATE, note.getDate());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + note.getId() + "'", null);
    }

    /**
     * proses penghapusan data pada NoteHelper dijabarkan dalam metode delete(). Idnya berasal dari item note yang dipilih sebagai acuan untuk menghapus data.
     * @param id
     * @return
     */
    public int delete(int id){
        return database.delete(TABLE_NOTE, _ID + " = '"+id+"'", null);
    }
}
