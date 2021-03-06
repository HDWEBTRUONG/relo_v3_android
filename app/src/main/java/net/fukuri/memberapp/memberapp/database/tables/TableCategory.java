package net.fukuri.memberapp.memberapp.database.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import framework.phvtUtils.AppLog;
import net.fukuri.memberapp.memberapp.database.MyDatabaseHelper;
import net.fukuri.memberapp.memberapp.model.CatagoryDTO;
import net.fukuri.memberapp.memberapp.util.ConstanArea;
import net.fukuri.memberapp.memberapp.util.Constant;
import net.fukuri.memberapp.memberapp.util.Utils;

/**
 * Created by tonkhanh on 7/21/17.
 */

public class TableCategory {
    public static Callable<List<CatagoryDTO>> getCategory(final MyDatabaseHelper mMyDatabaseHelper, final String area) {
        return new Callable<List<CatagoryDTO>>() {
            @Override
            public List<CatagoryDTO> call() {
                String now= Utils.valueNowTime();
                List<CatagoryDTO> datas= new ArrayList<>();
                String selectQuery = "SELECT "+TableCoupon.COLUMN_CATEGORY_ID+", "+TableCoupon.COLUMN_CATEGORY+
                        " FROM "+TableCoupon.TABLE_COUPON+"  where "+TableCoupon.COLUMN_AREA+" like '%"+area+
                        "%' AND "+ TableCoupon.COLUMN_EXPIRATION_FROM+" < "+ now + " AND "+
                        TableCoupon.COLUMN_EXPIRATION_TO +" > "+now +" group by "+TableCoupon.COLUMN_CATEGORY_ID+" having count("+TableCoupon.COLUMN_SHGRID+") > 0";
                SQLiteDatabase db = mMyDatabaseHelper.getSqLiteDatabase();
                Cursor cursor = db.rawQuery(selectQuery, null);
                try{
                    if (cursor!=null  && cursor.getCount()>0 && cursor.moveToFirst()) {
                        do {
                            CatagoryDTO catagoryDTO = new CatagoryDTO();
                            catagoryDTO.setCatagoryID(cursor.getString(0));
                            catagoryDTO.setGetCatagoryName(cursor.getString(1));
                            datas.add(catagoryDTO);
                        } while (cursor.moveToNext());
                    }
                    //cursor.close();
                    db.close();
                }catch (Exception ex){
                    AppLog.log(ex.toString());
                }
                if(ConstanArea.WHOLEJAPAN.equalsIgnoreCase(area)){
                    return sortBSJ(datas);
                }else{
                    return sortBSJArea(datas);
                }
            }
        };
    }

    public static List<CatagoryDTO> getCategorys(final MyDatabaseHelper mMyDatabaseHelper, final String area) {
        String now= Utils.valueNowTime();
        List<CatagoryDTO> datas= new ArrayList<>();
        String selectQuery = "SELECT "+TableCoupon.COLUMN_CATEGORY_ID+", "+TableCoupon.COLUMN_CATEGORY+
                " FROM "+TableCoupon.TABLE_COUPON+"  where "+TableCoupon.COLUMN_AREA+" like '%"+area+
                "%' AND "+ TableCoupon.COLUMN_EXPIRATION_FROM+" < "+ now + " AND "+
                TableCoupon.COLUMN_EXPIRATION_TO +" > "+now +" group by "+TableCoupon.COLUMN_CATEGORY_ID+" having count("+TableCoupon.COLUMN_SHGRID+") > 0";
        AppLog.log("Category: "+selectQuery);
        SQLiteDatabase db = mMyDatabaseHelper.getSqLiteDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try{
            if (cursor!=null  && !cursor.isClosed() && cursor.getCount()>0 && cursor.moveToFirst()) {
                do {
                    CatagoryDTO catagoryDTO = new CatagoryDTO();
                    catagoryDTO.setCatagoryID(cursor.getString(0));
                    catagoryDTO.setGetCatagoryName(cursor.getString(1));
                    datas.add(catagoryDTO);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }catch (Exception ex){
            AppLog.log(ex.toString());
        }
        if(ConstanArea.WHOLEJAPAN.equalsIgnoreCase(area)){
            return sortBSJ(datas);
        }else{
            return sortBSJArea(datas);
        }
    }

    private static List<CatagoryDTO> sortBSJ(List<CatagoryDTO> list){
        List<CatagoryDTO> datas= new ArrayList<>();
        List<String> temp = java.util.Arrays.asList(Constant.listCategoryName);
        for(String item : temp){
            for(int i = 0; i < list.size(); i++){
                if(item.equalsIgnoreCase(list.get(i).getGetCatagoryName())){
                    datas.add(list.get(i));
                    list.remove(i);
                    break;
                }
            }
        }

        //add item orther
        for(int i = 0; i < list.size(); i++){
            datas.add(list.get(i));
        }
        return datas;
    }
    private static List<CatagoryDTO> sortBSJArea(List<CatagoryDTO> list){
        List<CatagoryDTO> datas= new ArrayList<>();
        List<String> temp = java.util.Arrays.asList(Constant.listCategoryNameArea);
        for(String item : temp){
            for(int i = 0; i < list.size(); i++){
                if(item.equalsIgnoreCase(list.get(i).getGetCatagoryName())){
                    datas.add(list.get(i));
                    list.remove(i);
                    break;
                }
            }
        }

        //add item orther
        for(int i = 0; i < list.size(); i++){
            datas.add(list.get(i));
        }
        return datas;
    }
}
