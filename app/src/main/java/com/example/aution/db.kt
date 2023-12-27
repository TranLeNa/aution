import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_EMAIL + " TEXT UNIQUE," + COLUMN_PASSWORD + " TEXT)"
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addUser(id: Int, email: String?, password: String?) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ID, id)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun getUser(email: String): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_NAME, null, COLUMN_EMAIL + "=?", arrayOf(email), null, null, null)
    }

    fun getOneUser(): Cursor? {
        val db = this.readableDatabase
        return db.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_ID ASC", "1")
    }

    fun removeAllUsers() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)  // This will delete all rows in the user table
        db.close()
    }

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user"
        const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }
}