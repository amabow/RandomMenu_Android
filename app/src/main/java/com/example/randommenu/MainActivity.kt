package com.example.randommenu

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val dbName: String = "meal"
    private val tableName: String = "meal"
    private val dbVersion: Int = 1

    private lateinit var menuText: TextView
    private lateinit var menuText2: TextView
    private lateinit var btnShowMenu: Button
    private lateinit var btnMenu: Button
    private lateinit var btnList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        menuText = findViewById(R.id.text_Menu) as TextView
        menuText2 = findViewById(R.id.text_Menu_2) as TextView
        btnShowMenu = findViewById(R.id.btn_ShowMenu) as Button
        btnMenu = findViewById(R.id.btn_RandomMenu) as Button
        btnList = findViewById(R.id.btn_MenuList) as Button

        val dbHelper = menuDB_Helper(applicationContext, dbName, null, dbVersion)
        val database = dbHelper.writableDatabase

        btnList.setOnClickListener {
            val addIntent = Intent(this, MenuList::class.java)
            startActivity(addIntent)
        }

        btnShowMenu.setOnClickListener {

            val cursor = database.rawQuery("select * from meal", null)
            val randId: Int = Random.nextInt(0, cursor.count)
            val cursor1 = database.rawQuery("select menu from meal where id=" + randId, null)
            cursor1.moveToFirst()
            val name: String = cursor1.getString(0)
            val strUrl: String = "https://cookpad.com/search/" + name
            menuText.text = name + "（Cookpadにとびます）"
            menuText2.text = "はいかがでしょうか？"
            val pattern = Pattern.compile(name)
            val filter =
                Linkify.TransformFilter { match, url -> strUrl }
            Linkify.addLinks(menuText, pattern, strUrl, null, filter);
        }

    }

    private fun insertData(menu: String){
        try {
            val dbHelper = menuDB_Helper(applicationContext, dbName, null, dbVersion)
            val database = dbHelper.writableDatabase
            val cursor = database.rawQuery("select * from meal", null)
            val id: String
            cursor.moveToLast()
            id = (Integer.parseInt(cursor.getString(0)) + 1).toString()

            val values = ContentValues()
            values.put("id", id)
            values.put("menu", menu)

            database.insertOrThrow(tableName, null, values)
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