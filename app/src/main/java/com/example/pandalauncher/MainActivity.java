package com.example.pandalauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private List<AppItem> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadInstalledApps();

        GridView gridView = findViewById(R.id.apps_grid);
        gridView.setAdapter(new AppAdapter());

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AppItem app = appList.get(position);
            Intent launchIntent = packageManager.getLaunchIntentForPackage(app.packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        });
    }

    private void loadInstalledApps() {
        packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : availableActivities) {
            AppItem app = new AppItem();
            app.label = ri.loadLabel(packageManager).toString();
            app.packageName = ri.activityInfo.packageName;
            app.icon = ri.loadIcon(packageManager);
            appList.add(app);
        }
    }

    private static class AppItem {
        String label;
        String packageName;
        Drawable icon;
    }

    private class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() { return appList.size(); }

        @Override
        public Object getItem(int position) { return appList.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.app_item_layout, parent, false);
            }
            AppItem app = appList.get(position);

            ImageView iconView = convertView.findViewById(R.id.app_icon);
            TextView nameView = convertView.findViewById(R.id.app_name);

            iconView.setImageDrawable(app.icon);
            nameView.setText(app.label);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing on home screen
    }
}

