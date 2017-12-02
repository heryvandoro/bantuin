package net.slc.hoga.bantuin.Helper;

import android.content.Context;
import android.graphics.Color;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import net.slc.hoga.bantuin.R;

public class ImageRound {
    public static Transformation get(Context ctx) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .borderWidthDp(5)
                .cornerRadiusDp(50)
                .oval(false)
                .build();
    }

    public static Transformation get(Context ctx, int temp) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(temp))
                .borderWidthDp(5)
                .cornerRadiusDp(50)
                .oval(false)
                .build();
    }

    public static Transformation get(Context ctx, int border, int radius) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .borderWidthDp(border)
                .cornerRadiusDp(radius)
                .oval(false)
                .build();
    }

    public static Transformation get(Context ctx, boolean noBorder) {
        return new RoundedTransformationBuilder()
                .borderColor(ctx.getResources().getColor(R.color.primaryColor))
                .cornerRadiusDp(50)
                .oval(false)
                .build();
    }
}
