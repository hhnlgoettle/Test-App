package engineer.trustmeimansoftware.interactionrewardingadstestapp.viewmodels


import android.app.Application
import androidx.lifecycle.*
import engineer.trustmeimansoftware.interactionrewardingadstestapp.database.Currency
import engineer.trustmeimansoftware.interactionrewardingadstestapp.database.getDatabase
import kotlinx.coroutines.*


/**
 * DevByteViewModel designed to store and manage UI-related data in a lifecycle conscious way. This
 * allows data to survive configuration changes such as screen rotations. In addition, background
 * work such as fetching network results can continue through configuration changes and deliver
 * results after the new Fragment or Activity is available.
 *
 * @param application The application that this viewmodel is attached to, it's safe to hold a
 * reference to applications across rotation since Application is never recreated during actiivty
 * or fragment lifecycle events.
 */
class CurrencyViewModel(application: Application) : AndroidViewModel(application) {


    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)

    private val _coins: LiveData<Currency> = database.currencyDao.getCoins()
    val coins: LiveData<Currency>
        get() = _coins

    private val _energy: LiveData<Currency> = database.currencyDao.getEnergy()
    val energy: LiveData<Currency>
        get() = _energy

    fun addToEnergy(amount: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _energy.value?.value = _energy.value?.value?.plus(amount)!!
                database.currencyDao.update(_energy.value!!)
            }
        }
    }

    fun addToCoin(amount: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _coins.value?.value = _coins.value?.value?.plus(amount)!!
                database.currencyDao.update(_coins.value!!)
            }
        }
    }

    fun reset() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _coins.value?.value = 0
                _energy.value?.value = 0
                database.currencyDao.update(_coins.value!!)
                database.currencyDao.update(_energy.value!!)
            }
        }
    }


    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CurrencyViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
