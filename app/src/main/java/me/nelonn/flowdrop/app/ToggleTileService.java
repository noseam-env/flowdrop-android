package me.nelonn.flowdrop.app;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class ToggleTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        if (getQsTile().getState() == Tile.STATE_ACTIVE) {
            getQsTile().setState(Tile.STATE_INACTIVE);
        } else {
            getQsTile().setState(Tile.STATE_ACTIVE);
        }
        getQsTile().updateTile();
    }

}
