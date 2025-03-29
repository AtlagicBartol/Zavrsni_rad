package com.example.byciclecomputer

data class Day(
    private var calories: Double,
    private var distance: Double,
    private var averageVelocity: Double,
    private var time: Int
){
    public fun getCalories() : Double{
        return calories;
    }
    public fun getDistance() : Double{
        return distance;
    }
    public fun getAverageVelocity() : Double{
        return averageVelocity;
    }
    fun getTime() : Int{
        return time
    }
    public fun setAtributes(calories: Double, distance: Double, averageVelocity: Double,time : Int){
        this.calories = calories
        this.distance = distance
        this.averageVelocity = averageVelocity
        this.time = time
    }
    public fun setAverageVelocity(averageVelocity: Double){
        this.averageVelocity = averageVelocity
    }
    public fun setDistance(distance: Double){
        this.distance = distance
    }
    public fun setCalories(calories: Double){
        this.calories = calories;
    }
    public fun setTime(time: Int){
        this.time = time;
    }
}