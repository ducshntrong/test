package com.example.a23firebase.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.a23firebase.databinding.ActivityEmployeeDetailsBinding
import com.example.a23firebase.databinding.UpdateDialogBinding
import com.example.a23firebase.model.EmployeeModel
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream

class EmployeeDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmployeeDetailsBinding
    private lateinit var dialogBinding: UpdateDialogBinding
    lateinit var dialog:AlertDialog
    lateinit var dbRef: DatabaseReference
    private lateinit var bundle: Bundle
    var sImage: String? = ""
    lateinit var employ: EmployeeModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras ?: return
        employ = bundle.getParcelable("employee")!!
        val id = employ.empId.toString()

        dbRef = FirebaseDatabase.getInstance().getReference("Employees").child(id)

        setValueToView(id, employ.empName!!, employ.empAge!!, employ.empSalary!!, employ.empImg!!)

        binding.btnDelete.setOnClickListener {
            xoaData()
        }
        binding.btnUpdate.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        val build = AlertDialog.Builder(this)
        dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(this))
        build.setView(dialogBinding.root)
        build.setTitle("Updating ${employ.empName} record")
        dialog = build.create()
        dialog.show()

        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val emp = snapshot.getValue(EmployeeModel::class.java)
                if (emp != null){
                    dialogBinding.etEmpName.setText(emp.empName)
                    dialogBinding.etEmpAge.setText(emp.empAge)
                    dialogBinding.etEmpSalary.setText(emp.empSalary)

                    val bytes = Base64.decode(emp.empImg, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    dialogBinding.imageAvtUd.setImageBitmap(bitmap)
                }else{
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        dialogBinding.btnTaiAnh.setOnClickListener {
            selectImage()
        }

        dialogBinding.btnUpdateData.setOnClickListener {
            if (sImage!!.isEmpty()){
                UpdateData(employ.empId.toString(),
                    dialogBinding.etEmpName.text.toString(),
                    dialogBinding.etEmpAge.text.toString(),
                    dialogBinding.etEmpSalary.text.toString(),employ.empImg!!)
            }else{
                UpdateData(employ.empId.toString(),
                    dialogBinding.etEmpName.text.toString(),
                    dialogBinding.etEmpAge.text.toString(),
                    dialogBinding.etEmpSalary.text.toString(),sImage!!)
            }
        }
    }


    private fun UpdateData(id: String, name:String, age:String, salary:String, img:String) {
        val employee = EmployeeModel(id,name,age,salary, img)
        dbRef.setValue(employee)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Cập nhật không thành công", Toast.LENGTH_SHORT).show()
            }
    }


    private fun xoaData() {
        dbRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Xoá thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { err ->
                Toast.makeText(applicationContext, err.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val emp = snapshot.getValue(EmployeeModel::class.java)
                if (emp != null){
                    setValueToView(emp.empId!!, emp.empName!!, emp.empAge!!,emp.empSalary!!, emp.empImg!!)
                }else{
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setValueToView(id: String, name:String, age:String, salary:String, img:String) {
        binding.tvEmpId.text = id
        binding.tvEmpName.text = name
        binding.tvEmpAge.text = age
        binding.tvEmpSalary.text = salary
        val bytes = Base64.decode(img, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.imageAvt.setImageBitmap(bitmap)
    }

    private fun selectImage() {
        var myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myFileIntent.type = "image/*"
        activityResultLauncher.launch(myFileIntent)
    }

    private val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result: ActivityResult ->
        val dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(this))
        if (result.resultCode== RESULT_OK){
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                dialogBinding.imageAvtUd.setImageBitmap(myBitmap)
                inputStream!!.close()
            }catch (ex:Exception){
                Toast.makeText(this, ex.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}
