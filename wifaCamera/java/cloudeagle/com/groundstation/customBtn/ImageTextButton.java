package cloudeagle.com.groundstation.customBtn;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cloudeagle.com.groundstation.R;

/**
 * Created by albert on 2015/9/13.
 * 自定义按钮：包含一张图片和一行文字作为按钮的显示
 */
public class ImageTextButton extends RelativeLayout {
    private ImageView imageView;
    private TextView textView;

    public ImageTextButton(Context context) {
        super(context, null);
    }

    public ImageTextButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        LayoutInflater.from(context).inflate(R.layout.img_text_btn, this, true);

        this.imageView = (ImageView)findViewById(R.id.imgView);
        this.textView = (TextView)findViewById(R.id.textView);

        this.setClickable(true);
        this.setFocusable(true);
    }

    public void setImageResource(int resourceID) {
        this.imageView.setImageResource(resourceID);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextSize(float size) {
        this.textView.setTextSize(size);
    }

    public void initialCustomSetting(int btnImgResourceID, String btnText, float btnTextSize) {
        setImageResource(btnImgResourceID);
        setText(btnText);
        setTextSize(btnTextSize);
        setTextColor(Color.rgb(0, 0, 0));
    }

    public void initialCustomSetting(int btnImgResourceID, String btnText, float btnTextSize, int btnTextColor) {
        initialCustomSetting(btnImgResourceID, btnText, btnTextSize);
        setTextColor(btnTextColor);
    }
}
