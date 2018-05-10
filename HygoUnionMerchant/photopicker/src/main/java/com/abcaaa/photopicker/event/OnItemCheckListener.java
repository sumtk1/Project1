package com.abcaaa.photopicker.event;

import com.abcaaa.photopicker.entity.Photo;

/**
 * Created by Dlt on 2017/3/3 12:45
 */
public interface OnItemCheckListener {
    /***
     *
     * @param position 所选图片的位置
     * @param path     所选的图片
     * @param selectedItemCount  已选数量
     * @return enable check
     */
    boolean onItemCheck(int position, Photo path, int selectedItemCount);
}
