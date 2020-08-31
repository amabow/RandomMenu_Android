package com.example.randommenu

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog

class MenuList : AppCompatActivity() {

    private val dbName: String = "meal"
    private val tableName: String = "meal"
    private val dbVersion: Int = 1
    private var arrayListId: ArrayList<String> = arrayListOf()
    private var arrayListName: ArrayList<String> = arrayListOf()
    private var arrayListMeal: ArrayList<String> = arrayListOf()

    private lateinit var btnMenu: Button
    private lateinit var btnAll: Button
    private lateinit var btnAdd: Button
    private lateinit var btnDeleteAll: Button
    private lateinit var textHead: TextView
    private lateinit var element: EditText
    private lateinit var listViewMeal: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_list)

        btnMenu = findViewById(R.id.btn_RandomMenu) as Button
        btnAll = findViewById(R.id.btn_MenuList) as Button
        btnAdd = findViewById(R.id.btn_Add) as Button
        btnDeleteAll = findViewById(R.id.btn_deleteAll) as Button
        textHead = findViewById(R.id.text_Menu) as TextView
        element = findViewById(R.id.editText_Element) as EditText
        listViewMeal = findViewById(R.id.list_meal) as ListView

        textHead.setTextColor(Color.WHITE)

        val mealAdapter = ArrayAdapter(this, R.layout.row, arrayListMeal)
        val dbHelper = menuDB_Helper(applicationContext, dbName, null, dbVersion)
        val database = dbHelper.writableDatabase

        // read Data
        val cursor1 = database.rawQuery("select * from meal;", null)
        if (cursor1.count > 0) {
            cursor1.moveToFirst()
            while (!cursor1.isAfterLast) {
                arrayListMeal.add(cursor1.getString(1))
                cursor1.moveToNext()
            }
        }
        cursor1.close()

        listViewMeal.adapter = mealAdapter

        btnMenu.setOnClickListener{
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        btnAdd.setOnClickListener {
            val meal: String = element.text.toString()

            if(meal==""){
                AlertDialog.Builder(this).
                setTitle("エラー").
                setMessage("何か入力してから追加してください").
                setPositiveButton("OK"){dialog, which -> }.show()
            }else{
                insertData(meal)
                if(meal in arrayListMeal){
                    AlertDialog.Builder(this).
                    setTitle("エラー").
                    setMessage("同じメニューは追加できません。").
                    setPositiveButton("OK"){dialog, which -> }.show()
                }else{
                    arrayListMeal.add(meal)
                    listViewMeal.adapter = mealAdapter
                }
            }
        }

        btnDeleteAll.setOnClickListener {
            database.rawQuery("delete from meal", null)
            var i: Int = 0
            val arraySize: Int = arrayListMeal.size
            while(i < arraySize){
                arrayListMeal.removeAt(0)
                i++
            }
            listViewMeal.adapter = mealAdapter
        }

        listViewMeal.setOnItemLongClickListener { parent, view, position, id ->
            var meal: String = listViewMeal.getItemAtPosition(position).toString()
            AlertDialog.Builder(this).
            setTitle("削除").
            setMessage(meal + " を削除しますか？").
            setNegativeButton("NO"){dialog, which -> }.
            setPositiveButton("OK"){
                    dialog, which ->
                mealAdapter.remove(mealAdapter.getItem(position))
                mealAdapter.notifyDataSetChanged()
                deleteData(meal)
            }.show()

            return@setOnItemLongClickListener true
        }
    }

    private fun insertData(menu: String){
        try {
            val dbHelper = menuDB_Helper(applicationContext, dbName, null, dbVersion)
            val database = dbHelper.writableDatabase
            print("---------------\n" + database + "\n---------------")
            val cursor = database.rawQuery("select * from meal;" , null)
            var id : Int = 0
            if(cursor.count>0){
                cursor.moveToFirst()
                while(!cursor.isAfterLast){
                    if(Integer.parseInt(cursor.getString(0))==id){
                        id++
                    }else{
                        break
                    }
                    cursor.moveToNext()
                }
            }
            println("---------------\n" + id + "\n---------------")
            val values = ContentValues()
            values.put("id", id)
            values.put("menu", menu)

            database.insertOrThrow(tableName, null, values)
            cursor.close()
        }catch (exception: Exception) {
            Log.e("insertData", exception.toString())
        }
    }

    private fun deleteData(whereMenu: String) {
        try {
            val dbHelper = menuDB_Helper(applicationContext, dbName, null, dbVersion);
            val database = dbHelper.writableDatabase

            val whereClauses = "menu = ?"
            val whereArgs = arrayOf(whereMenu)
            database.delete(tableName, whereClauses, whereArgs)
        }catch (exception: Exception) {
            Log.e("deleteData", exception.toString())
        }
    }
}

