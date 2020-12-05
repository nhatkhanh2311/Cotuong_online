package com.example.cotuong_online;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class PieceAdapter extends BaseAdapter
{
    private Context context;
    private int piece;
    private List<Piece> listpiece;

    public PieceAdapter(Context context, int piece, List<Piece> listpiece)
    {
        this.context = context;
        this.piece = piece;
        this.listpiece = listpiece;
    }

    @Override
    public int getCount()
    {
        return listpiece.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(piece, null);
        ImageView image = convertView.findViewById(R.id.piece);
        Piece piececlass = listpiece.get(position);
        image.setImageResource(piececlass.getImage());
        return convertView;
    }
}
