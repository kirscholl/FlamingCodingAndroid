package com.example.flamingcoding.mvxprojectframe.mvp

class MVPPresenter(val mvpPresenterInterface: MVPPresenterInterface, val mvpModel: MVPModel) {

    fun showListData() {
        val dataList = mvpModel.getData()
        mvpPresenterInterface.showListData(dataList)
    }
}