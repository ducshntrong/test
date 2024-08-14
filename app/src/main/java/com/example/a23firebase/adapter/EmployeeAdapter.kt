package com.example.a23firebase.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a23firebase.R
import com.example.a23firebase.activity.EmployeeDetailsActivity
import com.example.a23firebase.model.EmployeeModel

class EmployeeAdapter(var list:ArrayList<EmployeeModel>):
        RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>(){
//    private lateinit var mListener: onItemClickListener
//    interface onItemClickListener {
//        fun onItemClick(pos:Int)
//    }
//    fun setOnItemClickListener(clickListener: onItemClickListener){
//        mListener = clickListener
//    }

    class EmployeeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.txtTen)
        val img = itemView.findViewById<ImageView>(R.id.imgAvt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.itemView.apply {
            val id = list[position].empId
            val name = list[position].empName
            val age = list[position].empAge
            val salary = list[position].empSalary
            val img = list[position].empImg

            holder.name.text = name
            val bytes = Base64.decode(img, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            holder.img.setImageBitmap(bitmap)

            holder.itemView.setOnClickListener {
                val mIntent = Intent(context,EmployeeDetailsActivity::class.java)
                val employ = EmployeeModel(id,name,age,salary,img)
                val bundle = Bundle()
                bundle.putParcelable("employee",employ)
                mIntent.putExtras(bundle)
                context.startActivity(mIntent)
            }
        }
    }
}

