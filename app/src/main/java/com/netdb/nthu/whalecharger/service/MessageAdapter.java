package com.netdb.nthu.whalecharger.service;


        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.List;

        import com.netdb.nthu.whalecharger.R;
        import com.netdb.nthu.whalecharger.model.Message;

/**
 * Created by user on 2015/1/16.
 */
public class MessageAdapter extends BaseAdapter {

    private List<Message> mMessageList;
    private LayoutInflater mMyInflater;

    public MessageAdapter(Context c, List<Message> list) {
        this.mMessageList = list;
        mMyInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setList(List<Message> list) {
        this.mMessageList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mMyInflater.inflate(R.layout.message_item, null);
        Message message = mMessageList.get(position);

        TextView itemTxt = (TextView) convertView.findViewById(R.id.txt_item);
        TextView priceTxt = (TextView) convertView.findViewById(R.id.txt_price);
        TextView dateTxt = (TextView) convertView.findViewById(R.id.txt_date);
        ImageView categoryImg = (ImageView)convertView.findViewById(R.id.img_category);

        itemTxt.setText(message.getItem());
        priceTxt.setText(message.getPrice().toString());
        dateTxt.setText(String.valueOf(message.getDay())+"/"+String.valueOf(message.getMonth())+"/"+String.valueOf(message.getYear()));
        switch(message.getCategory()){
            case 0:
                categoryImg.setImageResource(R.drawable.food);
                break;
            case 1:
                categoryImg.setImageResource(R.drawable.cloth);
                break;
            case 2:
                categoryImg.setImageResource(R.drawable.tramsportation);
                break;
            case 3:
                categoryImg.setImageResource(R.drawable.others);
        }

        return convertView;
    }

}
