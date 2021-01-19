package com.optimus.eds.ui.merchandize.asset_verification;

import com.optimus.eds.db.entities.Asset;

public interface AssetVerificationStatusListener {
    void onStatusChange(Asset asset);
}
