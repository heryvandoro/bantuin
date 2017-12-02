package net.slc.hoga.bantuin.Helper;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import net.slc.hoga.bantuin.R;

public class ImageRound {
    public static Transformation get(Context ctx) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .borderWidthDp(5).scaleType(ImageView.ScaleType.FIT_XY)
                .cornerRadiusDp(50)
                .oval(false)
                .build();
    }

    public static Transformation get(Context ctx, int temp) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(temp))
                .borderWidthDp(5).scaleType(ImageView.ScaleType.FIT_XY)
                .cornerRadiusDp(50)
                .oval(false)
                .build();
    }

    public static Transformation get(Context ctx, int border, int radius) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .borderWidthDp(border).scaleType(ImageView.ScaleType.FIT_XY)
                .cornerRadiusDp(radius)
                .oval(false)
                .build();
    }

    public static Transformation get(Context ctx, boolean noBorder) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .cornerRadiusDp(50).scaleType(ImageView.ScaleType.FIT_XY)
                .oval(false)
                .build();
    }
}
