package app.tek4tv.tek4tvpublishing.network

import com.squareup.moshi.Json

class Keyword (
    @Json(name = "Name")
    var name : String,
    @Json(name = "PrivateKey")
    var privateKey : String
)