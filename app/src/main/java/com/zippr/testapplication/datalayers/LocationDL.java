package com.zippr.testapplication.datalayers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.arpaul.utilitieslib.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zippr.testapplication.dataaccess.CPConstants;
import com.zippr.testapplication.models.SelLocDO;

import java.util.ArrayList;
import java.util.List;

import static com.zippr.testapplication.dataaccess.CPConstants.CU_SELLOC;
import static com.zippr.testapplication.dataaccess.CPConstants.T_QUES;


/**
 * Created by aritrapal on 31/07/17.
 */

public class LocationDL {

    public boolean insertLocation(Context context, SelLocDO objPostDO) {

        boolean isInserted = false;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CPConstants.T_SelLoc.LOC_ID, objPostDO.locId);
            contentValues.put(CPConstants.T_SelLoc.NAME, objPostDO.name);
            contentValues.put(CPConstants.T_SelLoc.PARCELCOUNT, objPostDO.parcelCount);
            contentValues.put(CPConstants.T_SelLoc.LOCLAT, objPostDO.locLat);
            contentValues.put(CPConstants.T_SelLoc.LOCLNG, objPostDO.locLng);

//            int tryUpdate = context.getContentResolver().update(CU_SELLOC,
//                    contentValues,
//                    CPConstants.T_SelLoc.LOC_ID + T_QUES,
//                    new String[]{objPostDO.locId});
//
//            if (tryUpdate <= 0){
                Uri uri = context.getContentResolver().insert(CU_SELLOC, contentValues);
                if(uri != null)
                    isInserted = true;
//            } else {
//                isInserted = true;
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return isInserted;
        }
    }

//    public boolean insertAllPost(Context context, List<PostDO> posts) {
//
//        boolean isInserted = false;
//        try {
//            ContentValues[] contentValues = new ContentValues[posts.size()];
//            for (int i = 0; i < posts.size(); i++) {
//                contentValues[i] = new ContentValues();
//                PostDO objPostDO = posts.get(i);
//
//                contentValues[i].put(CPConstants.T_SelLoc.POST_ID, objPostDO.getPostId());
//                contentValues[i].put(CPConstants.T_SelLoc.PROFILE_IMAGE_URL, objPostDO.getNeighbourhood_id());
//                contentValues[i].put(CPConstants.T_SelLoc.ZIPPR, objPostDO.getZippr());
//                contentValues[i].put(CPConstants.T_SelLoc.DISPLAY_NAME, objPostDO.getDisplayName());
//                contentValues[i].put(CPConstants.T_SelLoc.TAGS, new Gson().toJson(objPostDO.getTags(), new TypeToken<ArrayList<PostTagDO>>() {
//                }.getType()));
//                contentValues[i].put(CPConstants.T_SelLoc.POST, objPostDO.getPost());
//                contentValues[i].put(CPConstants.T_SelLoc.IMAGE_URLS, new Gson().toJson(objPostDO.getImageUrls(), new TypeToken<ArrayList<String>>() {
//                }.getType()));
//                contentValues[i].put(CPConstants.T_SelLoc.NO_OF_COMMENTS, objPostDO.getNoOfComments());
//                contentValues[i].put(CPConstants.T_SelLoc.NO_OF_VOTES, objPostDO.getNoOfUpvotes());
//                contentValues[i].put(CPConstants.T_SelLoc.USER_VOTES, objPostDO.getUserVote());
//                contentValues[i].put(CPConstants.T_SelLoc.STATE, objPostDO.getState());
//                contentValues[i].put(CPConstants.T_SelLoc.CREATED_BY, objPostDO.getCreatedBy());
//
//                String createdAt = getProperTime(objPostDO.getCreatedAt());
//                String updatedAt = getProperTime(objPostDO.getUpdatedAt());
//                contentValues[i].put(CPConstants.T_SelLoc.CREATED_AT, createdAt);
//                contentValues[i].put(CPConstants.T_SelLoc.UPDATED_AT, updatedAt);
//
//                contentValues[i].put(CPConstants.T_SelLoc.POST_STATUS, objPostDO.getCity());
//                contentValues[i].put(CPConstants.T_SelLoc.CURRENT_USER_CAN_UPVOTE, objPostDO.isIs_public());
//            }
//
//            int insertSize = context.getContentResolver().bulkInsert(CU_POSTS, contentValues);
//            if(insertSize > 0)
//                isInserted = true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            return isInserted;
//        }
//    }

    public boolean insertLocations(Context context, List<SelLocDO> posts) {

        boolean isInserted = false;
        int insertSize = 0;
        try {
            ContentValues[] contentValues = new ContentValues[posts.size()];
            for (int i = 0; i < posts.size(); i++) {
                contentValues[i] = new ContentValues();
                SelLocDO objPostDO = posts.get(i);

                boolean inserted = insertLocation(context, objPostDO);
                if(inserted)
                    insertSize++;
            }

            if(insertSize > 0)
                isInserted = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return isInserted;
        }
    }

    public List<SelLocDO> fetchAllLocs(Context context, Bundle bundle) {
        List<SelLocDO> listPostDO = null;
        SelLocDO objSelLocDO = null;
        Cursor cursor = null;
        try {
            String whereCondi = "";
//            if(bundle != null && bundle.containsKey(BUNDLE_FILTERED_POST))
//                whereCondi = bundle.getString(BUNDLE_FILTERED_POST);

            cursor = context.getContentResolver().query(CU_SELLOC, null, null, null, null);
            if(cursor != null && cursor.moveToFirst()) {
                listPostDO = new ArrayList<>();
                do {
                    String locId = cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.LOC_ID));
                    String name = cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.NAME));
                    int parcelcount = StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.PARCELCOUNT)));
                    double locLat = StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.LOCLAT)));
                    double locLng = StringUtils.getDouble(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.LOCLNG)));

                    objSelLocDO = new SelLocDO(locId, name, parcelcount, locLat, locLng);

                    listPostDO.add(objSelLocDO);
                } while(cursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
            return listPostDO;
        }
    }

//    public SelLocDO fetchLoc(Context context, Bundle bundle) {
//        SelLocDO objPostDO = null;
//        Cursor cursor = null;
//        try {
//            String postId = (String) bundle.getString(REQUEST_SelLoc_ID);
//            cursor = context.getContentResolver().query(CU_POSTS, null, CPConstants.T_SelLoc.POST_ID + T_QUES, new String[]{postId}, null);
//            if(cursor != null && cursor.moveToFirst()) {
//                objPostDO = new SelLocDO();
//                objPostDO.setPostId(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.POST_ID)));
//                objPostDO.setNeighbourhood_id(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.PROFILE_IMAGE_URL)));
//                objPostDO.setZippr(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.ZIPPR)));
//                objPostDO.setDisplayName(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.DISPLAY_NAME)));
//                objPostDO.setPost(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.POST)));
//                objPostDO.setNoOfComments(StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.NO_OF_COMMENTS))));
//                objPostDO.setNoOfUpvotes(StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.NO_OF_VOTES))));
//                objPostDO.setUserVote(StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.USER_VOTES))));
//                objPostDO.setState(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.STATE)));
//                objPostDO.setCreatedBy(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.CREATED_BY)));
//                objPostDO.setCreatedAt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.CREATED_AT)));
//                objPostDO.setUpdatedAt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.UPDATED_AT)));
//                objPostDO.setCity(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.POST_STATUS)));
//                objPostDO.setIs_public(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.CURRENT_USER_CAN_UPVOTE)).equalsIgnoreCase("true")? true : false);
//
//                ArrayList<String> tags = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.TAGS)),
//                        new TypeToken<ArrayList<String>>() {
//                        }.getType());
//                objPostDO.setTags(tags);
//
//                ArrayList<PostTagDO> posttags = new ArrayList<>();
//                for(String tag: tags) {
//                    PostTagDO objPOstTag = new PostTagDO(tag, "#000000");
//                    posttags.add(objPOstTag);
//                }
//
//                objPostDO.setPostTags(posttags);
//
//                ArrayList<String> imageUrls = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.IMAGE_URLS)),
//                        new TypeToken<ArrayList<String>>() {
//                        }.getType());
//                objPostDO.setImageUrls(imageUrls);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if(cursor != null && !cursor.isClosed())
//                cursor.close();
//            return objPostDO;
//        }
//    }

//    public List<SelLocDO> fetchLocs(Context context, Bundle bundle) {
//        List<SelLocDO> listPostDO = null;
//        SelLocDO objPostDO = null;
//        Cursor cursor = null;
//        try {
//            String zippr = (String) bundle.getString(ZIPPR);
//            cursor = context.getContentResolver().query(CU_POSTS, null, CPConstants.T_SelLoc.ZIPPR + T_QUES, new String[]{zippr}, sqlDateDesc(CPConstants.T_SelLoc.UPDATED_AT));
//            if(cursor != null && cursor.moveToFirst()) {
//                listPostDO = new ArrayList<>();
//                do {
//                    objPostDO = new SelLocDO();
//                    objPostDO.setPostId(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.POST_ID)));
//                    objPostDO.setNeighbourhood_id(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.PROFILE_IMAGE_URL)));
//                    objPostDO.setZippr(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.ZIPPR)));
//                    objPostDO.setDisplayName(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.DISPLAY_NAME)));
//                    objPostDO.setPost(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.POST)));
//                    objPostDO.setNoOfComments(StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.NO_OF_COMMENTS))));
//                    objPostDO.setNoOfUpvotes(StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.NO_OF_VOTES))));
//                    objPostDO.setUserVote(StringUtils.getInt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.USER_VOTES))));
//                    objPostDO.setState(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.STATE)));
//                    objPostDO.setCreatedBy(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.CREATED_BY)));
//                    objPostDO.setCreatedAt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.CREATED_AT)));
//                    objPostDO.setUpdatedAt(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.UPDATED_AT)));
//                    objPostDO.setCity(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.POST_STATUS)));
//                    objPostDO.setIs_public(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.CURRENT_USER_CAN_UPVOTE)).equalsIgnoreCase("true")? true : false);
//
//                    ArrayList<String> tags = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.TAGS)),
//                            new TypeToken<ArrayList<String>>() {
//                            }.getType());
//                    objPostDO.setTags(tags);
//
//                    ArrayList<PostTagDO> posttags = new ArrayList<>();
//                    for(String tag: tags) {
//                        PostTagDO objPOstTag = new PostTagDO(tag, "#000000");
//                        posttags.add(objPOstTag);
//                    }
//
//                    objPostDO.setPostTags(posttags);
//
//                    ArrayList<String> imageUrls = new Gson().fromJson(cursor.getString(cursor.getColumnIndex(CPConstants.T_SelLoc.IMAGE_URLS)),
//                            new TypeToken<ArrayList<String>>() {
//                            }.getType());
//                    objPostDO.setImageUrls(imageUrls);
//
//                    listPostDO.add(objPostDO);
//                } while(cursor.moveToNext());
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if(cursor != null && !cursor.isClosed())
//                cursor.close();
//            return listPostDO;
//        }
//    }

    public int fetchLocsCount(Context context) {
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = context.getContentResolver().query(CU_SELLOC, null, null, null, null);
            if(cursor != null && cursor.moveToFirst()) {
                count = cursor.getCount();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
            return count;
        }
    }

    public boolean deleteLoc(Context context, String post_id) {

        boolean isDeleted = false;
        try {
            int tryUpdate = context.getContentResolver().delete(CU_SELLOC,
                    CPConstants.T_SelLoc.LOC_ID + T_QUES,
                    new String[]{post_id});

            if (tryUpdate <= 0){
                isDeleted = true;
            } else {
                isDeleted = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return isDeleted;
        }
    }

    public boolean deleteAllPosts(Context context) {

        boolean isDeleted = false;
        try {
            int tryUpdate = context.getContentResolver().delete(CU_SELLOC, null, null);

            if (tryUpdate <= 0){
                isDeleted = true;
            } else {
                isDeleted = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return isDeleted;
        }
    }
}
