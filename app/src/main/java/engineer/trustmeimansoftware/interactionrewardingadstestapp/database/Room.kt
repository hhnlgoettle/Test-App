package engineer.trustmeimansoftware.interactionrewardingadstestapp.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executors

@Dao
interface CurrencyDao {
    @Query("select * from Currency")
    fun getCurrencies(): LiveData<List<Currency>>

    @Query("select * from Currency WHERE name = 'coin'")
    fun getCoins(): LiveData<Currency>

    @Query("select * from Currency WHERE name = 'energy'")
    fun getEnergy(): LiveData<Currency>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg currency: Currency )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg currency: Currency )

    @Update
    fun update(vararg currency: Currency)
}

@Database(entities = [Currency::class], version = 1)
abstract class CurrencyDatabase: RoomDatabase() {
    abstract val currencyDao: CurrencyDao
}

private var onCreateCallback: RoomDatabase.Callback = object: RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadScheduledExecutor()
            .execute {
                val myDB = getDatabase()
                Log.d("Hello", myDB.toString())
                myDB?.let {
                    it.currencyDao.insert(Currency("coin", 0L))
                    it.currencyDao.insert(Currency("energy", 0L))
                }
            }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
    }
}

private lateinit var INSTANCE: CurrencyDatabase

fun getDatabase(context: Context): CurrencyDatabase {
    synchronized(context) {
        if(!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,
                    "currency")
                .addCallback(onCreateCallback)
                .build()
            // prepopulate with default values
            // INSTANCE.currencyDao.insert(Currency("coin", 0L))
            // INSTANCE.currencyDao.insert(Currency("energy", 0L))
        }
    }

    return INSTANCE
}

fun getDatabase(): CurrencyDatabase? {
    return INSTANCE
}