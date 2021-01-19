package com.optimus.eds.ui.customer_complaints.model;

import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup;
import java.util.List;

/**
 * Created By apple on 5/1/19
 */
public class ComplaintTypeModel extends MultiCheckExpandableGroup {

    public ComplaintTypeModel(String title, List<ComplaintReasonModel> items) {
        super(title, items);
    }
}
