package com.suomifi.palvelutietovaranto.data.wfs

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.suomifi.palvelutietovaranto.data.wfs.dto.GetFeaturesResult
import java.lang.reflect.Type

class GetFeaturesDeserializer : JsonDeserializer<GetFeaturesResult> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): GetFeaturesResult {
        return GetFeaturesResult(json.asJsonObject.getAsJsonArray("features")?.mapNotNull {
            it.asJsonObject?.getAsJsonObject("properties")?.getAsJsonPrimitive("rootId")?.asString
        })
    }

}
