package com.aamys.fresh;

import android.util.Log;

import com.renzvos.ecommerceorderslist.OrderDetails;import java.util.Comparator;

public class SorterComparater {

     public static class DateSorter implements Comparator<OrderDetails>
    {


        @Override
        public int compare(OrderDetails o1, OrderDetails o2) {
            String format = "yyyy-MM-dd HH:mm:ss";
            Log.i("RZ_Order Details", "compare: " + o1.GetDate(format).getTime());
            Log.i("RZ_Order Details", "compare: " + o2.GetDate(format).getTime());
            if (o1.GetDate(format).getTime() < o2.GetDate(format).getTime())
                return 1;
            else if (o1.GetDate(format).getTime() > o2.GetDate(format).getTime())
                return -1;
            else
                return 0;
        }
    }
}
