package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Folder;

/**
 * 文件夹Adapter
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class FolderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Folder> mFolders = new ArrayList<>();

    int mImageSize;

    int lastSelected = 0;

    public FolderAdapter(Context context){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_cover_size);
    }

    /**
     * 设置数据集
     * @param folders
     */
    public void setData(List<Folder> folders) {
        if(folders != null && folders.size()>0){
            mFolders = folders;
        }else{
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size()+1;
    }

    @Override
    public Folder getItem(int i) {
        if(i == 0) return null;
        return mFolders.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if(i == 0){
                holder.name.setText(R.string.folder_all);
                holder.path.setText("/sdcard");
                holder.size.setText(String.format("%d%s",
                        getTotalImageSize(), mContext.getResources().getString(R.string.photo_unit)));
                if(mFolders.size()>0){
                    Folder f = mFolders.get(0);
                    int width = mContext.getResources().getDimensionPixelSize(R.dimen.folder_cover_size);
                    int height = mContext.getResources().getDimensionPixelSize(R.dimen.folder_cover_size);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(f.cover.path))
                            .setResizeOptions(new ResizeOptions(width, height))
                            .build();
                    AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setOldController(holder.cover.getController())
                            .setImageRequest(request)
                            .build();
                    holder.cover.setController(controller);
                    holder.cover.setImageURI(Uri.fromFile(new File(f.cover.path)));
                }
            }else {
                holder.bindData(getItem(i));
            }
            if(lastSelected == i){
                holder.indicator.setVisibility(View.VISIBLE);
            }else{
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize(){
        int result = 0;
        if(mFolders != null && mFolders.size()>0){
            for (Folder f: mFolders){
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if(lastSelected == i) return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex(){
        return lastSelected;
    }

    class ViewHolder{
        SimpleDraweeView cover;
        TextView name;
        TextView path;
        TextView size;
        ImageView indicator;
        ViewHolder(View view){
            cover = (SimpleDraweeView)view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            path = (TextView) view.findViewById(R.id.path);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        void bindData(Folder data) {
            if(data == null){
                return;
            }
            name.setText(data.name);
            path.setText(data.path);
            if (data.images != null) {
                size.setText(String.format("%d%s", data.images.size(), mContext.getResources().getString(R.string.photo_unit)));
            }else{
                size.setText("*"+mContext.getResources().getString(R.string.photo_unit));
            }
            if (data.cover != null){
                // 显示图片
                cover.setImageURI(Uri.fromFile(new File(data.cover.path)));
            }else{
                cover.setImageResource(R.drawable.default_error);
            }

        }
    }

}
