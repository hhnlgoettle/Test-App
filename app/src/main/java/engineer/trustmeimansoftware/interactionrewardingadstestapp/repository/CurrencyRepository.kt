package engineer.trustmeimansoftware.interactionrewardingadstestapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import engineer.trustmeimansoftware.interactionrewardingadstestapp.database.Currency
import engineer.trustmeimansoftware.interactionrewardingadstestapp.database.CurrencyDao

class CurrencyRepository(private val currencyDao: CurrencyDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allCurrencies: LiveData<List<Currency>> = currencyDao.getCurrencies()
}