package com.emobx.news.Utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DownloadFile {
    public static File outputFile;
    private String[] newsId;
    private String[] urls;

    public DownloadFile(ArrayList<String> id, ArrayList<String> url) {
        newsId = new String[id.size()];
        newsId = id.toArray(newsId);

        urls = new String[url.size()];
        urls = url.toArray(urls);

        new DownloadFileFromURL().execute(urls);
    }


    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showDialog(progress_bar_type);
//            Utils.showProgressDialog(activity);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                for (int i = 0; i < f_url.length; i++) {
                    File dir = new File(Constants.image_dir_path);

                    if (!dir.exists()) {
                        // Make it, if it doesn't exit
                        boolean success = dir.mkdirs();
                        if (!success) {
                            dir = new File(Constants.image_dir_path);

                        }
                    }

                    if (dir.exists()) {
                        outputFile = new File(dir + "/" + newsId[i] + "_" + (Utils.getFileName(f_url[i])));
                        if (!outputFile.exists()) {

                            URL url = new URL(f_url[i]);
                            URLConnection connection = url.openConnection();
                            connection.connect();

                            // this will be useful so that you can show a tipical 0-100%
                            // progress bar
                            int lenghtOfFile = connection.getContentLength();

                            // download the file
                            InputStream input = new BufferedInputStream(url.openStream(),
                                    8192);

                            // Output stream

                            OutputStream output = new FileOutputStream(outputFile);

                            byte data[] = new byte[1024];

                            long total = 0;

                            while ((count = input.read(data)) != -1) {
                                total += count;
                                // publishing the progress....
                                // After this onProgressUpdate will be called
                                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                                // writing data to file
                                output.write(data, 0, count);
                            }

                            // flushing output
                            output.flush();

                            // closing streams
                            output.close();
                            input.close();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

//            if (outputFile != null)
//                if (outputFile.exists())
//                    return outputFile.getAbsolutePath();
//                else
//                    return null;
//            else
            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
//            Utils.hideProgressDialog();

        }

    }

}
