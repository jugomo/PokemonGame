package com.site11.jugomo.pokemongame

import android.location.Location

/**
 * Created by julio on 25/07/17.
 */
class Pokemon {
    var name: String? = null
    var des: String? = null
    var image: Int? = null
    var power: Double? = null
    var lat: Double? = null
    var lon: Double? = null
    var isCatch: Boolean? = false
    var location: Location? = null

    constructor(image: Int, name: String, des: String, power: Double, lat: Double, lon: Double) {
        this.name = name
        this.des = des
        this.image = image
        this.power = power
        this.lat = lat
        this.lon = lon
        this.isCatch = false

        this.location = Location(name)
        this.location!!.latitude = lat
        this.location!!.longitude = lon
    }

}