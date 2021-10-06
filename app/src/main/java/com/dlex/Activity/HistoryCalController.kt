package com.dlex.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.dlex.Helper.*
import com.dlex.R
import java.text.DecimalFormat

class HistoryCalController : AppCompatActivity() {

    private lateinit var standard: SharedPreferences
    private lateinit var superView: View
    private lateinit var imgFirstFlag: ImageView
    private lateinit var imgSecondFlag: ImageView
    private lateinit var imgFirstSymbol: ImageView
    private lateinit var imgSecondSymbol: ImageView
    private lateinit var txtFirstCode: TextView
    private lateinit var txtSecondCode: TextView
    private lateinit var txtFirstName: TextView
    private lateinit var txtSecondName: TextView
    private lateinit var txtFirstValue: EditText
    private lateinit var txtSecondValue: TextView
    private lateinit var txtHisDate: TextView
    private lateinit var handleHisSecond: LinearLayout
    private lateinit var handleKeyboard: RelativeLayout
    private lateinit var subKey: String
    private lateinit var fetchCurrency: FetchCurrency

    private val circleImage = CircleImage
    private val loadCountry = LoadCountry
    private val singleDate = SingleDate()
    private val requestSecondCode = 1
    private var currencyFirst = ""
    private var currencySecond = ""
    private var currencyFirstValue = "1"
    private var currencySecondValue = "1"

    override fun onResume() {
        super.onResume()
        initializeLoading()
        refreshResult()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_cal)
        standard = this.getSharedPreferences("mainCurrency", 0)
        fetchCurrency = FetchCurrency(this)
        superView = LayoutInflater.from(this).inflate(R.layout.activity_history_cal, null)

        initializeLayout()
        initializeMain()

        handleHisSecond.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@HistoryCalController, CurrencyController::class.java)
                intent.putExtra("requestType", "1")
                startActivityForResult(intent, requestSecondCode)
            }
        })

        handleKeyboard.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val keyboard = Keyboard
                keyboard.hideKeyboard(this@HistoryCalController)
            }

        })

        txtFirstValue.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val decimalFormat = DecimalFormat("###,###.####")
                val runUpdate = {
                    txtFirstValue.removeTextChangedListener(this)
                    txtFirstValue.setText(decimalFormat.format(currencyFirstValue.toDouble()))
                    txtFirstValue.setSelection(txtFirstValue.text.length)
                    txtFirstValue.addTextChangedListener(this)
                }
                if (count < 1) {
                    currencyFirstValue = "0"
                    runUpdate()
                    refreshResult()
                } else {
                    val current = s.toString().replace(",", "")
                    if (current[current.length - 1].equals('.')) {
                        currencyFirstValue = current
                        runUpdate()
                    } else if (currencyFirstValue[currencyFirstValue.length - 1].equals('.') && !(current[current.length - 1].equals('.'))) {
                        currencyFirstValue = currencyFirstValue + current[current.length - 1]
                        runUpdate()
                        refreshResult()
                    } else if (currencyFirstValue.length >= 5 && current.contains('.') && currencyFirstValue.substring(currencyFirstValue.indexOf('.'), currencyFirstValue.length).length >= 4) {
                        val checkIndex = currencyFirstValue.indexOf('.')
                        val checkLength = currencyFirstValue.substring(checkIndex, currencyFirstValue.length)
                        if (checkLength.length == 4) {
                            currencyFirstValue = current
                        }
                        runUpdate()
                        refreshResult()
                    } else {
                        currencyFirstValue = current
                        runUpdate()
                        refreshResult()
                    }

                }
            }

        })

        initializeLoading()
        refreshResult()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestSecondCode == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                currencySecond = data!!.getStringExtra("currencySecond")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initializeLayout() {
        imgFirstFlag = findViewById(R.id.imgFirstFlag)
        imgSecondFlag = findViewById(R.id.imgSecondFlag)
        imgFirstSymbol = findViewById(R.id.imgFirstSymbol)
        imgSecondSymbol = findViewById(R.id.imgSecondSymbol)
        txtFirstCode = findViewById(R.id.txtFirstCode)
        txtSecondCode = findViewById(R.id.txtSecondCode)
        txtFirstName = findViewById(R.id.txtFirstName)
        txtSecondName = findViewById(R.id.txtSecondName)
        txtFirstValue = findViewById(R.id.txtFirstValue)
        txtSecondValue = findViewById(R.id.txtSecondValue)
        txtHisDate = findViewById(R.id.txtHisDate)
        handleHisSecond = findViewById(R.id.handleHisSecond)
        handleKeyboard = findViewById(R.id.handleHisKeyboard)
    }

    private fun initializeMain() {
        subKey = intent.getStringExtra("subKey")
        val currencySecondISO = standard.getString("secondCurrency", null)
        if (currencySecondISO != null) {
            currencyFirst = subKey.split(" ")[1]
            currencySecond = currencySecondISO
            txtFirstValue.setText(currencyFirstValue)
        } else {
            currencyFirst = loadCountry.getCurrencyCode(this)!!
            currencySecond = "USD"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeReload() {
        val firstFlagID = this.resources.getIdentifier(currencyFirst.toLowerCase(), "drawable", this.packageName)
        val firstSymbolID = this.resources.getIdentifier(currencyFirst.toLowerCase() + "_symbol", "drawable", this.packageName)
        val firstCurrencyID = loadCountry.getCurrencyName(this, currencyFirst)
        val secondFlagID = this.resources.getIdentifier(currencySecond.toLowerCase(), "drawable", this.packageName)
        val secondSymbolID = this.resources.getIdentifier(currencySecond.toLowerCase() + "_symbol", "drawable", this.packageName)
        val secondCurrencyID = loadCountry.getCurrencyName(this, currencySecond)
        circleImage.setImage(superView, firstFlagID, imgFirstFlag)
        circleImage.setImage(superView, secondFlagID, imgSecondFlag)
        imgFirstSymbol.setImageResource(firstSymbolID)
        imgSecondSymbol.setImageResource(secondSymbolID)
        txtFirstCode.text = currencyFirst + " -"
        txtFirstName.text = firstCurrencyID
        txtSecondCode.text = currencySecond + " -"
        txtSecondName.text = secondCurrencyID
        val update = standard.edit()
        update.putString("secondCurrency", currencySecond)
        update.apply()
    }

    private fun initializeData() {
        if (currencyFirst != currencySecond) {
            currencySecondValue = fetchCurrency.initializePairHistory(currencyFirst, currencySecond, subKey).toString()
        } else {
            currencySecondValue = "1"
        }
    }

    private fun refreshResult() {
        val decimalFormat = DecimalFormat("###,###.####")
        txtSecondValue.text = decimalFormat.format((currencyFirstValue.toDouble() * currencySecondValue.toDouble()))
    }

    private fun initializeLoading() {
        initializeReload()
        initializeData()
        displayDate()
    }

    private fun displayDate() {
        txtHisDate.text = singleDate.updatingDisplay(subKey.split(" ")[0])
    }

    fun handleHisDismiss(view: View) {
        finish()
    }
}
