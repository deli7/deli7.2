package com.delivame.delivame.deliveryman.utilities;


public class Constants {

    // GENERAL SETTINGS
    public static final boolean TEST_ADMOB = false;
    public static final boolean TESTING_MODE = false;
    public static final boolean TESTING_DISPLAY_APP_VERSION = false;
    public static final boolean DEMO_VERSION = false;

    public static final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    public static final long FASTEST_INTERVAL = 5000; /* page2 sec */
    public static final int MINIMUM_PASSWORD_LENGTH = 8;

    public static final int MAP_SPEED = 500;
    public static final long VEHICLE_CATEGORIES_REFRESH_INTERVAL = 60000;


    public static final int DELIVERY_ORDER_STATUS_NEW_ORDER = 0;
    public static final int DELIVERY_ORDER_STATUS_ORDER_ACCEPTED = 1;
    public static final int DELIVERY_ORDER_STATUS_ORDER_PICKED_UP = 2;
    public static final int DELIVERY_ORDER_STATUS_ORDER_DELIVERED = 3;
    public static final int DELIVERY_ORDER_STATUS_ORDER_COMPLETED = 4;
    public static final int DELIVERY_ORDER_STATUS_ORDER_CANCELLED = 5;

    // Order Statuses
    public static final int ORDER_STATUS_IDLE = 0;
    public static final int ORDER_STATUS_USER_REQUESTED_TAXI = 1;
    public static final int ORDER_STATUS_DRIVER_RESPONDED_AND_ON_THE_WAY = 2;
    public static final int ORDER_STATUS_TRIP_STARTED = 3;
    public static final int ORDER_STATUS_TRIP_END = 4;
    public static final int ORDER_STATUS_TRIP_CANCELLED = 5;
    public static final int ORDER_STATUS_TRIP_END2 = 6;
    public static final int ORDER_STATUS_TRIP_WAITING_USER_APPROVAL_TO_START = 7;

    // User Types
    public static final int USER_TYPE_UNVERIFIED_DRIVER = 1;
    public static final int USER_TYPE_PENDING_VERIFICATION_DRIVER = 2;
    public static final int USER_TYPE_VERIFIED_DRIVER = 3;
    public static final int USER_TYPE_DRIVER_SUSPENDED = 5;
    public static final int USER_TYPE_USER_SUSPENDED = 6;
    public static final int USER_TYPE_USER = 4;


    // USER FIREBASE KEYS
    public static final String FIREBASE_KEY_USER_TYPE = "user_type";
    public static final String FIREBASE_KEY_USER_FULLNAME = "full_name";
    public static final String FIREBASE_KEY_USER_PHONE_NUMBER = "phone_number";
    public static final String FIREBASE_KEY_USER_EMAIL = "email";
    public static final String FIREBASE_KEY_PHOTO_URL = "photo_url";
    public static final String FIREBASE_KEY_USER_RANK = "user_rank";
    public static final String FIREBASE_KEY_USER_COMPANY_PERCENT = "company_percent";
    public static final String FIREBASE_KEY_USER_PHOTO_URL = "photo_url";
    public static final String FIREBASE_KEY_USER_VEHICLE_NUMBER = "vehicle_number";
    public static final String FIREBASE_KEY_USER_VEHICLE_MODEL = "vehicle_model";
    public static final String FIREBASE_KEY_USER_VEHICLE_YEAR = "vehicle_year";
    public static final String FIREBASE_KEY_USER_ACCOUNT_POINTS = "account_points";

    // ORDER FIREBASE KEYS
    public static final String FIREBASE_KEY_ORDER_NUMBER = "order_number";
    public static final String FIREBASE_KEY_ORDER_USER_REQUEST_TIME = "order_user_request_time";
    public static final String FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TIME = "order_driver_response_time";
    public static final String FIREBASE_KEY_ORDER_TRIP_START_TIME = "order_trip_start_time";
    public static final String FIREBASE_KEY_ORDER_TRIP_END_TIME = "order_trip_end_time";
    public static final String FIREBASE_KEY_ORDER_STATUS = "order_status";
    public static final String FIREBASE_KEY_ORDER_USER_KEY = "order_user_key";
    public static final String FIREBASE_KEY_ORDER_DRIVER_KEY = "order_driver_key";
    public static final String FIREBASE_KEY_ORDER_ESTIMATED_TIME = "order_estimated_time";
    public static final String FIREBASE_KEY_ORDER_ESTIMATED_DISTANCE = "order_estimated_distance";
    public static final String FIREBASE_KEY_ORDER_DESTINATION_TITLE = "order_destination_title";
    public static final String FIREBASE_KEY_ORDER_PICKUP_TITLE = "order_pickup_title";
    public static final String FIREBASE_KEY_ORDER_DESTINATION_LAT = "order_destination_lat";
    public static final String FIREBASE_KEY_ORDER_DESTINATION_LNG = "order_destination_lng";
    public static final String FIREBASE_KEY_ORDER_ESTIMATED_COST = "order_estimated_cost";
    public static final String FIREBASE_KEY_ORDER_TRIP_RATING = "order_trip_rating";
    public static final String FIREBASE_KEY_ORDER_TRIP_STARS = "order_trip_stars";
    public static final String FIREBASE_KEY_ORDER_TRIP_FEEDBACK = "order_trip_feedback";
    public static final String FIREBASE_KEY_ORDER_STATUS_INT = "order_status_int";
    public static final String FIREBASE_KEY_ORDER_TRIP_DISTANCE = "order_trip_distance";
    public static final String FIREBASE_KEY_ORDER_TRIP_TIME = "order_trip_time";
    public static final String FIREBASE_KEY_ORDER_TRIP_USER_PAYMENT = "order_trip_user_payment";

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 9052;

    public static final String USER_ORDERS_LIST_TYPE = "orders_list_type";

    public static final int ORDERS_TYPE_COMPLETED_ORDERS = 1;
    public static final int ORDERS_TYPE_CANCELLED_ORDERS = 2;

    public static final String FIREBASE_KEY_COMPLETED_ORDERS = "CompletedOrders";
    public static final String FIREBASE_KEY_CANCELLED_ORDERS = "CancelledOrders";


    public static final String FIREBASE_KEY_ORDER_TRIP_DRIVER_EARNING = "order_trip_driver_earning";
    public static final String FIREBASE_KEY_ORDER_TRIP_COMPANY_EARNING = "order_trip_company_earning";

    public static final int STATISTICS_TYPE_MONTHLY = 20;
    public static final int STATISTICS_TYPE_YEARLY = 30;

    // SETTINGS FIREBASE KEYS

    public static final String FIREBASE_KEY_GENERAL_SETTING_PRIVACY_POLICY = "privacy_policy_link";
    public static final String FIREBASE_KEY_GENERAL_SETTING_TERMS_AND_CONDITIONS_LINK = "terms_and_conditions_link";

    public static final String STATISTICS_TYPE = "statistics_type";


    public static final String ERROR_INVALID_VEHICLE_NUMBER = "Invalid Vehicle Number";
    public static final CharSequence ERROR_MISSING_EMAIL_ADDRESS = "Error: Missing Email Address";
    public static final CharSequence ERROR_MINIMUM_PASSWORD_LENGTH = "Error: Minimum Password Length";
    public static final CharSequence ERROR_PASSWORDS_NOT_MATCHING = "Error: Passwords not matching";

    public static final int PICKFILE_RESULT_CODE = 3000;

    public static final int CAMERA_REQUEST = 3001;

    public static final int MAP_PADDING = 50;
    public static final String FIREBASE_KEY_USER_AVAILABLE = "user_available";
    public static final String FIREBASE_KEY_USER_LOGGED_IN = "user_loggedin";
    public static final String FIREBASE_KEY_USER_TOKEN = "token";
    public static final String FIREBASE_KEY_ALLOWED_VEHICLES = "allowed_vehicles";

    public static final String FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_ID = "vehicle_category";


    // ---------------------------------------------------------------------------------------
    // SETTINGS
    // ---------------------------------------------------------------------------------------
    public static final String SETTING_DELIVERY_MEN_OFFER_RANGE = "delivery_men_offer_range";
    public static final String SETTING_DISTANCE_RANGE_TO_DELIVERY = "distance_range_to_delivery";
    public static final String SETTING_CURRENCY_EN = "currency_en";
    public static final String SETTING_CURRENCY_AR = "currency_ar";
    public static final String SETTING_COMPANY_PERCENT = "company_percent";
    public static final String SETTING_TAX = "tax";
    public static final String SETTING_COMPANY_PHONE1 = "company_phone1";
    public static final String SETTING_COMPANY_PHONE2 = "company_phone2";
    public static final String SETTING_COMPANY_ADDRESS = "company_address";
    public static final String SETTING_COMPANY_EMAIL = "company_email";
    public static final String SETTING_SEARCH_RADIUS = "search_radius";
    public static final String SETTING_TERMS_AND_CONDITIONS_LINK = "terms_and_conditions_link";
    public static final String SETTING_PRIVACY_POLICY = "privacy_policy_link";
    public static final String SETTING_NEW_ORDER_LIFETIME = "new_order_lifetime";
    public static final String SETTING_MAX_CONCURRENT_OFFERS = "max_concurrent_offers";
    public static final String SETTING_GOOGLE_API_KEY = "google_api_key";
    public static final String SETTING_LOWER_CREDIT_ALLOWENCE = "lower_credit_allowence";
    public static final String SETTING_ALLOWED_VEHICLE_BASE_FARE = "base_fare";
    public static final String SETTING_ALLOWED_VEHICLE_FARE_PER_MIN = "fare_per_min";
    public static final String SETTING_ALLOWED_VEHICLE_FARE_PER_KM = "fare_per_km";
    public static final String SETTING_ALLOWED_VEHICLES_MODEL = "model";
    public static final String SETTING_ALLOWED_VEHICLES_YEAR = "year";
    public static final String SETTING_ALLOWED_VEHICLE_ICON = "allowed_vehicle_icon";
    public static final String SETTING_ALLOWED_VEHICLE_CATEGORY_ICON = "vehicle_category_icon";
    public static final String SETTING_VEHICLES = "vehicles";


    public static final String LOGIN_EMAIL = "login_email";
    public static final String LOGIN_PASSWORD = "login_password";
    public static final String FIREBASE_KEY_USERS_COUNT = "users_count";
    public static final String FIREBASE_KEY_USER_NUMBER = "user_number";


    public static final String FIREBASE_KEY_STATISTICS_USER_PAYMENTS = "user_payments";
    public static final String FIREBASE_KEY_STATISTICS_TRIPS_DISTANCES = "trips_distances";
    public static final String FIREBASE_KEY_STATISTICS_TRIPS_TIMES = "trips_times";
    public static final String FIREBASE_KEY_STATISTICS_NUMBER_OF_COMPLETED_TRIPS = "number_of_completed_trips";
    public static final String FIREBASE_KEY_STATISTICS_NUMBER_OF_CANCELLED_TRIPS = "number_of_cancelled_trips";
    public static final String FIREBASE_KEY_STATISTICS_DRIVERS_EARNINGS = "drivers_earnings";
    public static final String FIREBASE_KEY_STATISTICS_COMPANY_EARNINGS = "company_earnings";


    // SETTINGS
    public static final String PREF_SETTING_ZOOM_LEVEL = "zoom_level";
    public static final String PREF_SETTING_SEARCH_RADIUS = "search_radius";


    public static final String FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_ID = "vehicle_category_id";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_NAME = "vehicle_category_name";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_BASE_FARE = "vehicle_category_base_fare";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_FARE_PER_KM = "vehicle_category_fare_per_km";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_CATEGORY_FARE_PER_MIN = "vehicle_category_fare_per_min";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_MODEL = "vehicle_model";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_NUMBER = "vehicle_number";
    public static final String FIREBASE_KEY_ORDER_VEHICLE_YEAR = "vehicle_year";

    public static final String FIREBASE_KEY_ALLOWED_VEHICLE_CATEGORY_NAME = "vehicle_category_name";

    public static final String FIREBASE_KEY_USER_DRIVING_LICENSE = "driving_license";
    public static final String FIREBASE_KEY_USER_VEHICLE_PERMIT = "vehicle_permit";
    public static final String FIREBASE_KEY_USER_POLICE_CLEARANCE_CERTIFICATE= "police_clearance_certificate";
    public static final String FIREBASE_KEY_USER_COMMERCIAL_INSURANCE = "commercial_insurance";
    public static final String FIREBASE_KEY_USER_FITNESS_CERTIFICATE= "fitness_certificate";

    public static final String FIREBASE_KEY_ORDER_TAX_PERCENT = "tax_percent";

    public static final String PREF_SETTING_FIRST_TIME = "first_time";
    public static final float DEFAULT_ZOOM_LEVEL = (float) 14.0;
    public static final float DEFAULT_SEARCH_RADIUS = (float) 3.0;
    public static final int REQ_SETTINGS = 8060;

    public static final String POSITION = "position";
    public static final String ERROR_INVALID_FIREBASE_DATA_SNAPSHOT = "ERROR: Invalid Firebase DataSnapshot";
    public static final String FIREBASE_ON_CANCELLED_EVENT = "FIREBASE ON_CANCELLED EVENT";
    public static final String FIREBASE_ON_CHILD_ADDED_EVENT = "FIREBASE ON_CHILD_ADDED EVENT";
    public static final String FIREBASE_ON_CHILD_MOVED_EVENT = "FIREBASE ON_CHILD_MOVED EVENT";
    public static final String FIREBASE_ON_CHILD_REMOVED_EVENT = "FIREBASE ON_CHILD_REMOVED EVENT";

    public static final String FIREBASE_ON_CHILD_CHANGED_EVENT = "FIREBASE ON_CHILD_CHANGED EVENT";
    public static final String FIREBASE_KEY_ORDER_PICKUP_LNG = "order_pickup_lng";
    public static final String FIREBASE_KEY_ORDER_PICKUP_LAT = "order_pickup_lat";




    public static final String TAG = "AppInfo";
    public static final String TAG2 = "AppInfo2";


    // REQUESTS
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 9020;
    public static final int REQUEST_CALL_PHONE = 5552;
    public static final int REQ_PICKUP_MARKER_CHANGE_PLACE = 1234;
    public static final int REQ_SELECT_DROP_LOCATION = 1235;
    public static final int RC_SIGN_UP = 8040;
    public static final int RC_SIGN_IN = 8050;
    public static final int REQ_MY_PLACES = 1050;
    public static final int REQ_GOOGLE_PLACES = 1060;
    public static final int REQ_ADD_NEW_PLACE = 1070;
    public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 100;
    public static final int REQUEST_CHECK_SETTINGS = 200;

    public static final String FIREBASE_KEY_ORDER_IS_ROUND_TRIP = "order_is_round_trip";



    public static final String EXTRA_IDP_RESPONSE = "extra_idp_response";
    public static final String EXTRA_SIGNED_IN_CONFIG = "extra_signed_in_config";
    public static final String TAG1 = "AppInfo1";
    public static final String TRIP_POINT_LATITUDE = "latitude";
    public static final String TRIP_POINT_LONGITUDE = "longitude";
    public static final String TRIP_POINT_BEARING = "bearing";
    public static final String FIREBASE_KEY_ORDER_TRIP_POINTS = "trip_points";
    public static final String FIREBASE_KEY_ORDER_DRIVER_RESPONSE_TRIP_POINTS = "driver_response_trip_points";
    public static final float DEFAULT_ZOOM_OUT_LEVEL = 13;
    public static final String MESSAGE_DATE = "message_date";
    public static final String MESSAGE_TO = "message_to";
    public static final String MESSAGE_BODY = "message_body";
    public static final String MESSAGE_SUBJECT = "message_subject";
    public static final String MESSAGE_TO_ALL = "ALL";
    public static final String MESSAGE_TO_ALL_USERS = "ALL_USERS";
    public static final String MESSAGE_TO_ALL_DRIVERS = "ALL_DRIVERS";

    public static final String FIREBASE_KEY_BALANCE = "balance";
    public static final String FIREBASE_KEY_TRANSACTION_TYPE = "transaction_type";
    public static final String FIREBASE_KEY_TRANSACTION_VALUE = "transaction_value";

    public static final String FIREBASE_KEY_TRANSACTION_DESCRIPTION = "transaction_description";


    public static final String FIREBASE_KEY_ORDER_USER_FULLNAME = "order_user_fullname";
    public static final String FIREBASE_KEY_ORDER_DRIVER_FULLNAME = "order_driver_fullname";
    public static final String FIREBASE_KEY_MY_PLACES = "my_places";
    public static final String MY_PLACE_ADDRESS = "my_place_address";
    public static final String MY_PLACE_LAT = "my_place_lat";
    public static final String MY_PLACE_LNG = "my_place_lng";
    public static final String MY_PLACE_TITLE = "my_place_title";



    public static final String SERVICE_TAG = "ServiceTag";



    public static final String PREF_SETTING_IS_NAVIGATION_ENABLED = "is_navigation_enabled";



    public static final String PREF_VALUE_DESTINATION_DISTANCE = "destination_distance";
    public static final String PREF_VALUE_DESTINATION_TIME = "destination_time";
    public static final String PREF_VALUE_DESTINATION_COST = "destination_cost";
    public static final String PREF_VALUE_DESTINATION_LAT = "destination_lat";
    public static final String PREF_VALUE_DESTINATION_LNG = "destination_lng";
    public static final String PREF_VALUE_DESTINATION_TITLE = "destination_title";
    public static final String PREF_VALUE_PICKUP_LAT = "pickup_lat";
    public static final String PREF_VALUE_PICKUP_LNG = "pickup_lng";
    public static final String PREF_VALUE_PICKUP_TITLE = "pickup_title";
    public static final String PREF_VALUE_PICKUP_TIME = "pickup_time";
    public static final String FIREBASE_KEY_LICENSE_IS_VALID = "is_license_valid";
    public static final String FIREBASE_KEY_LICENSES = "licenses";
    public static final String FIREBASE_KEY_SETTINGS = "Settings";
    public static final String FIREBASE_KEY_LICENSE_EXPIRE_DATE = "license_expire_date";
    public static final String BUNDLE_PARAMS_BUSINESS_TYPE = "business_type";
    public static final String BUNDLE_PARAMS = "bundle_params";
    public static final String BUNDLE_PARAMS_STORE_NAME = "store_name";
    public static final String BUNDLE_PARAMS_STORE_PLACE_ID = "place_id";
    public static final String BUNDLE_PARAMS_STORE_VICINITY = "store_vicinity";
    public static final String BUNDLE_PARAMS_STORE_LAT = "store_lat";
    public static final String BUNDLE_PARAMS_STORE_LNG = "store_lng";
    public static final String CURRENT_LOCATION_LAT = "current_location_lat";
    public static final String CURRENT_LOCATION_LNG = "current_location_lng";



    public static final String ORDER_ID = "order_id";
    public static final String SUPPORT = "support";
    public static final String FIREBASE_KEY_OFFER_NUMBER = "offer_counter";
    public static final String ORDER_LIST_TYPE = "order_list_type";

    public static final int ORDER_LIST_TYPE_NEW = 0;
    public static final int ORDER_LIST_TYPE_MY_ORDERS = 1;
    public static final String OFFER_ID = "offer_id";
    public static final String MESSAGE_ID = "message_id";
    public static final int REQ_ADD_ORDER_RECEIPT = 1230;

    public static final String NEW_ORDERS_NOTIFICATIONS = "new_orders_notifications";
    public static final String TAG3 = "AppInfo3";
    public static final String WEB_URL = "web_url";
    public static final String ACTIVITY_TITLE = "activity_title";
    public static final String PREF_SETTING_RINGTONE = "ringtone";


    public static final String DISTANCE_TO_STORE = "distance_to_store";
    public static final String DISTANCE_TO_CLIENT = "distance_to_client";

    public static final double MAX_DISTANCE_STEP = 1000;


    public static final int OFFER_STATUS_NEW = 0;
    public static final int OFFER_STATUS_AWARDED = 1;
    public static final int OFFER_STATUS_CANCELLED = 2;
    public static final int OFFER_STATUS_FINISHED = 3;


    public static final String SETTING_CHARGE_INSTRUCTIONS = "charge_instructions";
}

