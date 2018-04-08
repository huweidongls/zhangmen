package com.zan99.guaizhangmen.Activity.Men;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.squareup.picasso.Picasso;
import com.zan99.guaizhangmen.BaseActivity;
import com.zan99.guaizhangmen.Model.MenModel;
import com.zan99.guaizhangmen.R;
import com.zan99.guaizhangmen.Util.CircleTransform;
import com.zan99.guaizhangmen.Util.DownloadUtil;
import com.zan99.guaizhangmen.Util.HttpUtil;
import com.zan99.guaizhangmen.Util.L;
import com.zan99.guaizhangmen.Util.Qiniu.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalSetActivity extends BaseActivity {


    @BindView(R.id.imagetouxiang)
    ImageView imagetouxiang;
    @BindView(R.id.sctouxiang)
    TextView sctouxiang;
    @BindView(R.id.button2)
    LinearLayout button2;
    @BindView(R.id.nick_name)
    EditText nick_nameText;
    @BindView(R.id.mobile)
    TextView mobileText;
    @BindView(R.id.uname)
    EditText unameText;
    @BindView(R.id.touxiangsrc)
    EditText touxiangsrc;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.pasmobile)
    TextView pasmobile;
    @BindView(R.id.button_left)
    Button button_left;


    private String member_id,nick_name,mobile,uname,touxiang,head_img,parentid;
//    private String AccessKey="A4zBs4WNunfb0hF8Ekgofgvi--m2KilaZuUODoC0";
//    private String SecretKey="o5VdMQUvByrs4wZ2dABKYdrCz9CJJgppKPF0NdI_";
//    private String bucket="wanghao";

    private String AccessKey="lVWTt_OxbmlDhY_6Xu6RksVZQGmQGtVhkVp0bnlU";
    private String SecretKey="t_uXWTU71d2Elfhqli4KrGdRcjCqmkTCYZbAlQ_w";
    private String bucket="guaizhangmen";
    private String domainurl="qiniu.guaizhangmen.com";

    private String srcPath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set);
        ButterKnife.bind(this);
        init();
    }

    @butterknife.OnClick({R.id.imagetouxiang, R.id.sctouxiang, R.id.button2, R.id.save, R.id.pasmobile,R.id.button_left})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imagetouxiang:
                changeHeadIcon();
                break;
            case R.id.button_left:
            case R.id.button2:
                finish();
                break;
            case R.id.save:
                savefile();
                break;
            case R.id.pasmobile:
                Intent savetel=new Intent(this,UnbundlingActivity.class);
                startActivity(savetel);
                break;

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    private void init(){
        SharedPreferences pref = getSharedPreferences(MenModel.FILENAME,MODE_PRIVATE);
        member_id= L.decrypt(pref.getString("member_id",""), MenModel.LKEY);
        head_img= L.decrypt(pref.getString("head_img",""), MenModel.LKEY);
        mobile= L.decrypt(pref.getString("mobile",""), MenModel.LKEY);
        nick_name= L.decrypt(pref.getString("nick_name",""), MenModel.LKEY);
        uname= L.decrypt(pref.getString("uname",""), MenModel.LKEY);
        parentid = L.decrypt(pref.getString("parentid", ""), MenModel.LKEY);
        if(!TextUtils.isEmpty(head_img)){
            touxiangsrc.setText(head_img);
            head_img="http://"+head_img+"?imageView2/2/w/300/h/800/interlace/0/q/100";
            System.out.println("head_img:"+head_img);
            Picasso.with(this).load(head_img).transform(new CircleTransform()).into(imagetouxiang);
        }

        if(uname.equals("0")){
            unameText.setVisibility(View.VISIBLE);
        }else{
            unameText.setVisibility(View.GONE);
            unameText.setText(uname);
        }

        nick_nameText.setText(nick_name);


        pasmobile.setText(settingphone(mobile));
        mobileText.setText(mobile);

    }




    private void changeHeadIcon() {

        final CharSequence[] items = { "相册", "拍照" };
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // 这里item是根据选择的方式，
                        if (item == 0) {
                            Intent local = new Intent();
                            local.setType("image/*");
                            local.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(local,2);
                        } else {
                            L.d("2");
                            Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(it,1);
                        }
                    }
                }).create();
        dlg.show();

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
                    //imagetouxiang.setImageBitmap(toRoundCorner(b,200));
                    String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    String fileNmae = Environment.getExternalStorageDirectory().toString()+ File.separator+"dong/image/"+name+".jpg";
                    //将刚拍照的相片在相册中显示
                    Uri localUri = Uri.fromFile(new File(fileNmae));
                    Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                    sendBroadcast(localIntent);

                    srcPath=fileNmae;
                    System.out.println(fileNmae+"----------保存路径1");
                    File myCaptureFile =new File(fileNmae);
                    try {
                        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            if(!myCaptureFile.getParentFile().exists()){
                                myCaptureFile.getParentFile().mkdirs();
                            }
                            System.out.println("====>myCaptureFile:"+myCaptureFile);
                            BufferedOutputStream bos;
                            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                            b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                            bos.flush();
                            bos.close();
                        }else{
                            Toast toast= Toast.makeText(this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:




                    Uri uri = data.getData();
                    //srcPath=getFilePathFromUrl(this,uri);
                    //L.d(srcPath);

                    String wholeID = DocumentsContract.getDocumentId(uri);
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = this.getApplication().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                            sel, new String[]{id}, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        srcPath = cursor.getString(columnIndex);
                    }
                    cursor.close();

                    break;
            }
            upImage(srcPath);
        }
    }

    /**
     * 从相册获取图片后，从uri获取路径
     */
    public static String getFilePathFromUrl(Context context, Uri uri) {
        String path = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String projection[] = {MediaStore.Images.ImageColumns.DATA};
            Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
            if (c != null && c.moveToFirst()) {
                path = c.getString(0);
            }
            if (c != null)
                c.close();

        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        }
        return path;
    }



    private void upImage(String picPath) {
        UploadManager uploadManager = new UploadManager();
        // 设置图片名字
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = "icon_" + sdf.format(new Date());
        uploadManager.put(picPath, key, Auth.create(AccessKey, SecretKey).uploadToken(bucket), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                if (info.isOK()) {
                    String headpicPath = "http://"+domainurl+"/" + key;

                    touxiangsrc.setText(domainurl+"/"+key);
                    Picasso.with(PersonalSetActivity.this).load(headpicPath).transform(new CircleTransform()).into(imagetouxiang);
                }else{
                    Toast.makeText(PersonalSetActivity.this,"头像上传失败，请稍后重试",Toast.LENGTH_SHORT).show();
                }

            }
        }, null);

    }


    //保存信息
    private void savefile(){
        nick_name=nick_nameText.getText().toString();
        mobile=mobileText.getText().toString();
        uname=unameText.getText().toString();
        if(TextUtils.isEmpty(uname)){
            uname = "0";
        }
        touxiang=touxiangsrc.getText().toString();

        String path = "/member.php/Interfaces/SaveInforMation";
        RequestParams params = new RequestParams();
        params.put("member_id", MenModel.member_id);
        params.put("nick_name",nick_name);
        params.put("mobile",mobile);
        params.put("uname",uname);
        params.put("head_img",touxiang);

        RequestBody requestBody = new FormBody.Builder()
                .add("member_id", MenModel.member_id)
                .add("nick_name", nick_name)
                .add("mobile", mobile)
                .add("uname", uname)
                .add("head_img", touxiang)
                .build();

        String path1 = "/member.php/Interfaces/SaveInforMation";

        initData1(path1,requestBody);

    }

    private void initData1(final String path, final RequestBody requestBody){
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                DownloadUtil.get().post(path, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("111", "parentid 请求失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        e.onNext(result);
                        e.onComplete();
                        Log.e("111", "parentid 请求成功");
                    }
                });
            }
        });
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                initView1(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void initView1(String result){
        try {
            JSONObject myJsonObject  = new JSONObject(result);
            String act=myJsonObject.getString("act");
            int code=myJsonObject.getInt("code");
            String parentId = myJsonObject.getString("parent_id");

            Log.e("111", "parentid = "+parentId+"..."+act+"..."+code);

            if(code==0&&!parentId.equals("0")) {
                SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                editor.putString("member_id", L.encrypt(member_id, MenModel.LKEY));
                editor.putString("head_img", L.encrypt(touxiang, MenModel.LKEY));
                editor.putString("mobile", L.encrypt(mobile, MenModel.LKEY));
                editor.putString("nick_name", L.encrypt(nick_name, MenModel.LKEY));
                editor.putString("uname", L.encrypt(uname, MenModel.LKEY));
                editor.putString("parentid", L.encrypt(parentId, MenModel.LKEY));
                editor.commit();
                Toast.makeText(PersonalSetActivity.this,act, Toast.LENGTH_SHORT).show();
                finish();
            }
            Toast.makeText(PersonalSetActivity.this,act, Toast.LENGTH_SHORT).show();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initData(final String path, RequestBody requestBody, final RequestParams params){
        HttpUtil.post(path, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                initView(responseBody,path,params.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                getTyChaterrmsg(path,params.toString());
            }
        });

    }

    private void initView(byte[] responseBody,String path,String params){
        String result = null;
        try {
            result = new String(responseBody, "utf-8");
            JSONObject myJsonObject  = new JSONObject(result);
            String act=myJsonObject.getString("act");
            int code=myJsonObject.getInt("code");

            if(code==0) {
                SharedPreferences.Editor editor = getSharedPreferences(MenModel.FILENAME, MODE_PRIVATE).edit();
                editor.putString("member_id", L.encrypt(member_id, MenModel.LKEY));
                editor.putString("head_img", L.encrypt(touxiang, MenModel.LKEY));
                editor.putString("mobile", L.encrypt(mobile, MenModel.LKEY));
                editor.putString("nick_name", L.encrypt(nick_name, MenModel.LKEY));
                editor.putString("uname", L.encrypt(uname, MenModel.LKEY));
                editor.commit();
                Toast.makeText(PersonalSetActivity.this,act, Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            getTyChaterrmsg(path,params);
        } catch (JSONException e) {
            e.printStackTrace();
            getTyChaterrmsg(path,params);
        }
    }


}


