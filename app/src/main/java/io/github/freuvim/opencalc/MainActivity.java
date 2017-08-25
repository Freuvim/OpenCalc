package io.github.freuvim.opencalc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import test.jinesh.easypermissionslib.EasyPermission;

public class MainActivity extends AppCompatActivity implements EasyPermission.OnPermissionResult {

    final EasyPermission easyPermission = new EasyPermission();
    private Boolean permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissions = false;
        easyPermission.requestPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        File imgFile = new File(Environment.getExternalStorageDirectory().getPath() + "/teste.jpg");
        int time = 3000;
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = findViewById(R.id.imageView);
            myImage.setImageBitmap(myBitmap);
        } else {
            time = 0;
            Toast.makeText(MainActivity.this, "Imagem não encontrada!" + imgFile.getPath(), Toast.LENGTH_LONG).show();
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                openCalc();
            }
        };
        Handler h = new Handler();
        if (permissions) {
            h.postDelayed(r, time);
        } else {
            Toast.makeText(MainActivity.this, "É necessário aceitar as permissões de acesso ao SDCARD!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        easyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(String permission, boolean isGranted) {
        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (isGranted) {
                permissions = true;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void openCalc() {
        ArrayList<HashMap<String, Object>> items = new ArrayList<>();

        final PackageManager pm = getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (PackageInfo pi : packs) {
            if ("calcul".contains(pi.packageName.toLowerCase())) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }
        if (items.size() >= 1) {
            String packageName = (String) items.get(0).get("packageName");
            Intent i = pm.getLaunchIntentForPackage(packageName);
            if (i != null)
                startActivity(i);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_CALCULATOR);
            startActivity(intent);
        }
        finish();//
    }
}
