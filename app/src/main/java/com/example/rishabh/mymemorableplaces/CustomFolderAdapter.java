package com.example.rishabh.mymemorableplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomFolderAdapter extends BaseAdapter implements Filterable{

    private LayoutInflater inflater;
    private Context context;
    ArrayList<Folder> folderList;
    ArrayList<Folder> filterList;
    CustomFilter filter;

    public CustomFolderAdapter(Context context,ArrayList<Folder> folderList){
        this.folderList = folderList;
        this.context = context;
        this.filterList = folderList;
    }

    @Override
    public int getCount() {
        return folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.custom_folder_layout,null);

        TextView folderName = convertView.findViewById(R.id.folderName);
        ImageView folderImage = convertView.findViewById(R.id.folderImage);

        folderName.setText(folderList.get(position).getFolderName().toString());

        Bitmap bmp = BitmapFactory.decodeByteArray(folderList.get(position).getImage(), 0, folderList.get(position).getImage().length);
        folderImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,200, false));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0 ){
                constraint = constraint.toString().toUpperCase();
                ArrayList<Folder> filters = new ArrayList<Folder>();

                for(int i = 0 ; i < filterList.size(); i++){
                    if(filterList.get(i).getFolderName().toUpperCase().contains(constraint)){
                        Folder f = new Folder(filterList.get(i).getImage(),filterList.get(i).getFolderName());
                        filters.add(f);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }
            else{
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            folderList = (ArrayList<Folder>) results.values;
            notifyDataSetChanged();
        }
    }
}
