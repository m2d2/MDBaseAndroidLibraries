package com.m2d2.base.commands;

import com.m2d2.base.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

public abstract class AutoBindingCommand extends Command {

    @Override
    public abstract void execute();


    // ==== user contributions ==== //

    public abstract PersistentModel getRecordWithId(String id);

    public String getIdFieldName() {
        return "id";
    }

    // ==== events ==== //

    public void willStartProcessing() {
        // start db transaction
    }

    public void didEndProcessing() {
        // end db transaction
    }


    // ==== processing ==== //

    public void processRecords(JSONArray list) throws JSONException {
        if (list == null) {
            return;
        }

        willStartProcessing();

        try {

            for (int i = 0; i < list.length(); i++) {
                if (!list.isNull(i)) {
                    process(list.getJSONObject(i));
                }
            }

        } finally {

            didEndProcessing();
        }

    }

    public void processRecord(JSONObject item) throws JSONException {
        willStartProcessing();
        try {
            process(item);
        } finally {
            didEndProcessing();
        }
    }

    protected void process(JSONObject item) throws JSONException {
        String id = item.getString(getIdFieldName());
        PersistentModel record = getRecordWithId(id);

        assert (record != null);

        Iterator<String> keys = item.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String setMethodName = "set" + Utils.toCamelCase(key);
            try {
                Method setMethod = record.getClass().getMethod(setMethodName, item.get(key).getClass());
                if (setMethod != null) {
                    setMethod.invoke(record, item.get(key));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        record.save();
    }

}
