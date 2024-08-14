package com.example.a23firebase.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a23firebase.adapter.EmployeeAdapter
import com.example.a23firebase.databinding.ActivityFetchingBinding
import com.example.a23firebase.model.EmployeeModel
import com.google.firebase.database.*

class FetchingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFetchingBinding
    private lateinit var adapter: EmployeeAdapter
    private lateinit var ds: ArrayList<EmployeeModel>
    private lateinit var dbReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbReference = FirebaseDatabase.getInstance().getReference("Employees")
        ds = arrayListOf<EmployeeModel>()
        binding.rvEmp.layoutManager = LinearLayoutManager(this)
        binding.rvEmp.setHasFixedSize(true)
        getDataFromFirebase()
    }

    private fun getDataFromFirebase() {
        binding.txtLoadingData.visibility = View.VISIBLE
        binding.rvEmp.visibility = View.GONE
        //Để đọc data tại 1 đường dẫn và lắng nghe các thay đổi
        //hãy sd addValueEventListener
        dbReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Lấy thông tin data trên table Employees
                ds.clear()
                if (snapshot.exists()){//ktra xem snapshot có tồn tại hay k
                    for (empSnap in snapshot.children){//chạy từng dòng 1 ở cái ảnh chụp dữ liệu
                        //lấy thông tin gán vào empData
                        val empData = empSnap.getValue(EmployeeModel::class.java)
                        ds.add(empData!!)
                    }
                    adapter = EmployeeAdapter(ds)
                    binding.rvEmp.adapter = adapter
                    binding.txtLoadingData.visibility = View.GONE
                    binding.rvEmp.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}