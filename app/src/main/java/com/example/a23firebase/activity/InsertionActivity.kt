package com.example.a23firebase.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.a23firebase.model.EmployeeModel
import com.example.a23firebase.databinding.ActivityInsertionBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

/*DatabaseReference: Tham chiếu Firebase đại diện cho một vị trí cụ thể trong Cơ sở dữ liệu của bạn
và có thể được sử dụng để đọc hoặc ghi dữ liệu vào vị trí Cơ sở dữ liệu đó.
Lớp này là điểm bắt đầu cho tất cả các hoạt động Cơ sở dữ liệu. Sau khi bạn
đã khởi tạo nó bằng một URL, bạn có thể sử dụng nó để đọc dữ liệu, ghi dữ liệu và để tạo DatabaseReferences mới.*/
class InsertionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertionBinding
    private lateinit var dbRef: DatabaseReference
    var sImage: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().getReference("Employees")
        binding.btnSave.setOnClickListener {
            saveEmployeeData()
        }
        binding.btnTaiAnh.setOnClickListener {
            binding.imageSp.visibility = View.VISIBLE
            selectImage()
        }
    }


    private fun saveEmployeeData() {
        val empName = binding.edtEmpName.text.toString()
        val empAge = binding.edtEmpAge.text.toString()
        val empSalary = binding.edtEmpSalary.text.toString()

        //Đẩy data
        val empId = dbRef.push().key!!//id không trùng nhau
        val employee = EmployeeModel(empId,empName, empAge,empSalary,sImage)

        if (empName.isEmpty()){
            binding.edtEmpName.error = "Please enter name"
            return
        }else if (empAge.isEmpty()){
            binding.edtEmpAge.error = "Please enter age"
            return
        }else if (empSalary.isEmpty()){
            binding.edtEmpSalary.error = "Please enter salary"
            return
        }else if (sImage!!.isEmpty()){
            Toast.makeText(this, "Vui lòng thêm ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        //Chèn data
        dbRef.child(empId).setValue(employee)
            .addOnCompleteListener {
                Toast.makeText(this, "Data insert thành công", Toast.LENGTH_SHORT).show()
                val i = Intent(this, FetchingActivity::class.java)
                startActivity(i)
            }
            .addOnFailureListener{ err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun selectImage(){
        var myFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        myFileIntent.type = "image/*"
        activityResultLauncher.launch(myFileIntent)
    }
    private val activityResultLauncher = registerForActivityResult<Intent,ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result:ActivityResult->
        if (result.resultCode== RESULT_OK){
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.imageSp.setImageBitmap(myBitmap)
                inputStream!!.close()
            }catch (ex:Exception){
                Toast.makeText(this, ex.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
}