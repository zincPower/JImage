package com.zinc.libimage.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.zinc.libimage.R;
import com.zinc.libimage.model.LocalMedia;
import com.zinc.libimage.model.LocalMediaFolder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static com.zinc.libimage.config.JImageConfig.TYPE_IMAGE;
import static com.zinc.libimage.config.JImageConfig.TYPE_VIDEO;

/**
 * @author Jiang zinc
 * @date 创建时间 2017/12/5
 * @Description 获取手机的多媒体资源
 */
public class LocalMediaLoader {

    //图片获取的数据列
    private final static String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    //影像获取的数据列
    private final static String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DURATION};

    private int type = TYPE_IMAGE;
    private FragmentActivity activity;

    public LocalMediaLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    //存储文件路径
    HashSet<String> mDirPaths = new HashSet<String>();

    //获取全多媒体
    public void loadAllMedia(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager()
                .initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {

                    //这个方法在初始化Loader时回调，我们要在这个方法中实例化CursorLoader
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                        CursorLoader cursorLoader = null;

                        if (id == TYPE_IMAGE) {     //图像类型

                            cursorLoader = new CursorLoader(
                                    activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                    + MediaStore.Images.Media.MIME_TYPE + "=?",
                                    new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");

                        } else if (id == TYPE_VIDEO) {  //影像类型

                            cursorLoader = new CursorLoader(
                                    activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC");

                        }

                        return cursorLoader;
                    }

                    //加载数据完成后回调到这个方法，进行回调刷新
                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                        //图像文件
                        ArrayList<LocalMediaFolder> imageFolders = new ArrayList<LocalMediaFolder>();
                        //全部的图片文件
                        LocalMediaFolder allImageFolder = new LocalMediaFolder();
                        //全部图片
                        List<LocalMedia> allImages = new ArrayList<LocalMedia>();

                        //遍历获取的资源
                        while (data != null && data.moveToNext()) {

                            //获取资源文件的路径
                            String path = data.getString(data.getColumnIndex(MediaStore.Images.Media.DATA));

                            //检查文件是否存在
                            File file = new File(path);
                            if (!file.exists()){
                                continue;
                            }

                            //获取该文件所在文件夹路径名,确定是否存在
                            File parentFile = file.getParentFile();
                            if (parentFile == null || !parentFile.exists()){
                                continue;
                            }

                            //获取所在文件夹绝对路径
                            String dirPath = parentFile.getAbsolutePath();

                            //利用一个HashSet防止多次扫描同一个文件夹
                            if (mDirPaths.contains(dirPath)) {
                                continue;
                            } else {
                                mDirPaths.add(dirPath);
                            }

                            if (parentFile.list() == null){
                                continue;
                            }

                            //获取path对应的文件夹，为LocalMediaFolder格式
                            LocalMediaFolder localMediaFolder = getImageFolder(path, imageFolders);

                            //过滤文件
                            File[] files = parentFile.listFiles(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String filename) {
                                    if (filename.endsWith(".jpg")
                                            || filename.endsWith(".png")
                                            || filename.endsWith(".jpeg"))
                                        return true;
                                    return false;
                                }
                            });

                            //循环获取的文件夹下的文件，进行格式转换
                            ArrayList<LocalMedia> images = new ArrayList<>();
                            for (int i = 0; i < files.length; i++) {
                                File f = files[i];
                                LocalMedia localMedia = new LocalMedia(f.getAbsolutePath());
                                allImages.add(localMedia);
                                images.add(localMedia);
                            }

                            if (images.size() > 0) {
                                localMediaFolder.setImages(images);
                                localMediaFolder.setImageNum(localMediaFolder.getImages().size());
                                imageFolders.add(localMediaFolder);
                            }
                        }

                        //添加所有的图片到"所有图片"夹
                        allImageFolder.setImages(allImages);
                        allImageFolder.setImageNum(allImageFolder.getImages().size());
                        allImageFolder.setFirstImagePath(allImages.get(0).getPath());
                        allImageFolder.setName(activity.getString(R.string.jimage_all_image));
                        imageFolders.add(allImageFolder);
                        sortFolder(imageFolders);

                        imageLoadListener.loadComplete(imageFolders);

                        if (data != null) data.close();

                    }

                    //这个方法是在重启Loader时调用，一般可以不管
                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                    }
                });
    }

    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<LocalMediaFolder>() {
            @Override
            public int compare(LocalMediaFolder lhs, LocalMediaFolder rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    /**
     *
     * @date 创建时间 2017/12/5
     * @author Jiang zinc
     * @Description 获取图片所在的文件夹
     * @version 1.0
     *
     */
    private LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        //查看是否已存在于文件夹list
        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }

        //若不在文件夹list中，进行创建
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);

        return newFolder;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<LocalMediaFolder> folders);
    }

}
