package com.amirafalistia.absensikaryawanuts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_absen.*
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.min

class AbsenActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    override fun onClick(p0: View?) {
        when (p0!!.id){
            R.id.btnAbsen -> {
                kar.idKaryawan = edtIdKar.text.toString()
                kar.namaKaryawan = edtNamaKar.text.toString()
                kar.locKaryawan = edtlocKaryawan.text.toString()
                kar.karDateTime = edtDateKarTime.text.toString()
                db.child(kar.idKaryawan!!).setValue(kar)
            }
            R.id.btnDelete -> {
                db.child(kar.idKaryawan!!).removeValue()
            }
        }
    edtIdKar.setText("");edtNamaKar.setText("");edtlocKaryawan.setText("");edtDateKarTime.setText("")
    }
    lateinit var db : DatabaseReference
    lateinit var adapter : ListAdapter
    var alKar = ArrayList<HashMap<String, Any>>()
    var kar = Karyawan()
    var hm = HashMap<String, Any>()

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedday = 0
    var savedmonth = 0
    var savedyear = 0
    var savedhour = 0
    var savedminute = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absen)

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        btnAbsen.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        lvKar.setOnItemClickListener(itemClick)
        pickdate()
    }

    private fun getDateTimeCalender(){
        val cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }
    fun pickdate(){
        btnDate.setOnClickListener {
            getDateTimeCalender()
            DatePickerDialog(this,this,year,month,day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        savedday = dayOfMonth
        savedmonth = month
        savedyear = year

        getDateTimeCalender()
        TimePickerDialog(this,this,hour,minute,true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedhour = hourOfDay
        savedminute = minute
        edtDateKarTime.setText("$savedday/$savedmonth/$savedyear, Jam $savedhour ; $savedminute")
    }

    val itemClick = AdapterView.OnItemClickListener { adapterView, view, i, l ->
        hm = HashMap()
        hm = alKar.get(i)
        edtIdKar.setText(hm.get("idKar").toString()!!)
        edtNamaKar.setText(hm.get("namaKar").toString()!!)
        edtlocKaryawan.setText(hm.get("locKar").toString()!!)
        edtDateKarTime.setText(hm.get("datetime").toString()!!)
    }

    override fun onStart() {
        super.onStart()
        db = FirebaseDatabase.getInstance().getReference("TabelKaryawan")
        showData()
    }

    fun showData(){
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var dataSnapShotIterable = snapshot.children
                var iterator = dataSnapShotIterable.iterator()
                alKar.clear()
                while (iterator.hasNext()) {
                    kar = iterator.next().getValue(Karyawan::class.java)!!
                    hm = HashMap()
                    hm.put("idKar", kar.idKaryawan!!)
                    hm.put("namaKar", kar.namaKaryawan!!)
                    hm.put("locKar", kar.locKaryawan!!)
                    hm.put("datetime", kar.karDateTime!!)
                    alKar.add(hm)
                }
                adapter = SimpleAdapter(
                    this@AbsenActivity,
                    alKar,
                    R.layout.row_karyawan,
                    arrayOf("idKar","namaKar","locKar","datetime"),
                    intArrayOf(R.id.txIdKar,R.id.txNama,R.id.txLocation,R.id.txDateTime)
                )
                lvKar.setAdapter(adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AbsenActivity,
                    "Connection to database error : ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }



}