package com.suomifi.palvelutietovaranto.ui.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.suomifi.palvelutietovaranto.domain.ptv.model.Poi
import java.lang.reflect.Type

class PoiTargetDeserializer : JsonDeserializer<PoiTarget> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PoiTarget {
        val jsonObject = json.asJsonObject
        return PoiTarget(
                deserializePoi(jsonObject.get("poi").asJsonObject),
                PoiTargetAction.valueOf(jsonObject.get("action").asString)
        )
    }

    private fun deserializePoi(jsonObject: JsonObject) =
            Poi(jsonObject.get("latitude").asDouble, jsonObject.get("longitude").asDouble).apply {
                name = jsonObject.get("name").asString
                isParkingMeter = jsonObject.get("isParkingMeter").asBoolean
            }

}
