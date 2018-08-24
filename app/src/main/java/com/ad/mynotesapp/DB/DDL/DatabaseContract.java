package com.ad.mynotesapp.DB.DDL;

import android.provider.BaseColumns;

/**
 *  digunakan untuk mempermudah akses nama table dan nama field di dalam database kita
 *  Jika anda perhatikan, tidak ada kolom id di dalam kelas contract
 *   Alasannya, kolom id sudah ada secara otomatis di dalam kelas BaseColumns
 */
public class DatabaseContract {

    public static String TABLE_NOTE = "note";

    public static final class NoteColumns implements BaseColumns {

        //Note title
        public static String TITLE = "title";
        //Note description
        public static String DESCRIPTION = "description";
        //Note date
        public static String DATE = "date";
    }
}