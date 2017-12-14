package edu.bluejack17_1.bantuin.Helper;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import edu.bluejack17_1.bantuin.R;

public class ImageRound {
    public static Transformation get(Context ctx, float radius, boolean withBorder) {
        RoundedTransformationBuilder temp = new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .cornerRadiusDp(radius)
                .oval(false);

        if (withBorder) {
            temp.borderWidthDp((float) 2);
            temp.borderColor(ctx.getResources().getColor(R.color.primaryDarkColor));
        }
        return temp.build();
    }
}
