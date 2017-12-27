package com.example.pedro.readingtime.model

/**
 * Created by pedro on 26/12/17.
 */
data class Book(var id:String="", var name:String="", var author:String="", var artist:String?=null, var publisher:String="", var type:String="", var pages:Int=0, var category:String="")