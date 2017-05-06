package br.com.rlmg.jokenpo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.rlmg.jokenpo.R;

/**
 * Created by Tiago on 05/05/2017.
 */

public class ImagePicker {
    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
        addIntentsToList(context, intentList, pickIntent);
        addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), context.getString(R.string.select_Image));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static void addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), "tempImage");
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    public static Bitmap getImageFromResult(Context context, int resultCode,
                                            Intent imageReturnedIntent) {
        Bitmap bitmap = null;
        File imageFile = getTempFile(context);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null  ||
                    imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {
                selectedImage = Uri.fromFile(imageFile);
            } else {
                selectedImage = imageReturnedIntent.getData();
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
            } catch (IOException e) {}

        }

        return bitmap;
    }
}