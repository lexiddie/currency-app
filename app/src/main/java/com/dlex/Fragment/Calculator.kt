package com.dlex.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dlex.Activity.CurrencyController
import com.dlex.Helper.*
import com.dlex.R
import java.text.DecimalFormat


class Calculator : Fragment() {

    lateinit var mainController: AppCompatActivity
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
    private lateinit var txtCalDate: TextView
    private lateinit var handleCalFirst: LinearLayout
    private lateinit var handleCalSecond: LinearLayout
    private lateinit var handleKeyboard: RelativeLayout
    private lateinit var fetchCurrency: FetchCurrency

    private val circleImage = CircleImage
    private val loadCountry = LoadCountry
    private val toast = SingleToast
    private val singleDate = SingleDate()
    private val internet = Internet
    private val handler = Handler()
    private val requestFirstCode = 0
    private val requestSecondCode = 1
    private var currencyFirst = ""
    private var currencySecond = ""
    private var currencyFirstValue = "1"
    private var currencySecondValue = "1"
    private var noConnection = false
    private var reLoop = false

    override fun onResume() {
        super.onResume()
        initializeLoading()
        refreshResult()
        displayDate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        standard = mainController.getSharedPreferences("mainCurrency", 0)
        fetchCurrency = FetchCurrency(mainController)
        superView = inflater.inflate(R.layout.fragment_calculator, container, false)

        initializeLayout()
        initializeMain()

        handleCalFirst.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(superView.context, CurrencyController::class.java)
                intent.putExtra("requestType", "0")
                startActivityForResult(intent, requestFirstCode)
            }
        })
        handleCalSecond.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(superView.context, CurrencyController::class.java)
                intent.putExtra("requestType", "1")
                startActivityForResult(intent, requestSecondCode)
            }
        })


        handleKeyboard.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val keyboard = Keyboard
                keyboard.hideKeyboard(mainController)
                displayDate()
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

        return superView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestFirstCode == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                currencyFirst = data!!.getStringExtra("currencyFirst")
            }
        } else if (this.requestSecondCode == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                currencySecond = data!!.getStringExtra("currencySecond")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun initializeLayout() {
        imgFirstFlag = superView.findViewById(R.id.imgFirstFlag)
        imgSecondFlag = superView.findViewById(R.id.imgSecondFlag)
        imgFirstSymbol = superView.findViewById(R.id.imgFirstSymbol)
        imgSecondSymbol = superView.findViewById(R.id.imgSecondSymbol)
        txtFirstCode = superView.findViewById(R.id.txtFirstCode)
        txtSecondCode = superView.findViewById(R.id.txtSecondCode)
        txtFirstName = superView.findViewById(R.id.txtFirstName)
        txtSecondName = superView.findViewById(R.id.txtSecondName)
        txtFirstValue = superView.findViewById(R.id.txtFirstValue)
        txtSecondValue = superView.findViewById(R.id.txtSecondValue)
        txtCalDate = superView.findViewById(R.id.txtCalDate)
        handleCalFirst = superView.findViewById(R.id.handleCalFirst)
        handleCalSecond = superView.findViewById(R.id.handleCalSecond)
        handleKeyboard = superView.findViewById(R.id.handleCalKeyboard)
    }

    private fun initializeMain() {
        val currencyFirstISO = standard.getString("mainCurrency", null)
        val currencySecondISO = standard.getString("secondCurrency", null)
        if (currencyFirstISO != null && currencySecondISO != null) {
            currencyFirst = currencyFirstISO
            currencySecond = currencySecondISO
        } else {
            currencyFirst = loadCountry.getCurrencyCode(mainController)!!
            currencySecond = "USD"
        }
        txtFirstValue.setText(currencyFirstValue)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeReload() {
        val firstFlagID = mainController.resources.getIdentifier(currencyFirst.toLowerCase(), "drawable", mainController.packageName)
        val firstSymbolID = mainController.resources.getIdentifier(currencyFirst.toLowerCase() + "_symbol", "drawable", mainController.packageName)
        val firstCurrencyID = loadCountry.getCurrencyName(mainController, currencyFirst)
        val secondFlagID = mainController.resources.getIdentifier(currencySecond.toLowerCase(), "drawable", mainController.packageName)
        val secondSymbolID = mainController.resources.getIdentifier(currencySecond.toLowerCase() + "_symbol", "drawable", mainController.packageName)
        val secondCurrencyID = loadCountry.getCurrencyName(mainController, currencySecond)
        circleImage.setImage(superView, firstFlagID, imgFirstFlag)
        circleImage.setImage(superView, secondFlagID, imgSecondFlag)
        imgFirstSymbol.setImageResource(firstSymbolID)
        imgSecondSymbol.setImageResource(secondSymbolID)
        txtFirstCode.text = currencyFirst + " -"
        txtFirstName.text = firstCurrencyID
        txtSecondCode.text = currencySecond + " -"
        txtSecondName.text = secondCurrencyID
        val update = standard.edit()
        update.putString("mainCurrency", currencyFirst)
        update.putString("secondCurrency", currencySecond)
        update.apply()
        fetchCurrency.initializeMemoryData(currencyFirst)
    }

    private fun initializeData() {
        if (currencyFirst != currencySecond) {
            currencySecondValue = fetchCurrency.initializePair(currencyFirst, currencySecond).toString()
        } else {
            currencySecondValue = "1"
        }
    }

    private fun refreshResult() {
        val decimalFormat = DecimalFormat("###,###.####")
        txtSecondValue.text = decimalFormat.format((currencyFirstValue.toDouble() * currencySecondValue.toDouble()))
    }

    private fun initializeLoading() {
        if (internet.isConnected(mainController)) {
            noConnection = false
        } else {
            noConnection = true
            initializeRestart()
            toast.show(mainController, "Internet is not available!", Toast.LENGTH_LONG)
        }
        initializeReload()
        initializeData()
        displayDate()
    }

    private fun initializeRestart() {
        if (noConnection) {
            handler.postDelayed({
                initializeLoading()
            }, 10000)
        }
    }

    private fun displayDate() {
        txtCalDate.visibility = View.VISIBLE
        txtCalDate.text = singleDate.showDateDisplay()
        reLoop = true
        handler.postDelayed({
            if (reLoop) {
                txtCalDate.visibility = View.GONE
                reLoop = false
                handler.removeCallbacksAndMessages(null)
            }
        },5000)
    }
}
