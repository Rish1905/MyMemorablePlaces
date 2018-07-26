package com.developer.rishabh.mymemorableplaces;

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
    private  int layout;
    ArrayList<Folder> folderList;
    ArrayList<Folder> filterList;
    CustomFilter filter;

    public CustomFolderAdapter(Context context,int layout, ArrayList<Folder> folderList){
        this.folderList = folderList;
        this.context = context;
        this.filterList = folderList;
        this.layout = layout;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView folderName;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        View row = convertView;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(row == null) {
            row = inflater.inflate(layout, null);

            holder.folderName = row.findViewById(R.id.folderName);
            holder.imageView = row.findViewById(R.id.folderImage);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        Folder f = folderList.get(position);

        holder.folderName.setText(f.getFolderName());

        Bitmap bmp = BitmapFactory.decodeByteArray(f.getImage(), 0, f.getImage().length);
        holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,200, false));

        return row;
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
