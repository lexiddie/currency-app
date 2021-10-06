package com.dlex.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.dlex.Activity.CurrencyController
import com.dlex.Activity.NotificationService
import com.dlex.Adapter.HomeAdapter
import com.dlex.Helper.*
import com.dlex.Model.CurrencyValue
import com.dlex.R
import java.text.DecimalFormat


class Home : Fragment() {

    lateinit var mainController: AppCompatActivity
    lateinit var handleGesture: Button
    private lateinit var standard: SharedPreferences
    private lateinit var superView: View
    private lateinit var imgFlag: ImageView
    private lateinit var imgSymbol: ImageView
    private lateinit var txtCode: TextView
    private lateinit var txtName: TextView
    private lateinit var txtValue: EditText
    private lateinit var txtHomeDate: TextView
    private lateinit var handleCurrency: LinearLayout
    private lateinit var handleKeyboard: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var adapter: HomeAdapter
    private lateinit var fetchCurrency: FetchCurrency
    private lateinit var savingStandard: SavingStandard
    private var currencyList = mutableListOf<CurrencyValue>()
    private var sortedList = mutableListOf<CurrencyValue>()
    private val circleImage = CircleImage
    private val loadCountry = LoadCountry

    private val toast = SingleToast
    private val internet = Internet
    private val singleDate = SingleDate()
    private val handler = Handler()

    private val requestCode = 1
    private var currencyType = ""
    private var currencyValue = "1"
    private var noConnection = false
    private var reLoop = false

    override fun onResume() {
        super.onResume()
        initializeLoading()
        refreshAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        standard = mainController.getSharedPreferences("mainCurrency", 0)
        fetchCurrency = FetchCurrency(mainController)
        savingStandard = SavingStandard(mainController)
        superView = inflater.inflate(R.layout.fragment_home, container, false)

        initializeLayout()
        initializeMain()

        handleCurrency.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(superView.context, CurrencyController::class.java)
                intent.putExtra("requestType", "0")
                startActivityForResult(intent, requestCode)
            }
        })

        handleKeyboard.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val keyboard = Keyboard
                keyboard.hideKeyboard(mainController)
                displayDate()
            }

        })

        handleGesture.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val check = savingStandard.currentSave(currencyType)
                if (!check) {
                    toast.show(mainController, "Currency $currencyType already saved", Toast.LENGTH_LONG)
                } else {
                    toast.show(mainController, "Saving $currencyType ${singleDate.showYearDisplay()}", Toast.LENGTH_LONG)
                }
            }

        })

        txtValue.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val decimalFormat = DecimalFormat("###,###.####")
                val runUpdate = {
                    txtValue.removeTextChangedListener(this)
                    txtValue.setText(decimalFormat.format(currencyValue.toDouble()))
                    txtValue.setSelection(txtValue.text.length)
                    txtValue.addTextChangedListener(this)
                }
                if (count < 1) {
                    currencyValue = "0"
                    runUpdate()
                    refreshAdapter()
                } else {
                    val current = s.toString().replace(",", "")

                    if (current[current.length - 1].equals('.')) {
                        currencyValue = current
                        runUpdate()
                    } else if (currencyValue[currencyValue.length - 1].equals('.') && !(current[current.length - 1].equals('.'))) {
                        currencyValue = currencyValue + current[current.length - 1]
                        runUpdate()
                        refreshAdapter()
                    } else if (currencyValue.length >= 5 && current.contains('.') && currencyValue.substring(currencyValue.indexOf('.'), currencyValue.length).length >= 4) {
                        val checkIndex = currencyValue.indexOf('.')
                        val checkLength = currencyValue.substring(checkIndex, currencyValue.length)
                        if (checkLength.length == 4) {
                            currencyValue = current
                        }
                        runUpdate()
                        refreshAdapter()
                    } else {
                        currencyValue = current
                        runUpdate()
                        refreshAdapter()
                    }

                }
            }
        })

        initializeLoading()
        refreshAdapter()
        displayDate()

        pullToRefresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            pullToRefresh.setRefreshing(false)
            initializeLoading()
        }

        return superView
    }


    private fun initializeLayout() {
        pullToRefresh = superView.findViewById(R.id.srlHomeRefresh)
        imgFlag = superView.findViewById(R.id.imgFlag)
        imgSymbol = superView.findViewById(R.id.imgSymbol)
        txtCode = superView.findViewById(R.id.txtCode)
        txtName = superView.findViewById(R.id.txtName)
        txtValue = superView.findViewById(R.id.txtValue)
        txtHomeDate = superView.findViewById(R.id.txtHomeDate)
        txtValue.imeOptions = (EditorInfo.IME_ACTION_DONE)
        recyclerView = superView.findViewById(R.id.recyclerHome)
        recyclerView.layoutManager = LinearLayoutManager(mainController, LinearLayout.VERTICAL, false)
        handleCurrency = superView.findViewById(R.id.handleCurrency)
        handleKeyboard = superView.findViewById(R.id.handleHomeKeyboard)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestCode == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                currencyType = data!!.getStringExtra("currencyFirst")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initializeMain() {
        val currencyISO = standard.getString("mainCurrency", null)
        if (currencyISO != null) {
            currencyType = currencyISO
        } else {
            currencyType = loadCountry.getCurrencyCode(mainController)!!
        }
        txtValue.setText(currencyValue)
    }

    @SuppressLint("CommitPrefEdits", "SetTextI18n")
    private fun initializeReload() {
        val flagID = mainController.resources.getIdentifier(currencyType.toLowerCase(), "drawable", mainController.packageName)
        val symbolID = mainController.resources.getIdentifier(currencyType.toLowerCase() + "_symbol", "drawable", mainController.packageName)
        val currencyID = loadCountry.getCurrencyName(mainController, currencyType)
        circleImage.setImage(superView, flagID, imgFlag)
        imgSymbol.setImageResource(symbolID)
        txtName.text = currencyID
        txtCode.text = currencyType + " -"
        val update = standard.edit()
        update.putString("mainCurrency", currencyType)
        update.apply()
        fetchCurrency.initializeMemoryData(currencyType)
    }

    private fun initializeData() {
        currencyList = fetchCurrency.initializeData(currencyType)
        sortedList = currencyList.sortedWith(compareBy(CurrencyValue::name)) as MutableList<CurrencyValue>
    }

    private fun refreshAdapter() {
        adapter = HomeAdapter(mainController, currencyValue, R.layout.home_cell, sortedList)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
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
    }

    private fun initializeRestart() {
        if (noConnection) {
            handler.postDelayed({
                initializeLoading()
                refreshAdapter()
            }, 10000)
        }
    }

    private fun displayDate() {
        txtHomeDate.visibility = View.VISIBLE
        txtHomeDate.text = singleDate.showDateDisplay()
        reLoop = true
        handler.postDelayed({
            if (reLoop) {
                txtHomeDate.visibility = View.GONE
                reLoop = false
                handler.removeCallbacksAndMessages(null)
            }
        },5000)
    }
}
