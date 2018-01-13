package com.example.hbl.bluetooth.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;


/**
 * power by
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MM.:  .:'   `:::  .:`MMMMMMMMMMM|`MMM'|MMMMMMMMMMM':  .:'   `:::  .:'.MM
 * MMMM.     :          `MMMMMMMMMM  :*'  MMMMMMMMMM'        :        .MMMM
 * MMMMM.    ::    .     `MMMMMMMM'  ::   `MMMMMMMM'   .     ::   .  .MMMMM
 * MMMMMM. :   :: ::'  :   :: ::'  :   :: ::'      :: ::'  :   :: ::.MMMMMM
 * MMMMMMM    ;::         ;::         ;::         ;::         ;::   MMMMMMM
 * MMMMMMM .:'   `:::  .:'   `:::  .:'   `:::  .:'   `:::  .:'   `::MMMMMMM
 * MMMMMM'     :           :           :           :           :    `MMMMMM
 * MMMMM'______::____      ::    .     ::    .     ::     ___._::____`MMMMM
 * MMMMMMMMMMMMMMMMMMM`---._ :: ::'  :   :: ::'  _.--::MMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMM::.         ::  .--MMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM-.     ;::-MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM. .:' .M:F_P:MMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM.   .MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\ /MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMVMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * Created by Paper on 16/8/1.
 */
public class CommonFlexAdapter<T extends IFlexible> extends FlexibleAdapter {
  private int status = 0;

  private int positionOld = -1;
  private ArrayMap<String, Object> Tags = new ArrayMap<>();

  public CommonFlexAdapter(@NonNull List items) {
    super(items, null, true);
  }

  public CommonFlexAdapter(@NonNull List items, @Nullable Object listeners) {
    super(items, listeners, true);
  }

  public void setTag(String key, Object value) {
    Tags.put(key, value);
  }

  public Object getTag(String key) {
    return Tags.get(key);
  }

  //@Override public List<Animator> getAnimators(View itemView, int position, boolean isSelected) {
  //  List<Animator> animators = new ArrayList<Animator>();
  //
  //  if (position != positionOld) positionOld = position;
  //
  //  return animators;
  //}

  @Override public boolean hasNewSearchText(String newText) {
    //return super.hasNewSearchText(newText);
    return true;
  }

  public int index(T t) {
    if (t == null) {
      return -1;
    }
    for (int i = 0; i < getItemCount(); i++) {
      if (getItem(i).equals(t)) return i;
    }
    return -1;
  }

  public List getMainItems() {
    List ret = new ArrayList();
    for (int i = 0; i < getItemCount(); i++) {
      ret.add(getItem(i));
    }
    return ret;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}