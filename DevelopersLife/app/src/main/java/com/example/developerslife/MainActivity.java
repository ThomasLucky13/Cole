package com.example.developerslife;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.developerslife.DataBase.DevelopersLifeDBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ImageView IVTry;            //ImageView Gif-изображение с использованием Glide
    TextView text;              //TextView с самим текстом
    TextView author, date;      //TextView автор и дата поста
    ImageButton PreviousButton; //Кнопка "обратно". Нужно для "включения/отключения" кликабельности
    ImageButton NextButton;     //Кнопка "следующее". Нужно для "включения/отключения" кликабельности
    ImageButton GetLikedButton; //Кнопа "добавить в любимое". Нужно для "включения/отключения" кликабельности
    Button RandButton, LatestButton, TopButton, HotButton, LikedButton; //Нужно для "включения/отключения" кликабельности
    String gifURL;              //Хранение URL gif-изображения для проверки
    int ind;                    //Текущий индекс

    private DevelopersLifeDBManager DBManager;


    boolean RandSection;    //Выбран раздел Random
    ArrayList <MemStruct> MemRand = new ArrayList<MemStruct>();    // Rand раздел
    int indRand = -1;    // Индекс Rand раздела

    boolean LatestSection;  //Выбран раздел Latest
    ArrayList <MemStruct> MemLatest = new ArrayList<MemStruct>();  // Latest раздел
    int indLatest = -1;  // Индекс Latest раздела

    boolean TopSection;  //Выбран раздел Top
    ArrayList <MemStruct> MemTop = new ArrayList<MemStruct>();  // Top раздел
    int indTop = -1;  // Индекс Top раздела

    boolean HotSection;  //Выбран раздел Hot
    ArrayList <MemStruct> MemHot = new ArrayList<MemStruct>();  // Hot раздел
    int indHot = -1;  // Индекс Hot раздела

    boolean LikedSection;  //Выбран раздел Liked
    ArrayList <MemStruct> MemLiked = new ArrayList<MemStruct>();  // Liked раздел
    int indLiked = 0;  // Индекс Liked раздела

    //----------------------------- Получение данных -----------------------------//
    //--------------- Получение JSON текста из Reader`а ---------------//
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    //-----------------------------------------------------------------//
    //---------------------- Получить JSONObject ----------------------//
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        JSONObject json = new JSONObject(jsonText);
        is.close();
        return json;
    }
    //-----------------------------------------------------------------//
    //---------------------- Получить JSONArray -----------------------//
    public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        jsonText = jsonText.replaceFirst("(.*)result\":","");         //Удаление лишнего
        jsonText = jsonText.replaceFirst(",\"totalCount\":(.*)","");  //////////////////
        JSONArray json = new JSONArray(jsonText);
        is.close();
        return json;
    }
    //-----------------------------------------------------------------//
//---------------------------- Работа с разделами ---------------------------//
    //----------------- Поток для получения с Random ------------------//
    class GetRand implements Runnable
    {
        public void run()
        {
            try {
                JSONObject json = readJsonFromUrl("https://developerslife.ru/random?json=true");
                MemRand.add(new MemStruct(json.getString("description"),json.getString("gifURL"),
                        json.getString("author"), json.getString("date")));
                ind++;
                if (ind==1)
                    PreviousButton.setClickable(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //-----------------------------------------------------------------//
    //----------------- Поток для получения с latest ------------------//
    class GetLatest implements Runnable
    {
        public void run()
        {
            try  {
                ind++;
                JSONArray array = readJsonArrayFromUrl("https://developerslife.ru/latest/"+ind/5+"?json=true");
                JSONObject json = array.getJSONObject(ind%5);
                MemLatest.add(new MemStruct(json.getString("description"),json.getString("gifURL"),
                        json.getString("author"), json.getString("date")));
                if (ind==1)
                    PreviousButton.setClickable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //-----------------------------------------------------------------//
    //------------------ Поток для получения с Top --------------------//
    class GetTop implements Runnable
    {
        public void run()
        {
            try  {
                ind++;
                JSONArray array = readJsonArrayFromUrl("https://developerslife.ru/top/"+ind/5+"?json=true");
                JSONObject json = array.getJSONObject(ind%5);
                MemTop.add(new MemStruct(json.getString("description"),json.getString("gifURL"),
                        json.getString("author"), json.getString("date")));
                if (ind==1)
                    PreviousButton.setClickable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //-----------------------------------------------------------------//
    //------------------ Поток для получения с Hot --------------------//
    class GetHot implements Runnable
    {
        public void run()
        {
            try  {
                ind++;
                JSONArray array = readJsonArrayFromUrl("https://developerslife.ru/hot/"+ind/5+"?json=true");
                if(array.isNull(ind%5))
                {
                    ind--;
                    return;
                }
                JSONObject json = array.getJSONObject(ind%5);
                MemHot.add(new MemStruct(json.getString("description"),json.getString("gifURL"),
                        json.getString("author"), json.getString("date")));
                if (ind==1)
                    PreviousButton.setClickable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //-----------------------------------------------------------------//
//----------------------------------------------------------------------------//
    //----------------------------------------------------------------------------//
    //--------------------------- Проверка наличия сети --------------------------//
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
    //----------------------------------------------------------------------------//
    //-------------------------------- onCreate() --------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBManager = new DevelopersLifeDBManager(this);

        IVTry = findViewById(R.id.ivTry);
        text = findViewById(R.id.textView);
        author = findViewById(R.id.author);
        date = findViewById(R.id.date);
        GetLikedButton = findViewById(R.id.SaveLike);
        PreviousButton = findViewById(R.id.imageButton2);
        PreviousButton.setClickable(false);
        NextButton = findViewById(R.id.imageButton);

        RandButton = findViewById(R.id.random);
        LatestButton = findViewById(R.id.Latest);
        TopButton = findViewById(R.id.Top);
        HotButton = findViewById(R.id.Hot);
        LikedButton = findViewById(R.id.Liked);

        RandSection = true;
        LatestSection = false;
        TopSection = false;
        HotSection = false;
        LikedSection = false;

        DBManager.openDb();
        //DBManager.DeleteTables(MemStructDB.Name);
        MemLiked = DBManager.GetMemStruct();
        DBManager.closeDb();

        ind = indRand;

        RandButton.setClickable(false);
        RandButton.setTextColor(Color.parseColor("#23549C"));
        GenNew();
    }
    //----------------------------------------------------------------------------//
    //--------------------------- Установка из раздела ---------------------------//
    //------------------- Получение gifURL -------------------//
    private boolean getGifURL()
    {
        if (RandSection)
        {
            if (MemRand.size()==0)
                return false;
            gifURL = MemRand.get(ind).gifURL;
        }
        else if (LatestSection)
        {
            if (MemLatest.size()==0)
                return false;
            gifURL = MemLatest.get(ind).gifURL;
        }
        else if (TopSection)
        {
            if (MemTop.size()==0)
                return false;
            gifURL = MemTop.get(ind).gifURL;
        }
        else if (HotSection)
        {
            if (MemHot.size()==0)
                return false;
            gifURL = MemHot.get(ind).gifURL;
        }else if (LikedSection)
        {
            if (MemLiked.size()==0)
                return false;
            gifURL = MemLiked.get(ind).gifURL;
        }
        return  true;
    }
    //--------------------------------------------------------//
    private void setSelectionMem( ArrayList<MemStruct> arrayMem)
    {
        if(MemLiked.indexOf(arrayMem.get(ind))==-1)
        {
            GetLikedButton.setClickable(true);
            GetLikedButton.setVisibility(View.VISIBLE);
        } else
        {
            GetLikedButton.setClickable(false);
            GetLikedButton.setVisibility(View.INVISIBLE);
        }

        text.setText(arrayMem.get(ind).Text);
        author.setText(arrayMem.get(ind).author);
        date.setText(arrayMem.get(ind).date);
        Glide.with(MainActivity.this)
                .load(gifURL)
                .placeholder(R.drawable.progress_bar)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(IVTry);
    }
    //--------------- Не удается получить пост ---------------//
    private void ErrorMem()
    {
        GetLikedButton.setClickable(false);
        text.setText("Упс... Похоже DevelopersLife ушли пить чай...");
        author.setText("");
        date.setText("");
        Glide.with(MainActivity.this)
                .load(R.drawable.error)
                .placeholder(R.drawable.progress_bar)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(IVTry);
    }
    //--------------------------------------------------------//
    private void SetPrevBut()
    {
        if (ind>0)
        {
            PreviousButton.setClickable(true);
            PreviousButton.setVisibility(View.VISIBLE);
        } else
        if (ind==0)
        {
            PreviousButton.setClickable(false);
            PreviousButton.setVisibility(View.INVISIBLE);
        }
    }
    private void setNextBut()
    {
        if (ind==MemLiked.size()-2)
        {
            NextButton.setClickable(true);
            NextButton.setVisibility(View.VISIBLE);
        } else
        if (ind==MemLiked.size()-1)
        {
            NextButton.setClickable(false);
            NextButton.setVisibility(View.INVISIBLE);
        }
    }
    //--------------- Установка текущего поста ---------------//
    public  void setMem()
    {
        if (getGifURL())
        {
            if (LikedSection)
            {
                setSelectionMem(MemLiked);
                SetPrevBut();
                setNextBut();
            } else
            {
                if ((gifURL.indexOf("https://")==0)&&(gifURL.endsWith(".gif")))
                {
                    if (RandSection)
                        setSelectionMem(MemRand);
                    else
                    if (LatestSection)
                        setSelectionMem(MemLatest);
                    else
                    if (TopSection)
                        setSelectionMem(MemTop);
                    else
                    if (HotSection)
                        setSelectionMem(MemHot);
                    SetPrevBut();
                } else
                    ErrorMem();
            }
        } else
            ErrorMem();
    }
    //--------------------------------------------------------//
    //----------------------------------------------------------------------------//
    //-------------------------- Получение нового поста --------------------------//
    //---------------- Получение поста из Rand ---------------//
    private void GenRand()
    {
        GetRand thread1 = new GetRand();
        Thread startThread = new Thread(thread1);
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------//
    //--------------- Получение поста из Latest --------------//
    private void GenLatest()
    {
        GetLatest thread1 = new GetLatest();
        Thread startThread = new Thread(thread1);
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------//
    //---------------- Получение поста из Top ----------------//
    private void GenTop()
    {
        GetTop thread1 = new GetTop();
        Thread startThread = new Thread(thread1);
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------//
    //---------------- Получение поста из Hot ----------------//
    private void GenHot()
    {
        GetHot thread1 = new GetHot();
        Thread startThread = new Thread(thread1);
        startThread.start();
        try {
            startThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //--------------------------------------------------------//
    //---------------- Получение нового поста ----------------//
    public void GenNew()
    {
        if(isOnline(this))
        {
            if (RandSection)
                GenRand();
            else if (LatestSection)
                GenLatest();
            else if (TopSection)
                GenTop();
            else if (TopSection)
                GenHot();
            setMem();
        } else
            ErrorMem();
    }
    //--------------------------------------------------------//
    //----------------------------------------------------------------------------//
    //------------------------ Вернуться к прошлому посту ------------------------//
    public  void previous (View view)
    {
        if (ind>0)
        {
            ind--;
            setMem();
        }
    }
    //----------------------------------------------------------------------------//
    //------------------------ Перейти к следующему посту ------------------------//
    //------------------- Проверка размера -------------------//
    private boolean CheckSize()
    {
        if (RandSection)
        {
            if (ind==MemRand.size()-1)
                return true;
        } else
            if (LatestSection)
            {
                if (ind==MemLatest.size()-1)
                    return true;
            }
            else
                if(TopSection)
                {
                    if (ind==MemTop.size()-1)
                        return true;
                }
                else
                    if (HotSection)
                    {
                        if (ind==MemHot.size()-1)
                        return true;
                    }
                    else
                    if (LikedSection)
                    {
                        if (ind==MemLiked.size()-1)
                            return true;
                    }
        return false;
    }
    //--------------------------------------------------------//
    //-------------- Перейти к следующему посту --------------//
    public void Next(View view)
    {
        if (CheckSize())
        {
            if (!LikedSection)
                GenNew();
        } else
        {
            ind++;
            setMem();
        }
    }
    //--------------------------------------------------------//
    //----------------------------------------------------------------------------//
    //------------------------- Переходы между разделами -------------------------//
    private void CloseRand()
    {
        indRand = ind;
        RandSection = false;
        RandButton.setClickable(true);
        RandButton.setTextColor(Color.parseColor("#000000"));
    }
    private void CloseLatest()
    {
        indLatest = ind;
        LatestSection = false;
        LatestButton.setClickable(true);
        LatestButton.setTextColor(Color.parseColor("#000000"));
    }
    private void CloseHot()
    {
        indHot = ind;
        HotSection = false;
        HotButton.setClickable(true);
        HotButton.setTextColor(Color.parseColor("#000000"));
    }
    private void CloseTop()
    {
        indTop = ind;
        TopSection = false;
        TopButton.setClickable(true);
        TopButton.setTextColor(Color.parseColor("#000000"));
    }
    private void CloseLiked()
    {
        indLiked = ind;
        LikedSection = false;
        LikedButton.setClickable(true);
        LikedButton.setTextColor(Color.parseColor("#000000"));
        GetLikedButton.setClickable(true);
        NextButton.setClickable(true);
        NextButton.setVisibility(View.VISIBLE);
    }
    //------------------ Выбор Rand раздела ------------------//
    public void ChooseRand(View view)
    {
        if (LatestSection)
        {
            CloseLatest();
        } else
            if (TopSection)
            {
                CloseTop();
            } else
                if (HotSection)
                {
                    CloseHot();
                }
                else
                    if (LikedSection)
                    {
                        CloseLiked();
                    }
        ind = indRand;
        if (ind!=-1)
            ind--;
        RandSection = true;
        RandButton.setClickable(false);
        RandButton.setTextColor(Color.parseColor("#23549C"));
        Next(view);
    }
    //--------------------------------------------------------//
    //----------------- Выбор Latest раздела -----------------//
    @SuppressLint("ResourceType")
    public void ChooseLatest(View view)
    {
        if (RandSection)
        {
            CloseRand();
        } else
        if (TopSection)
        {
            CloseTop();
        } else
        if (HotSection)
        {
            CloseHot();
        }
        else
        if (LikedSection)
        {
            CloseLiked();
        }
        ind = indLatest;
        if (ind!=-1)
            ind--;
        LatestSection = true;
        LatestButton.setClickable(false);
        LatestButton.setTextColor(Color.parseColor("#23549C"));
        Next(view);
    }
    //--------------------------------------------------------//
    //------------------- Выбор Top раздела ------------------//
    public void ChooseTop(View view)
    {
        if (RandSection)
        {
            CloseRand();
        } else
        if (LatestSection)
        {
            CloseLatest();
        } else
        if (HotSection)
        {
            CloseHot();
        } else
        if (LikedSection)
        {
            CloseLiked();
        }
        ind = indTop;
        if (ind!=-1)
            ind--;
        TopSection = true;
        TopButton.setClickable(false);
        TopButton.setTextColor(Color.parseColor("#23549C"));
        Next(view);
    }
    //--------------------------------------------------------//
    //------------------- Выбор Hot раздела ------------------//
    public void ChooseHot(View view)
    {
        if (RandSection)
        {
            CloseRand();
        } else
        if (LatestSection)
        {
            CloseLatest();
        } else
        if (TopSection)
        {
            CloseTop();
        } else
        if (LikedSection)
        {
            CloseLiked();
        }
        ind = indHot;
        if (ind!=-1)
            ind--;
        HotSection = true;
        HotButton.setClickable(false);
        HotButton.setTextColor(Color.parseColor("#23549C"));
        Next(view);
    }
    //--------------------------------------------------------//
    //------------------ Выбор Liked раздела -----------------//
    public void ChooseLiked(View view)
    {
        if (RandSection)
        {
            CloseRand();
        } else
        if (LatestSection)
        {
            CloseLatest();
        } else
        if (TopSection)
        {
            CloseTop();
        } else
        if (HotSection)
        {
            CloseHot();
        }
        ind = indLiked;
        LikedSection = true;
        LikedButton.setClickable(false);
        LikedButton.setTextColor(Color.parseColor("#23549C"));
        GetLikedButton.setClickable(false);

        setMem();
    }
    //--------------------------------------------------------//
    //----------------------------------------------------------------------------//
    private MemStruct GetCurrentMemStruct()
    {
        MemStruct res = new MemStruct();
        if (RandSection)
            res = MemRand.get(ind);
        else
        if (LatestSection)
            res = MemLatest.get(ind);
        else
        if (TopSection)
            res = MemTop.get(ind);
        else
        if (HotSection)
            res = MemHot.get(ind);
        return res;
    }
    public boolean NewMem(MemStruct mem)
    {
        int MemCount = MemLiked.size();
        MemStruct CheckMem;
        for (int i=0; i<MemCount; i++)
        {
            CheckMem=MemLiked.get(i);
            if((CheckMem.author==mem.author)&&(CheckMem.date==mem.date))
                return false;
        }
        return true;
    }
    public void SaveAsLiked(View view)
    {
        if (ind>=0)
        {
            MemStruct mem = GetCurrentMemStruct();
            if (MemLiked.indexOf(mem)==-1)
            {
                DBManager.openDb();
                DBManager.insertMemStruct(mem.Text, mem.gifURL, mem.author, mem.date);
                DBManager.closeDb();
                if (MemLiked.get(0).author=="")
                    MemLiked.clear();
                MemLiked.add(mem);
                GetLikedButton.setClickable(false);
                GetLikedButton.setVisibility(View.INVISIBLE);
            }
        }
    }

}