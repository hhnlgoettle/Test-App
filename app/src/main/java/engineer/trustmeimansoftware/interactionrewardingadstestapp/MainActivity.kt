package engineer.trustmeimansoftware.interactionrewardingadstestapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import engineer.trustmeimansoftware.adlib.ad.Ad
import engineer.trustmeimansoftware.adlib.ad.AdRequest
import engineer.trustmeimansoftware.adlib.ad.InteractionRewardedAd
import engineer.trustmeimansoftware.adlib.callback.AdLoadCallback
import engineer.trustmeimansoftware.adlib.callback.FullscreenContentCallback
import engineer.trustmeimansoftware.adlib.callback.OnUserRewardedListener
import engineer.trustmeimansoftware.adlib.manager.AdManager
import engineer.trustmeimansoftware.adlib.manager.AdManagerBuildOpts
import engineer.trustmeimansoftware.adlib.reward.RewardItem
import engineer.trustmeimansoftware.interactionrewardingadstestapp.viewmodels.CurrencyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Timer
import kotlin.concurrent.schedule
import java.lang.Error
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button
    private lateinit var reset: Button
    private lateinit var rewardText: TextView

    private lateinit var coinsTextView: TextView
    private lateinit var energyTextView: TextView

    // viewmodel for the in game currencies
    private val viewModel: CurrencyViewModel by lazy {
       ViewModelProvider(this, CurrencyViewModel.Factory(this.application)).get(CurrencyViewModel::class.java)
    }

    // reference to the ad
    var myAd: InteractionRewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // build ad manager
        val buildOpts = AdManagerBuildOpts.default()

        buildOpts.offlineMode = false
        buildOpts.testMode = false

        AdManager.build(this, buildOpts)


        btn = findViewById(R.id.btnAd)
        reset = findViewById(R.id.btnReset)
        rewardText = findViewById(R.id.txtReward)
        coinsTextView = findViewById(R.id.txt_coins)
        energyTextView = findViewById(R.id.txt_energy)

        btn.setOnClickListener {
            if(myAd == null) {
                loadAd()
            } else {
                showAd()
            }
        }

        reset.setOnClickListener {
            viewModel.reset()
        }

        viewModel.energy.observe(this, {
            if(it == null) return@observe;
            energyTextView.text = "Energy: "+it.value.toString()
        })
        viewModel.coins.observe(this, {
            if(it == null) return@observe;
            coinsTextView.text = "Coins: "+it.value.toString()
        })

    }

    fun showAd() {
        val onUserRewardedListener = object : OnUserRewardedListener {
            override fun onRewardEarned(rewards: Array<RewardItem>) {
                Toast.makeText(applicationContext, "User received ${rewards.size} reward(s)!", Toast.LENGTH_SHORT).show()

                addRewardItemsToCurrencies(rewards)
                var rewardText = ""
                rewards.forEach {
                    if(it.isExtraReward) {
                        rewardText+= "You additionally received ${it.amount} ${it.type}!\n"
                    } else {
                        rewardText+= "You received ${it.amount} ${it.type}!\n"
                    }
                }
                showReward(rewardText)
            }
        }

        val fullscreenContentCallback = object : FullscreenContentCallback {
            override fun onDismissed() {
                Toast.makeText(applicationContext, "Ad dismissed", Toast.LENGTH_SHORT).show()
                myAd = null
                btn.text = "LOAD AD"
            }

            override fun onFailedToShow(error: Error) {
                Toast.makeText(applicationContext, "Ad failed to show: ${error.message}", Toast.LENGTH_SHORT).show()
                myAd = null
                btn.text = "LOAD AD"
            }
        }

        myAd?.let {
            it.onUserRewardedListener = onUserRewardedListener
            it.fullscreenContentCallback = fullscreenContentCallback

            // this is the base reward for this ad
            // user should receive this regardless of interactions
            it.rewardType = Constants.energy
            it.rewardAmount = 5L

            // this the user's additional reward
            it.additionalReward = RewardItem(Constants.coin, 100)
            // this is the reference to your activity
            it.show(this)
        }
    }

    fun loadAd() {
        btn.text = "LOADING ..."
        val request = AdRequest.build(Constants.displayBlockId);

        val loadCallback = object: AdLoadCallback {
            override fun onAdFailedToLoad(error: Error) {
                Toast.makeText(applicationContext, "An Error occurred while loading the ad: "+error.message, Toast.LENGTH_SHORT).show()
                btn.text = "LOAD AD"
            }

            override fun onAdLoaded(ad: Ad) {
                Toast.makeText(applicationContext, "Ad loaded!", Toast.LENGTH_SHORT).show()
                myAd = ad as InteractionRewardedAd
                btn.text = "WATCH AD"
            }
        }

        InteractionRewardedAd.load(this, request, loadCallback)
    }

    fun showReward(text: String) {
        rewardText.visibility = View.VISIBLE
        rewardText.text = text
        val time = 5000L
        // val scope = CoroutineScope(Dispatchers.Main)
        Timer("DisableRewardText", false).schedule(object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    rewardText.visibility = View.INVISIBLE
                    rewardText.text = ""
                }
            }
        }, time)
    }

    fun addRewardItemsToCurrencies(rewards: Array<RewardItem>) {
        rewards.forEach { reward ->
            when(reward.type) {
                Constants.coin -> {
                    viewModel.addToCoin(reward.amount)
                }
                Constants.energy -> {
                    viewModel.addToEnergy(reward.amount)
                }
            }
        }
    }
}