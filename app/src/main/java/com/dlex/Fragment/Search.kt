package com.dlex.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*


class Search : Fragment() {

    lateinit var mainController: AppCompatActivity
    lateinit var handleGesture: Button
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
    private lateinit var txtSearchDate: TextView
    private lateinit var handleSearchFirst: LinearLayout
    private lateinit var handleSearchSecond: LinearLayout
    private lateinit var handleSearchDate: LinearLayout
    private lateinit var handleSearch: Button
    private lateinit var handleKeyboard: RelativeLayout
    private lateinit var handleDisplaySearch: LinearLayout
    private lateinit var fetchCurrency: FetchCurrency
    private lateinit var savingStandard: SavingStandard

    private val circleImage = CircleImage
    private val loadCountry = LoadCountry
    private val toast = SingleToast
    private val singleDate = SingleDate()
    private val internet = Internet
    private val handler = Handler()
    private val mainCalendar = Calendar.getInstance()
    private val minDate = Calendar.getInstance()
    private val requestFirstCode = 0
    private val requestSecondCode = 1
    private var currencyFirst = ""
    private var currencySecond = ""
    private var previousCurrency = ""
    private var previousDate = ""
    private var currencyFirstValue = "1"
    private var currencySecondValue = "1"
    private var noConnection = false
    private var searchDate = ""

    override fun onResume() {
        super.onResume()
        initializeReload()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        standard = mainController.getSharedPreferences("mainCurrency", 0)
        fetchCurrency = FetchCurrency(mainController)
        savingStandard = SavingStandard(mainController)
        superView = inflater.inflate(R.layout.fragment_search, container, false)

        initializeLayout()
        initializeMain()


        handleSearchFirst.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                previousCurrency = currencyFirst
                val intent = Intent(superView.context, CurrencyController::class.java)
                intent.putExtra("requestType", "0")
                startActivityForResult(intent, requestFirstCode)
            }
        })
        handleSearchSecond.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(superView.context, CurrencyController::class.java)
                intent.putExtra("requestType", "1")
                startActivityForResult(intent, requestSecondCode)
            }
        })

        @SuppressLint("SimpleDateFormat")
        fun updateLabel(){
            val dateFormatDigit = SimpleDateFormat("dd MM yyyy", Locale.US)
            val dateFormatDiplay = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
            previousDate = searchDate
            searchDate = dateFormatDigit.format(mainCalendar.time)
            txtSearchDate.text = dateFormatDiplay.format(mainCalendar.time)
        }

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            mainCalendar.set(Calendar.YEAR, year)
            mainCalendar.set(Calendar.MONTH, monthOfYear)
            mainCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
            loadingLayout()
        }

        handleSearchDate.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                minDate.set(2001, 1, 1)
                val dpDialog = DatePickerDialog(mainController, R.style.DialogTheme, date, mainCalendar.get(Calendar.YEAR), mainCalendar.get(Calendar.MONTH), mainCalendar.get(Calendar.DAY_OF_MONTH))
                dpDialog.datePicker.minDate = minDate.timeInMillis
                dpDialog.datePicker.maxDate = Date().time
                dpDialog.show()
            }
        })


        handleKeyboard.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val keyboard = Keyboard
                keyboard.hideKeyboard(mainController)
            }

        })

        handleSearch.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val update = standard.edit()
                update.remove("tempSearch")
                update.apply()
                if (!singleDate.showDateDigit().equals(searchDate)) {
                    if (internet.isConnected(mainController)) {
                        fetchCurrency.initializeSearch(currencyFirst, searchDate)
                        noConnection = false
                    } else {
                        noConnection = true
                        toast.show(mainController, "Internet is not available!", Toast.LENGTH_LONG)
                    }
                }
                previousDate = searchDate
                previousCurrency = currencyFirst
                initializeLoading()
            }

        })

        handleGesture.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val check: Boolean
                if (singleDate.showDateDigit().equals(searchDate)) {
                    check = savingStandard.currentSave(currencyFirst)
                } else {
                    check = savingStandard.searchSave(currencyFirst, searchDate)
                }

                if (!check) {
                    toast.show(mainController, "Currency $currencyFirst already saved", Toast.LENGTH_LONG)
                } else {
                    toast.show(mainController, "Saving $currencyFirst ${txtSearchDate.text}", Toast.LENGTH_LONG)
                }
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
        return superView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestFirstCode == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                currencyFirst = data!!.getStringExtra("currencyFirst")
                loadingLayout()
            }
        } else if (this.requestSecondCode == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                currencySecond = data!!.getStringExtra("currencySecond")
                initializeLoading()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initializeMain() {
        txtSearchDate.text = singleDate.showYearDisplay()
        searchDate = singleDate.showDateDigit()
        previousDate = searchDate
        val currencyFirstISO = standard.getString("mainCurrency", null)
        val currencySecondISO = standard.getString("secondCurrency", null)
        if (currencyFirstISO != null && currencySecondISO != null) {
            currencyFirst = currencyFirstISO
            currencySecond = currencySecondISO
            txtFirstValue.setText(currencyFirstValue)
        } else {
            currencyFirst = loadCountry.getCurrencyCode(mainController)!!
            currencySecond = "USD"
        }
        previousCurrency = currencyFirst
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
        txtSearchDate = superView.findViewById(R.id.txtSearchDate)
        handleSearchFirst = superView.findViewById(R.id.handleSearchFirst)
        handleSearchSecond = superView.findViewById(R.id.handleSearchSecond)
        handleSearchDate = superView.findViewById(R.id.handleSearchDate)
        handleKeyboard = superView.findViewById(R.id.handleSearchKeyboard)
        handleSearch = superView.findViewById(R.id.handleSearch)
        handleDisplaySearch = superView.findViewById(R.id.handleDisplaySearch)
    }

    private fun refreshResult() {
        val decimalFormat = DecimalFormat("###,###.####")
        txtSecondValue.text = decimalFormat.format((currencyFirstValue.toDouble() * currencySecondValue.toDouble()))
    }

    private fun initializeData() {
        if (currencyFirst != currencySecond) {
            if (singleDate.showDateDigit().equals(searchDate)) {
                currencySecondValue = fetchCurrency.initializePair(currencyFirst, currencySecond).toString()
            } else {
                if (!noConnection) {
                    currencySecondValue = fetchCurrency.initializeSearchData(currencyFirst, currencySecond, searchDate).toString()
                }
            }
        } else {
            currencySecondValue = "1"
        }
    }

    private fun initializeLoading() {
        if (internet.isConnected(mainController)) {
            noConnection = false
        } else {
            noConnection = true
            initializeRestart()
            toast.show(mainController, "Internet is not available!", Toast.LENGTH_LONG)
        }
        initializeData()
        loadingLayout()
        refreshResult()
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

    private fun initializeRestart() {
        if (noConnection) {
            handler.postDelayed({
                initializeLoading()
            }, 10000)
        }
    }

    private fun loadingLayout() {
        if (currencySecondValue.equals("-1.0") || !previousDate.equals(searchDate) || !previousCurrency.equals(currencyFirst) || noConnection) {
            handleDisplaySearch.visibility = View.GONE
            handleGesture.visibility = View.GONE
            handleSearch.visibility = View.VISIBLE
        } else {
            handleDisplaySearch.visibility = View.VISIBLE
            handleGesture.visibility = View.VISIBLE
            handleSearch.visibility = View.GONE
        }
    }
}
