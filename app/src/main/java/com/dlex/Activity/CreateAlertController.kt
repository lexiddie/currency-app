package com.dlex.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.dlex.Helper.*
import com.dlex.Model.Alert
import com.dlex.R
import java.text.DecimalFormat

class CreateAlertController : AppCompatActivity() {

    private lateinit var standard: SharedPreferences
    private lateinit var superView: View
    private lateinit var imgFirstFlag: ImageView
    private lateinit var imgSecondFlag: ImageView
    private lateinit var imgFirstSymbol: ImageView
    private lateinit var imgSecondSymbol: ImageView
    private lateinit var txtFirstCode: TextView
    private lateinit var txtSecondCode: TextView
    private lateinit var txtFirstValue: EditText
    private lateinit var txtSecondValue: EditText
    private lateinit var txtAlertName: EditText
    private lateinit var handleAlertFirst: LinearLayout
    private lateinit var handleAlertSecond: LinearLayout
    private lateinit var handleKeyboard: RelativeLayout
    private lateinit var savingStandard: SavingStandard

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
    private var alertSizeCount = 0

    override fun onResume() {
        super.onResume()
        initializeLoading()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_alert)
        standard = this.getSharedPreferences("mainCurrency", 0)
        savingStandard = SavingStandard(this)
        superView = LayoutInflater.from(this).inflate(R.layout.activity_create_alert, null)

        initializeLayout()
        initializeMain()
        checkAlertSize()

        handleAlertFirst.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@CreateAlertController, CurrencyController::class.java)
                intent.putExtra("requestType", "0")
                startActivityForResult(intent, requestFirstCode)
            }
        })

        handleAlertSecond.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@CreateAlertController, CurrencyController::class.java)
                intent.putExtra("requestType", "1")
                startActivityForResult(intent, requestSecondCode)
            }
        })

        handleKeyboard.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val keyboard = Keyboard
                keyboard.hideKeyboard(this@CreateAlertController)
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
                } else {
                    val current = s.toString().replace(",", "")
                    if (current[current.length - 1].equals('.')) {
                        currencyFirstValue = current
                        runUpdate()
                    } else if (currencyFirstValue[currencyFirstValue.length - 1].equals('.') && !(current[current.length - 1].equals('.'))) {
                        currencyFirstValue = currencyFirstValue + current[current.length - 1]
                        runUpdate()
                    } else if (currencyFirstValue.length >= 5 && current.contains('.') && currencyFirstValue.substring(currencyFirstValue.indexOf('.'), currencyFirstValue.length).length >= 4) {
                        val checkIndex = currencyFirstValue.indexOf('.')
                        val checkLength = currencyFirstValue.substring(checkIndex, currencyFirstValue.length)
                        if (checkLength.length == 4) {
                            currencyFirstValue = current
                        }
                        runUpdate()
                    } else {
                        currencyFirstValue = current
                        runUpdate()
                    }

                }
            }

        })

        txtSecondValue.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val decimalFormat = DecimalFormat("###,###.####")
                val runUpdate = {
                    txtSecondValue.removeTextChangedListener(this)
                    txtSecondValue.setText(decimalFormat.format(currencySecondValue.toDouble()))
                    txtSecondValue.setSelection(txtSecondValue.text.length)
                    txtSecondValue.addTextChangedListener(this)
                }
                if (count < 1) {
                    currencySecondValue = "0"
                    runUpdate()
                } else {
                    val current = s.toString().replace(",", "")
                    if (current[current.length - 1].equals('.')) {
                        currencySecondValue = current
                        runUpdate()
                    } else if (currencySecondValue[currencySecondValue.length - 1].equals('.') && !(current[current.length - 1].equals('.'))) {
                        currencySecondValue = currencySecondValue + current[current.length - 1]
                        runUpdate()
                    } else if (currencySecondValue.length >= 5 && current.contains('.') && currencySecondValue.substring(currencySecondValue.indexOf('.'), currencySecondValue.length).length >= 4) {
                        val checkIndex = currencySecondValue.indexOf('.')
                        val checkLength = currencySecondValue.substring(checkIndex, currencySecondValue.length)
                        if (checkLength.length == 4) {
                            currencySecondValue = current
                        }
                        runUpdate()
                    } else {
                        currencySecondValue = current
                        runUpdate()
                    }

                }
            }

        })

        initializeLoading()

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
        imgFirstFlag = findViewById(R.id.imgFirstFlag)
        imgSecondFlag = findViewById(R.id.imgSecondFlag)
        imgFirstSymbol = findViewById(R.id.imgFirstSymbol)
        imgSecondSymbol = findViewById(R.id.imgSecondSymbol)
        txtFirstCode = findViewById(R.id.txtFirstCode)
        txtSecondCode = findViewById(R.id.txtSecondCode)
        txtFirstValue = findViewById(R.id.txtFirstValue)
        txtSecondValue = findViewById(R.id.txtSecondValue)
        txtAlertName = findViewById(R.id.txtAlertName)
        handleAlertFirst = findViewById(R.id.handleAlertFirst)
        handleAlertSecond = findViewById(R.id.handleAlertSecond)
        handleKeyboard = findViewById(R.id.handleAlertKeyboard)
    }

    private fun initializeMain() {
        val currencyFirstISO = standard.getString("mainCurrency", null)
        val currencySecondISO = standard.getString("secondCurrency", null)
        if (currencyFirstISO != null && currencySecondISO != null) {
            currencyFirst = currencyFirstISO
            currencySecond = currencySecondISO
        } else {
            currencyFirst = loadCountry.getCurrencyCode(this)!!
            currencySecond = "USD"
        }
        txtFirstValue.setText(currencyFirstValue)
        txtSecondValue.setText(currencySecondValue)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeReload() {
        val firstFlagID = this.resources.getIdentifier(currencyFirst.toLowerCase(), "drawable", this.packageName)
        val firstSymbolID = this.resources.getIdentifier(currencyFirst.toLowerCase() + "_symbol", "drawable", this.packageName)
        val secondFlagID = this.resources.getIdentifier(currencySecond.toLowerCase(), "drawable", this.packageName)
        val secondSymbolID = this.resources.getIdentifier(currencySecond.toLowerCase() + "_symbol", "drawable", this.packageName)
        circleImage.setImage(superView, firstFlagID, imgFirstFlag)
        circleImage.setImage(superView, secondFlagID, imgSecondFlag)
        imgFirstSymbol.setImageResource(firstSymbolID)
        imgSecondSymbol.setImageResource(secondSymbolID)
        txtFirstCode.text = currencyFirst
        txtSecondCode.text = currencySecond
        val update = standard.edit()
        update.putString("mainCurrency", currencyFirst)
        update.putString("secondCurrency", currencySecond)
        update.apply()
    }

    private fun initializeLoading() {
        if (internet.isConnected(this)) {
            noConnection = false
        } else {
            noConnection = true
            initializeRestart()
            toast.show(this, "Internet is not available!", Toast.LENGTH_LONG)
        }
        initializeReload()
    }

    private fun initializeRestart() {
        if (noConnection) {
            handler.postDelayed({
                initializeLoading()
            }, 10000)
        }
    }

    fun handleConfirm(view: View) {
        if (txtAlertName.text.isNotEmpty()) {
            if (!currencyFirst.equals(currencySecond)) {
                val date = singleDate.showDateDigit()
                val dateSplit = date.split(" ")
                val calculateRate = (1 * currencySecondValue.toDouble()) / currencyFirstValue.toDouble()
                val alertData = Alert(date, dateSplit[0], dateSplit[1], dateSplit[2], txtAlertName.text.toString().trimEnd(), currencyFirst, currencySecond, "1", calculateRate.toString(), false)
                val check = savingStandard.alertSave(txtAlertName.text.toString(), alertData)
                if (alertSizeCount == 10) {
                    toast.show(this, "Price alerts only allow 10 alerts per device.", Toast.LENGTH_LONG)
                } else if (alertSizeCount < 10) {
                    if (check) {
                        toast.show(this, "${txtAlertName.text} has been saved", Toast.LENGTH_LONG)
                    } else {
                        toast.show(this, "${txtAlertName.text} name already exist", Toast.LENGTH_LONG)
                    }
                } else {
                    toast.show(this, "Price alerts only allow 10 alerts per device.", Toast.LENGTH_LONG)
                }
            } else {
                toast.show(this, "Both currencies can't be the same!.", Toast.LENGTH_LONG)
            }
        } else {
            toast.show(this, "Please input Alert name", Toast.LENGTH_LONG)
        }
        checkAlertSize()
        txtAlertName.text.clear()
    }

    private fun checkAlertSize() {
        val alertList = standard.getStringSet("alertList", null)
        if (alertList != null ) {
            alertSizeCount = alertList.size
        }
    }

    fun handleAlertDismiss(view: View) {
        finish()
    }
}
