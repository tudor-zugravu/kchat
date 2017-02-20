package com.example.user.kchat01;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static com.example.user.kchat01.R.id.bubble;
import static com.example.user.kchat01.R.id.imageProfile;

/**
 * Created by user on 19/02/2017.
 */

public class ArrayAdapterChat extends BaseAdapter {


    private final List<ListItemChat> chatMessages;
    private Activity context;

    public ArrayAdapterChat(Activity context, List<ListItemChat> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ListItemChat getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ListItemChat chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.list_item_chat, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = chatMessage.getIsme() ;//Just a dummy check to simulate whether it me or other sender
        setAlignment(holder, myMsg);
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtInfo.setText(chatMessage.getDate());


        return convertView;
    }

    public void add(ListItemChat message) {
        chatMessages.add(message);
    }

    public void add(List<ListItemChat> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (isMe) {
            holder.bubble.setBackgroundResource(R.drawable.bubble2);

            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) holder.imageProfile.getLayoutParams();
            rlp.addRule(RelativeLayout.RIGHT_OF, R.id.bubble);
            holder.imageProfile.setLayoutParams(rlp);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.bubble.setBackgroundResource(R.drawable.bubble1);

            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) holder.bubble.getLayoutParams();
            rlp.addRule(RelativeLayout.RIGHT_OF, R.id.imageProfile);
            holder.bubble.setLayoutParams(rlp);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.leftMargin = 30;
            holder.txtMessage.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.textViewMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.bubble = (LinearLayout) v.findViewById(bubble);
        holder.txtInfo = (TextView) v.findViewById(R.id.textViewTime);
        holder.imageProfile = (ImageView)v.findViewById(imageProfile);
        return holder;
    }


    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout bubble;
        public ImageView imageProfile;
    }

}
