package com.gramainexus.smartvillage.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class HealthcareFacility(
    val id: String,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val phone: String = "",
    val address: String = ""
)

class HealthcarePreferences {

    companion object {

        // Primary Overpass server
        private const val PRIMARY_OVERPASS_URL =
            "https://overpass-api.de/api/interpreter"

        // Fallback server
        private const val FALLBACK_OVERPASS_URL =
            "https://overpass.kumi.systems/api/interpreter"

        // Connection timeout
        private const val CONNECT_TIMEOUT = 15_000

        // Read timeout
        private const val READ_TIMEOUT = 25_000

        // Maximum search distance
        private const val DEFAULT_RADIUS_METERS = 10_000

        // Maximum number of results returned
        private const val MAX_RESULTS = 100
    }


    /**
     * Searches OpenStreetMap for nearby healthcare facilities.
     *
     * No Firebase data is required.
     *
     * The search includes:
     *
     * Hospital
     * Clinic
     * Doctor
     * Pharmacy
     * Dentist
     *
     * Results are sorted by distance.
     */
    suspend fun getNearbyHealthcareFacilities(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = DEFAULT_RADIUS_METERS
    ): List<HealthcareFacility> {

        return withContext(Dispatchers.IO) {

            if (!isValidCoordinate(
                    latitude,
                    longitude
                )
            ) {
                return@withContext emptyList()
            }

            val safeRadius =
                radiusMeters.coerceIn(
                    500,
                    50_000
                )

            val query =
                buildOverpassQuery(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = safeRadius
                )

            // ---------------------------------------------
            // TRY PRIMARY SERVER
            // ---------------------------------------------

            val primaryResponse =
                executeOverpassRequest(
                    baseUrl =
                        PRIMARY_OVERPASS_URL,

                    query =
                        query
                )

            if (
                primaryResponse != null
            ) {

                val results =
                    parseHealthcareResponse(
                        response =
                            primaryResponse,

                        userLatitude =
                            latitude,

                        userLongitude =
                            longitude
                    )

                if (results.isNotEmpty()) {

                    return@withContext results
                }
            }


            // ---------------------------------------------
            // TRY FALLBACK SERVER
            // ---------------------------------------------

            val fallbackResponse =
                executeOverpassRequest(
                    baseUrl =
                        FALLBACK_OVERPASS_URL,

                    query =
                        query
                )

            if (
                fallbackResponse != null
            ) {

                return@withContext parseHealthcareResponse(
                    response =
                        fallbackResponse,

                    userLatitude =
                        latitude,

                    userLongitude =
                        longitude
                )
            }

            emptyList()
        }
    }


    /**
     * Builds the OpenStreetMap Overpass query.
     */
    private fun buildOverpassQuery(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): String {

        return """
            [out:json][timeout:25];

            (
                node["amenity"="hospital"]
                    (around:$radiusMeters,$latitude,$longitude);

                way["amenity"="hospital"]
                    (around:$radiusMeters,$latitude,$longitude);

                relation["amenity"="hospital"]
                    (around:$radiusMeters,$latitude,$longitude);


                node["amenity"="clinic"]
                    (around:$radiusMeters,$latitude,$longitude);

                way["amenity"="clinic"]
                    (around:$radiusMeters,$latitude,$longitude);

                relation["amenity"="clinic"]
                    (around:$radiusMeters,$latitude,$longitude);


                node["amenity"="doctors"]
                    (around:$radiusMeters,$latitude,$longitude);

                way["amenity"="doctors"]
                    (around:$radiusMeters,$latitude,$longitude);

                relation["amenity"="doctors"]
                    (around:$radiusMeters,$latitude,$longitude);


                node["amenity"="pharmacy"]
                    (around:$radiusMeters,$latitude,$longitude);

                way["amenity"="pharmacy"]
                    (around:$radiusMeters,$latitude,$longitude);

                relation["amenity"="pharmacy"]
                    (around:$radiusMeters,$latitude,$longitude);


                node["amenity"="dentist"]
                    (around:$radiusMeters,$latitude,$longitude);

                way["amenity"="dentist"]
                    (around:$radiusMeters,$latitude,$longitude);

                relation["amenity"="dentist"]
                    (around:$radiusMeters,$latitude,$longitude);
            );

            out center tags;
        """.trimIndent()
    }


    /**
     * Executes an Overpass request.
     *
     * Returns null if the server fails.
     */
    private fun executeOverpassRequest(
        baseUrl: String,
        query: String
    ): String? {

        var connection:
                HttpURLConnection? = null

        return try {

            val encodedQuery =
                URLEncoder.encode(
                    query,
                    "UTF-8"
                )

            val requestUrl =
                "$baseUrl?data=$encodedQuery"

            connection =
                URL(requestUrl)
                    .openConnection()
                        as HttpURLConnection

            connection.requestMethod = "GET"

            connection.connectTimeout =
                CONNECT_TIMEOUT

            connection.readTimeout =
                READ_TIMEOUT

            connection.useCaches = false

            connection.instanceFollowRedirects =
                true

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.setRequestProperty(
                "User-Agent",
                "SmartVillage Android App/1.0"
            )

            val responseCode =
                connection.responseCode

            if (
                responseCode !=
                HttpURLConnection.HTTP_OK
            ) {

                return null
            }

            val inputStream =
                connection.inputStream

            BufferedReader(
                InputStreamReader(
                    inputStream,
                    Charsets.UTF_8
                )
            ).use { reader ->

                reader.readText()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            null

        } finally {

            connection?.disconnect()
        }
    }


    /**
     * Converts Overpass JSON into
     * HealthcareFacility objects.
     */
    private fun parseHealthcareResponse(
        response: String,
        userLatitude: Double,
        userLongitude: Double
    ): List<HealthcareFacility> {

        val facilities =
            mutableListOf<HealthcareFacility>()

        try {

            val root =
                JSONObject(response)

            val elements =
                root.optJSONArray(
                    "elements"
                )
                    ?: return emptyList()


            for (index in
            0 until elements.length()
            ) {

                val element =
                    elements.optJSONObject(
                        index
                    )
                        ?: continue


                val id =
                    buildFacilityId(
                        element
                    )


                val tags =
                    element.optJSONObject(
                        "tags"
                    )


                val coordinates =
                    getCoordinates(
                        element
                    )
                        ?: continue


                val facilityLatitude =
                    coordinates.first

                val facilityLongitude =
                    coordinates.second


                if (
                    !isValidCoordinate(
                        facilityLatitude,
                        facilityLongitude
                    )
                ) {
                    continue
                }


                val amenity =
                    tags
                        ?.optString(
                            "amenity"
                        )
                        ?.trim()
                        ?.lowercase(
                            Locale.getDefault()
                        )


                val type =
                    getFacilityType(
                        amenity
                    )


                val name =
                    getFacilityName(
                        tags,
                        amenity
                    )


                val phone =
                    getPhoneNumber(
                        tags
                    )


                val address =
                    buildAddress(
                        tags
                    )


                val distanceKm =
                    calculateDistanceKm(
                        latitude1 =
                            userLatitude,

                        longitude1 =
                            userLongitude,

                        latitude2 =
                            facilityLatitude,

                        longitude2 =
                            facilityLongitude
                    )


                facilities.add(
                    HealthcareFacility(

                        id =
                            id,

                        name =
                            name,

                        type =
                            type,

                        latitude =
                            facilityLatitude,

                        longitude =
                            facilityLongitude,

                        distanceKm =
                            distanceKm,

                        phone =
                            phone,

                        address =
                            address
                    )
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }


        return facilities

            // Remove duplicate OSM objects
            .distinctBy {
                it.id
            }

            // Sort nearest first
            .sortedBy {
                it.distanceKm
            }

            // Avoid an excessively large list
            .take(
                MAX_RESULTS
            )
    }


    /**
     * Creates a stable ID for each OSM element.
     */
    private fun buildFacilityId(
        element: JSONObject
    ): String {

        val type =
            element.optString(
                "type",
                "unknown"
            )

        val id =
            element.optString(
                "id",
                ""
            )

        return "$type-$id"
    }


    /**
     * Gets coordinates from:
     *
     * node:
     * lat / lon
     *
     * way/relation:
     * center.lat / center.lon
     */
    private fun getCoordinates(
        element: JSONObject
    ): Pair<Double, Double>?
    {

        val directLatitude =
            element.optDouble(
                "lat",
                Double.NaN
            )

        val directLongitude =
            element.optDouble(
                "lon",
                Double.NaN
            )


        if (
            !directLatitude.isNaN() &&
            !directLongitude.isNaN()
        ) {

            return Pair(
                directLatitude,
                directLongitude
            )
        }


        val center =
            element.optJSONObject(
                "center"
            )
                ?: return null


        val centerLatitude =
            center.optDouble(
                "lat",
                Double.NaN
            )

        val centerLongitude =
            center.optDouble(
                "lon",
                Double.NaN
            )


        if (
            !centerLatitude.isNaN() &&
            !centerLongitude.isNaN()
        ) {

            return Pair(
                centerLatitude,
                centerLongitude
            )
        }


        return null
    }


    /**
     * Gets a readable healthcare facility name.
     */
    private fun getFacilityName(
        tags: JSONObject?,
        amenity: String?
    ): String {

        if (tags != null) {

            val nameKeys =
                listOf(
                    "name:en",
                    "name",
                    "official_name",
                    "short_name"
                )


            for (key in nameKeys) {

                val name =
                    tags.optString(
                        key
                    ).trim()

                if (
                    name.isNotBlank()
                ) {

                    return name
                }
            }
        }


        return getDefaultFacilityName(
            amenity
        )
    }


    /**
     * Converts OSM amenity values into
     * user-friendly categories.
     */
    private fun getFacilityType(
        amenity: String?
    ): String {

        return when (amenity) {

            "hospital" ->
                "Hospital"

            "clinic" ->
                "Clinic"

            "doctors" ->
                "Doctor"

            "pharmacy" ->
                "Pharmacy"

            "dentist" ->
                "Dental Clinic"

            else ->
                "Healthcare"
        }
    }


    /**
     * Creates a fallback facility name.
     */
    private fun getDefaultFacilityName(
        amenity: String?
    ): String {

        return when (amenity) {

            "hospital" ->
                "Nearby Hospital"

            "clinic" ->
                "Nearby Clinic"

            "doctors" ->
                "Nearby Doctor"

            "pharmacy" ->
                "Nearby Pharmacy"

            "dentist" ->
                "Nearby Dental Clinic"

            else ->
                "Healthcare Facility"
        }
    }


    /**
     * Gets the best available phone number
     * from OSM contact tags.
     */
    private fun getPhoneNumber(
        tags: JSONObject?
    ): String {

        if (tags == null) {
            return ""
        }


        val phoneKeys =
            listOf(
                "phone",
                "contact:phone",
                "mobile",
                "contact:mobile"
            )


        for (key in phoneKeys) {

            val phone =
                tags.optString(
                    key
                ).trim()

            if (
                phone.isNotBlank()
            ) {

                return phone
            }
        }


        return ""
    }


    /**
     * Builds a readable address from
     * OpenStreetMap address tags.
     */
    private fun buildAddress(
        tags: JSONObject?
    ): String {

        if (tags == null) {
            return ""
        }


        val parts =
            mutableListOf<String>()


        fun addTag(
            key: String
        ) {

            val value =
                tags.optString(
                    key
                ).trim()

            if (
                value.isNotBlank() &&
                !parts.contains(value)
            ) {

                parts.add(value)
            }
        }


        addTag(
            "addr:housenumber"
        )

        addTag(
            "addr:street"
        )

        addTag(
            "addr:suburb"
        )

        addTag(
            "addr:village"
        )

        addTag(
            "addr:town"
        )

        addTag(
            "addr:city"
        )

        addTag(
            "addr:district"
        )

        addTag(
            "addr:state"
        )

        addTag(
            "addr:postcode"
        )


        return parts.joinToString(
            separator = ", "
        )
    }


    /**
     * Validates geographic coordinates.
     */
    private fun isValidCoordinate(
        latitude: Double,
        longitude: Double
    ): Boolean {

        return !latitude.isNaN() &&
                !longitude.isNaN() &&
                latitude in -90.0..90.0 &&
                longitude in -180.0..180.0
    }


    /**
     * Calculates the distance between
     * two coordinates using the Haversine formula.
     *
     * Result: kilometres.
     */
    private fun calculateDistanceKm(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): Double {

        val earthRadiusKm =
            6371.0


        val latitudeDifference =
            Math.toRadians(
                latitude2 - latitude1
            )

        val longitudeDifference =
            Math.toRadians(
                longitude2 - longitude1
            )


        val latitude1Radians =
            Math.toRadians(
                latitude1
            )

        val latitude2Radians =
            Math.toRadians(
                latitude2
            )


        val sinLatitude =
            sin(
                latitudeDifference / 2
            )

        val sinLongitude =
            sin(
                longitudeDifference / 2
            )


        val a =
            sinLatitude *
                    sinLatitude +
                    cos(
                        latitude1Radians
                    ) *
                    cos(
                        latitude2Radians
                    ) *
                    sinLongitude *
                    sinLongitude


        val safeA =
            a.coerceIn(
                0.0,
                1.0
            )


        val c =
            2.0 *
                    atan2(
                        sqrt(safeA),
                        sqrt(
                            1.0 - safeA
                        )
                    )


        return earthRadiusKm * c
    }
}