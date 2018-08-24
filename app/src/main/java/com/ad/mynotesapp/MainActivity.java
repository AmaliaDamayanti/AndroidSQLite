package com.ad.mynotesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.ad.mynotesapp.Adapter.NoteAdapter;
import com.ad.mynotesapp.DB.DML.NoteHelper;
import com.ad.mynotesapp.Entity.Note;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.ad.mynotesapp.FormAddUpdateActivity.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    RecyclerView rvNotes;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;

    private LinkedList<Note> list;
    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Notes");

        rvNotes = (RecyclerView)findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        /**
         * Aturan utama dalam penggunaan dan akses database SQLite adalah membuat instance
         * dan membuka koneksi pada metode onCreate()
         */
        noteHelper = new NoteHelper(this);
        noteHelper.open();

        list = new LinkedList<>();

        adapter = new NoteAdapter(this);
        adapter.setListNotes(list);
        rvNotes.setAdapter(adapter);

        new LoadNoteAsync().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add){
            Intent intent = new Intent(MainActivity.this, FormAddUpdateActivity.class);
            startActivityForResult(intent, FormAddUpdateActivity.REQUEST_ADD);
        }
    }

    /**
     * Me-load data dari tabel dan menampilkannya ke dalam list secara asynchronous dengan menggunakan AsyncTask
     */
    private class LoadNoteAsync extends AsyncTask<Void, Void, ArrayList<Note>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            if (list.size() > 0){
                list.clear();
            }
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            /**
             * Pada NoteHelper proses load data dilakukan dengan eksekusi query().
             * Obyek Cursor yang dihasilkan ditampung ke dalam obyek ArrayList
             */
            return noteHelper.query();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            progressBar.setVisibility(View.GONE);

            list.addAll(notes);
            adapter.setListNotes(list);
            adapter.notifyDataSetChanged();

            if (list.size() == 0){
                showSnackbarMessage("Tidak ada data saat ini");
            }
        }
    }

    /**
     * Melakukan aksi setelah menerima nilai balik dari semua aksi yang dilakukan di FormAddUpdateActivity
     * Setiap aksi yang dilakukan pada FormAddUpdateActivity akan berdampak pada MainActivity entah itu untuk penambahan, pembaharuan, dan atau penghapusan.
     * Metode onActivityResult() akan melakukan penerimaan data dari intent yang dikirimkan dan diseleksi berdasarkan jenis requestCode dan resultCode-nya.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Baris di bawah akan dijalankan ketika terjadi penambahan data
         * pada FormAddUpdateActivity. Alhasil, ketika metode ini dijalankan, obyek RecyclerViewrvNotes
         * akan melakukan smoothscrolling ke posisi teratas (indeks ke 0) dari list.
         */
        if (requestCode == FormAddUpdateActivity.REQUEST_ADD){
            if (resultCode == FormAddUpdateActivity.RESULT_ADD){
                new LoadNoteAsync().execute();
                showSnackbarMessage("Satu item berhasil ditambahkan");
                rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }

        /**
         * Baris di atas akan dijalankan ketika terjadi perubahan data pada FormAddUpdateActivity.
         * Alhasil, ketika metode ini dijalankan,obyek RecyclerViewrvNotes akan melakukan smoothscrolling ke posisi di mana item tersebut di-update. Selanjutnya,
         * ia pun menampilkan notifikasi pesan dengan menggunakan Snackbar.
         */
        else if (requestCode == REQUEST_UPDATE) {

            if (resultCode == FormAddUpdateActivity.RESULT_UPDATE) {
                new LoadNoteAsync().execute();
                showSnackbarMessage("Satu item berhasil diubah");
                int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);
            }

            /**
             * Ketika metode onActivityResult() dijalankan,
             * item data pada posisi tujuan menjadi terhapus.
             * Adapter diatur ulang dengan sumber data yang baru. Karena itu,
             * lakukan proses penyegaran (refresh) pada adapter sehingga data yang ditampilkan dalam list tetap update.
             */
            else if (resultCode == FormAddUpdateActivity.RESULT_DELETE) {
                int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                list.remove(position);
                adapter.setListNotes(list);
                adapter.notifyDataSetChanged();
                showSnackbarMessage("Satu item berhasil dihapus");
            }
        }
    }

    /**
     * dan menutup koneksi pada metode onDestroy() (atau onStop()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null){
            noteHelper.close();
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }
}
