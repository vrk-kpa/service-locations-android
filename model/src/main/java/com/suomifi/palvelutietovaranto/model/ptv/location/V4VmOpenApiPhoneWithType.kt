package com.suomifi.palvelutietovaranto.model.ptv.location

/** View Model of phone with type */
data class V4VmOpenApiPhoneWithType(
        /** Phone number type (Phone, Sms or Fax). */
        val type: String,
        /** Additional information. (Max.Length: 150). */
        val additionalInformation: String?,
        /**
         * Service charge type. Possible values are: Chargeable, FreeOfCharge or Other.
         * In version 7 and older: Charged, Free or Other.
         */
        val serviceChargeType: String?,
        /** Charge description. (Max.Length: 150). */
        val chargeDescription: String?,
        /** Prefix for the phone number. */
        val prefixNumber: String?,
        /** Defines if number is Finnish service number. If true prefix number can be left empty. */
        val isFinnishServiceNumber: Boolean?,
        /** Phone number. */
        val number: String,
        /** Language of this object. Valid values are: fi, sv or en. */
        val language: String
)
