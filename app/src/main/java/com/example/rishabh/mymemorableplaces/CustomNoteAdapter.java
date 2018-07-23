package com.example.rishabh.mymemorableplaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomNoteAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    ArrayList<Note> noteList;
    ArrayList<Note> filterList;
    CustomFolderAdapter.CustomFilter filter;

    public CustomNoteAdapter(Context context,int layout, ArrayList<Note> noteList){
        this.noteList = noteList;
        this.context = context;
        this.filterList = noteList;
        this.layout = layout;
    }

    private class ViewHolder{
        ImageView imageNoteView;
        TextView noteTitle;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
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

            holder.noteTitle = row.findViewById(R.id.noteTitle);
            holder.imageNoteView = row.findViewById(R.id.ImageNoteView);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        Note f = noteList.get(position);

        holder.noteTitle.setText(f.getTitle());

        Bitmap bmp = BitmapFactory.decodeByteArray(f.getData(), 0, f.getData().length);
        holder.imageNoteView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,200, false));

        return row;
    }

    @Override
    public Filter getFilter() {
      /*  if(filter == null){
            filter = new CustomFolderAdapter.CustomFilter();
        }
        return filter;*/
      return null;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

          /*  FilterResults results = new FilterResults();
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
            }*/
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          /*  folderList = (ArrayList<Folder>) results.values;
            notifyDataSetChanged();*/
        }
    }
}
