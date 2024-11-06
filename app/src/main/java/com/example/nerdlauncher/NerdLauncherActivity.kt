package com.example.nerdlauncher

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
private const val TAG = "NerdLauncherActivity"
// 后面小括号的意思是，调用父类中那个无参数的构造函数
// 如果后者括号内有参数，就是指，调用其那个有参数的构造函数
class NerdLauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActivityAdapter
// 从父类继承的方法（有些可覆盖）
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "你好你好你好啊！")

// 4，特别重要：该方法返回的是一个新对象，还是对某个对象的引用？
        recyclerView = findViewById(R.id.app_recycler_view)
//        语法糖和构造函数
        recyclerView.layoutManager = LinearLayoutManager(this)
        val activities = loadActivities()
        recyclerView.adapter = ActivityAdapter(activities)
    }
// 自己的方法
    /*
    * 4，addCategory(Intent.CATEGORY_LAUNCHER)很重要。
如果你不加，
packageManager.queryIntentActivities(startIntent, 0)这个方法会返回手机上所有APP的所有Activity。
你加了，它就只返回首页Activity。

如果，你加一个
intent.setPackage(ctx.getPackageName());
它就会返回指定APP的所有Activity。
    * */
    @SuppressLint("QueryPermissionsNeeded")
    private fun loadActivities(): List<ResolveInfo> {
        val startIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

//        val activities: (Mutable)List<ResolveInfo!>
//    假如手机里有60个APP，PackageManager对象都能把它们找出来
        val activities = packageManager.queryIntentActivities(startIntent, 0)
        Log.d(TAG, activities.size.toString())
//        a, b 代表着ResolveInfo
//        sortWith方法接收一个比较器对象当参数，目的，是按首字母对MutableList进行排序
//    1，点亮
//    2，这个lambda值参会执行几次？
//    这里是list，它就会执行多次（对每两个子项进行比较）

//    它这是按APP的首字母排序
        activities.sortWith { a, b ->
//            sortWith方法体内部会遍历整个list
//            它会拿相邻的每两项进行比较，产生1 0 -1这三个结果
//            然后，传给sortWith方法体中
            String.CASE_INSENSITIVE_ORDER.compare(
//                loadLabel方法会返回其包含的那个应用的名字
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        }

    return activities

//    String.Companion.CASE_INSENSITIVE_ORDER: Comparator<String>
//    这家伙的类型居然是Comparator<String>这个比较器接口

//        for (resolveInfo in activities) {
//            Log.d(TAG, resolveInfo.loadLabel(packageManager).toString())
//        }
//        Log.d(TAG, activities.size.toString())
    }
}