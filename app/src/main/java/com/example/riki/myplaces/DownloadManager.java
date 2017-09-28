package com.example.riki.myplaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadManager {
    private static DownloadManager instance = new DownloadManager();
    private OkHttpClient client = new OkHttpClient();
    private IThreadWakeUp wakeUp;

    private DownloadManager()
    {

    }

    public static DownloadManager getInstance()
    {
        return instance;
    }

    public void getUser(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getAnyUser(final String apiToken, final String id)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user/" + id)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void register(final String name, final String email, final String password)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("name", name)
                            .add("email", email)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/register")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void login(final String email, final String password)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/login")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    public void update(final String firstname, final String lastname,final String phone, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("first_name", firstname)
                            .add("last_name", lastname)
                            .add("phone_number",phone)
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/update")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


   /* public void upload1(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("other_field", "other_field_value")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        Response response = this.client.newCall(request).execute();
    }*/


    public void newUpdate(final String firstname, final String lastname, final String phone, final File photo, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("first_name", firstname)
                            .addFormDataPart("last_name", lastname)
                            .addFormDataPart("phone_number",phone)
                            .addFormDataPart("avatar", photo.getName(),
                                    RequestBody.create(MediaType.parse("image/png"), photo))
                            //.addFormDataPart("avatar", photo.getName(),
                           // RequestBody.create(MediaType.parse("text/plain"), photo))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/update")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    public void updatePass(final String opass, final String npass, final String email, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("old_password", opass)
                            .add("new_password",npass)
                            .add("email",email )
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/update")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getFriends(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user/friends")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addFriend(final int friendId, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("friend_id", String.valueOf(friendId))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user/friends")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addLocation(final double latitude, final double longitude, final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("latitude", String.valueOf(latitude))
                            .add("longitude", String.valueOf(longitude))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/location")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addPoints(final String apiToken, final int points)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("points", String.valueOf(points))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user/points/add")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void subtractPoints(final String apiToken, final int points)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    RequestBody formBody = new FormBody.Builder()
                            .add("points", String.valueOf(points))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user/points/subtract")
                            .post(formBody)
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getUserWifis(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/user/wifi")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getAllWifis(final String apiToken)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://wi-finder-server.herokuapp.com/api/wifi")
                            .addHeader("api", apiToken)
                            .build();

                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    wakeUp.ResponseOk(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void setThreadWakeUp(IThreadWakeUp i)
    {
        wakeUp = i;
    }
}
