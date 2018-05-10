package com.abcaaa.photopicker.event;

import com.abcaaa.photopicker.entity.Photo;

/**
 * Created by Dlt on 2017/3/3 12:47
 */
public interface Selectable {


    /**
     * Indicates if the item at position position is selected
     *
     * @param photo Photo of the item to check
     * @return true if the item is selected, false otherwise
     */
    boolean isSelected(Photo photo);

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Photo of the item to toggle the selection status for
     */
    void toggleSelection(Photo photo);

    /**
     * Clear the selection status for all items
     */
    void clearSelection();

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    int getSelectedItemCount();
}
