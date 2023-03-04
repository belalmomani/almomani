package com.noSmoking.smoking;

/*smokeData: متغير من نوع SmokeData لحفظ بيانات الدخان.

dailyCigaretteCount: متغير لتعقب عدد السجائر التي تم التدخين بها يومياً.
preferences: متغير من نوع SharedPreferences لحفظ تفضيلات المستخدم.
editor: متغير من نوع SharedPreferences.Editor لتحرير تفضيلات المستخدم.
cigaretteCountTextView: عنصر TextView لعرض عدد السجائر.
timeLeftTextView: عنصر TextView لعرض الوقت المتبقي.
resetButton: عنصر زر Button لإعادة تعيين البيانات إلى القيمة الافتراضية.
dailyCountTextView: عنصر TextView لعرض عدد السجائر المدخنة اليوم.
ACTION_UPDATE_CIGARETTE_COUNT: ثابت String يحتوي على اسم العملية الإضافية المستخدمة لتحديث عدد السجائر.
EXTRA_CIGARETTE_COUNT: ثابت String يحتوي على اسم مفتاح القيمة الإضافية المستخدمة لتحديث عدد السجائر.
NOTIFICATION_CHANNEL_ID: ثابت String يحتوي على معرف قناة الإشعار.
NOTIFICATION_ID: ثابت int يحتوي على معرف الإشعار.
smokeTimer: متغير من نوع SmokeTimer لإدارة المؤقت للإشعارات.
timerRunning: متغير boolean لتعقب ما إذا كان المؤقت يعمل أم لا.
timerTextView: عنصر TextView لعرض الوقت المتبقي.
timerHandler: متغير من نوع Handler لإنشاء مهمة العد التنازلي للمؤقت.
notificationHelper: متغير من نوع SmokeNotificationHelper لإنشاء الإشعارات.
startButton: عنصر زر Button لبدء المؤقت.
stopButton: عنصر زر Button لإيقاف المؤقت.
mPreferences: متغير من نوع SharedPreferences لحفظ تفضيلات المستخدم.
sharedPrefFile: String يحتوي على اسم ملف SharedPreferences المستخدم لتخزين التف




هذا مقدمة الكود في تطبيق "No Smoking" وتمثل المتغيرات المستخدمة في الصفحة الرئيسية للتطبيق. يتم تعريف متغيرات لتمثيل العناصر الرسومية في الواجهة مثل الأزرار والنصوص والعدادات، بالإضافة إلى الإعدادات المخزنة في SharedPreferences. كما يتم تعريف بعض المتغيرات الأخرى للتعامل مع عمليات الحسابات في التطبيق، مثل عدد السجائر المدخنة والوقت المتبقي والتكاليف المدخرة. يتم استخدام بعض المتغيرات للتعامل مع العمليات الزمنية في التطبيق مثل العداد الزمني والمؤشر الزمني ومساعد الإشعارات. يستخدم التطبيق بعض المتغيرات للتعامل مع عمليات الحسابات مثل العدد الكلي للسجائر وسعر الحزمة وعرض البيانات المحفوظة في SharedPreferences.*/


import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private SmokeData smokeData;
    private int dailyCigaretteCount = 0;
    private SharedPreferences preferences, mPreferences;
    private SharedPreferences.Editor editor;
    private TextView cigaretteCountTextView, timeLeftTextView, dailyCountTextView, timerTextView, mNumberOfCigarettesTextView, mMoneySavedTextView, fact_textview, money_saved_text_view, number_of_cigarettes_text_view, cigarette_price_label, title_textview, cigarette_count_label;
    private EditText cigarette_count_edittext;
    private Button addButton, resetButton, startButton, stopButton, calculate_button;
    private boolean timerRunning = false;
    private Handler timerHandler = new Handler();
    private SmokeTimer smokeTimer;
    private SmokeNotificationHelper notificationHelper;
    private String sharedPrefFile = "com.noSmoking.smoking";
    private int mNumberOfCigarettes, mPacketPrice;
    public static final String ACTION_UPDATE_CIGARETTE_COUNT = "com.noSmoking.smoking.action.UPDATE_CIGARETTE_COUNT";
    public static final String EXTRA_CIGARETTE_COUNT = "com.noSmoking.smoking.EXTRA_CIGARETTE_COUNT";
    public static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel_id";
    public static final int NOTIFICATION_ID =22 ;
    int cigaretteCount = 0;


    private TextView timeSinceLastSmokeTextView;
    private Button smokeButton;
    private Handler handler;
    private Runnable updateRunnable;
    private SimpleDateFormat dateFormat;

    private long timeSinceLastSmokeInMillis;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            // تحديث الوقت هنا
            timerHandler.postDelayed(this, 1000); // إعادة تشغيل المهمة كل ثانية واحدة
        }
    };

    private void startTimer() {
        timerRunning = true;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void pauseTimer() {
        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }


    public static int getCigaretteCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return prefs.getInt("cigaretteCount", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView timeSinceLastSmokeTextView = findViewById(R.id.time_since_last_smoke_text_view);
        long timeSinceLastSmokeInMillis = 60000; // عدد الدقائق التي مرت منذ آخر مرة تم فيها تدخين السجائر



        timeSinceLastSmokeTextView = findViewById(R.id.time_since_last_smoke_textview);
        smokeButton = findViewById(R.id.smoke_button);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("daily_cigarette_count", dailyCigaretteCount);
        editor.apply();

// تحديث عدد السجائر المدخنة
        smokeData = new SmokeData(0, new Date(), "Unknown");
        smokeData.setCigaretteCount(cigaretteCount);

        editor = preferences.edit();
        editor.putInt("cigarette_count", smokeData.getCigaretteCount());
        editor.apply();
        // Reset time since last smoke
        timeSinceLastSmokeInMillis = 0;
        // Show a congratulatory message every 10 cigarettes




        Date now = new Date();
        long timeSinceLastSmokeInMins = (now.getTime() - smokeData.getTimestamp().getTime()) / 1000 / 60;
        timeSinceLastSmokeInMillis = timeSinceLastSmokeInMins * 60 * 1000;

        Handler handler = new Handler();
        Runnable updateRunnable = new Runnable() {
            TextView timeSinceLastSmokeTextView = findViewById(R.id.time_since_last_smoke_text_view);
            long timeSinceLastSmokeInMillis = 60000;

            @Override
            public void run() {
                timeSinceLastSmokeInMillis += 1000;
                String timeSinceLastSmoke = formatMillisAsTime(timeSinceLastSmokeInMillis);
                String timeSinceLastSmokeMessage = getString(R.string.time_since_last_smoke, timeSinceLastSmoke);
                timeSinceLastSmokeTextView.setText(timeSinceLastSmokeMessage);

                handler.postDelayed(this, 1000);

            }
        };
        handler.postDelayed(updateRunnable, 1000);



        handler.postDelayed(updateRunnable, 1000);

        cigaretteCountTextView.setText(String.valueOf(smokeData.getCigaretteCount()));

        cigarette_count_label = findViewById(R.id.cigarette_count_label);
        cigarette_count_label.setText(String.valueOf(cigaretteCount));


        title_textview = findViewById(R.id.title_textview);
        title_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Welcome to Smoke Free app", Toast.LENGTH_SHORT).show();
            }
        });

        money_saved_text_view = findViewById(R.id.money_saved_text_view);
        money_saved_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText cigarette_price_edittext = findViewById(R.id.cigarette_price_edittext);
                EditText cigarette_count_edittext = findViewById(R.id.cigarette_count_edittext);

                double cigarettePrice = Double.parseDouble(cigarette_price_edittext.getText().toString());
                int cigaretteCount = Integer.parseInt(cigarette_count_edittext.getText().toString());
                double savedMoney = cigarettePrice * cigaretteCount * 30; // يفترض أن تدخن حزمة واحدة يوميًا لمدة 30 يومًا
                String savedMoneyString = String.format("%.2f", savedMoney); // تقليل عدد الأرقام العشرية إلى رقمين فقط
                String message = "You saved $" + savedMoneyString + " so far!";
                money_saved_text_view.setText(message);
            }
        });



        cigarette_count_edittext = findViewById(R.id.cigarette_count_edittext);
        cigarette_count_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // clear the text inside the EditText when clicked
                cigarette_count_edittext.getText().clear();
            }
        });

        cigarette_price_label = findViewById(R.id.cigarette_price_label);
        cigarette_price_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = "سعر السجائر هو 10 دولارات";
                Toast.makeText(getApplicationContext(), price, Toast.LENGTH_SHORT).show();
            }
        });

        dailyCountTextView = findViewById(R.id.daily_count_textview);

        number_of_cigarettes_text_view = findViewById(R.id.number_of_cigarettes_text_view);
        number_of_cigarettes_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter Daily Cigarette Count");

                // إنشاء EditText وإضافتها إلى الحوار
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // إضافة زر حفظ للحوار
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputValue = input.getText().toString();
                        int dailyCigaretteCount = Integer.parseInt(inputValue);

                        // تحديث ال TextView المعني بعدد السجائر
                        TextView dailyCountTextView = findViewById(R.id.daily_count_textview);


                        // حفظ العدد المدخل باستخدام SharedPreferences
                        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("displayed_cigarette_count", dailyCigaretteCount);
                        editor.apply();
                    }
                });



                // إضافة زر إلغاء للحوار
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        // إضافة OnClickListener للزر الذي يقوم بإضافة السجائر الجديدة
        addButton = findViewById(R.id.add_button);
        SharedPreferences.Editor finalEditor = editor;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // زيادة عدد السجائر المدخنة لهذا اليوم
                dailyCigaretteCount++;
                finalEditor.putInt("displayed_cigarette_count", dailyCigaretteCount);
                finalEditor.apply();



                // تحديث الوقت الذي تم فيه تدخين السيجارة الأخيرة
                long lastSmokedTime = System.currentTimeMillis();
                finalEditor.putLong("last_smoked_time", lastSmokedTime);
                finalEditor.apply();

                // إظهار إشعار يذكر المستخدم بأنه قام بتدخين سيجارة جديدة
                SmokeNotificationHelper notificationHelper = new SmokeNotificationHelper(MainActivity.this);
                notificationHelper.showNotification();
            }
        });









    // تعيين preferences و editor
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        smokeData = new SmokeData(this);
        notificationHelper = new SmokeNotificationHelper(this, this);

       /* SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("number_of_cigarettes", mNumberOfCigarettes);
        editor.putInt("packet_price", mPacketPrice);
        editor.apply();

// عرض العدد الجديد للسجائر والمبلغ الموفر
        mNumberOfCigarettesTextView.setText(getString(R.string.number_of_cigarettes));
        //updateMoneySaved();

*/

        // حساب dailyCigaretteCount وتحديث preferences إذا لزم الأمر
        int cigaretteCount = preferences.getInt("cigarette_count", 0);
        if (cigaretteCount == 0) {
            dailyCigaretteCount = 10; // إذا لم يتم تدخين أي سجائر سابقًا، فسيكون الحد الأدنى 10 سجائر في اليوم الواحد
            editor.putInt("daily_cigarette_count", dailyCigaretteCount);
            editor.apply();
        } else {
            long lastSmokedTime = preferences.getLong("last_smoked_time", 0);
            long elapsedTime = System.currentTimeMillis() - lastSmokedTime;
            int minutesElapsed = (int) (elapsedTime / 1000 / 60);
            int daysElapsed = minutesElapsed / 1440;
            int extraMinutes = minutesElapsed % 1440;

            dailyCigaretteCount = cigaretteCount / (daysElapsed + 1);
            dailyCigaretteCount += extraMinutes / (2 * (daysElapsed + 1));
            editor.putInt("daily_cigarette_count", dailyCigaretteCount);
            editor.apply();
        }
        int displayedCigaretteCount = preferences.getInt("displayed_cigarette_count", dailyCigaretteCount);
        TextView dailyCountTextView = findViewById(R.id.daily_count_textview);
        dailyCountTextView.setText("Daily count: " + displayedCigaretteCount);
        // عرض عدد السجائر اليومي في TextView



        cigaretteCountTextView = findViewById(R.id.cigarette_count_text_view);

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        smokeTimer = new SmokeTimer(this);


        fact_textview = findViewById(R.id.fact_textview);
        fact_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] facts = {"تدخين السجائر يسبب أكثر من 480,000 وفاة سنويًا في الولايات المتحدة فقط.", "تعتبر السجائر الإلكترونية وغيرها من المنتجات التي تحتوي على النيكوتين غير صحية أيضًا.", "تزيد أضرار التدخين على الجلد من حب الشباب وتجاعيد البشرة."};
                int randomIndex = new Random().nextInt(facts.length);
                Toast.makeText(MainActivity.this, facts[randomIndex], Toast.LENGTH_SHORT).show();
            }
        });


        Button settingsButton = findViewById(R.id.settings_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        timerTextView = findViewById(R.id.timerTextView);





        calculate_button = findViewById(R.id.calculate_button);
        calculate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get inputs from EditTexts
                EditText input1 = findViewById(R.id.input1);
                EditText input2 = findViewById(R.id.input2);
                double value1 = Double.parseDouble(input1.getText().toString());
                double value2 = Double.parseDouble(input2.getText().toString());

                // perform calculation
                double result = value1 + value2;

                // display result in TextView
                TextView resultTextView = findViewById(R.id.result_text_view);
                resultTextView.setText(String.valueOf(result));
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cigaretteCount = 0;
                try {
                    cigaretteCount = Integer.parseInt(cigaretteCountTextView.getText().toString());
                } catch (NumberFormatException e) {
                    Log.e("MainActivity", "Error converting cigarette count to integer: " + e.getMessage());
                }
                smokeTimer.startTimer();
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smokeTimer.stopTimer();
            }
        });
    }

    public void updateTimer(long milliseconds) {
        // Calculate remaining time in minutes and seconds
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

// Update the UI with the remaining time
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);

// Play a sound when there's 1 minute left
        if (minutes == 1 && seconds == 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            mediaPlayer.start();
        }

// If the timer has finished, show a message and reset the UI
        if (milliseconds <= 0) {
            timerTextView.setText("00:00");
            Toast.makeText(this, "Congratulations, you have completed the task!", Toast.LENGTH_LONG).show();
            resetTimer();
        }
    }

    private void resetTimer() {
        // Reset the timer
        int totalTime = 0;
        int cigaretteCount = 0;
        smokeTimer.stopTimer();
        smokeTimer.startTimer();
    }



    public void onFinish() {
        // When the countdown is finished
        updateTimer(0);

        Toast.makeText(getApplicationContext(), "Congratulations, you have completed the challenge!", Toast.LENGTH_LONG).show();
        // Reset the cigarette count
        int cigaretteCount = 0;
    }

    CountDownTimer timer = new CountDownTimer(60000, 1000) {

        public void onTick(long millisUntilFinished) {
            // Do something on tick
        }

        public void onFinish() {
            // Do something when the timer finishes
        }

    }.start();
/*
    private void updateMoneySaved() {
        int money_saved = (mNumberOfCigarettes / 20) * mPacketPrice;
        mMoneySavedTextView.setText(getString(R.string.money_saved));
    }

    public void SettingsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
    */



@Override
protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(updateRunnable);
}
    private String formatMillisAsTime(long millis) {
        String output;
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        if (hours > 0) {
            output = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            output = String.format("%d:%02d", minutes, seconds);
        }

        return output;
    }
}
