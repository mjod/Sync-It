package com.example.matthew.finalproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew on 3/1/2015.
 */
public class DrawerData {
    List<Map<String, ?>> dataList;

    public List<Map<String, ?>> getDataList() {
        return dataList;
    }

    public int getSize() {
        return dataList.size();
    }

    public HashMap getItem(int i) {
        if (i >= 0 && i < dataList.size()) {
            return (HashMap) dataList.get(i);
        } else return null;
    }

    public void makeFalse() {
        for (Map<String, ?> data : dataList) {
            final HashMap<String, Boolean> itemMap_bool = (HashMap<String, Boolean>) data;
            itemMap_bool.put("selection", false);
        }
    }

    public DrawerData() {
        dataList = new ArrayList<Map<String, ?>>();
        Integer viewType = 0;
        String title = "Upcoming Events";
        Integer image = R.drawable.ppl;
        boolean selection = true;
        dataList.add(createDrawer(title, image, selection, viewType));
        title = "Calendar";
        image = R.drawable.calendar;
        selection = false;
        dataList.add(createDrawer(title, image, selection, viewType));
        title = "Parental Controls";
        image = R.drawable.settings;
        selection = false;
        dataList.add(createDrawer(title, image, selection, viewType));
        title = "About Me";
        image = R.drawable.star;
        selection = false;
        dataList.add(createDrawer(title, image, selection, viewType));
        title = "Logout";
        image = null;
        selection = false;
        dataList.add(createDrawer(title, image, selection, viewType));

    }

    private HashMap createDrawer(String name, Integer image, boolean selection, Integer viewType) {
        HashMap drawer = new HashMap();
        drawer.put("image", image);
        drawer.put("name", name);
        drawer.put("selection", selection);
        drawer.put("viewtype", viewType);
        return drawer;
    }
}
