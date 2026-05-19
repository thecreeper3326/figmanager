package net.johnseagull.figManager;

import java.util.List;

/**
 * Can be used to group settings
 */
public class FigGroup {
    public List<String> value;
    public int columns;
    public boolean showDesc = false;
    public boolean hL;
    public float ratio;
    /**
     * Widget that can be used to combine Fig entries into groups
     * @param value List of strings containing the field names of the desired config values
     * @param columns Number of columns to split the Figs provided in <code>value</code> into
     * @param horizontalLayout Determines if the option label should be displayed beside (true) or above (false) the option entry.
     * @param ratio If <code>horizontalLayout</code> is true, this will determine the ratio between label and entry.<br>Example: 0.2f would mean 20% entry and 80% label
     * */
    public FigGroup(List<String> value, int columns, boolean horizontalLayout, float ratio) {

        this.columns = columns;
        this.value = value;
        this.hL = horizontalLayout;
        this.ratio = ratio;
    }
}
