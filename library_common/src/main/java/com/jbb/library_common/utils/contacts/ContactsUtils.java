package com.jbb.library_common.utils.contacts;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ContactsUtils {

    private Context context;
    private String LOG_TAG ="ContactsUtils";

    public ContactsUtils(Context context) {
        this.context = context;
    }

    private static final Map<String, String> dbMap = new HashMap<String, String>();

    static {
        dbMap.put("id", ContactsContract.Data.CONTACT_ID);
        dbMap.put("displayName", ContactsContract.Contacts.DISPLAY_NAME);
        dbMap.put("name", ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        dbMap.put("name.formatted", ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        dbMap.put("name.familyName", ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
        dbMap.put("name.givenName", ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        dbMap.put("name.middleName", ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
        dbMap.put("name.honorificPrefix", ContactsContract.CommonDataKinds.StructuredName.PREFIX);
        dbMap.put("name.honorificSuffix", ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
        dbMap.put("nickname", ContactsContract.CommonDataKinds.Nickname.NAME);
        dbMap.put("phoneNumbers", ContactsContract.CommonDataKinds.Phone.NUMBER);
        dbMap.put("phoneNumbers.value", ContactsContract.CommonDataKinds.Phone.NUMBER);
        dbMap.put("emails", ContactsContract.CommonDataKinds.Email.DATA);
        dbMap.put("emails.value", ContactsContract.CommonDataKinds.Email.DATA);
        dbMap.put("addresses", ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
        dbMap.put("addresses.formatted", ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
        dbMap.put("addresses.streetAddress", ContactsContract.CommonDataKinds.StructuredPostal.STREET);
        dbMap.put("addresses.locality", ContactsContract.CommonDataKinds.StructuredPostal.CITY);
        dbMap.put("addresses.region", ContactsContract.CommonDataKinds.StructuredPostal.REGION);
        dbMap.put("addresses.postalCode", ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
        dbMap.put("addresses.country", ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
        dbMap.put("ims", ContactsContract.CommonDataKinds.Im.DATA);
        dbMap.put("ims.value", ContactsContract.CommonDataKinds.Im.DATA);
        dbMap.put("organizations", ContactsContract.CommonDataKinds.Organization.COMPANY);
        dbMap.put("organizations.name", ContactsContract.CommonDataKinds.Organization.COMPANY);
        dbMap.put("organizations.department", ContactsContract.CommonDataKinds.Organization.DEPARTMENT);
        dbMap.put("organizations.title", ContactsContract.CommonDataKinds.Organization.TITLE);
        dbMap.put("birthday", ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
        dbMap.put("note", ContactsContract.CommonDataKinds.Note.NOTE);
        dbMap.put("photos.value", ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        //dbMap.put("categories.value", null);
        dbMap.put("urls", ContactsContract.CommonDataKinds.Website.URL);
        dbMap.put("urls.value", ContactsContract.CommonDataKinds.Website.URL);
    }



    public JSONArray search(JSONArray fields, JSONObject options) {
        // Get the find options
        String searchTerm = "";
        int limit = Integer.MAX_VALUE;
        boolean multiple = true;
        boolean hasPhoneNumber = false;

        if (options != null) {
            searchTerm = options.optString("filter");
            if (searchTerm.length() == 0) {
                searchTerm = "%";
            }
            else {
                searchTerm = "%" + searchTerm + "%";
            }

            try {
                multiple = options.getBoolean("multiple");
                if (!multiple) {
                    limit = 1;
                }
            } catch (JSONException e) {
                // Multiple was not specified so we assume the default is true.
//                LOG.e(LOG_TAG, e.getMessage(), e);
            }

            try {
                hasPhoneNumber = options.getBoolean("hasPhoneNumber");
            } catch (JSONException e) {
                // hasPhoneNumber was not specified so we assume the default is false.
            }
        }
        else {
            searchTerm = "%";
        }

        // Loop through the fields the user provided to see what data should be returned.
        HashMap<String, Boolean> populate = buildPopulationSet(options);

        // Build the ugly where clause and where arguments for one big query.
        WhereOptions whereOptions = buildWhereClause(fields, searchTerm, hasPhoneNumber);

        // Get all the id's where the search term matches the fields passed in.
        Cursor idCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[] { ContactsContract.Data.CONTACT_ID },
                whereOptions.getWhere(),
                whereOptions.getWhereArgs(),
                ContactsContract.Data.CONTACT_ID + " ASC");

        // Create a set of unique ids
        Set<String> contactIds = new HashSet<String>();
        int idColumn = -1;
        while (idCursor.moveToNext()) {
            if (idColumn < 0) {
                idColumn = idCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            }
            contactIds.add(idCursor.getString(idColumn));
        }
        idCursor.close();

        // Build a query that only looks at ids
        WhereOptions idOptions = buildIdClause(contactIds, searchTerm, hasPhoneNumber);

        // Determine which columns we should be fetching.
        HashSet<String> columnsToFetch = new HashSet<String>();
        columnsToFetch.add(ContactsContract.Data.CONTACT_ID);
        columnsToFetch.add(ContactsContract.Data.RAW_CONTACT_ID);
        columnsToFetch.add(ContactsContract.Data.MIMETYPE);

        if (isRequired("displayName", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        }
        if (isRequired("name", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredName.PREFIX);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
        }
        if (isRequired("phoneNumbers", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Phone._ID);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Phone.TYPE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Phone.LABEL);
        }
        if (isRequired("emails", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Email._ID);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Email.DATA);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Email.TYPE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Email.LABEL);
        }
        if (isRequired("addresses", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal._ID);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization.TYPE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.STREET);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.CITY);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.REGION);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
            columnsToFetch.add(ContactsContract.CommonDataKinds.StructuredPostal.LABEL);
        }
        if (isRequired("organizations", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization._ID);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization.TYPE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization.DEPARTMENT);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization.COMPANY);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization.TITLE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Organization.LABEL);
        }
        if (isRequired("ims", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Im._ID);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Im.DATA);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Im.TYPE);
        }
        if (isRequired("note", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Note.NOTE);
        }
        if (isRequired("nickname", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Nickname.NAME);
        }
        if (isRequired("urls", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Website._ID);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Website.URL);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Website.TYPE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Website.LABEL);
        }
        if (isRequired("birthday", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Event.START_DATE);
            columnsToFetch.add(ContactsContract.CommonDataKinds.Event.TYPE);
        }
        if (isRequired("photos", populate)) {
            columnsToFetch.add(ContactsContract.CommonDataKinds.Photo._ID);
        }

        // Do the id query
        Cursor c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                columnsToFetch.toArray(new String[] {}),
                idOptions.getWhere(),
                idOptions.getWhereArgs(),
                ContactsContract.Data.CONTACT_ID + " ASC");

        JSONArray contacts = populateContactArray(limit, populate, c);

        if (!c.isClosed()) {
            c.close();
        }
        return contacts;
    }



    protected HashMap<String, Boolean> buildPopulationSet(JSONObject options) {
        HashMap<String, Boolean> map = new HashMap<String, Boolean>();

        String key;
        try {
            JSONArray desiredFields = null;
            if (options!=null && options.has("desiredFields")) {
                desiredFields = options.getJSONArray("desiredFields");
            }
            if (desiredFields == null || desiredFields.length() == 0) {
                map.put("displayName", true);
                map.put("name", true);
                map.put("nickname", true);
                map.put("phoneNumbers", true);
                map.put("emails", true);
                map.put("addresses", true);
                map.put("ims", true);
                map.put("organizations", true);
                map.put("birthday", true);
                map.put("note", true);
                map.put("urls", true);
                map.put("photos", true);
                map.put("categories", true);
            } else {
                for (int i = 0; i < desiredFields.length(); i++) {
                    key = desiredFields.getString(i);
                    if (key.startsWith("displayName")) {
                        map.put("displayName", true);
                    } else if (key.startsWith("name")) {
                        map.put("displayName", true);
                        map.put("name", true);
                    } else if (key.startsWith("nickname")) {
                        map.put("nickname", true);
                    } else if (key.startsWith("phoneNumbers")) {
                        map.put("phoneNumbers", true);
                    } else if (key.startsWith("emails")) {
                        map.put("emails", true);
                    } else if (key.startsWith("addresses")) {
                        map.put("addresses", true);
                    } else if (key.startsWith("ims")) {
                        map.put("ims", true);
                    } else if (key.startsWith("organizations")) {
                        map.put("organizations", true);
                    } else if (key.startsWith("birthday")) {
                        map.put("birthday", true);
                    } else if (key.startsWith("note")) {
                        map.put("note", true);
                    } else if (key.startsWith("urls")) {
                        map.put("urls", true);
                    } else if (key.startsWith("photos")) {
                        map.put("photos", true);
                    } else if (key.startsWith("categories")) {
                        map.put("categories", true);
                    }
                }
            }
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return map;
    }


    private WhereOptions buildIdClause(Set<String> contactIds, String searchTerm, boolean hasPhoneNumber) {
        WhereOptions options = new WhereOptions();

        // If the user is searching for every contact then short circuit the method
        // and return a shorter where clause to be searched.
        if (searchTerm.equals("%") && !hasPhoneNumber) {
            options.setWhere("(" + ContactsContract.Data.CONTACT_ID + " LIKE ? )");
            options.setWhereArgs(new String[] { searchTerm });
            return options;
        }

        // This clause means that there are specific ID's to be populated
        Iterator<String> it = contactIds.iterator();
        StringBuffer buffer = new StringBuffer("(");

        while (it.hasNext()) {
            buffer.append("'" + it.next() + "'");
            if (it.hasNext()) {
                buffer.append(",");
            }
        }
        buffer.append(")");

        options.setWhere(ContactsContract.Data.CONTACT_ID + " IN " + buffer.toString());
        options.setWhereArgs(null);

        return options;
    }

    private WhereOptions buildWhereClause(JSONArray fields, String searchTerm, boolean hasPhoneNumber) {

        ArrayList<String> where = new ArrayList<String>();
        ArrayList<String> whereArgs = new ArrayList<String>();

        WhereOptions options = new WhereOptions();

        /*
         * Special case where the user wants all fields returned
         */
        if (isWildCardSearch(fields)) {
            // Get all contacts with all properties
            if ("%".equals(searchTerm) && !hasPhoneNumber) {
                options.setWhere("(" + ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? )");
                options.setWhereArgs(new String[] { searchTerm });
                return options;
            } else {
                // Get all contacts that match the filter but return all properties
                where.add("(" + dbMap.get("displayName") + " LIKE ? )");
                whereArgs.add(searchTerm);
                where.add("(" + dbMap.get("name") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("nickname") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("phoneNumbers") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("emails") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("addresses") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("ims") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("organizations") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("note") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
                where.add("(" + dbMap.get("urls") + " LIKE ? AND "
                        + ContactsContract.Data.MIMETYPE + " = ? )");
                whereArgs.add(searchTerm);
                whereArgs.add(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
            }
        }

        /*
         * Special case for when the user wants all the contacts but
         */
        if ("%".equals(searchTerm) && !hasPhoneNumber) {
            options.setWhere("(" + ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? )");
            options.setWhereArgs(new String[] { searchTerm });
            return options;
        }else if(!("%".equals(searchTerm))){
            String key;
            try {
                //LOG.d(LOG_TAG, "How many fields do we have = " + fields.length());
                for (int i = 0; i < fields.length(); i++) {
                    key = fields.getString(i);

                    if (key.equals("id")) {
                        where.add("(" + dbMap.get(key) + " = ? )");
                        whereArgs.add(searchTerm.substring(1, searchTerm.length() - 1));
                    }
                    else if (key.startsWith("displayName")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? )");
                        whereArgs.add(searchTerm);
                    }
                    else if (key.startsWith("name")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("nickname")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("phoneNumbers")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("emails")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("addresses")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("ims")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("organizations")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
                    }
                    //        else if (key.startsWith("birthday")) {
                    //          where.add("(" + dbMap.get(key) + " LIKE ? AND "
                    //              + ContactsContract.Data.MIMETYPE + " = ? )");
                    //        }
                    else if (key.startsWith("note")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE);
                    }
                    else if (key.startsWith("urls")) {
                        where.add("(" + dbMap.get(key) + " LIKE ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ? )");
                        whereArgs.add(searchTerm);
                        whereArgs.add(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                    }
                }
            } catch (JSONException e) {
//                LOG.e(LOG_TAG, e.getMessage(), e);
            }
        }

        // Creating the where string
        StringBuffer selection = new StringBuffer();
        for (int i = 0; i < where.size(); i++) {
            selection.append(where.get(i));
            if (i != (where.size() - 1)) {
                selection.append(" OR ");
            }
        }

        //Only contacts with phone number informed
        if(hasPhoneNumber){
            if(where.size()>0){
                selection.insert(0,"(");
                selection.append(") AND (" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?)");
                whereArgs.add("1");
            }else{
                selection.append("(" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?)");
                whereArgs.add("1");
            }
        }

        options.setWhere(selection.toString());

        // Creating the where args array
        String[] selectionArgs = new String[whereArgs.size()];
        for (int i = 0; i < whereArgs.size(); i++) {
            selectionArgs[i] = whereArgs.get(i);
        }
        options.setWhereArgs(selectionArgs);

        return options;
    }


    protected boolean isRequired(String key, HashMap<String,Boolean> map) {
        Boolean retVal = map.get(key);
        return (retVal == null) ? false : retVal.booleanValue();
    }

    private boolean isWildCardSearch(JSONArray fields) {
        // Only do a wildcard search if we are passed ["*"]
        if (fields.length() == 1) {
            try {
                if ("*".equals(fields.getString(0))) {
                    return true;
                }
            } catch (JSONException e) {
                return false;
            }
        }
        return false;
    }


    private JSONArray populateContactArray(int limit,
                                           HashMap<String, Boolean> populate, Cursor c) {

        String contactId = "";
        String rawId = "";
        String oldContactId = "";
        boolean newContact = true;
        String mimetype = "";

        JSONArray contacts = new JSONArray();
        JSONObject contact = new JSONObject();
        JSONArray organizations = new JSONArray();
        JSONArray addresses = new JSONArray();
        JSONArray phones = new JSONArray();
        JSONArray emails = new JSONArray();
        JSONArray ims = new JSONArray();
        JSONArray websites = new JSONArray();
        JSONArray photos = new JSONArray();

        // Column indices
        int colContactId = c.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int colRawContactId = c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
        int colMimetype = c.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int colDisplayName = c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME);
        int colNote = c.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE);
        int colNickname = c.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME);
        int colEventType = c.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE);

        if (c.getCount() > 0) {
            while (c.moveToNext() && (contacts.length() <= (limit - 1))) {
                try {
                    contactId = c.getString(colContactId);
                    rawId = c.getString(colRawContactId);

                    // If we are in the first row set the oldContactId
                    if (c.getPosition() == 0) {
                        oldContactId = contactId;
                    }

                    // When the contact ID changes we need to push the Contact object
                    // to the array of contacts and create new objects.
                    if (!oldContactId.equals(contactId)) {
                        // Populate the Contact object with it's arrays
                        // and push the contact into the contacts array
                        contacts.put(populateContact(contact, organizations, addresses, phones,
                                emails, ims, websites, photos));

                        // Clean up the objects
                        contact = new JSONObject();
                        organizations = new JSONArray();
                        addresses = new JSONArray();
                        phones = new JSONArray();
                        emails = new JSONArray();
                        ims = new JSONArray();
                        websites = new JSONArray();
                        photos = new JSONArray();

                        // Set newContact to true as we are starting to populate a new contact
                        newContact = true;
                    }

                    // When we detect a new contact set the ID and display name.
                    // These fields are available in every row in the result set returned.
                    if (newContact) {
                        newContact = false;
                        contact.put("id", contactId);
                        contact.put("rawId", rawId);
                    }

                    // Grab the mimetype of the current row as it will be used in a lot of comparisons
                    mimetype = c.getString(colMimetype);

                    if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE) && isRequired("displayName", populate)) {
                        contact.put("displayName", c.getString(colDisplayName));
                    }

                    if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            && isRequired("name", populate)) {
                        contact.put("name", nameQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            && isRequired("phoneNumbers", populate)) {
                        phones.put(phoneQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            && isRequired("emails", populate)) {
                        emails.put(emailQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            && isRequired("addresses", populate)) {
                        addresses.put(addressQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            && isRequired("organizations", populate)) {
                        organizations.put(organizationQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                            && isRequired("ims", populate)) {
                        ims.put(imQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                            && isRequired("note", populate)) {
                        contact.put("note", c.getString(colNote));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                            && isRequired("nickname", populate)) {
                        contact.put("nickname", c.getString(colNickname));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                            && isRequired("urls", populate)) {
                        websites.put(websiteQuery(c));
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)) {
                        if (isRequired("birthday", populate) &&
                                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY == c.getInt(colEventType)) {

                            Date birthday = getBirthday(c);
                            if (birthday != null) {
                                contact.put("birthday", birthday.getTime());
                            }
                        }
                    }
                    else if (mimetype.equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            && isRequired("photos", populate)) {
                        JSONObject photo = photoQuery(c, contactId);
                        if (photo != null) {
                            photos.put(photo);
                        }
                    }
                } catch (JSONException e) {
//                    LOG.e(LOG_TAG, e.getMessage(), e);
                }

                // Set the old contact ID
                oldContactId = contactId;

            }

            // Push the last contact into the contacts array
            if (contacts.length() < limit) {
                contacts.put(populateContact(contact, organizations, addresses, phones,
                        emails, ims, websites, photos));
            }
        }
        c.close();
        return contacts;
    }


    private JSONObject populateContact(JSONObject contact, JSONArray organizations,
                                       JSONArray addresses, JSONArray phones, JSONArray emails,
                                       JSONArray ims, JSONArray websites, JSONArray photos) {
        try {
            // Only return the array if it has at least one entry
            if (organizations.length() > 0) {
                contact.put("organizations", organizations);
            }
            if (addresses.length() > 0) {
                contact.put("addresses", addresses);
            }
            if (phones.length() > 0) {
                contact.put("phoneNumbers", phones);
            }
            if (emails.length() > 0) {
                contact.put("emails", emails);
            }
            if (ims.length() > 0) {
                contact.put("ims", ims);
            }
            if (websites.length() > 0) {
                contact.put("urls", websites);
            }
            if (photos.length() > 0) {
                contact.put("photos", photos);
            }
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return contact;
    }

    private JSONObject photoQuery(Cursor cursor, String contactId) {
        JSONObject photo = new JSONObject();
        Cursor photoCursor = null;
        try {
            photo.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Photo._ID)));
            photo.put("pref", false);
            photo.put("type", "url");
            Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, (Long.valueOf(contactId)));
            Uri photoUri = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            photo.put("value", photoUri.toString());

            // Query photo existance
            photoCursor = context.getContentResolver().query(photoUri, new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
            if (photoCursor == null) return null;
            if (!photoCursor.moveToFirst()) {
                photoCursor.close();
                return null;
            }
            photoCursor.close();
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (SQLiteException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if(photoCursor != null && !photoCursor.isClosed()) {
                photoCursor.close();
            }
        }
        return photo;
    }


    private Date getBirthday(JSONObject contact) {
        try {
            Long timestamp = contact.getLong("birthday");
            return new Date(timestamp);
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, "Could not get birthday from JSON object", e);
            return null;
        }
    }

    private JSONObject websiteQuery(Cursor cursor) {
        JSONObject website = new JSONObject();
        try {
            int typeCode = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website.TYPE));
            String typeLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website.LABEL));
            String type = (typeCode == ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM) ? typeLabel : getContactType(typeCode);
            website.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website._ID)));
            website.put("pref", false); // Android does not store pref attribute
            website.put("value", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website.URL)));
            website.put("type", type);
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return website;
    }


    private int getContactType(String string) {
        int type = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
        if (string != null) {

            String lowerType = string.toLowerCase(Locale.getDefault());

            if ("home".equals(lowerType)) {
                return ContactsContract.CommonDataKinds.Email.TYPE_HOME;
            }
            else if ("work".equals(lowerType)) {
                return ContactsContract.CommonDataKinds.Email.TYPE_WORK;
            }
            else if ("other".equals(lowerType)) {
                return ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
            }
            else if ("mobile".equals(lowerType)) {
                return ContactsContract.CommonDataKinds.Email.TYPE_MOBILE;
            }
            return ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM;
        }
        return type;
    }

    private String getContactType(int type) {
        String stringType;
        switch (type) {
            case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM:
                stringType = "custom";
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                stringType = "home";
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                stringType = "work";
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                stringType = "mobile";
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
            default:
                stringType = "other";
                break;
        }
        return stringType;
    }

    private Date getBirthday(Cursor c) {

        try {
            int colBirthday = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.START_DATE);
            return Date.valueOf(c.getString(colBirthday));
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, "Failed to get birthday for contact from cursor", e);
            return null;
        }
    }

    private JSONObject imQuery(Cursor cursor) {
        JSONObject im = new JSONObject();
        try {
            im.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Im._ID)));
            im.put("pref", false); // Android does not store pref attribute
            im.put("value", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Im.DATA)));
            String protocol = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Im.PROTOCOL));
            if (!isInteger(protocol) || Integer.parseInt(protocol) == ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM) {
                // the protocol is custom, get its name and put it into JSON
                protocol = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
                im.put("type", protocol);
            } else {
                // (the protocol is one of the standard ones) look up its type and then put it into JSON
                im.put("type", getImType(Integer.parseInt(protocol)));
            }
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return im;
    }

    private String getImType(int type) {
        String stringType;
        switch (type) {
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_AIM:
                stringType = "AIM";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK:
                stringType = "Google Talk";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_ICQ:
                stringType = "ICQ";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER:
                stringType = "Jabber";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN:
                stringType = "MSN";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_NETMEETING:
                stringType = "NetMeeting";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ:
                stringType = "QQ";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE:
                stringType = "Skype";
                break;
            case ContactsContract.CommonDataKinds.Im.PROTOCOL_YAHOO:
                stringType = "Yahoo";
                break;
            default:
                stringType = "custom";
                break;
        }
        return stringType;
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }

    private JSONObject organizationQuery(Cursor cursor) {
        JSONObject organization = new JSONObject();
        try {
            int typeCode = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.TYPE));
            String typeLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.LABEL));
            String type = (typeCode == ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM) ? typeLabel : getOrgType(typeCode);
            organization.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization._ID)));
            organization.put("pref", false); // Android does not store pref attribute
            organization.put("type", type);
            organization.put("department", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DEPARTMENT)));
            organization.put("name", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.COMPANY)));
            organization.put("title", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.TITLE)));
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return organization;
    }

    /**
     * Create a ContactAddress JSONObject
     * @param cursor the current database row
     * @return a JSONObject representing a ContactAddress
     */
    private JSONObject addressQuery(Cursor cursor) {
        JSONObject address = new JSONObject();
        try {
            int typeCode = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
            String typeLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.LABEL));
            String type = (typeCode == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM) ? typeLabel : getAddressType(typeCode);
            address.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal._ID)));
            address.put("pref", false); // Android does not store pref attribute
            address.put("type", type);
            address.put("formatted", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
            address.put("streetAddress", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.STREET)));
            address.put("locality", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.CITY)));
            address.put("region", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.REGION)));
            address.put("postalCode", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)));
            address.put("country", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)));
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return address;
    }

    private String getAddressType(int type) {
        String stringType;
        switch (type) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                stringType = "home";
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                stringType = "work";
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
            default:
                stringType = "other";
                break;
        }
        return stringType;
    }

    private String getOrgType(int type) {
        String stringType;
        switch (type) {
            case ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM:
                stringType = "custom";
                break;
            case ContactsContract.CommonDataKinds.Organization.TYPE_WORK:
                stringType = "work";
                break;
            case ContactsContract.CommonDataKinds.Organization.TYPE_OTHER:
            default:
                stringType = "other";
                break;
        }
        return stringType;
    }

    private JSONObject emailQuery(Cursor cursor) {
        JSONObject email = new JSONObject();
        try {
            int typeCode = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.TYPE));
            String typeLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.LABEL));
            String type = (typeCode == ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM) ? typeLabel : getContactType(typeCode);
            email.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email._ID)));
            email.put("pref", false); // Android does not store pref attribute
            email.put("value", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA)));
            email.put("type", type);
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return email;
    }

    private JSONObject nameQuery(Cursor cursor) {
        JSONObject contactName = new JSONObject();
        try {
            String familyName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
            String givenName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
            String middleName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
            String honorificPrefix = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.PREFIX));
            String honorificSuffix = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.SUFFIX));

            // Create the formatted name
            StringBuffer formatted = new StringBuffer("");
            if (!TextUtils.isEmpty(honorificPrefix)) {
                formatted.append(honorificPrefix + " ");
            }
            if (!TextUtils.isEmpty(givenName)) {
                formatted.append(givenName + " ");
            }
            if (!TextUtils.isEmpty(middleName)) {
                formatted.append(middleName + " ");
            }
            if (!TextUtils.isEmpty(familyName)) {
                formatted.append(familyName);
            }
            if (!TextUtils.isEmpty(honorificSuffix)) {
                formatted.append(" " + honorificSuffix);
            }
            if (TextUtils.isEmpty(formatted)) {
                formatted = null;
            }

            contactName.put("familyName", familyName);
            contactName.put("givenName", givenName);
            contactName.put("middleName", middleName);
            contactName.put("honorificPrefix", honorificPrefix);
            contactName.put("honorificSuffix", honorificSuffix);
            contactName.put("formatted", formatted);
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        }
        return contactName;
    }

    /**
     * Create a ContactField JSONObject
     * @param cursor the current database row
     * @return a JSONObject representing a ContactField
     */
    private JSONObject phoneQuery(Cursor cursor) {
        JSONObject phoneNumber = new JSONObject();
        try {
            int typeCode = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
            String typeLabel = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL));
            String type = (typeCode == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) ? typeLabel : getPhoneType(typeCode);
            phoneNumber.put("id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID)));
            phoneNumber.put("pref", false); // Android does not store pref attribute
            phoneNumber.put("value", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            phoneNumber.put("type",type);
        } catch (JSONException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
//            LOG.e(LOG_TAG, e.getMessage(), e);
        } catch (Exception excp) {
//            LOG.e(LOG_TAG, excp.getMessage(), excp);
        }
        return phoneNumber;
    }

    private String getPhoneType(int type) {
        String stringType;

        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                stringType = "custom";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                stringType = "home fax";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                stringType = "work fax";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                stringType = "home";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                stringType = "mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                stringType = "pager";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                stringType = "work";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                stringType = "callback";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                stringType = "car";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                stringType = "company main";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                stringType = "other fax";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                stringType = "radio";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                stringType = "telex";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                stringType = "tty tdd";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                stringType = "work mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                stringType = "work pager";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                stringType = "assistant";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                stringType = "mms";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                stringType = "isdn";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
            default:
                stringType = "other";
                break;
        }
        return stringType;
    }
}
