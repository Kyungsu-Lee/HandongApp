package ghost.android.ghosthguapp.hgutube;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import ghost.android.ghosthguapp.hgutube.ImageDownloader.DownloadedDrawable;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>
{
    public String url;
    public String targetUrl;
    private WeakReference<ImageView> imageViewReference;


    public ImageDownloaderTask(String url, ImageView imageView)
    {
        this.targetUrl = url;
        this.imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected Bitmap doInBackground(String... params)
    {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        if(isCancelled())
        {
            bitmap = null;
        }

        if(imageViewReference != null)
        {
            ImageView imageView = imageViewReference.get();
            ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

            if(this == bitmapDownloaderTask)
            {
                ImageDownloader.mImageCache.put(targetUrl, bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView)
    {
        if(imageView != null)
        {
            Drawable drawable = imageView.getDrawable();
            if(drawable instanceof DownloadedDrawable)
            {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    private Bitmap downloadBitmap(String url)
    {
        try {
            InputStream inputStream = null;

            URL newUrl = new URL(url);
            inputStream = newUrl.openConnection().getInputStream();


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;

            final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);

            inputStream.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    static class FlushedInputStream extends FilterInputStream
    {
        public FlushedInputStream(InputStream inputStream)
        {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException
        {
            long totalBytesSkipped = 0L;
            while(totalBytesSkipped < n)
            {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if(bytesSkipped == 0L)
                {
                    int bytes = read();
                    if(bytes < 0)
                    {
                        break;
                    }
                    else
                    {
                        bytesSkipped = 1;
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
