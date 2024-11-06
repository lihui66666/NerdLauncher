package com.example.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "ActivityAdapter"
//  RecyclerView.Adapter后面这个泛型表示的是，它这个Adapter中的每一项VH的类型
// 主构造函数没有方法体
// 如果你想在主构造函数中做点什么，可以使用init函数
class ActivityAdapter(private val activities: List<ResolveInfo>)
    : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {
//  parent就是VH寄身的RV
//    更准确地说，是RV的那个LinearLayoutManager(它的类型，就是ViewGroup)
//    它是操作系统这个玩家传给onCreateViewHolder方法的
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
//    创建填充器
        val layoutInflater = LayoutInflater.from(parent.context)
//        val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
//    inflate方法是绑定的意思，把子项布局绑定到parent的RV上去
        val view = layoutInflater.inflate(R.layout.activity_item, parent, false)

//    这个方法启动后，onBind方法也会启动(很重要)
        return ActivityViewHolder(view)
    }

//  要记住，下面这个itemView代表着子项布局
    inner class ActivityViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
//    下面是VH的实例属性和实例方法，我今天才明确地意识到这一点
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textView: TextView = itemView.findViewById(R.id.textView)
//        private val nameTextView = itemView as TextView
        private lateinit var resolveInfo: ResolveInfo

        init {
            itemView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
//            itemView.context就可以获得context！！！
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            textView.text = appName
//            textView.
            val res = resolveInfo.loadIcon(packageManager)
            imageView.setImageDrawable(res)
        }
    /*
    * 5，ResolveInfo类实例可以保存Application的相关信息。
这个方法的意思是，去查一下，哪些Activity能够响应这个隐式Intent。
获取它们所在的APP的AM文件中Application标签内的属性信息，保存为一个ResolveInfo类实例。
要用的时候，再取出来（见最下方那两个load方法）。
* */
        override fun onClick(v: View?) {
//            要小心，要确保这个方法调用前，resolveInfo已经完成了初始化
//            毕竟，它上面用的是lateinit
            val activityInfo = resolveInfo.activityInfo
//            1，类和接口是大文件，对象是小文件
//            2，左边是变量，右边是对象，左边代表着右边
            val intent = Intent(Intent.ACTION_MAIN).apply {
//                第一个参数是响应的应用程序的名字，第二个参数是具体的那个响应Activity的名字
//                一个应用程序可能包含N个Activity
                setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
//                下面这个方法很重要
//                如果不用，它会在本APP中打开新页面，而不会单开一个任务栈
//                有它之后，你就可以在上滑时看到，它和本APP并列
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//  我们需要NerdLauncher在新任务中启动activity，如图23-9所示。这样，点击NerdLauncher启动器中的应用项
//  可以让应用拥有自己的任务，用户就可以通过概览屏在运行的应用间自由切换了。
                Log.d(TAG, activityInfo.applicationInfo.packageName)
                Log.d(TAG, activityInfo.name)
            }
//            2，左边是变量，右边是对象，左边代表着右边
//            特别重要：该方法返回的是一个新对象，还是对某个对象的引用？
//            这一点至关重要
            val context = v?.context
//        多态与强制类型转换
//            (context as NerdLauncherActivity).supportFragmentManager
            context?.startActivity(intent)

            context?.apply {
                startActivity(intent)
            }

            /*
            * 特别注意：关于Context，这段话非常重要。
这两个方法并不能创建这两个对象。
它们创建的，只是这两个对象的引用。
因为，Activity和Application对象都是由操作系统创建的，我们开发者无法手动创建它们。
* */
        }
}

    /*
    * 基础知识：多态与强制类型转换。
            (context as NerdLauncherActivity).supportFragmentManager
    *
Activity是Context抽象类类的一个间接子类。
它继承了Context抽象类的所有方法。

同时，它自己又新增了一些属性和方法，这是它独有的。

在这里，getContext方法返回的就是对NerdLauncherActivity类实例的引用。
但是，我们想使用它时，只能使用它从Context继承过来的那些属性和方法。
如果，我们想使用NerdLauncherActivity从Activity中继承过来的那些独创属性和方法，就必须进行强制类型转换。

所以，多态和强制类型转换总是相伴出现。

* */

    override fun getItemCount(): Int = activities.size
// 分工！要记住三个玩家的分工！
//    这个adapter对象是由操作系统执行的
//    用户这个玩家滑动RV这个对象时，与它绑定的Adapter对象就会被操作系统这个玩家去执行
//    当然，是执行内部的onCreateViewHolder方法和onBindViewHolder方法
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val resolveInfo = activities[position]
        holder.bindActivity(resolveInfo)
    }


}