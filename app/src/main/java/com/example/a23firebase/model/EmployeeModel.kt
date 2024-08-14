package com.example.a23firebase.model

import android.os.Parcel
import android.os.Parcelable

//Class quản lý các thành phần trong table sau này cần thêm
class EmployeeModel(
    var empId: String? = null,
    var empName: String? = null,
    var empAge: String? = null,
    var empSalary: String? = null,
    var empImg: String?=""
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(empId)
        parcel.writeString(empName)
        parcel.writeString(empAge)
        parcel.writeString(empSalary)
        parcel.writeString(empImg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmployeeModel> {
        override fun createFromParcel(parcel: Parcel): EmployeeModel {
            return EmployeeModel(parcel)
        }

        override fun newArray(size: Int): Array<EmployeeModel?> {
            return arrayOfNulls(size)
        }
    }
}