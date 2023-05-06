package me.soda.bfg;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class FavGetter {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private final HttpRequest.Builder builder;
    private final long id;


    // 获取收藏夹列表
    public FavList getFavFolders() throws IOException, InterruptedException {
        URI uri = URI.create("https://api.bilibili.com/x/v3/fav/folder/created/list-all?up_mid=" + id);
        var res = CLIENT.send(builder.uri(uri).build(), HttpResponse.BodyHandlers.ofInputStream());
        InputStream in = res.body();
        if (res.statusCode() != 200 || in == null) throw new IOException();
        return GSON.fromJson(new InputStreamReader(in), FavList.class);
    }

    // 获取多个收藏夹所有视频
    public List<FolderResult> getFolderResults(List<FavFolder> folders) throws IOException, InterruptedException {
        List<FolderResult> results = new ArrayList<>();
        for (FavFolder folder : folders) {
            results.add(getFolderResult(folder));
        }
        return results;
    }

    // 获取单个收藏夹所有视频
    public FolderResult getFolderResult(FavFolder folder) throws IOException, InterruptedException {
        int pages = (int) Math.ceil(folder.media_count / 20f);
        FolderResult realResult = null;
        for (int i = 1; i <= pages; i++) {
            Thread.sleep(500);
            URI uri = URI.create("https://api.bilibili.com/x/v3/fav/resource/list?media_id=" + folder.id + "&pn=" + i + "&ps=20&keyword=&order=mtime&type=0&tid=0&platform=web");
            var res = CLIENT.send(builder.uri(uri).build(), HttpResponse.BodyHandlers.ofInputStream());
            InputStream in = res.body();
            if (res.statusCode() != 200 || in == null) throw new IOException();
            FolderResult result = GSON.fromJson(new InputStreamReader(in), FolderResult.class);
            if (realResult != null) realResult.data.medias.addAll(result.data.medias);
            else realResult = result;
            System.out.println("获取收藏夹" + i + "/" + pages);
        }
        return realResult;
    }

    public FavGetter(long id, String sessData) {
        this.id = id;
        builder = HttpRequest.newBuilder().headers("Cookie", "SESSDATA=" + sessData,
                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
    }

    public static class FavList {
        int code;
        String message;
        int ttl;
        FavData data;
    }

    public static class FavData {
        int count;
        List<FavFolder> list;
        Object season;
    }

    public static class FavFolder {
        long id;
        long fid;
        long mid;
        int attr;
        String title;
        int fav_state;
        int media_count;
    }

    public static class FolderResult {
        int code;
        String message;
        int ttl;
        FolderData data;
    }

    public static class FolderData {
        FolderInfo info;
        List<Media> medias;
        boolean has_more;
        int ttl;
    }

    public static class Media {
        long id;
        int type;
        String title;
        String cover;
        String intro;
        int page;
        int duration;
        MediaUpper upper;
        int attr;
        MediaCntInfo cnt_info;
        String link;
        long ctime;
        long pubtime;
        long fav_time;
        String bv_id;
        String bvid;
        Object season;
        Object ogv;
        UGC ugc;
    }

    public static class FolderInfo {
        long id;
        long fid;
        long mid;
        int attr;
        String title;
        String cover;
        Upper upper;
        int cover_type;
        CntInfo cnt_info;
        int type;
        String intro;
        long ctime;
        long mtime;
        int state;
        int fav_state;
        int like_state;
        int media_count;
    }

    public static class Upper {
        long mid;
        String name;
        String face;
        boolean followed;
        int vip_type;
        int vip_statue;
    }

    public static class CntInfo {
        int collect;
        int play;
        int thumb_up;
        int share;
    }

    public static class MediaUpper {
        long mid;
        String name;
        String face;
    }

    public static class MediaCntInfo {
        int collect;
        int play;
        int danmaku;
        int vt;
        int play_switch;
        int reply;
    }

    public static class UGC {
        long first_cid;
    }
}
